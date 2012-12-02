package com.education.lessons.viewmodel.node;

import com.education.lessons.viewmodel.core.CompositeDTO;

public class AssignmentDTO extends CompositeDTO {

	private static final long serialVersionUID = 1L;
	
	protected String assignedTo;

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
}
