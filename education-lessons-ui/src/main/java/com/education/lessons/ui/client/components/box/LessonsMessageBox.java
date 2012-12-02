package com.education.lessons.ui.client.components.box;

import java.util.HashSet;
import java.util.Set;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Popup;
import com.extjs.gxt.ui.client.widget.Status;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;

public class LessonsMessageBox extends MessageBox {

	/**
	 * The CSS style name that provides the SUCCESS icon image.
	 */
	public static String SUCCESS = "ext-mb-done";

	static private com.extjs.gxt.ui.client.widget.Status status = null;
	static private com.extjs.gxt.ui.client.widget.Window statusWindow = null;

	static public Set<Object> messages = new HashSet<Object>();

	public static Popup showContentWindow(String title, String content) {
		return showContentWindow(title, new HTML(content), 100);
	}

	public static Popup showContentWindow(String title, Widget content,
			int width) {
		Popup popup = new Popup();
		popup.setTitle(title);
		popup.add(content);
		popup.setWidth(width);
		popup.show();
		popup.center();
		return popup;
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 * @param listener
	 */
	public static void showInfoWindow(String title, final String msg,
			Listener<MessageBoxEvent> listener) {
		if (msg == null)
			return;
		if (title == null)
			title = "Info"; // Default title

		if (!isAlreadyOpen(msg)) {
			MessageBox.info(title, msg, getMessageBoxListener(msg, listener))
					.setMaxWidth(300);
		}
	}

	/**
	 * Shows Info popup with the given title and message
	 * 
	 * @param title
	 * @param msg
	 */
	public static void showInfoWindow(String title, final String msg) {
		showInfoWindow(title, msg, null);
	}

	/**
	 * Shows Info popup with the given message and default title
	 * 
	 * @param msg
	 */
	public static void showInfoWindow(String msg) {
		showInfoWindow("Info", msg);
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 * @param listener
	 */
	public static void showQuestionWindow(String title, final String msg,
			Listener<MessageBoxEvent> listener) {
		if (msg == null)
			return;
		if (title == null)
			title = "Confirmation"; // Default title

		if (!isAlreadyOpen(msg)) {
			MessageBox
					.confirm(title, msg, getMessageBoxListener(msg, listener))
					.setMaxWidth(300);
		}
	}

	/**
	 * Shows Question popup with the given title and message
	 * 
	 * @param title
	 * @param msg
	 */
	public static void showQuestionWindow(String title, final String msg) {
		showQuestionWindow(title, msg, null);
	}

	/**
	 *Shows Question popup with the given message and default title
	 * 
	 * @param msg
	 */
	public static void showQuestionWindow(String msg) {
		showQuestionWindow("Confirmation", msg);
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 * @param listener
	 */
	public static void showAlertWindow(String title, String msg,
			Listener<MessageBoxEvent> listener) {
		if (msg == null)
			return;

		if (title == null)
			title = "Info"; // Default title

		if (!isAlreadyOpen(msg)) {
			MessageBox.alert(title, msg, getMessageBoxListener(msg, listener))
					.setMaxWidth(300);
		}
	}

	/**
	 * Shows Alert popup with the given title and message
	 * 
	 * @param title
	 * @param msg
	 */
	public static void showAlertWindow(String title, String msg) {
		showAlertWindow(title, msg, null);
	}

	/**
	 * Shows Alert popup with the given message and default title
	 * 
	 * @param msg
	 */
	public static void showAlertWindow(String msg) {
		showAlertWindow("Info", msg);
	}

	/**
	 * Shows Alert popup with the given message and default title
	 * 
	 * @param msg
	 * @param listener
	 */
	public static void showAlertWindow(String msg,
			Listener<MessageBoxEvent> listener) {
		showAlertWindow("Info", msg, listener);
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 * @param listener
	 */
	public static void showErrorWindow(String title, String msg,
			Listener<MessageBoxEvent> listener) {
		if (msg == null)
			return;

		if (title == null)
			title = "Error";

		if (!isAlreadyOpen(msg)) {
			LessonsMessageBox.error(title, msg,
					getMessageBoxListener(msg, listener)).setMaxWidth(300);
		}
	}

	/**
	 * Shows Error popup with the given title and message
	 * 
	 * @param title
	 * @param msg
	 */
	public static void showErrorWindow(String title, String msg) {
		showErrorWindow(title, msg, null);
	}

	/**
	 * Shows Error popup with the given message and default title
	 * 
	 * @param msg
	 */
	public static void showErrorWindow(String msg) {
		showErrorWindow("Error", msg);
	}

	/**
	 * 
	 * @param title
	 * @param msg
	 * @param listener
	 */
	public static void showSuccessWindow(String title, String msg,
			Listener<MessageBoxEvent> listener) {
		if (msg == null)
			return;

		if (title == null)
			title = "Success";

		if (!isAlreadyOpen(msg)) {
			LessonsMessageBox.success(title, msg,
					getMessageBoxListener(msg, listener)).setMaxWidth(300);
		}
	}

	/**
	 * Shows Success popup with the given title and message
	 * 
	 * @param title
	 * @param msg
	 */
	public static void showSuccessWindow(String title, String msg) {
		showSuccessWindow(title, msg, null);
	}

	/**
	 * Shows Success popup with the given message and default title
	 * 
	 * @param msg
	 */
	public static void showSuccessWindow(String msg) {
		showSuccessWindow("Success", msg);
	}

	public static void showWaitWindow(String msg) {
		if (statusWindow == null) {
			initStatusWindow();
		}

		status.setBusy(msg.trim());
		statusWindow.show();
	}

	public static void hideWaitWindow() {
		if (status != null) {
			status.clearStatus("");
			statusWindow.hide();
		}
	}

	private static void initStatusWindow() {
		statusWindow = new com.extjs.gxt.ui.client.widget.Window();
		statusWindow.setModal(true);
		statusWindow.setHeading("Processing");
		statusWindow.setTitle("Processing");
		statusWindow.setClosable(false);
		statusWindow.setHeaderVisible(true);
		statusWindow.setLayout(new CenterLayout());

		status = new Status();
		// status.setAutoWidth(true);

		statusWindow.setBodyBorder(false);
		statusWindow.add(status);
		statusWindow.setWidth(250);
	}

	public static MessageBox success(String title, String msg,
			Listener<MessageBoxEvent> callback) {
		MessageBox box = new MessageBox();
		box.setTitle(title);
		box.setMessage(msg);
		box.addCallback(callback);
		box.setButtons(OK);
		box.setIcon(SUCCESS);
		box.show();
		return box;
	}

	public static MessageBox error(String title, String msg,
			Listener<MessageBoxEvent> callback) {
		MessageBox box = new MessageBox();
		box.setTitle(title);
		box.setMessage(msg);
		box.addCallback(callback);
		box.setButtons(OK);
		box.setIcon(ERROR);
		box.show();
		return box;
	}

	public static boolean isAlreadyOpen(String msg) {
		boolean result = false;

		if (messages.contains(msg)) {
			result = true;
		} else {
			messages.add(msg);
		}
		return result;
	}

	public static Listener<MessageBoxEvent> getMessageBoxListener(
			final String msg, final Listener<MessageBoxEvent> listener) {
		return new Listener<MessageBoxEvent>() {
			@Override
			public void handleEvent(MessageBoxEvent be) {
				if ("ok".equalsIgnoreCase(be.getButtonClicked().getItemId())) { // or
					messages.remove(msg);
				}

				if (listener != null)
					listener.handleEvent(be);
			}
		};
	}

	public static void showWindow(String heading, String url) {
		Window w = new Window();        
		w.setHeading(heading);
		w.setModal(true);
		w.setSize(600, 400);
		w.setMaximizable(true);
		w.setUrl(url);
		w.show();
		w.center();
	}
}
