package com.education.lessons.dao.context;

import com.education.lessons.dao.model.user.User;

public interface AuditContext {

	/**
	 * The application user.
	 */
	User getUser();

}
