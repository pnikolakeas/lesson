package com.education.lessons.ui.server.core;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.education.lessons.dao.context.AuditContext;
import com.education.lessons.dao.model.user.User;

public class WebContext implements AuditContext {

	/** User session attribute. */
	public static final String CONTEXT_USER_ATTR = "contextUser";

	protected final HttpServletRequest request;
	protected final HttpServletResponse response;

	public WebContext(ServletRequest request, ServletResponse response) {
		this.request = (HttpServletRequest) request;
		this.response = (HttpServletResponse) response;
	}

	@Override
	public User getUser() {
		HttpSession session = request.getSession();
		return (User) session.getAttribute(CONTEXT_USER_ATTR);
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}
}
