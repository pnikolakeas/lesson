package com.education.lessons.ui.client.mvc;

import static com.education.lessons.ui.client.events.LessonsEvents.CREATE_LESSON_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.INIT;
import static com.education.lessons.ui.client.events.LessonsEvents.MOVEMENTS_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.REFRESH_CHILDREN_TO_MOVE_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.REFRESH_VIEW_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.REMOVAL_ALL_CHILDREN_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.REMOVAL_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.UPDATE_TREE_VIEW_AFTER_ALL_CHILDREN_REMOVAL;
import static com.education.lessons.ui.client.events.LessonsEvents.UPDATE_TREE_VIEW_AFTER_MANIPULATIONS_EVENT;
import static com.education.lessons.ui.client.events.LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

public class NavigationController extends Controller {

	private NavigationView navigationView;

	public NavigationController() {
		navigationView = new NavigationView(this);
		registerEventTypes(INIT, MOVEMENTS_EVENT,
				REMOVAL_EVENT, REMOVAL_ALL_CHILDREN_EVENT,
				UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES,
				UPDATE_TREE_VIEW_AFTER_ALL_CHILDREN_REMOVAL,
				UPDATE_TREE_VIEW_AFTER_MANIPULATIONS_EVENT,
				REFRESH_CHILDREN_TO_MOVE_EVENT,
				REFRESH_VIEW_EVENT,
				CREATE_LESSON_EVENT);
	}

	@Override
	public void handleEvent(AppEvent event) {
		EventType t = event.getType();
		if (t == INIT
				|| t == MOVEMENTS_EVENT
				|| t == UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES
				|| t == UPDATE_TREE_VIEW_AFTER_ALL_CHILDREN_REMOVAL
				|| t == REMOVAL_EVENT
				|| t == REMOVAL_ALL_CHILDREN_EVENT
				|| t == UPDATE_TREE_VIEW_AFTER_MANIPULATIONS_EVENT
				|| t == REFRESH_CHILDREN_TO_MOVE_EVENT
				|| t == REFRESH_VIEW_EVENT
				|| t == CREATE_LESSON_EVENT)
			forwardToView(navigationView, event);
	}
}
