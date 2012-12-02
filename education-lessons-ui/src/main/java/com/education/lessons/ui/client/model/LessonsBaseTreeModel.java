package com.education.lessons.ui.client.model;

import java.util.Collection;
import java.util.Map;

import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

@SuppressWarnings("unchecked")
public class LessonsBaseTreeModel extends BaseTreeModel {

	private static final long serialVersionUID = 1L;

	transient protected BeanModel beanModel;
	
	/**
	 * Sets the bean.
	 * 
	 * @param bean the bean
	 */
	public void setBean(Object bean) {
		BeanModelFactory factory = BeanModelLookup.get().getFactory(bean.getClass());
		this.beanModel = factory.createModel(bean);
	}

	/**
	 * Returns the bean.
	 * 
	 * @return the bean
	 */
	public <X> X getBean() {
		return (X) beanModel.getBean();
	}

	@Override
	public <X> X get(String property) {
		return (X) beanModel.get(property);
	}

	@Override
	public <X> X get(String property, X valueWhenNull) {
		return (X) beanModel.get(property, valueWhenNull);
	}

	@Override
	public Map<String, Object> getProperties() {
		return beanModel.getProperties();
	}

	@Override
	public Collection<String> getPropertyNames() {
		return beanModel.getPropertyNames();
	}

	@Override
	public boolean isAllowNestedValues() {
		return beanModel.isAllowNestedValues();
	}

	@Override
	public void setAllowNestedValues(boolean allowNestedValues) {
		beanModel.setAllowNestedValues(allowNestedValues);
	}

	@Override
	public void setProperties(Map<String, Object> properties) {
		beanModel.setProperties(properties);
	}
}
