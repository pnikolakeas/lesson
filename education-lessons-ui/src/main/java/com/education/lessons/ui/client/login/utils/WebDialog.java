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
package com.education.lessons.ui.client.login.utils;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.CssResource.ImportedWithPrefix;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;

/**
 * A closable dialog box.
 */
public class WebDialog extends DialogBox {

	private static final Resources res;
	private static final Style css;

	static {
		res = GWT.create(Resources.class);
		res.style().ensureInjected();
		css = res.style();
	}

	private boolean closable;
	private Button closeButton;

	public WebDialog() {
		this(false);
	}

	public WebDialog(boolean autoHide) {
		this(autoHide, true);
	}

	public WebDialog(boolean autoHide, boolean modal) {
		super(autoHide, modal);
		addStyleName("WebDialog");
	}

	public boolean isClosable() {
		return closable;
	}

	public void setClosable(boolean closable) {
		this.closable = closable;
	}

	public Button getCloseButton() {
		if (closeButton == null) {
			closeButton = new Button();
			closeButton.addStyleName("closeButton");
			closeButton.addStyleName(css.closeButton());
			closeButton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					hide();
				}
			});
		}

		return closeButton;
	}

	@Override
	protected void onLoad() {
		super.onLoad();

		if (isClosable()) {
			Element topCenterInner = getCellElement(0, 1);
			topCenterInner.insertFirst(getCloseButton().getElement());
			adopt(getCloseButton());
		}
	}

	@Override
	protected void onUnload() {
		if (isClosable()) {
			orphan(getCloseButton());
			Element topCenterInner = getCellElement(0, 1);
			topCenterInner.removeChild(getCloseButton().getElement());
		}

		super.onUnload();
	}

	public interface Resources extends ClientBundle {

		@Source("WebDialog.css")
		Style style();
	}

	@ImportedWithPrefix("mojo-WebDialog")
	public interface Style extends CssResource {

		String closeButton();
	}
}
