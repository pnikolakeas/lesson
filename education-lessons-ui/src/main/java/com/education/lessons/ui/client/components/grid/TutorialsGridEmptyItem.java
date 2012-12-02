package com.education.lessons.ui.client.components.grid;

import java.util.List;

import com.education.lessons.ui.client.components.extended.TreePanelDropTargetExtended;
import com.education.lessons.ui.client.permission.PermissionLogic;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;

public class TutorialsGridEmptyItem extends BasicGridItem {

	public TutorialsGridEmptyItem(String text, TreePanelDropTargetExtended dropTarget) {
		super(text);
		if(PermissionLogic.dragDropPermission()) initTreePanelDropTarget(dropTarget);
	}

	private void initTreePanelDropTarget(TreePanelDropTargetExtended dropTarget) {
		if(target == null) {
			target = dropTarget;
		}
		initDropRestrictions();
	}

	@Override
	protected void initDropRestrictions() {
		target.getDropRestrictions().clear();
		target.addDropRestriction(ComponentTypeEnum.TUTORIAL, ComponentTypeEnum.TUTORIAL_CONTENT);
	}

	protected List<ColumnConfig> initColumnConfigs() {

		TextField<String> text = new TextField<String>();
		text.setAllowBlank(false);

		ColumnConfig column = new ColumnConfig("title", "TITLE", 276);
		column.setEditor(new CellEditor(text));
		configs.add(column);
		
		text = new TextField<String>();
		text.setAllowBlank(true);

		column = new ColumnConfig("description", "DESCRIPTION", 221);
		column.setEditor(new CellEditor(text));
		configs.add(column);

		text = new TextField<String>();
		text.setAllowBlank(true);

		column = new ColumnConfig("filePath", "FILE", 276);
		column.setEditor(new CellEditor(text));
		configs.add(column);

		return configs;
	}
}
