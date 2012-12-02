package com.education.lessons.dao.model.composite;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;
import javax.persistence.Table;

import com.education.lessons.dao.model.core.BaseEntity;

@Entity
@Inheritance
@Table(name = "lesson_composite")
public class Composite extends BaseEntity {

	private static final long serialVersionUID = 1L;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	protected CompositeTypeEnum type;

	@ManyToOne(fetch = FetchType.LAZY)
	protected Composite parent;

	@OrderBy("id")
	@OneToMany(mappedBy = "parent", cascade = { CascadeType.REMOVE })
	protected List<Composite> children;

	@Column(nullable = false)
	protected String title;
	protected String description;
	protected boolean terminal;
	protected String keyword;
	protected String filePath;
	protected String presentedBy;
	protected String assignedTo;
	protected String url;

	/**
	 * Helper method.
	 */
	public boolean isRoot() {
		return getParent() == null;
	}

	/**
	 * Helper method.
	 */
	public boolean hasChildren() {
		return children != null && !children.isEmpty();
	}

	/**
	 * Helper method.
	 */
	public Composite create(CompositeTypeEnum type) {
		Composite composite = new Composite();
		composite.setType(type);
		composite.setTitle(type.getValue());
		getChildren().add(composite);
		composite.setParent(this);
		return composite;
	}

	/**
	 * Helper method.
	 */
	public Composite add(Composite composite) {
		composite.setParent(this);
		getChildren().add(composite);
		return composite;
	}

	/**
	 * Helper method.
	 */
	public void add(List<Composite> composites) {
		for(Composite child : composites) {
			child.setParent(this);
		}
		getChildren().addAll(composites);
	}

	/**
	 * Helper method.
	 */
	public void remove(Composite component) {
		component.setParent(null);
		getChildren().remove(component);
	}

	/**
	 * Helper method.
	 */
	public void remove(List<Composite> composites) {
		for(Composite child : composites) {
			child.setParent(null);
			getChildren().remove(child);
		}
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public boolean isTerminal() {
		return terminal;
	}

	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
	}

	public CompositeTypeEnum getType() {
		return type;
	}

	public void setType(CompositeTypeEnum type) {
		this.type = type;
	}

	public Composite getParent() {
		return parent;
	}

	public void setParent(Composite parent) {
		this.parent = parent;
	}

	public List<Composite> getChildren() {
		if(children == null) {
			children = new ArrayList<Composite>();
		}
		return children;
	}

	public void setChildren(List<Composite> children) {
		this.children = children;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getPresentedBy() {
		return presentedBy;
	}

	public void setPresentedBy(String presentedBy) {
		this.presentedBy = presentedBy;
	}

	public String getAssignedTo() {
		return assignedTo;
	}

	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
