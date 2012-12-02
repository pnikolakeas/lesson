package com.education.lessons.viewmodel.core;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.education.lessons.viewmodel.type.ComponentTypeEnum;

public class CompositeDTO extends BaseDTO {

	private static final long serialVersionUID = 1L;

	protected String title;
	protected String description;

	protected ComponentTypeEnum type;
	protected CompositeDTO parent;
	protected List<CompositeDTO> children = new ArrayList<CompositeDTO>();

	protected String keyword;
	protected String filePath;
	protected boolean terminal;
	protected String presentedBy;
	protected String assignedTo;
	protected String url;

	public boolean isRoot() {
		return getParent() == null;
	}

	public boolean isFolder() {
		return !isLeaf();
	}

	public boolean isLeaf() {
		return  ComponentTypeEnum.ASSIGNMENT_CONTENT.equals(type) 
				|| ComponentTypeEnum.PRESENTATION_CONTENT.equals(type)
				|| ComponentTypeEnum.LINK_CONTENT.equals(type)
				|| ComponentTypeEnum.TUTORIAL_CONTENT.equals(type);
	}

	public String toString() {
		return this != null ? this.getTitle() : null;
	}

	public CompositeDTO getRoot() {
		if (isRoot()) return this;
		return getParent().getRoot();
	}
	
	public boolean isItemsEqual(CompositeDTO o1, CompositeDTO o2) {
		if (o1 == null)
			return o2 == null;
		else
			return o1.equals(o2);
	}

	public CompositeDTO findFolder(CompositeDTO item) {
		if (isItemsEqual(item, this))
			return this;

		for (Iterator<CompositeDTO> i = getChildren().iterator(); i.hasNext();) {
			CompositeDTO entry = (CompositeDTO) i.next();
			if (entry.isFolder()) {
				CompositeDTO folder = (CompositeDTO) entry;
				if (isItemsEqual(folder, item)) return folder;
				CompositeDTO found = folder.findFolder(item);
				if (found != null) return found;
			}
		}
		// Case Not found
		return null;
	}

	// JUST FOR DEBUGGING PURPOSES
	public void print(int indent) {

		for (int i = 0; i < indent; i++)
			System.out.print(" ");

		System.out.println(this.getType() + " : " + this.getTitle());

		if (!this.isLeaf()) {
			for (Iterator<CompositeDTO> i = children.iterator(); i.hasNext();) {
				CompositeDTO ComponentDTO = i.next();
				ComponentDTO.print(indent + 2);
			}
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

	public String getFilePath() {
		return filePath;
	}

	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	public ComponentTypeEnum getType() {
		return type;
	}

	public void setType(ComponentTypeEnum type) {
		this.type = type;
	}

	public CompositeDTO getParent() {
		return parent;
	}

	public void setParent(CompositeDTO parent) {
		this.parent = parent;
	}

	public List<CompositeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<CompositeDTO> children) {
		this.children = children;
	}

	public boolean isTerminal() {
		return terminal;
	}

	public void setTerminal(boolean terminal) {
		this.terminal = terminal;
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
