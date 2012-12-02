package com.education.lessons.viewmodel.core;

import java.io.Serializable;

public class BaseDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer id;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
}


