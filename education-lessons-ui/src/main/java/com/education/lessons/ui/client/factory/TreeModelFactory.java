package com.education.lessons.ui.client.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import com.education.lessons.ui.client.model.LessonsBaseTreeModel;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.google.gwt.core.client.GWT;

public class TreeModelFactory {

	protected static TreeModelFactory instance;

	public static TreeModelFactory get() {
		if (instance == null) {
			if (GWT.isClient()) {
				instance = GWT.create(TreeModelFactory.class);
			}
		}
		return instance;
	}

	/**
	 * Creates a new base tree model instance.
	 * 
	 * @param bean
	 * @return the new model
	 */
	public LessonsBaseTreeModel createModel(CompositeDTO bean) {
		LessonsBaseTreeModel root = new LessonsBaseTreeModel();
		createInnerStructure(root, bean);
		return root;
	}

	/**
	 * Responsible for recursively creation of TreeModel
	 * 
	 * @param parent
	 * @param bean
	 */
	private void createInnerStructure(LessonsBaseTreeModel parent,
			CompositeDTO bean) {
		if (bean != null) {
			parent.setBean(bean);
			List<CompositeDTO> nestedList = bean.getChildren();
			if (nestedList != null && !nestedList.isEmpty()) {
				for (CompositeDTO componentDTO : nestedList) {
					LessonsBaseTreeModel child = new LessonsBaseTreeModel();
					parent.add(child);
					createInnerStructure(child, componentDTO);
				}
			}
		}
	}

	/**
	 * Creates a list new base tree model instances.
	 * 
	 * @param beans
	 *            the list of beans
	 * @return the list of models
	 */
	public List<LessonsBaseTreeModel> createModel(Collection<CompositeDTO> beans) {
		List<LessonsBaseTreeModel> models = new ArrayList<LessonsBaseTreeModel>();
		for (CompositeDTO obj : beans) {
			models.add(createModel(obj));
		}
		return models;
	}
}
