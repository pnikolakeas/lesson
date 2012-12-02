package com.education.lessons.dao.service.composite;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.education.lessons.dao.exception.DataException;
import com.education.lessons.dao.model.composite.Composite;
import com.education.lessons.dao.model.composite.CompositeTypeEnum;

@Service
@Transactional
public class CompositeService {

	protected final Logger logger = LoggerFactory.getLogger(getClass());

	@Autowired protected CompositeDao dao;

	public CompositeDao getDao() {
		return dao;
	}
	
	public Composite updateComposites(Integer parentID, List<Composite> entities) throws DataException {
		if(parentID == null || entities == null || entities.isEmpty()) throw new DataException();
		Composite parent = getComposite(parentID);
		for(Composite entity : entities) {
			if(entity == null || entity.getId() == null) throw new DataException();
			entity.setParent(parent);
			this.getDao().update(entity);
		}
		return getRoot();
	}
	
	public Composite updateComposite(Composite entity) throws DataException {
		if(entity == null || entity.getId() == null) throw new DataException();
		this.getDao().update(entity);
		return getRoot();
	}

	public Composite deleteComposite(Integer ID) throws DataException {
		if(ID == null) throw new DataException();
		this.getDao().delete(Composite.class, ID);
		return getRoot();
	}
	
	public Composite getComposite(Integer ID) throws DataException {
		if(ID == null) throw new DataException();
		return this.getDao().get(Composite.class, ID);
	}
	
	public Composite getCompositeWithChildren(Integer ID) throws DataException {
		if(ID == null) throw new DataException();
		Composite composite = getDao().get(Composite.class, ID);
		initChildren(composite);
		return composite;
	}

	public Composite createAssignment(Integer parentID) {
		Composite parent = this.getDao().get(Composite.class, parentID);

		Composite assignmentsContent = new Composite();
		assignmentsContent.setType(CompositeTypeEnum.ASSIGNMENT_CONTENT);
		assignmentsContent.setTitle(CompositeTypeEnum.ASSIGNMENT_CONTENT.getValue());
		assignmentsContent.setTerminal(true);

		parent.add(assignmentsContent);

		this.getDao().insert(assignmentsContent);
		return assignmentsContent;
	}

	public Composite createPresentation(Integer parentID) {
		Composite parent = this.getDao().get(Composite.class, parentID);

		Composite presentationsContent = new Composite();
		presentationsContent.setType(CompositeTypeEnum.PRESENTATION_CONTENT);
		presentationsContent.setTitle(CompositeTypeEnum.PRESENTATION_CONTENT.getValue());
		presentationsContent.setTerminal(true);

		parent.add(presentationsContent);

		this.getDao().insert(presentationsContent);

		return presentationsContent;
	}

	public Composite createLink(Integer parentID) {
		Composite parent = this.getDao().get(Composite.class, parentID);

		Composite linksContent = new Composite();
		linksContent.setType(CompositeTypeEnum.LINK_CONTENT);
		linksContent.setTitle(CompositeTypeEnum.LINK_CONTENT.getValue());
		linksContent.setTerminal(true);

		parent.add(linksContent);

		this.getDao().insert(linksContent);
		return linksContent;
	}

	public Composite createTutorial(Integer parentID) {
		Composite parent = this.getDao().get(Composite.class, parentID);

		Composite tutorialsContent = new Composite();
		tutorialsContent.setType(CompositeTypeEnum.TUTORIAL_CONTENT);
		tutorialsContent.setTitle(CompositeTypeEnum.TUTORIAL_CONTENT.getValue());
		tutorialsContent.setTerminal(true);

		parent.add(tutorialsContent);

		this.getDao().insert(tutorialsContent);

		return tutorialsContent;
	}
	
	public Composite createGrade(Integer parentID) {
		Composite parent = this.getDao().get(Composite.class, parentID);

		Composite gradesContent = new Composite();
		gradesContent.setType(CompositeTypeEnum.GRADE_CONTENT);
		gradesContent.setTitle(CompositeTypeEnum.GRADE_CONTENT.getValue());
		gradesContent.setTerminal(true);

		parent.add(gradesContent);

		this.getDao().insert(gradesContent);

		return gradesContent;
	}

