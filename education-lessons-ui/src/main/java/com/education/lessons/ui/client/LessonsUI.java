package com.education.lessons.ui.client;

import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.login.OpenIDPanel;
import com.education.lessons.ui.client.login.utils.UIHelper;
import com.education.lessons.ui.client.mvc.LessonsController;
import com.education.lessons.ui.client.mvc.NavigationController;
import com.education.lessons.ui.client.mvc.WorkspaceController;
import com.education.lessons.ui.client.utils.IConstants;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.GWT.UncaughtExceptionHandler;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.user.client.Window;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class LessonsUI implements EntryPoint {

	private Dispatcher dispatcher;

	public void onModuleLoad() {
		initJS();

//		if (Utils.isAdminUser()) {
//			UIHelper.alert("TEST", "USER IS LOGGED IN AND ADMIN");
//		}
//		else if (Utils.isSimpleUser()) {
//			UIHelper.alert("TEST", "USER IS LOGGED IN AND IS USER");
//		}

		handleUncaughtExceptions();

		dispatcher = Dispatcher.get();

		dispatcher.addController(new LessonsController());
		dispatcher.addController(new NavigationController());
		dispatcher.addController(new WorkspaceController());

		initializeApplication();

		GXT.hideLoadingPanel(IConstants.LOADING);
	}

	private void initializeApplication() {
		//IF you want to add login during the application initialization
		// you should handle this event from every view
		dispatcher.dispatch(LessonsEvents.INIT);
	}

	/**
	 * Setup callback functions.
	 */
	public static native void initJS()
	/*-{
		$wnd.session = {
			signIn: @com.education.lessons.ui.client.LessonsUI::signIn(),
			signUp: @com.education.lessons.ui.client.LessonsUI::signUp(Lcom/google/gwt/core/client/JavaScriptObject;)
		};
	}-*/;

	public static void requireLogin() {
		UIHelper.openDialog(new OpenIDPanel(), "Login");
	}

	public static void signIn() {
		UIHelper.alert("AFTER USER SIGN IN", "WINDOW REFERSH ...");
		Window.Location.reload();
	}

	public static void signUp(JavaScriptObject jso) {
		UIHelper.alert("BEFORE USER SIGN UP", "THIS IS NOT SUPPOSED TO HAPPEN !!!");
		UIHelper.closeDialog();
	}

	private void handleUncaughtExceptions() {
		if (!GWT.isScript()) {
			GWT.setUncaughtExceptionHandler(new UncaughtExceptionHandler() {
				public void onUncaughtException(Throwable e) {
					e.printStackTrace();
					MessageBox.alert("Unexpected Error", e.getMessage(), null);
				}
			});
		}
	}
}
