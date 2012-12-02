package com.education.lessons.ui.client.mvc;

import java.util.List;

import com.education.lessons.ui.client.components.AbstractCallback;
import com.education.lessons.ui.client.components.tree.BasicTree;
import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.services.AsyncServiceFactory;
import com.education.lessons.ui.client.services.DataProviderServiceAsync;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.ui.client.utils.Utils;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class NavigationView extends View {

	private ContentPanel westPanel;
	private TabPanel tabPanel;
	private TabItem searchItem;

	private BasicTree treePanel;

	public NavigationView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		westPanel = (ContentPanel) Registry.get(IConstants.WEST_PANEL);
		westPanel.mask(IConstants.LOADING);
		westPanel.setHeading(IConstants.NAVIGATION);
		westPanel.setLayout(new FitLayout());
		westPanel.add(createTabPanel());
		initTreeContentPanel();
		westPanel.syncSize();
		westPanel.unmask();
	}

	private TabPanel createTabPanel() {
		tabPanel = new TabPanel();
		tabPanel.setMinTabWidth(300);
		tabPanel.setBorderStyle(true);
		tabPanel.setBodyBorder(true);
		tabPanel.setTabPosition(TabPosition.TOP);

		searchItem = new TabItem();
		searchItem.setLayout(new AccordionLayout());
		searchItem.setText(IConstants.GRAPH);

		tabPanel.add(searchItem);

		return tabPanel;
	}

	private void initTreeContentPanel() {
		treePanel = new BasicTree();
		this.searchItem.add(treePanel);
	}

	private void modelMovements(final CompositeDTO newParentComponentDTO, final List<CompositeDTO> childrenToMove) {
		treePanel.mask(IConstants.LOADING);
		AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {
			
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				treePanel.unmask();
			}

			@Override
			public void onSuccess(CompositeDTO updatedGraph) {
				AppEvent event = new AppEvent(LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES);
				event.setData(IConstants.UPDATED_GRAPH, updatedGraph);
				Dispatcher.get().dispatch(event);
				treePanel.unmask();
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).moveTreeContent(newParentComponentDTO.getId(), Utils.getListOfIDs(childrenToMove), callback);
	}

	private void modelUpdates(final Integer parentID, final List<CompositeDTO> childrenToUpdate) {
		treePanel.mask(IConstants.LOADING);
		AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				treePanel.unmask();
			}

			@Override
			public void onSuccess(CompositeDTO updatedGraph) {
				AppEvent event = new AppEvent(LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES);
				event.setData(IConstants.UPDATED_GRAPH, updatedGraph);
				Dispatcher.get().dispatch(event);
				treePanel.unmask();
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).updateTreeContent(parentID, childrenToUpdate, callback);
	}
	
	private void modelAllChildrenRemoval(CompositeDTO parentComponentDTO, final Boolean isRootNode) {
		treePanel.mask(IConstants.LOADING);
		AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				treePanel.unmask();
			}

			@Override
			public void onSuccess(CompositeDTO updatedGraph) {
				AppEvent event = new AppEvent(LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES);
				event.setData(IConstants.UPDATED_GRAPH, updatedGraph);
				Dispatcher.get().dispatch(event);
				treePanel.unmask();
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).removeAllChildrenTreeContent(parentComponentDTO.getId(), callback);
	}

	private void modelRemoval(final Integer childID, final Boolean isLeafNode) {
		treePanel.mask(IConstants.LOADING);
		AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {
			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				treePanel.unmask();
			}

			@Override
			public void onSuccess(CompositeDTO updatedGraph) {
				AppEvent event = new AppEvent(LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES);
				event.setData(IConstants.UPDATED_GRAPH, updatedGraph);
				Dispatcher.get().dispatch(event);
				treePanel.unmask();
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).removeTreeContent(childID, callback);
	}

	@Override
	protected void handleEvent(AppEvent event) {
		EventType t = event.getType();

		if (treePanel != null) {
			if (t == LessonsEvents.MOVEMENTS_EVENT) {
				CompositeDTO newParentComponentDTO = event.getData(IConstants.NEW_PARENT_NODE);
				List<CompositeDTO> childrenToMove = event.getData(IConstants.CHILDREN_NODES);
				modelMovements(newParentComponentDTO, childrenToMove);
			}

			if (t == LessonsEvents.REMOVAL_EVENT) {
				CompositeDTO childComponentDTO = event.getData(IConstants.CHILD_NODE);
				Boolean isLeafNode = event.getData(IConstants.IS_LEAF_NODE);
				modelRemoval(childComponentDTO.getId(), isLeafNode);
			}
			
			if (t == LessonsEvents.REMOVAL_ALL_CHILDREN_EVENT) {
				CompositeDTO parentComponentDTO = event.getData(IConstants.PARENT_NODE);
				Boolean isRootNode = event.getData(IConstants.IS_ROOT_NODE);
				modelAllChildrenRemoval(parentComponentDTO, isRootNode);
			}

			if (t == LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES) {
				CompositeDTO updatedGraph = event.getData(IConstants.UPDATED_GRAPH);
				treePanel.updateTreeModel(updatedGraph);
			}

			if (t == LessonsEvents.UPDATE_TREE_VIEW_AFTER_MANIPULATIONS_EVENT) {
				List<CompositeDTO> modifiedChildren = event.getData(IConstants.DIRTY_CHILDREN_NODES);
				if(!modifiedChildren.isEmpty())
					modelUpdates(modifiedChildren.get(0).getParent().getId(), modifiedChildren);
			}

			if (t == LessonsEvents.REFRESH_VIEW_EVENT) {
				treePanel.retrieveTreeModel();
			}
			
			if(t == LessonsEvents.REFRESH_CHILDREN_TO_MOVE_EVENT) {
				List<CompositeDTO> childrenToMove = event
				.getData(IConstants.CHILDREN_NODES);
				if(childrenToMove != null && !childrenToMove.isEmpty()) {
					treePanel.setChildren(childrenToMove);
					treePanel.setPreviousParentNode(childrenToMove.get(0).getParent());
				}
			}
			
			if(t == LessonsEvents.CREATE_LESSON_EVENT) {
				String name = event.getData(IConstants.NAME);
				String description = event.getData(IConstants.DESCRIPTION);
				treePanel.addNewLesson(name,description);
			}
		}
	}
}
