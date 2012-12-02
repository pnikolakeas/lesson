package com.education.lessons.ui.client.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.education.lessons.ui.client.components.AbstractCallback;
import com.education.lessons.ui.client.components.box.LessonsMessageBox;
import com.education.lessons.ui.client.components.extended.TreePanelDropTargetExtended;
import com.education.lessons.ui.client.components.grid.BasicGridItem;
import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.factory.GridItemFactory;
import com.education.lessons.ui.client.services.AsyncServiceFactory;
import com.education.lessons.ui.client.services.DataProviderServiceAsync;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.ui.client.utils.Utils;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.EventType;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.TabPanel;
import com.extjs.gxt.ui.client.widget.TabPanel.TabPosition;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Image;

public class WorkspaceView extends View {

	private ContentPanel centerPanel;
	private TabPanel tabPanel;
	private TabItem logoItem;
	private BasicGridItem detailsItem;

	Map<Integer, HashMap<String, BasicGridItem>> componentItemsMap = new HashMap<Integer, HashMap<String, BasicGridItem>>();

	public WorkspaceView(Controller controller) {
		super(controller);
	}

	private TabItem constructLogoTabItem() {
		TabItem logoItem = new TabItem(IConstants.START);
		logoItem.setLayout(new CenterLayout());
		Image img = new Image();
		img.setUrl("images/LessonsPlans.jpg");
		logoItem.add(img);
		return logoItem;
	}

	@Override
	protected void initialize() {
		centerPanel = (ContentPanel) Registry.get(IConstants.CENTER_PANEL);
		centerPanel.mask(IConstants.LOADING);
		centerPanel.setLayout(new FitLayout());
		centerPanel.add(createTabPanel());
		centerPanel.syncSize();
		centerPanel.unmask();
	}

	private TabPanel createTabPanel() {
		tabPanel = new TabPanel();
		tabPanel.setTabScroll(true);
		tabPanel.setBorderStyle(true);
		tabPanel.setBodyBorder(true);
		tabPanel.setTabPosition(TabPosition.TOP);
		logoItem = constructLogoTabItem();
		tabPanel.add(logoItem);
		tabPanel.setSelection(logoItem);
		return tabPanel;
	}

	private void constructDetailsItem(CompositeDTO selectedFolder, TreePanelDropTargetExtended dropTarget) {
		if (componentItemsMap != null && selectedFolder != null) {
			HashMap<String, BasicGridItem> itemsMap = componentItemsMap.get(selectedFolder.getId());
			if (itemsMap == null) itemsMap = new HashMap<String, BasicGridItem>();
			String type = (Utils.resolveComponentDTONameOfType(selectedFolder));
			if (Utils.notEmptyAndNullString(type)) {
				detailsItem = itemsMap.get(type);
				if (detailsItem == null) {
					detailsItem = GridItemFactory.get().createGridItemByType(Utils.resolveComponentDTOType(selectedFolder), dropTarget, selectedFolder);
					if (true == detailsItem.load(selectedFolder)) {
						itemsMap.put(type, detailsItem);
						componentItemsMap.put(selectedFolder.getId(), itemsMap);
						tabPanel.add(detailsItem);
						tabPanel.setSelection(detailsItem);
						detailsItem.setText(selectedFolder.getTitle());
					} else {
						if (detailsItem.isAttached() && detailsItem.isClosable())
							detailsItem.close();
					}
				} else {
					if(detailsItem.getGrid() != null) {
						detailsItem.getGrid().getSelectionModel().deselectAll();
						detailsItem.makeButtonsDisabled();
					}
					if(!detailsItem.isAttached()) {
						tabPanel.add(detailsItem);
					}
					tabPanel.setSelection(detailsItem);
				}
			}
		}
	}

