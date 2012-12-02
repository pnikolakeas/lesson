package com.education.lessons.dao.model.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.education.lessons.dao.model.core.BaseEntity;
import com.education.lessons.dao.model.general.Country;
import com.education.lessons.dao.model.general.Language;


@Entity
@Table(name = "Users")
public class User extends BaseEntity {

	private static final long serialVersionUID = 1L;

	public enum Gender {
		MALE, FEMALE
	}

	@Column(nullable = false)
	private String password;

	@Column(nullable = false)
	private String nickname;

	private String fullname;

	@Column(nullable = false)
	private String email;

	@Enumerated(EnumType.STRING)
	private Gender gender;

	@Temporal(TemporalType.DATE)
	private Date birthday;

	@ManyToOne(fetch = FetchType.LAZY)
	private Country country;

	@ManyToOne(fetch = FetchType.LAZY)
	private Language language;

	private String postcode;

	private String timezone;

	@OneToMany(mappedBy = "user")
	private List<OpenID> openIDs;
	
	private Boolean admin = Boolean.FALSE;

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getFullname() {
		return fullname;
	}

	public void setFullname(String fullname) {
		this.fullname = fullname;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Gender getGender() {
		return gender;
	}

	public void setGender(Gender gender) {
		this.gender = gender;
	}

	public Date getBirthday() {
		return birthday;
	}

	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	public Country getCountry() {
		return country;
	}

	public void setCountry(Country country) {
		this.country = country;
	}

	public Language getLanguage() {
		return language;
	}

	public void setLanguage(Language language) {
		this.language = language;
	}

	public String getPostcode() {
		return postcode;
	}

	public void setPostcode(String postcode) {
		this.postcode = postcode;
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}

	public List<OpenID> getOpenIDs() {
		if (openIDs == null) {
			openIDs = new ArrayList<OpenID>();
		}

		return openIDs;
	}

	public void setOpenIDs(List<OpenID> openIDs) {
		this.openIDs = openIDs;
	}

	public OpenID addOpenID(String address) {
		OpenID openID = new OpenID();
		openID.setAddress(address);
		getOpenIDs().add(openID);
		openID.setUser(this);
		return openID;
	}

	public Boolean getAdmin() {
		return admin;
	}

	public void setAdmin(Boolean admin) {
		this.admin = admin;
	}
}
