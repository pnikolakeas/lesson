/*
 * Copyright (C) 2010 Dimitrios Menounos
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.education.lessons.ui.client.login;

import com.education.lessons.ui.client.login.utils.UIHelper;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class OpenIDPanel extends Composite {

	interface Binder extends UiBinder<Widget, OpenIDPanel> {
	}

	private static final Binder binder = GWT.create(Binder.class);

	@UiField
	Button googleButton;

	@UiField
	Button yahooButton;

	@UiField
	Button openidButton;

	@UiField
	TextBox urlField;

	@UiField
	Button submitButton;

	@UiField
	FlowPanel formPanel;

	@UiField
	FlowPanel eastPanel;

	public OpenIDPanel() {
		initWidget(binder.createAndBindUi(this));

		collapsePanel();

		googleButton.addStyleName("googleButton");
		googleButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				openPopup("https://www.google.com/accounts/o8/id", 500, 500);
			}
		});

		yahooButton.addStyleName("yahooButton");
		yahooButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				openPopup("http://open.login.yahooapis.com/openid20/www.yahoo.com/xrds", 500, 500);
			}
		});

		openidButton.addStyleName("openidButton");
		openidButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				expandPanel();
			}
		});

		submitButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (urlField.getValue().trim().isEmpty()) {
					UIHelper.tip("You have to fill in your OpenID<br/>e.g. myname.myopenid.com", urlField);
					return;
				}

				urlField.setReadOnly(true);
				submitButton.setEnabled(false);

				openPopup(urlField.getValue(), 700, 500);
			}
		});
	}

	private void expandPanel() {
		formPanel.setVisible(true);
		eastPanel.setVisible(true);
		DockPanel dock = (DockPanel) getWidget();
		dock.setCellWidth(eastPanel, "300px");
	}

	private void collapsePanel() {
		formPanel.setVisible(false);
		eastPanel.setVisible(false);
		DockPanel dock = (DockPanel) getWidget();
		dock.setCellWidth(eastPanel, "0");
	}

	private void openPopup(String identifier, int width, int height) {
		String setupURL = GWT.getModuleBaseURL() + "../app/login/openid/setup";
		String url = setupURL + "?openid_identifier=" + encodeURIComponent(identifier);
		String features = "width=" + width + ",height=" + height;
		openPopup(url, features);
	}

	private native String encodeURIComponent(String str)
	/*-{
		return encodeURIComponent(str);
	}-*/;

	private native void openPopup(String url, String features)
	/*-{
		var name = "openid_popup";
		var popup = $wnd.open("", name, features);
		popup.document.write("Loading...");
		popup.document.close();
		$wnd.open(url, name);
	}-*/;
}
