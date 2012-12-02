package com.education.lessons.ui.client.services;

import java.util.List;

import com.education.lessons.viewmodel.core.CompositeDTO;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("data")
public interface DataProviderService extends RemoteService {

	public CompositeDTO retrieveTreeModel();
	public PagingLoadResult<CompositeDTO> retrieveGridModel(PagingLoadConfig loadConfig, Integer compositeID);
	public CompositeDTO addTreeContent(Integer parentID);
	public CompositeDTO removeTreeContent(Integer childID);
	public CompositeDTO removeAllChildrenTreeContent(Integer parentID);
	public CompositeDTO moveTreeContent(Integer newParentID, List<Integer> childrenToMove);
	public String displayNode(Integer compositeID);
	public CompositeDTO assosiateUploadedFile(Integer compositeID, String keyword, String filepath);
	public CompositeDTO addLesson(String name, String description);
	public CompositeDTO updateTreeContent(Integer parentID, List<CompositeDTO> children);
}
