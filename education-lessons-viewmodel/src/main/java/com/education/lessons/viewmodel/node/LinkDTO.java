package com.education.lessons.viewmodel.node;

import com.education.lessons.viewmodel.core.CompositeDTO;

public class LinkDTO extends CompositeDTO {

	private static final long serialVersionUID = 1L;
	
	protected String url;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
