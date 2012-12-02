package com.education.lessons.ui.server.core;

public class WebContextHolder {

	//------------Associates state with a thread---------//
	private static final ThreadLocal<WebContext> context = new ThreadLocal<WebContext>();

	public static WebContext getCurrentContext() {
		return context.get();
	}

	public static void setCurrentContext(WebContext webContext) {
		context.set(webContext);
	}

	public static void removeCurrentContext() {
		context.remove();
	}
}
