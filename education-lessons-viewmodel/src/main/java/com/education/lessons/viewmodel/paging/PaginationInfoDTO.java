package com.education.lessons.viewmodel.paging;

import java.io.Serializable;

public class PaginationInfoDTO implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Integer firstRow;
	private Integer maxResults;
	private String sortingColumn;
	private boolean sortAscending;

	public PaginationInfoDTO() {
		this(null, null);
	}

	public PaginationInfoDTO(Integer firstRow, Integer maxResults) {
		this(firstRow, maxResults, null, true);
	}

	public PaginationInfoDTO(Integer firstRow, Integer maxResults,
			String sortingColumn, boolean sortAscending) {
		this.firstRow = firstRow;
		this.maxResults = maxResults;
		this.sortingColumn = sortingColumn;
		this.sortAscending = sortAscending;
	}

	public Integer getFirstRow() {
		return firstRow;
	}

	public void setFirstRow(Integer firstRow) {
		this.firstRow = firstRow;
	}

	public Integer getMaxResults() {
		return maxResults;
	}

	public void setMaxResults(Integer maxResults) {
		this.maxResults = maxResults;
	}

	public String getSortingColumn() {
		return sortingColumn;
	}

	public void setSortingColumn(String sortingColumn) {
		this.sortingColumn = sortingColumn;
	}

	public boolean isSortAscending() {
		return sortAscending;
	}

	public void setSortAscending(boolean sortAscending) {
		this.sortAscending = sortAscending;
	}
}
