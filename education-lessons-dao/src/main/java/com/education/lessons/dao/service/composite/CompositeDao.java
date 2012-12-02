package com.education.lessons.dao.service.composite;

import java.util.List;

import javax.persistence.Query;

import org.springframework.stereotype.Repository;

import com.education.lessons.dao.model.composite.Composite;
import com.education.lessons.dao.service.core.BaseDao;

@Repository
public class CompositeDao extends BaseDao<Composite> {
	
	@SuppressWarnings("unchecked")
	public Composite getRoot() {
		StringBuilder sb = new StringBuilder();
		sb.append(" select c ");
		sb.append(" from Composite c ");
		sb.append(" where c.parent is null ");

		Query query = entityManager.createQuery(sb.toString());

		List<Composite> composites = query.getResultList();

		if (composites.size() > 0) {
			return composites.get(0);
		}

		return null;
	}
}
