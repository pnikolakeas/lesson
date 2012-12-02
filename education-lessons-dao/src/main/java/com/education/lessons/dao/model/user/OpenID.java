package com.education.lessons.dao.model.user;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.education.lessons.dao.model.core.BaseEntity;

@Entity
@Table(name = "OpenIDs")
public class OpenID extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@JoinColumn(nullable = false)
	@ManyToOne(fetch = FetchType.LAZY)
	private User user;
	
	@Column(nullable = false)
	private String address;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}
}
