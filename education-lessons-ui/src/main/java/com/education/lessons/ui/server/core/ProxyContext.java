package com.education.lessons.ui.server.core;

import com.education.lessons.dao.context.AuditContext;
import com.education.lessons.dao.model.user.User;

public class ProxyContext implements AuditContext {
	
	@Override
	public User getUser() {
		return WebContextHolder.getCurrentContext().getUser();
	}
}