	private void downloadNode(CompositeDTO selectedNode) {
		AsyncCallback<String> callback = new AbstractCallback<String>() {

			@Override
			public void onSuccess(String displayUrl) {
				if(Utils.notEmptyAndNullString(displayUrl)) {
					String key = extractSupportedTypes(displayUrl);
					if(Utils.notEmptyAndNullString(key))
						Window.open(Utils.getContextPath() + "/LessonsUI/data/" +"DownloadServlet?filePath="+displayUrl+"&key="+key, "_new", "location=0,status=0,scrollbars=1,resizable=1,width=600,height=400");
					else
						LessonsMessageBox.showErrorWindow(IConstants.DISPLAY_FAILED_DUE_TO_UNRECOGNICED_FILE);
				}
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).displayNode(selectedNode.getId(), callback);
	}
	
	private void uploadedFileAssosiation(CompositeDTO selectedNodeDTO, String keyword, String filePath) {
		AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {

			@Override
			public void onSuccess(CompositeDTO updatedGraph) {
				AppEvent event = new AppEvent(LessonsEvents.UPDATE_TREE_VIEW_EVENT_AFTER_UPDATES);
				event.setData(IConstants.UPDATED_GRAPH, updatedGraph);
				Dispatcher.get().dispatch(event);
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).assosiateUploadedFile(selectedNodeDTO.getId(), keyword, filePath, callback);
	}
	
	@Override
	protected void handleEvent(AppEvent event) {
		EventType t = event.getType();
		
		if (t == LessonsEvents.SHOW_DETAILS_EVENT) {
			CompositeDTO selectedNode = event.getData(IConstants.SELECTED_NODE);
			TreePanelDropTargetExtended dropTarget = event.getData(IConstants.TREE_DROP_TARGET);
			if (selectedNode != null)
				constructDetailsItem(selectedNode, dropTarget);
		}
		
		if(t == LessonsEvents.DOWNLOAD_EVENT) {
			CompositeDTO selectedNode = event.getData(IConstants.SELECTED_NODE);
			if (selectedNode != null)
				downloadNode(selectedNode);
		}

		if (t == LessonsEvents.UPDATE_GRID_VIEW_EVENT_AFTER_UPDATES) {
			CompositeDTO graphDTO = event.getData(IConstants.GRAPH);
			if(graphDTO != null) {
				
				removeAllOrhansGrid(graphDTO);
				
				if (componentItemsMap != null && componentItemsMap.size() > 0) {
					Integer graphId = graphDTO.getId();
					HashMap<String, BasicGridItem> pItemsMap = componentItemsMap.get(graphId);
					if (pItemsMap != null && pItemsMap.size() > 0) {
						String typeOfGraphDTO = Utils.resolveComponentDTONameOfType(graphDTO);
						detailsItem = pItemsMap.get(typeOfGraphDTO);
						CompositeDTO gridSelectionDTO = detailsItem.getParentDTO();
						if (gridSelectionDTO != null && gridSelectionDTO.equals(graphDTO)) {
							if (detailsItem.reload(graphDTO) == false && detailsItem.isAttached()) detailsItem.close();
							else if(detailsItem != null 
									&& detailsItem.isAttached() 
									&& detailsItem.getHeader() != null 
									&& Utils.notEmptyAndNullString(graphDTO.getTitle())) 
								detailsItem.setText(graphDTO.getTitle());
						}
					}
				}
				updateUpGraph(graphDTO);
				updateDownGraph(graphDTO);
			}
		}
		
		if(t == LessonsEvents.FILE_ASSOSIATION_EVENT) {
			 CompositeDTO selectedNodeDTO = event.getData(IConstants.SELECTED_NODE);
			 String filePath = event.getData(IConstants.FILE_PATH);
			 String keyword = event.getData(IConstants.KEYWORD);
			 uploadedFileAssosiation(selectedNodeDTO, keyword, filePath);
		}
	}

	private void removeAllOrhansGrid(CompositeDTO graphDTO) {
		List<Integer> ids = new ArrayList<Integer>();
		if(graphDTO == null || graphDTO.getId() == null || graphDTO.isTerminal()) return;
		ids.add(graphDTO.getId());
		initChildren(ids, graphDTO);
		
		if(componentItemsMap != null && componentItemsMap.size() > 0 && !ids.isEmpty()) {
			Map<Integer, HashMap<String, BasicGridItem>> shouldHideGrids = new HashMap<Integer, HashMap<String, BasicGridItem>>();
			for(Entry<Integer, HashMap<String, BasicGridItem>> entry : componentItemsMap.entrySet()) {
				if(entry.getKey() != null && !ids.contains(entry.getKey()))
					shouldHideGrids.put(entry.getKey(), entry.getValue());
			}
			for(Entry<Integer, HashMap<String, BasicGridItem>> entry : shouldHideGrids.entrySet()) {
				HashMap<String, BasicGridItem> value = componentItemsMap.get(entry.getKey());
				for(Entry<String, BasicGridItem> gridItem : value.entrySet()) {
					BasicGridItem basicGridItem = gridItem.getValue();
					if(basicGridItem != null && basicGridItem.isAttached())
						basicGridItem.close();
				}
			}
		}
	}

	private void initChildren(List<Integer> ids, CompositeDTO parent) {
		if(parent != null && parent.getChildren() != null && !parent.getChildren().isEmpty()) {
			for (CompositeDTO child : parent.getChildren()) {
				if(child != null && child.getId() != null && !child.isTerminal()) ids.add(child.getId());
				initChildren(ids, child);
			}
		}
	}
	
	private void updateUpGraph(CompositeDTO node) 
	{
		if(node != null && node.getId() != null) 
		{
			updateMap(node);
			if(!node.isRoot())
			{
				CompositeDTO pNodeDTO = node.getParent();
				if(pNodeDTO != null && pNodeDTO.getId() != null) 
				{
					updateMap(pNodeDTO);
					updateUpGraph(pNodeDTO);
				}
			}
		}
	}
	
	private void updateDownGraph(CompositeDTO node) 
	{
		if(node != null && node.getChildren() != null && !node.getChildren().isEmpty()) 
		{
			List<CompositeDTO> children = node.getChildren();
			for(CompositeDTO childNodeDTO : children) 
			{
				updateMap(childNodeDTO);
				updateDownGraph(childNodeDTO);
			}
		}
	}
	
	private void updateMap(CompositeDTO node) {
		HashMap<String, BasicGridItem> itemsMap = componentItemsMap.get(node.getId());
		if (itemsMap != null && itemsMap.size() > 0) {
			String typeOfNode = Utils.resolveComponentDTONameOfType(node);
			detailsItem = itemsMap.get(typeOfNode);
			if(detailsItem != null) { 
				if (detailsItem.reload(node) == false 
						&& detailsItem.isAttached()) 
				{
					detailsItem.close();
				}
				else if(detailsItem != null 
						&& detailsItem.isAttached() 
						&& detailsItem.getHeader() != null 
						&& Utils.notEmptyAndNullString(node.getTitle()))
				{
					detailsItem.setText(node.getTitle());
				}
			}
		}
	}
	
	private String extractSupportedTypes(String displayUrl) {
		if(Utils.notEmptyAndNullString(displayUrl) && displayUrl.contains(".")) {
			int lastDot = displayUrl.lastIndexOf(".");
			String key = displayUrl.substring(lastDot, displayUrl.length());
			if(IConstants.EXCEL.equals(key) || IConstants.PDF.equals(key) || IConstants.DOC.equals(key))
				return key;
		}
		return IConstants.EMPTY_STR;
	}
}
