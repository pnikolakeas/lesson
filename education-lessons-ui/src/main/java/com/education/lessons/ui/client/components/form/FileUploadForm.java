package com.education.lessons.ui.client.components.form;

import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.ui.client.utils.Utils;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Encoding;
import com.extjs.gxt.ui.client.widget.form.FormPanel.Method;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

public class FileUploadForm extends LayoutContainer {

	private VerticalPanel vp;
	private FormData formData;

	private TextField<String> keyword;
	private FileUploadField file;

	private CompositeDTO assosiatedModel;

	public FileUploadForm() {
		setTitle("Upload File Form");
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

		final FormPanel panel = new FormPanel();
		panel.setHeading(" Provide Upload Details ");
		panel.setFrame(true);
		panel.setAction(Utils.getContextPath() + "/LessonsUI/data/UploadServlet");
		panel.setEncoding(Encoding.MULTIPART);
		panel.setMethod(Method.POST);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		panel.setWidth(350);

		keyword = new TextField<String>();
		keyword.setFieldLabel("Keyword");
		panel.add(keyword, formData);

		file = new FileUploadField();
		file.setAllowBlank(false);
		file.setName("uploadedfile");
		file.setFieldLabel("File");
		panel.add(file, formData);

		SelectionListener<ButtonEvent> l = new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Button source = null;
				if (ce.getButton() != null) {
					source = ce.getButton();
					String id = source.getId();
					if (IConstants.CANCEL.equals(id))
						clearFormAndcloseUploadDocumentPopup();
					if (IConstants.UPLOAD.equals(id)) {
						panel.submit();
					}
				}
			}
		};

		Button s = new Button(IConstants.UPLOAD, l);
		s.setId(IConstants.UPLOAD);
		panel.addButton(s);
		Button c = new Button(IConstants.CANCEL, l);
		c.setId(IConstants.CANCEL);
		panel.addButton(c);

		panel.setButtonAlign(HorizontalAlignment.CENTER);

		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(s);

		panel.addListener(Events.Submit, new Listener<FormEvent>() {
			public void handleEvent(FormEvent fe) {
				if (Utils.notEmptyAndNullString(fe.getResultHtml())) {
					String uploadedFolderPath = fe.getResultHtml();
					fireEventForModelUpdate(keyword.getValue(), uploadedFolderPath);
					clearFormComponents();
					closeUploadPopup();
				}
			}
		});

		vp.add(panel);
	}

	private void clearFormAndcloseUploadDocumentPopup() {
		clearFormComponents();
		closeUploadPopup();
	}

	private void clearFormComponents() {
		keyword.clear();
		file.clear();
	}

	private void closeUploadPopup() {
		((Popup) getParent()).hide();
	}

	private void fireEventForModelUpdate(String keyword, String uploadedFolderPath) {
		AppEvent event = new AppEvent(LessonsEvents.FILE_ASSOSIATION_EVENT);
		event.setData(IConstants.SELECTED_NODE, getAssosiatedModel());
		event.setData(IConstants.FILE_PATH, uploadedFolderPath);
		event.setData(IConstants.KEYWORD, keyword);
		Dispatcher.get().dispatch(event);
	}

	public CompositeDTO getAssosiatedModel() {
		return assosiatedModel;
	}

	public void setAssosiatedModel(CompositeDTO assosiatedModel) {
		this.assosiatedModel = assosiatedModel;
	}
}