	public Composite createLesson(String name, String description) {
		Composite root = this.getDao().getRoot();

		Composite lesson = root.create(CompositeTypeEnum.LESSON);
		if(name != null && !"".equals(name))
			lesson.setTitle(name);
		lesson.setDescription(description);
		this.getDao().insert(lesson);

		Composite tutorials = lesson.create(CompositeTypeEnum.TUTORIAL);
		this.getDao().insert(tutorials);

		Composite links = lesson.create(CompositeTypeEnum.LINK);
		this.getDao().insert(links);

		Composite presentations = lesson.create(CompositeTypeEnum.PRESENTATION);
		this.getDao().insert(presentations);

		Composite assignments = lesson.create(CompositeTypeEnum.ASSIGNMENT);
		this.getDao().insert(assignments);
		
		Composite grades = lesson.create(CompositeTypeEnum.GRADE);
		this.getDao().insert(grades);

		return lesson;
	}

	public Composite getRoot() {
		logger.debug("LOADING ROOT");
		Composite root = getDao().getRoot();
		initChildren(root);
		return root;
	}

	private void initChildren(Composite parent) {
		logger.debug("INITIALIZING CHILDREN FOR " + parent);
		if(parent != null && !parent.getChildren().isEmpty()) {
			for (Composite child : parent.getChildren()) {
				initChildren(child);
			}
		}
	}

	public void removeTreeContent(Integer childID) throws DataException {
		if (childID == null) throw new DataException();
		this.getDao().delete(Composite.class, childID);
	}

	public void removeAllChildrenTreeContent(Integer parentID) throws DataException {
		if (parentID == null) throw new DataException();
		Composite parent = this.getDao().get(Composite.class, parentID);
		if (parent == null) throw new DataException();
		List<Composite> children = parent.getChildren();
		for (Composite child : children) {
			this.getDao().delete(Composite.class, child.getId());
		}
	}

	public Composite addTreeContent(Integer parentID) throws DataException {
		if (parentID == null) throw new DataException();
		Composite parent = getComposite(parentID);
		if (parent == null || parent.getType() == null) throw new DataException();
		switch (parent.getType()) {
			case PRESENTATION: {
				createPresentation(parent.getId());
				break;
			}
			case ASSIGNMENT: {
				createAssignment(parent.getId());
				break;	
			}
			case LINK: {
				createLink(parent.getId());
				break;	
			}
			case TUTORIAL: {
				createTutorial(parent.getId());
				break;	
			}
			case GRADE: {
				createGrade(parent.getId());
				break;	
			}
			default:
				throw new DataException("Unrecognized type");
		}
		return getRoot();
	}

	public Composite moveTreeContent(Integer newParentID, List<Integer> childrenToMove) throws DataException {
		if (newParentID == null || childrenToMove == null || childrenToMove.isEmpty()) throw new DataException();
		Composite newParent = getComposite(newParentID);
		if(newParent == null) throw new DataException();
		for (Integer childID : childrenToMove) {
			Composite childToMove = getComposite(childID);
			if (childToMove == null) throw new DataException();
			childToMove.setParent(newParent);
			this.getDao().update(childToMove);
		}
		return getRoot();
	}

	public String displayNode(Integer compositeID) throws DataException  {
		if (compositeID == null) throw new DataException();
		Composite composite = getComposite(compositeID);
		if(composite == null) throw new DataException();
		return composite.getFilePath();
	}
	
	public Composite assosiateUploadedFile(Integer compositeID, String keyword, String filepath) throws DataException {
		if (compositeID == null || filepath == null || "".equals(filepath)) throw new DataException();
		Composite composite = this.getComposite(compositeID);
		if(composite == null) throw new DataException();
		composite.setKeyword(keyword);
		composite.setFilePath(filepath);
		this.getDao().update(composite);
		return getRoot();
	}
}
