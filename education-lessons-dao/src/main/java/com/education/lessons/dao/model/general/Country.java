package com.education.lessons.dao.model.general;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.education.lessons.dao.model.core.BaseEntity;

@Entity
@Table(name = "Countries")
public class Country extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Column(nullable = false)
	private String code;
	
	@Column(nullable = false)
	private String name;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
