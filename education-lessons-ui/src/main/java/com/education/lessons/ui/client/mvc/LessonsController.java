package com.education.lessons.ui.client.mvc;

import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;

import static com.education.lessons.ui.client.events.LessonsEvents.*;

public class LessonsController extends Controller {

	private LessonsView lessonsView;

	public LessonsController() {
		lessonsView = new LessonsView(this);
		registerEventTypes(INIT);
	}

	@Override
	public void handleEvent(AppEvent event) {
		EventType t = event.getType();
		if (t == INIT)
			forwardToView(lessonsView, event);
	}
}
