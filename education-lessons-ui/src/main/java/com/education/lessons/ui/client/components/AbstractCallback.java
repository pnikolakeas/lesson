package com.education.lessons.ui.client.components;

import com.education.lessons.ui.client.LessonsUI;
import com.education.lessons.ui.client.components.box.LessonsMessageBox;
import com.education.lessons.ui.client.utils.IConstants;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.StatusCodeException;

public abstract class AbstractCallback<T> implements AsyncCallback<T> {
	
	@Override
	public void onFailure(Throwable caught) {
		caught.getStackTrace();

		if (caught instanceof StatusCodeException) {
			StatusCodeException sce = (StatusCodeException) caught;

			if (sce.getStatusCode() == Response.SC_FORBIDDEN) {
				LessonsUI.requireLogin();
				return;
			}
		}

		caught.printStackTrace();
		LessonsMessageBox.showErrorWindow(IConstants.RETRIEVE_MODEL_FAILED );
	}
}
