package com.education.lessons.ui.client.components.extended;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.education.lessons.ui.client.components.box.LessonsMessageBox;
import com.education.lessons.ui.client.model.LessonsBaseTreeModel;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class TreePanelDropTargetExtended extends TreePanelDropTarget {

	private Boolean allowDropOnNullNode = true;

	private HashMap<ComponentTypeEnum, List<ComponentTypeEnum>> dropRestrictions = new HashMap<ComponentTypeEnum, List<ComponentTypeEnum>>();
	
	public TreePanelDropTargetExtended(TreePanel<?> tree) {
		super(tree);
	}

	@Override
	@SuppressWarnings("unchecked")
	protected void showFeedback(DNDEvent event) {
		super.showFeedback(event);
		if (this.activeItem != null) {
			try {
				List<BeanModel> gridList = new ArrayList<BeanModel>();
				List<LessonsBaseTreeModel> treePanelList = new ArrayList<LessonsBaseTreeModel>();
				if(event.getDragSource() != null && event.getDragSource().getComponent() != null) {
					Component component = event.getDragSource().getComponent();
					if(component instanceof Grid<?>) {
						Grid<BeanModel> grid = (Grid<BeanModel>) component;
						gridList = grid.getSelectionModel().getSelection();
					} else if (component instanceof TreePanel<?>) {
						TreePanel<LessonsBaseTreeModel> treePanel = (TreePanel<LessonsBaseTreeModel>) component;
						treePanelList = treePanel.getSelectionModel().getSelection();
					}
				}
				for (BeanModel gridSourceModel : gridList) {
					if (this.canDropOn(gridSourceModel) == false)
						clearStyles(event);
				}
				for (LessonsBaseTreeModel treeSourceModel : treePanelList) {
					if (this.canDropOn(treeSourceModel) == false)
						clearStyles(event);
				}
			} catch (Exception e) {
				LessonsMessageBox.showErrorWindow(e.getMessage());
			}
		} else if (this.allowDropOnNullNode == false) {
			clearStyles(event);
		}
	}
	
	/**
	 * @return <code>true<code> if there are no restrictions or the source
	 * is dropped on a valid target otherwise <code>false</code>
	 */

	private boolean canDropOn(BeanModel gridSourceModel) {
		
		LessonsBaseTreeModel targetObject = (LessonsBaseTreeModel) activeItem.getModel();
		
		CompositeDTO targetObjectDTO = null;
		CompositeDTO sourceObjectDTO = null;

		if (this.dropRestrictions.isEmpty() == true) {
			return true;
		} else {
			if (targetObject != null) {
				targetObjectDTO = targetObject.getBean();
			}
			if (gridSourceModel != null) {
				sourceObjectDTO = gridSourceModel.getBean();
			}
			if (this.dropRestrictions.get(targetObjectDTO.getType()) != null
					&& this.dropRestrictions.get(targetObjectDTO.getType()).contains(sourceObjectDTO.getType())) {
				return true;
			}
		}
		return false;
	}
	
	private boolean canDropOn(LessonsBaseTreeModel sourceObject) {
		
		LessonsBaseTreeModel targetObject = (LessonsBaseTreeModel) activeItem.getModel();

		CompositeDTO targetObjectDTO = null;
		CompositeDTO sourceObjectDTO = null;

		if (this.dropRestrictions.isEmpty() == true) {
			return true;
		} else {
			if (targetObject != null) {
				targetObjectDTO = targetObject.getBean();
				System.out.println("TARGER CLASS: "  + targetObjectDTO.getType());
			}
			if (sourceObject != null) {
				sourceObjectDTO = sourceObject.getBean();
				System.out.println("SOURCE CLASS: "  + sourceObjectDTO.getType());
			}
			if (this.dropRestrictions.get(targetObjectDTO.getType()) != null
					&& this.dropRestrictions.get(targetObjectDTO.getType()).contains(sourceObjectDTO.getType())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Adds a new drop restriction.
	 */
	public void addDropRestriction(ComponentTypeEnum target, ComponentTypeEnum source) {
		List<ComponentTypeEnum> allowedSources = this.dropRestrictions.get(target);
		if (allowedSources == null) {
			allowedSources = new ArrayList<ComponentTypeEnum>();
		}
		allowedSources.add(source);
		this.dropRestrictions.put(target, allowedSources);
	}

	/**
	 * Returns whether drops are allowed on null nodes of the tree panel.
	 */
	public boolean isAllowDropOnNullNode() {
		return allowDropOnNullNode;
	}

	public void setAllowDropOnNullNode(Boolean allowDropOnNullNode) {
		this.allowDropOnNullNode = allowDropOnNullNode;
	}

	public HashMap<ComponentTypeEnum, List<ComponentTypeEnum>> getDropRestrictions() {
		return dropRestrictions;
	}
}
