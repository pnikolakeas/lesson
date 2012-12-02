package com.education.lessons.ui.client.services;

import java.util.List;

import com.education.lessons.viewmodel.core.CompositeDTO;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * @author pnikolakeas
 */
public interface DataProviderServiceAsync {

	public void retrieveTreeModel(AsyncCallback<CompositeDTO> callback);
	public void retrieveGridModel(PagingLoadConfig loadConfig, Integer compositeID, AsyncCallback<PagingLoadResult<CompositeDTO>> callback);
	public void addTreeContent(Integer parentID, AsyncCallback<CompositeDTO> callback);
	public void removeTreeContent(Integer childID, AsyncCallback<CompositeDTO> callback);
	public void removeAllChildrenTreeContent(Integer parentID, AsyncCallback<CompositeDTO> callback);
	public void moveTreeContent(Integer newParentID, List<Integer> childrenToMove, AsyncCallback<CompositeDTO> callback);
	public void displayNode(Integer compositeID, AsyncCallback<String> callback);
	public void assosiateUploadedFile(Integer compositeID, String keyword, String filepath, AsyncCallback<CompositeDTO> callback);
	public void updateTreeContent(Integer parentID, List<CompositeDTO> childrenToUpdate, AsyncCallback<CompositeDTO> callback);
	public void addLesson(String name, String description, AsyncCallback<CompositeDTO> callback);
}
