package com.education.lessons.ui.client.factory;

import com.education.lessons.ui.client.components.extended.TreePanelDropTargetExtended;
import com.education.lessons.ui.client.components.grid.AssignmentsGridItem;
import com.education.lessons.ui.client.components.grid.BasicGridItem;
import com.education.lessons.ui.client.components.grid.GradesGridItem;
import com.education.lessons.ui.client.components.grid.LinksGridItem;
import com.education.lessons.ui.client.components.grid.PresentationsGridItem;
import com.education.lessons.ui.client.components.grid.TutorialsGridEmptyItem;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.google.gwt.core.client.GWT;

public final class GridItemFactory {
	
	protected static GridItemFactory instance;

	public static GridItemFactory get() {
		if (instance == null) {
			if (GWT.isClient()) {
				instance = GWT.create(GridItemFactory.class);
			}
		}
		return instance;
	}

	public BasicGridItem createGridItemByType(ComponentTypeEnum type, TreePanelDropTargetExtended dropTarget, CompositeDTO selectedFolder) {
		if (type == null || selectedFolder == null) return null;
		
		if(selectedFolder.isRoot()) {
			return createLessonsGridItem(IConstants.LESSONS);
		}

		String name = selectedFolder.getTitle();
		if (type.equals(ComponentTypeEnum.LESSON)) {
			return createLessonsGridItem(name);
		}
		else if (type.equals(ComponentTypeEnum.TUTORIAL)) {
			return createTutorialsGridItem(name, dropTarget);
		}
		else if (type.equals(ComponentTypeEnum.LINK)) {
			return createLinksGridItem(name, dropTarget);
		}
		else if (type.equals(ComponentTypeEnum.PRESENTATION)) {
			return createPresentationsGridItem(name, dropTarget);
		}
		else if (type.equals(ComponentTypeEnum.ASSIGNMENT)) {
			return createAssignmentsGridItem(name, dropTarget);
		}
		else if (type.equals(ComponentTypeEnum.GRADE)) {
			return createGradesGridItem(name, dropTarget);
		}
		return null;
	}

	private BasicGridItem createGradesGridItem(String name, TreePanelDropTargetExtended dropTarget) {
		return new GradesGridItem(name,dropTarget);
	}

	private LinksGridItem createLinksGridItem(String name, TreePanelDropTargetExtended dropTarget) {
		return new LinksGridItem(name,dropTarget);
	}

	private TutorialsGridEmptyItem createTutorialsGridItem(String name, TreePanelDropTargetExtended dropTarget) {
		return new TutorialsGridEmptyItem(name,dropTarget);
	}

	private AssignmentsGridItem createAssignmentsGridItem(String name, TreePanelDropTargetExtended dropTarget) {
		return new AssignmentsGridItem(name,dropTarget);
	}

	private PresentationsGridItem createPresentationsGridItem(String name, TreePanelDropTargetExtended dropTarget) {
		return new PresentationsGridItem(name,dropTarget);
	}
	
	private BasicGridItem createLessonsGridItem(String name) {
		return new BasicGridItem(name);
	}
}
