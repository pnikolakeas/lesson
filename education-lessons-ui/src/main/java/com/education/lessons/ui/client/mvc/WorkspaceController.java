package com.education.lessons.ui.client.mvc;

import static com.education.lessons.ui.client.events.LessonsEvents.DOWNLOAD_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.FILE_ASSOSIATION_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.INIT;
import static com.education.lessons.ui.client.events.LessonsEvents.SHOW_DETAILS_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.UPDATE_GRID_VIEW_AFTER_All_CHILDREN_REMOVALS_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.UPDATE_GRID_VIEW_EVENT_AFTER_UPDATES;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

public class WorkspaceController extends Controller {

	private WorkspaceView workspaceView;

	public WorkspaceController() {
		workspaceView = new WorkspaceView(this);
		registerEventTypes(INIT,
				SHOW_DETAILS_EVENT,
				UPDATE_GRID_VIEW_AFTER_All_CHILDREN_REMOVALS_EVENT,
				UPDATE_GRID_VIEW_EVENT_AFTER_UPDATES, 
				DOWNLOAD_EVENT,
				FILE_ASSOSIATION_EVENT);
	}

	@Override
	public void handleEvent(AppEvent event) {
		EventType t = event.getType();
		if (t == INIT || t == SHOW_DETAILS_EVENT
				|| t == UPDATE_GRID_VIEW_AFTER_All_CHILDREN_REMOVALS_EVENT
				|| t == UPDATE_GRID_VIEW_EVENT_AFTER_UPDATES
				|| t == DOWNLOAD_EVENT
				|| t == FILE_ASSOSIATION_EVENT)
			forwardToView(workspaceView, event);
	}
}
