package com.education.lessons.dao.service.login;

import java.util.List;

import javax.persistence.Query;

import com.education.lessons.dao.model.general.Country;
import com.education.lessons.dao.model.general.Language;
import com.education.lessons.dao.model.user.User;
import com.education.lessons.dao.service.core.BaseDao;

import org.springframework.stereotype.Repository;

@Repository
public class LoginDao extends BaseDao<User> {

	@SuppressWarnings("unchecked")
	public User findUserByNicknameOrEmail(String username) {
		StringBuilder sb = new StringBuilder();
		sb.append("select u ");
		sb.append("from User u ");
		sb.append("left join fetch u.country c ");
		sb.append("left join fetch u.language l ");
		sb.append("where u.nickname = :username or u.email = :username");

		Query query = entityManager.createQuery(sb.toString());
		query.setParameter("username", username);

		List<User> users = query.getResultList();

		if (users.size() > 0) {
			return users.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public User findUserByOpenID(String identifier) {
		StringBuilder sb = new StringBuilder();
		sb.append("select u ");
		sb.append("from OpenID o ");
		sb.append("join o.user u ");
		sb.append("left join fetch u.country c ");
		sb.append("left join fetch u.language l ");
		sb.append("where o.address = :identifier");

		Query query = entityManager.createQuery(sb.toString());
		query.setParameter("identifier", identifier);

		List<User> users = query.getResultList();

		if (users.size() > 0) {
			return users.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public Country findCountryByCode(String code) {
		StringBuilder sb = new StringBuilder();
		sb.append("select c ");
		sb.append("from Country c ");
		sb.append("where c.code = :code");

		Query query = entityManager.createQuery(sb.toString());
		query.setParameter("code", code);

		List<Country> countries = query.getResultList();

		if (countries.size() > 0) {
			return countries.get(0);
		}

		return null;
	}

	@SuppressWarnings("unchecked")
	public Language findLanguageByCode(String code) {
		StringBuilder sb = new StringBuilder();
		sb.append("select l ");
		sb.append("from Language l ");
		sb.append("where l.code = :code");

		Query query = entityManager.createQuery(sb.toString());
		query.setParameter("code", code);

		List<Language> languages = query.getResultList();

		if (languages.size() > 0) {
			return languages.get(0);
		}
		return null;
	}
}
