package com.education.lessons.ui.client.components.form;

import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.utils.IConstants;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class InitLessonForm extends LayoutContainer {

	private VerticalPanel vp;
	private FormData formData;

	private TextField<String> lessonName;
	private TextArea description;

	public InitLessonForm() {
		setTitle("Init Lesson Form");
		initComponents();
	}

	protected void initComponents() {
		formData = new FormData("-20");
		vp = new VerticalPanel();
		vp.setSpacing(10);
		createForm();
		add(vp);
	}

	private void createForm() {
		setStyleAttribute("margin", "10px");
		FormPanel simple = new FormPanel();
		simple.setHeading(" Provide Lesson Details ");
		simple.setFrame(true);
		simple.setWidth(350);

		lessonName = new TextField<String>();
		lessonName.setFieldLabel("Name");
		lessonName.setAllowBlank(false);
		simple.add(lessonName, formData);

		description = new TextArea();
		description.setPreventScrollbars(true);
		description.setFieldLabel("Description");
		simple.add(description, formData);

		SelectionListener<ButtonEvent> l = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Button source = null;
				if (ce.getButton() != null) {
					source = ce.getButton();
					String id = source.getId();
					if (IConstants.CANCEL.equals(id))
						clearFormAndcloseInitLessonPopup();
					if (IConstants.SUBMIT.equals(id))
						fireSubmitInitLessonEvent(lessonName.getValue(),
								description.getValue());
				}
			}
		};

		Button s = new Button(IConstants.SUBMIT, l);
		s.setId(IConstants.SUBMIT);
		simple.addButton(s);
		Button c = new Button("Cancel", l);
		c.setId(IConstants.CANCEL);
		simple.addButton(c);

		simple.setButtonAlign(HorizontalAlignment.CENTER);

		FormButtonBinding binding = new FormButtonBinding(simple);
		binding.addButton(s);
		vp.add(simple);
	}

	private void fireSubmitInitLessonEvent(String name, String description) {
		AppEvent event = new AppEvent(LessonsEvents.CREATE_LESSON_EVENT);
		event.setData(IConstants.NAME, name);
		event.setData(IConstants.DESCRIPTION, description);
		Dispatcher.get().dispatch(event);
		closeCreationPopup();
	}

	private void clearFormAndcloseInitLessonPopup() {
		lessonName.clear();
		description.clear();
		closeCreationPopup();
	}

	private void closeCreationPopup() {
		((Popup) getParent()).hide();
	}
}
