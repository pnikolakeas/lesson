package com.education.lessons.viewmodel.node;

import com.education.lessons.viewmodel.core.CompositeDTO;

public class PresentationDTO extends CompositeDTO {

	private static final long serialVersionUID = 1L;
	
	protected String presentedBy;

	public String getPresentedBy() {
		return presentedBy;
	}

	public void setPresentedBy(String presentedBy) {
		this.presentedBy = presentedBy;
	}
}
