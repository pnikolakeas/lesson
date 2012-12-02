package com.education.lessons.dao.service.core;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;

import com.education.lessons.dao.model.core.BaseEntity;

@Repository
@SuppressWarnings({"rawtypes", "unchecked"})
public class BaseDao<T extends BaseEntity> {

	@PersistenceContext
	protected EntityManager entityManager;
	
	public void insert(T entity) {
		entityManager.persist(entity);
		entityManager.flush();
	}
	
	public void update(T entity) {
		entityManager.merge(entity);
		entityManager.flush();
	}
	
	public void delete(Class clazz, Integer id) {
		T entity = (T) entityManager.find(clazz, id);
		entityManager.remove(entity);
		entityManager.flush();
	}

	public T get(Class clazz, Integer ID) {
		return (T) entityManager.find(clazz, ID);
	}
}
