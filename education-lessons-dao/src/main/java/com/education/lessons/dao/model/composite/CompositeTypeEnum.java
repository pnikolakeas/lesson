package com.education.lessons.dao.model.composite;

public enum CompositeTypeEnum {

	ROOT("Root"), 
	PRESENTATION("Presentation"), 
	PRESENTATION_CONTENT("Presentation_Content"), 
	TUTORIAL("Tutorial"), 
	TUTORIAL_CONTENT("Tutorial_Content"), 
	ASSIGNMENT("Assignment"), 
	ASSIGNMENT_CONTENT("Assignment_Content"), 
	LINK("Link"), 
	LINK_CONTENT("Link_Content"),
	GRADE("Grade"),
	GRADE_CONTENT("Grade_Content"), 
	LESSON("Lesson"), 
	LEAF("Leaf");

	private String value;

	private CompositeTypeEnum(String value) {
		this.value = value;
	}

	public static CompositeTypeEnum getEnumByStringValue(String value) {
		if (value == null || value.isEmpty()) {
			return null;
		}

		for (CompositeTypeEnum type : CompositeTypeEnum.values()) {
			if (value.equals(type.getValue())) {
				return type;
			}
		}

		return null;
	}

	public String getValue() {
		return value;
	}
}
