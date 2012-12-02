package com.education.lessons.ui.client.components.grid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.education.lessons.ui.client.components.box.LessonsMessageBox;
import com.education.lessons.ui.client.components.extended.TreePanelDropTargetExtended;
import com.education.lessons.ui.client.components.form.FileUploadForm;
import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.permission.PermissionLogic;
import com.education.lessons.ui.client.services.AsyncServiceFactory;
import com.education.lessons.ui.client.services.DataProviderServiceAsync;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.ui.client.utils.Utils;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.Style.SortDir;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelReader;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.dnd.GridDragSource;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.Record;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.EditorGrid.ClicksToEdit;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BasicGridItem extends TabItem {

	protected Grid<BeanModel> grid;
	private BasePagingLoader<PagingLoadResult<?>> loader;
	private CompositeDTO parentDTO;
	private List<CompositeDTO> children;
	private ContentPanel inboxGridPanel = new ContentPanel();
	
	protected ListStore<BeanModel> store;
	protected List<ColumnConfig> configs = new ArrayList<ColumnConfig>();
	protected TreePanelDropTargetExtended target;
	protected Menu contextMenu;
	protected MenuItem downloadMenuItem;
	protected MenuItem uploadMenuItem;
	protected MenuItem editMenuItem;
	protected Map<String, MenuItem> menuItemMap = new HashMap<String, MenuItem>();
	protected RowEditor<BeanModel> re;
	
	protected FileUploadForm fileUploadForm;
	
	protected Button saveBtn;
	protected Button cancelBtn;
	protected Button downloadBtn;
	protected Button refreshBtn;
	protected Button uploadBtn;
	
	public BasicGridItem(String text) {
		super(text);
		init();
	}

	protected void init() {
		this.setLayout(new FitLayout());
		this.add(inboxGridPanel);
		this.setClosable(true);
		this.addListener(Events.Close, new Listener<BaseEvent>() {
			@Override
			public void handleEvent(BaseEvent be) {
				removeFromParent();
				grid.getSelectionModel().deselectAll();
				downloadBtn.setEnabled(false);
            	uploadBtn.setEnabled(false);
			}
		});
		buildInboxGrid();
		if(PermissionLogic.dragDropPermission()) buildDragSource();
		buildContextMenu();
		attachClickListener();
	}
	
	protected void buildContextMenu() {
		contextMenu = new Menu();
		contextMenu.setWidth(150);
		editMenuItem = new MenuItem();
		editMenuItem.setText(IConstants.EDIT);
		menuItemMap.put(IConstants.EDIT, editMenuItem);
		editMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				 BeanModel s = grid.getSelectionModel().getSelectedItem();
			      if (s != null) {
			        int index = grid.getStore().indexOf(s);
			        re.startEditing(index, true);
			      }
			}
		});
		contextMenu.add(editMenuItem);
		
		downloadMenuItem = new MenuItem();
		downloadMenuItem.setText(IConstants.DOWNLOAD);
		menuItemMap.put(IConstants.DOWNLOAD, downloadMenuItem);
		downloadMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				 BeanModel selectionModel = grid.getSelectionModel().getSelectedItem();
				 if(selectionModel != null) { 
					CompositeDTO selectionDTO = selectionModel.getBean(); 
					firesDownloadEvent(selectionDTO);
				 }
			}
		});
		contextMenu.add(downloadMenuItem);
		
		uploadMenuItem = new MenuItem();
		uploadMenuItem.setText(IConstants.UPLOAD);
		menuItemMap.put(IConstants.UPLOAD, uploadMenuItem);
		uploadMenuItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				 BeanModel selectionModel = grid.getSelectionModel().getSelectedItem();
				 if(selectionModel != null) { 
					CompositeDTO selectionDTO = selectionModel.getBean(); 
					firesUploadEvent(selectionDTO);
				 }
			}
		});
		contextMenu.add(uploadMenuItem);
		
		grid.addListener(Events.OnContextMenu,
				new Listener<GridEvent<BeanModel>>() {
					@Override
					public void handleEvent(GridEvent<BeanModel> be) {
						BeanModel selectedModel = be.getGrid().getSelectionModel().getSelectedItem();
						if(selectedModel != null) {
							handleContextMenuVisibility(selectedModel);
						}
					}
		});
		
		grid.setContextMenu(contextMenu);
	}
	
	private void buildInboxGrid() {
		inboxGridPanel.setFrame(true);
		inboxGridPanel.setHeaderVisible(false);
		addToolBar();

		RpcProxy<PagingLoadResult<CompositeDTO>> proxy = new RpcProxy<PagingLoadResult<CompositeDTO>>() {

			@Override
			protected void load(Object loadConfig, AsyncCallback<PagingLoadResult<CompositeDTO>> callback) {

				if (((PagingLoadConfig) loadConfig).getSortField() == null) {
					((PagingLoadConfig) loadConfig).setSortField("name");
					((PagingLoadConfig) loadConfig).setSortDir(SortDir.ASC);
				}

				AsyncServiceFactory.getService(DataProviderServiceAsync.class).retrieveGridModel((PagingLoadConfig) loadConfig, getParentDTO().getId(),callback);
			}
		};

		BeanModelReader reader = new BeanModelReader();
		reader.setFactoryForEachBean(true);

		loader = new BasePagingLoader<PagingLoadResult<?>>(proxy, reader);
		loader.addLoadListener(new LoadListener() {
			public void loaderBeforeLoad(LoadEvent le) {
				grid.mask(IConstants.LOADING);
			}

			public void loaderLoad(LoadEvent le) {
				grid.unmask();
			}

			public void loaderLoadException(LoadEvent le) {
				grid.unmask();
			}
		});
		loader.setRemoteSort(false);
		store = new ListStore<BeanModel>(loader);
		
		final ColumnModel cm = new ColumnModel(initColumnConfigs());

		grid = new Grid<BeanModel>(store, cm);
		grid.getSelectionModel().setSelectionMode(SelectionMode.MULTI);
		grid.setStripeRows(false);
		grid.setTrackMouseOver(true);
		grid.getView().setForceFit(true);
		re = new RowEditor<BeanModel>();  
		re.setClicksToEdit(ClicksToEdit.TWO);
		grid.addPlugin(re);

		inboxGridPanel.setLayout(new FillLayout());
		inboxGridPanel.add(grid);

		addButtons();
	}

	private void buildDragSource() {
		
		DNDListener dragSourceListener = new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {

				List<BeanModel> selections = grid.getSelectionModel().getSelectedItems();
				List<CompositeDTO> selectedDTOList = new ArrayList<CompositeDTO>();

				for (BeanModel beanModel : selections) {
					CompositeDTO componentDTO = beanModel.getBean();
					selectedDTOList.add(componentDTO);
				}

				if (selectedDTOList == null || selectedDTOList.isEmpty() || target == null) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				} else {
					initDropRestrictions();
					setChildren(selectedDTOList);
					firesChildrenToMoveRefreshEvent();
				}
			}
		};
		
		GridDragSource source = new GridDragSource(grid);
		source.addDNDListener(dragSourceListener);
	}


	protected List<ColumnConfig> initColumnConfigs() {
		
		TextField<String> text = new TextField<String>();
		text.setAllowBlank(false);

		ColumnConfig column = new ColumnConfig("title", "TITLE", 368);
		column.setEditor(new CellEditor(text));
		configs.add(column);

		text = new TextField<String>();
		text.setAllowBlank(true);

		column = new ColumnConfig("description", "DESCRIPTION", 368);
		column.setEditor(new CellEditor(text));
		configs.add(column);
		
		text = new TextField<String>();
		text.setAllowBlank(true);
		
		column = new ColumnConfig("filePath", "FILE", 276);
		column.setEditor(new CellEditor(text));
		configs.add(column);
		
		return configs;
	}
	
	private void addButtons() {
		inboxGridPanel.setButtonAlign(HorizontalAlignment.CENTER);
		initGridFooterButtons();
		inboxGridPanel.addButton(saveBtn);
		inboxGridPanel.addButton(cancelBtn);
	}
	
	protected void initGridFooterButtons() {
		initGridFooterSaveButton();
		initGridFooterCancelButton();
	}

	protected void initGridFooterCancelButton() {
		cancelBtn = new Button(IConstants.CANCEL,
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						store.rejectChanges();
					}
		});
	}

	protected void initGridFooterSaveButton() {
		saveBtn = new Button(IConstants.SAVE,
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						List<CompositeDTO> modifiedModels = new ArrayList<CompositeDTO>();
						List<Record> modifiedRecords = store.getModifiedRecords();
						for (Record r : modifiedRecords) {
							BeanModel modifiedBeanModel = (BeanModel) r
									.getModel();
							
							CompositeDTO modifiedModel = modifiedBeanModel.getBean();
							modifiedModels.add(modifiedModel);
						}
						if (modifiedModels.size() > 0) {
							store.commitChanges();
							firesManipulationsEvent(modifiedModels);
						}
					}
		});
	}

	protected void firesManipulationsEvent(List<? extends CompositeDTO> modifiedChildren) {
		AppEvent event = new AppEvent(LessonsEvents.UPDATE_TREE_VIEW_AFTER_MANIPULATIONS_EVENT);
		event.setData(IConstants.DIRTY_CHILDREN_NODES, modifiedChildren);
		Dispatcher.get().dispatch(event);		
	}
	
	private void addToolBar() {
		ToolBar toolBar = new ToolBar();
		refreshBtn = new Button(IConstants.REFRESH);
		refreshBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				firesRefreshEvent();
				downloadBtn.setEnabled(false);
				uploadBtn.setEnabled(false);
			}
		});

		refreshBtn.setBorders(true);
		
		downloadBtn = new Button(IConstants.DOWNLOAD);
		downloadBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				BeanModel sm = grid.getSelectionModel().getSelectedItem();
				if(sm!=null && sm.getBean()!=null) {
					CompositeDTO selectionDTO = sm.getBean(); 
					firesDownloadEvent(selectionDTO);
				}
			}
		});

		downloadBtn.setBorders(true);
		downloadBtn.setEnabled(false);
		
		uploadBtn = new Button(IConstants.UPLOAD);
		uploadBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				BeanModel sm = grid.getSelectionModel().getSelectedItem();
				if(sm!=null && sm.getBean()!=null) {
					CompositeDTO selectionDTO = sm.getBean(); 
					firesUploadEvent(selectionDTO);
				}
			}
		});

		uploadBtn.setBorders(true);
		uploadBtn.setEnabled(false);
		
		toolBar.add(refreshBtn);
		toolBar.add(downloadBtn);
		toolBar.add(uploadBtn);

		toolBar.setVisible(true);
		inboxGridPanel.setTopComponent(toolBar);
	}

	private void firesRefreshEvent() {
		AppEvent event = new AppEvent(LessonsEvents.REFRESH_VIEW_EVENT);
		event.setData(IConstants.SELECTED_NODE, getParentDTO());
		Dispatcher.get().dispatch(event);
	}
	
	private void firesChildrenToMoveRefreshEvent() {
		AppEvent event = new AppEvent(LessonsEvents.REFRESH_CHILDREN_TO_MOVE_EVENT);
		event.setData(IConstants.CHILDREN_NODES, getChildren());
		Dispatcher.get().dispatch(event);
	}
	
	protected void firesDownloadEvent(CompositeDTO assosiatedModel) {
		AppEvent event = new AppEvent(LessonsEvents.DOWNLOAD_EVENT);
		event.setData(IConstants.SELECTED_NODE, assosiatedModel);
		Dispatcher.get().dispatch(event);
	}
	
	protected void firesUploadEvent(CompositeDTO assosiatedModel) {
		if(fileUploadForm == null)
			fileUploadForm = new FileUploadForm();
		
		fileUploadForm.setAssosiatedModel(assosiatedModel);
		LessonsMessageBox.showContentWindow(IConstants.EMPTY_STR, fileUploadForm, 350);
	}
	
	public boolean reload(CompositeDTO selectedFolder) {
		return load(selectedFolder);
	}

	public boolean load(CompositeDTO selectedFolder) {
		grid.mask(IConstants.LOADING);
		store.removeAll();
		setParentDTO(selectedFolder);
		List<BeanModel> gridStore = Utils.getBeanModelListStore(selectedFolder.getChildren(), getObjectClass());
		if (gridStore.isEmpty()) { 
			grid.unmask();
			return false;
		}
		store.add(gridStore);
		makeButtonsDisabled();
		grid.unmask();
		return true;
	}
	
	protected Class<?> getObjectClass() {
		return CompositeDTO.class;
	}

	public void updateWorkspaceViewByServiceCall() {
		store.removeAll();
		loader.load(0, IConstants.PAGE_SIZE);
	}
	
	public CompositeDTO getParentDTO() {
		return parentDTO;
	}

	public void setParentDTO(CompositeDTO parentDTO) {
		this.parentDTO = parentDTO;
	}

	public void clearGrid() {
		grid.clearState();
		grid.getStore().removeAll();
	}

	public TreePanelDropTargetExtended getTarget() {
		return target;
	}

	public void setTarget(TreePanelDropTargetExtended target) {
		this.target = target;
	}

	public List<CompositeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<CompositeDTO> children) {
		this.children = children;
	}
	
	protected void initDropRestrictions() {
		target.getDropRestrictions().clear();
		target.addDropRestriction(ComponentTypeEnum.LINK, ComponentTypeEnum.LINK_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.ASSIGNMENT, ComponentTypeEnum.ASSIGNMENT_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.PRESENTATION, ComponentTypeEnum.PRESENTATION_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.TUTORIAL, ComponentTypeEnum.TUTORIAL_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.GRADE, ComponentTypeEnum.GRADE_CONTENT);
	}

	public void makeButtonsDisabled() {
		if(uploadBtn!=null && uploadBtn.isEnabled())
			uploadBtn.setEnabled(false);
		if(downloadBtn!=null && downloadBtn.isEnabled())
			downloadBtn.setEnabled(false);
	}

	public Grid<BeanModel> getGrid() {
		return  grid;
	}
	
	protected void handleContextMenuVisibility(BeanModel selectedModel) {
		contextMenu.removeAll();
		
		if(Utils.isAdminUser())
			contextMenu.add(editMenuItem);
		
		contextMenu.add(uploadMenuItem);
		
		if(selectedModel != null && selectedModel.getBean() != null){
			CompositeDTO presentation = selectedModel.getBean();
			if(presentation!=null && Utils.notEmptyAndNullString(presentation.getFilePath()))
				contextMenu.add(downloadMenuItem);
		}
	}
	
	protected void attachClickListener() {
		grid.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<BeanModel>() {
            @Override
            public void selectionChanged(SelectionChangedEvent<BeanModel> se) {
            	BeanModel selectedItem = grid.getSelectionModel().getSelectedItem();	   
                if(selectedItem != null) {
                	uploadBtn.setEnabled(true);
                	CompositeDTO leafDTO = selectedItem.getBean();
                	if(leafDTO != null) {
                		if(Utils.notEmptyAndNullString(leafDTO.getFilePath()))
                			downloadBtn.setEnabled(true);
                		else
                			downloadBtn.setEnabled(false);	
                	}
                }
            }
        });
	}
}
