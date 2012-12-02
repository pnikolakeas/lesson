package com.education.lessons.ui.client.permission;

import com.education.lessons.ui.client.utils.Utils;

public class PermissionLogic {
	
	public static boolean expandAllPermission(int lessonsCount) {
		return lessonsCount > 0;
	}
	
	public static boolean collapseAllPermission(int lessonsCount) {
		return lessonsCount > 0;
	}

	public static boolean addLessonPermission() {
		return Utils.isAdminUser();
	}

	public static boolean showLessonsPermission(int lessonsCount) {
		return lessonsCount > 0;
	}
	
	public static boolean deleteLessonsPermission(int lessonsCount) {
		return Utils.isAdminUser() && lessonsCount > 0;
	}

	public static boolean insertPermission() {
		return Utils.isAdminUser();
	}

	public static boolean removePermission() {
		return Utils.isAdminUser();
	}

	public static boolean displayPermission() {
		return true;
	}

	public static boolean removeAllPermission() {
		return Utils.isAdminUser();
	}
	
	public static boolean dragDropPermission() {
		return Utils.isAdminUser();
	}
}
