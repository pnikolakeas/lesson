package com.education.lessons.ui.client.factory;

import com.google.gwt.core.client.GWT;

public final class PojoFactory {
	
	protected static PojoFactory instance;

	public static PojoFactory get() {
		if (instance == null) {
			if (GWT.isClient()) {
				instance = GWT.create(PojoFactory.class);
			}
		}
		return instance;
	}
}
