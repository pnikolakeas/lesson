package com.education.lessons.ui.client.components.tree;

import java.util.ArrayList;
import java.util.List;

import com.education.lessons.ui.client.components.AbstractCallback;
import com.education.lessons.ui.client.components.box.LessonsMessageBox;
import com.education.lessons.ui.client.components.extended.TreePanelDropTargetExtended;
import com.education.lessons.ui.client.components.form.InitLessonForm;
import com.education.lessons.ui.client.events.LessonsEvents;
import com.education.lessons.ui.client.factory.TreeModelFactory;
import com.education.lessons.ui.client.model.LessonsBaseTreeModel;
import com.education.lessons.ui.client.permission.PermissionLogic;
import com.education.lessons.ui.client.services.AsyncServiceFactory;
import com.education.lessons.ui.client.services.DataProviderServiceAsync;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.ui.client.utils.Utils;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.dnd.DND.Feedback;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class BasicTree extends ContentPanel {

	private TreeStore<ModelData> store;
	private TreePanel<ModelData> treePanel;
	private TreePanelDropTargetExtended target;
	private Menu contextMenu;
	private CompositeDTO previousParentNode;
	private CompositeDTO newParentNode;
	private CompositeDTO parentNode;
	private List<CompositeDTO> children;
	private LessonsBaseTreeModel selectedNode;
	private LessonsBaseTreeModel graphNode;
	protected ContentPanel innerPanel;

	public BasicTree() {
		setScrollMode(Scroll.AUTO);
		setCollapsible(true);
		setHeading("View & Actions Menu");
		initComponents();
	}

	public void initComponents() {
		if (treePanel == null) {
			store = new TreeStore<ModelData>();
			treePanel = new TreePanel<ModelData>(store);
			treePanel.setDisplayProperty(IConstants.TREE_DISPLAY_PROPERTY);
		}
		retrieveTreeModel();
		initToolBar();
		if(Utils.isAdminUser()) {
			initAdminContextMenu();
		}
		else if(Utils.isSimpleUser()) {
			initUserContextMenu();
		}
		if(PermissionLogic.dragDropPermission()) {
			initDragDropMenu();
		}
		initClickMenu();
		add(treePanel, new FlowData(4));
	}

	private void initClickMenu() {
		treePanel.addListener(Events.OnClick,
				new Listener<TreePanelEvent<ModelData>>() {
					@Override
					public void handleEvent(TreePanelEvent<ModelData> be) {
						LessonsBaseTreeModel selectedItem = (LessonsBaseTreeModel) be
								.getItem();
						if (selectedItem != null) {
							setSelectedNode(selectedItem);
							CompositeDTO selectedComponentDTO = selectedItem
									.getBean();
							String valueOfType = selectedComponentDTO.getType()
									.getValue();
							ComponentTypeEnum type = ComponentTypeEnum
									.getEnumByStringValue(valueOfType);
							boolean fireShowEvent = !(type
									.equals(ComponentTypeEnum.ASSIGNMENT_CONTENT)
									|| type.equals(ComponentTypeEnum.LINK_CONTENT)
									|| type.equals(ComponentTypeEnum.PRESENTATION_CONTENT) 
									|| type.equals(ComponentTypeEnum.TUTORIAL_CONTENT)
									|| type.equals(ComponentTypeEnum.GRADE_CONTENT));
							if (fireShowEvent)
								firesShowDetailsEvent(false);
						}
					}
				});
	}

	private void initDragDropMenu() {

		DNDListener dragTargetListener = new DNDListener() {
			@Override
			public void dragDrop(DNDEvent e) {
				firesMovementsEvent();
			}
		};

		DNDListener dragSourceListener = new DNDListener() {
			@Override
			public void dragStart(DNDEvent e) {

				List<ModelData> selections = treePanel.getSelectionModel()
						.getSelectedItems();
				List<CompositeDTO> selectedDTOList = new ArrayList<CompositeDTO>();

				for (ModelData modelData : selections) {
					LessonsBaseTreeModel treeModel = (LessonsBaseTreeModel) modelData;
					CompositeDTO componentDTO = treeModel.getBean();
					selectedDTOList.add(componentDTO);
				}

				if (selectedDTOList == null || selectedDTOList.isEmpty()) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				} else {
					initDropRestrictions();
					setChildren(selectedDTOList);
					setPreviousParentNode(selectedDTOList.get(0).getParent());
				}
			}
		};

		TreePanelDragSource source = new TreePanelDragSource(treePanel);
		source.addDNDListener(dragSourceListener);

		target = new TreePanelDropTargetExtended(treePanel) {
			@Override
			protected List<ModelData> prepareDropData(Object data,
					boolean convertTreeStoreModel) {
				ModelData modelData = this.activeItem.getModel();
				LessonsBaseTreeModel baseTreeModel = (LessonsBaseTreeModel) modelData;
				setNewParentNode((CompositeDTO) baseTreeModel.getBean());
				return super.prepareDropData(data, convertTreeStoreModel);
			}
		};

		target.addDNDListener(dragTargetListener);
		target.setAllowSelfAsSource(true);
		target.setAllowDropOnNullNode(false);
		target.setAllowDropOnLeaf(true);

		target.setAutoExpand(true);
		target.setFeedback(Feedback.APPEND);

		initDropRestrictions();
	}

	private void initDropRestrictions() {
		target.getDropRestrictions().clear();
		target.addDropRestriction(ComponentTypeEnum.LINK, ComponentTypeEnum.LINK_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.ASSIGNMENT, ComponentTypeEnum.ASSIGNMENT_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.PRESENTATION, ComponentTypeEnum.PRESENTATION_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.TUTORIAL, ComponentTypeEnum.TUTORIAL_CONTENT);
		target.addDropRestriction(ComponentTypeEnum.GRADE, ComponentTypeEnum.GRADE_CONTENT);
	}
	
	protected MenuItem expandItem;
	protected MenuItem collapseItem;
	protected MenuItem addLessonItem;
	protected MenuItem showDetailsItem;
	protected MenuItem removeAllChildrenItem;

	public void initToolBar() {

		ToolBar toolBar = new ToolBar();

		Menu viewMenu = new Menu();

		expandItem = new MenuItem(IConstants.EXPAND_ALL);
		viewMenu.add(expandItem);

		expandItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				treePanel.expandAll();
			}
		});

		collapseItem = new MenuItem(IConstants.COLLAPSE_ALL);
		viewMenu.add(collapseItem);

		collapseItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				treePanel.collapseAll();
			}
		});

		Menu actionsMenu = new Menu();

		addLessonItem = new MenuItem(IConstants.ADD_LESSON);
		actionsMenu.add(addLessonItem);

		addLessonItem.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				LessonsMessageBox.showContentWindow(IConstants.EMPTY_STR,
						new InitLessonForm(), 350);
			}
		});

		showDetailsItem = new MenuItem(IConstants.SHOW_DETAILS_ALL);
		actionsMenu.add(showDetailsItem);

		showDetailsItem
				.addSelectionListener(new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						firesShowDetailsEvent(true);
					}
				});

		removeAllChildrenItem = new MenuItem(IConstants.REMOVE_ALL_LESSONS);
		actionsMenu.add(removeAllChildrenItem);

		removeAllChildrenItem
		.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				LessonsBaseTreeModel graphNode = getGraphNode();
				CompositeDTO graphDTO = graphNode.getBean();
				firesRemovalAllChildrenEvent(graphDTO);
			}
		});

		ToggleButton viewBtn = new ToggleButton("View");
		viewBtn.setMenu(viewMenu);
		toolBar.add(viewBtn);

		toolBar.add(new SeparatorToolItem());

		ToggleButton actionsBtn = new ToggleButton("Actions");
		actionsBtn.setMenu(actionsMenu);
		toolBar.add(actionsBtn);
	    
	    setTopComponent(toolBar);
	    
		handleMenuItemsVisibility(0);
	}
	
	private MenuItem insertNode;
	private MenuItem removeNode;
	private MenuItem displayNode;
	private MenuItem infoNode;
	private MenuItem removeAllChildrenNode;
	
	private void initUserContextMenu() {

		contextMenu = new Menu();
		contextMenu.setWidth(150);

		displayNode = new MenuItem();
		displayNode.setText(IConstants.DISPLAY_NODE);
		displayNode.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				LessonsBaseTreeModel selectedNode = getSelectedNode();
				if (selectedNode != null && selectedNode.getBean() != null && selectedNode.getBean() instanceof CompositeDTO) {
					CompositeDTO selectedNodeDTO = selectedNode.getBean();
					firesDownloadEvent(selectedNodeDTO);
				}
			}
		});
		
		infoNode = new MenuItem();
		infoNode.setText(IConstants.NO_INFO_NODE);
		infoNode.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				LessonsMessageBox.showInfoWindow(IConstants.NO_INFO_NODE_HELP);
			}
		});
		
		treePanel.setContextMenu(contextMenu);
		treePanel.addListener(Events.OnContextMenu, new Listener<TreePanelEvent<ModelData>>() {
			@Override
			public void handleEvent(TreePanelEvent<ModelData> be) {
				LessonsBaseTreeModel selectedItem = (LessonsBaseTreeModel) be.getItem();
				setSelectedNode(selectedItem);
				CompositeDTO selectedNodeDTO = selectedItem.getBean();
				attachUserContextMenu(selectedNodeDTO);
			}
		});
	}
	
	private void attachUserContextMenu(CompositeDTO selectedComponentDTO) {
		contextMenu.removeAll();
		String valueOfType = selectedComponentDTO.getType().getValue();
		ComponentTypeEnum type = ComponentTypeEnum.getEnumByStringValue(valueOfType);
		if (ComponentTypeEnum.ASSIGNMENT_CONTENT.equals(type) && Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
			displayNode.setText(IConstants.DISPLAY_ASSIGNMENT);
			contextMenu.add(displayNode);
		} else if (ComponentTypeEnum.LINK_CONTENT.equals(type) && Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
			displayNode.setText(IConstants.DISPLAY_LINK);
			contextMenu.add(displayNode);
		} else if (ComponentTypeEnum.PRESENTATION_CONTENT.equals(type) && Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath()))  {
			displayNode.setText(IConstants.DISPLAY_PRESENTATION);
			contextMenu.add(displayNode);
		} else if (ComponentTypeEnum.TUTORIAL_CONTENT.equals(type) && Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
			displayNode.setText(IConstants.DISPLAY_TUTORIAL);
			contextMenu.add(displayNode);
		} else if (ComponentTypeEnum.GRADE_CONTENT.equals(type) && Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
			displayNode.setText(IConstants.DISPLAY_GRADE);
			contextMenu.add(displayNode);
		} else {
			contextMenu.add(infoNode);
		}
	}

	private void initAdminContextMenu() {

		contextMenu = new Menu();
		contextMenu.setWidth(150);

		displayNode = new MenuItem();
		displayNode.setText(IConstants.DISPLAY_NODE);

		displayNode.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				LessonsBaseTreeModel selectedNode = getSelectedNode();
				if (selectedNode != null && selectedNode.getBean() != null && selectedNode.getBean() instanceof CompositeDTO) {
					CompositeDTO selectedNodeDTO = selectedNode.getBean();
					firesDownloadEvent(selectedNodeDTO);
				}
			}
		});

		insertNode = new MenuItem();
		insertNode.setText(IConstants.INSERT_NODE);

		insertNode.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {

				LessonsBaseTreeModel parent = getSelectedNode();
				if (parent != null && parent.getBean() != null && parent.getBean() instanceof CompositeDTO) {
					final CompositeDTO parentDTO = parent.getBean();
					String sType = parentDTO.getType().getValue();
					addContent(parent, parentDTO,
							ComponentTypeEnum.getEnumByStringValue(sType));
				}
			}

			private void addContent(final LessonsBaseTreeModel parent, final CompositeDTO parentDTO, ComponentTypeEnum parentType) {

				treePanel.mask(IConstants.LOADING);
				AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {

					@Override
					public void onFailure(Throwable caught) {
						super.onFailure(caught);
						treePanel.unmask();
					}

					@Override
					public void onSuccess(CompositeDTO childDTO) {
						LessonsBaseTreeModel child = new LessonsBaseTreeModel();

						child.setBean(childDTO);
						child.setParent(parent);

						store.add(parent, child, true);
						setSelectedNode(parent);

						treePanel.unmask();

						retrieveTreeModel();
					}
				};
				AsyncServiceFactory.getService(DataProviderServiceAsync.class).addTreeContent(parentDTO.getId(), callback);
			}
		});

		removeNode = new MenuItem();
		removeNode.setText(IConstants.REMOVE_NODE);

		removeNode.addSelectionListener(new SelectionListener<MenuEvent>() {
			public void componentSelected(MenuEvent ce) {
				LessonsBaseTreeModel selectedNode = getSelectedNode();
				if (selectedNode != null) {
					Boolean isLeafNode = null;
					CompositeDTO childDTO = (CompositeDTO) selectedNode
							.getBean();

					LessonsBaseTreeModel parent = (LessonsBaseTreeModel) store
							.getParent(selectedNode);

					if (parent == null) {
						store.remove(selectedNode);
						setSelectedNode(selectedNode);
						isLeafNode = false;
					} else {
						store.remove(parent, selectedNode);
						setSelectedNode(parent);
						isLeafNode = true;
					}
					firesRemovalEvent(childDTO, isLeafNode);
				}
			}
		});

		removeAllChildrenNode = new MenuItem();
		removeAllChildrenNode.setText(IConstants.REMOVE_ALL_CHILDREN_NODES);

		removeAllChildrenNode.addSelectionListener(new SelectionListener<MenuEvent>() {
		@Override
		public void componentSelected(MenuEvent ce) {

				LessonsBaseTreeModel selectedParentNode = getSelectedNode();

				if (selectedParentNode != null) {
					store.removeAll(selectedParentNode);
					CompositeDTO selectedParentNodeDTO = (CompositeDTO) selectedParentNode.getBean();
					setSelectedNode(selectedParentNode);
					firesRemovalAllChildrenEvent(selectedParentNodeDTO);
				}
			}
		});

		treePanel.setContextMenu(contextMenu);
		treePanel.addListener(Events.OnContextMenu, new Listener<TreePanelEvent<ModelData>>() {
			@Override
			public void handleEvent(TreePanelEvent<ModelData> be) {
				LessonsBaseTreeModel selectedItem = (LessonsBaseTreeModel) be.getItem();
				setSelectedNode(selectedItem);
				CompositeDTO selectedNodeDTO = selectedItem.getBean();
				attachAdminContextMenu(selectedNodeDTO);
			}
		});
	}

	private void firesShowDetailsEvent(boolean showAll) {
		AppEvent event = new AppEvent(LessonsEvents.SHOW_DETAILS_EVENT);
		LessonsBaseTreeModel selectedNode = getSelectedNode();
		CompositeDTO selectedNodeDTO = selectedNode.getBean();
	
		if (showAll == true) {
			LessonsBaseTreeModel graphNode = getGraphNode();
			selectedNodeDTO = graphNode.getBean();
		}

		if (selectedNodeDTO != null) {
			event.setData(IConstants.TREE_DROP_TARGET, getDropTarget());
			event.setData(IConstants.SELECTED_NODE, selectedNodeDTO);
			if (selectedNodeDTO.getChildren() != null && !selectedNodeDTO.getChildren().isEmpty()) {
				Dispatcher.get().dispatch(event);
			} else
				LessonsMessageBox.showInfoWindow(IConstants.NO_DETAILS_INFO);
		}
	}

	private void firesDownloadEvent(CompositeDTO selectionDTO) {
		AppEvent event = new AppEvent(LessonsEvents.DOWNLOAD_EVENT);
		event.setData(IConstants.SELECTED_NODE, selectionDTO);
		Dispatcher.get().dispatch(event);
	}

	private void firesRemovalEvent(CompositeDTO childDTO, Boolean isLeafNode) {
		AppEvent event = new AppEvent(LessonsEvents.REMOVAL_EVENT);
		event.setData(IConstants.CHILD_NODE, childDTO);
		event.setData(IConstants.IS_LEAF_NODE, isLeafNode);
		Dispatcher.get().dispatch(event);
	}

	private void firesRemovalAllChildrenEvent(CompositeDTO selectedParentNodeDTO) {
		AppEvent event = new AppEvent(LessonsEvents.REMOVAL_ALL_CHILDREN_EVENT);
		event.setData(IConstants.PARENT_NODE, selectedParentNodeDTO);
		event.setData(IConstants.IS_ROOT_NODE, selectedParentNodeDTO.isRoot());
		Dispatcher.get().dispatch(event);
	}

	private void firesMovementsEvent() {
		if (children != null && !children.isEmpty()) {
			AppEvent event = new AppEvent(LessonsEvents.MOVEMENTS_EVENT);
			event.setData(IConstants.CHILDREN_NODES, getChildren());
			event.setData(IConstants.NEW_PARENT_NODE, getNewParentNode());
			Dispatcher.get().dispatch(event);
		}
	}

	private void attachAdminContextMenu(CompositeDTO selectedComponentDTO) {
		contextMenu.removeAll();
		String valueOfType = selectedComponentDTO.getType().getValue();
		ComponentTypeEnum type = ComponentTypeEnum.getEnumByStringValue(valueOfType);
		if (type.equals(ComponentTypeEnum.ASSIGNMENT_CONTENT)) {
			removeNode.setText(IConstants.DELETE_ASSIGNMENT);
			contextMenu.add(removeNode);
			if (Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
				displayNode.setText(IConstants.DISPLAY_ASSIGNMENT);
				contextMenu.add(displayNode);
			}
		} else if (type.equals(ComponentTypeEnum.LINK_CONTENT)) {
			removeNode.setText(IConstants.DELETE_LINK);
			contextMenu.add(removeNode);
			if (Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
				displayNode.setText(IConstants.DISPLAY_LINK);
				contextMenu.add(displayNode);
			}
		} else if (type.equals(ComponentTypeEnum.PRESENTATION_CONTENT)) {
			removeNode.setText(IConstants.DELETE_PRESENTATION);
			contextMenu.add(removeNode);
			if (Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
				displayNode.setText(IConstants.DISPLAY_PRESENTATION);
				contextMenu.add(displayNode);
			}
		} else if (type.equals(ComponentTypeEnum.TUTORIAL_CONTENT)) {
			removeNode.setText(IConstants.DELETE_TUTORIAL);
			contextMenu.add(removeNode);
			if (Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
				displayNode.setText(IConstants.DISPLAY_TUTORIAL);
				contextMenu.add(displayNode);
			}
		}
		else if (type.equals(ComponentTypeEnum.GRADE_CONTENT)) {
			removeNode.setText(IConstants.DELETE_GRADE);
			contextMenu.add(removeNode);
			if (Utils.notEmptyAndNullString(selectedComponentDTO.getFilePath())) {
				displayNode.setText(IConstants.DISPLAY_GRADE);
				contextMenu.add(displayNode);
			}
		}
		else if (type.equals(ComponentTypeEnum.LESSON)) {
			removeNode.setText(IConstants.DELETE_LESSON);
			contextMenu.add(removeNode);
		}
		else if (type.equals(ComponentTypeEnum.ASSIGNMENT)) {
			insertNode.setText(IConstants.INSERT_ASSIGNMENT);
			contextMenu.add(insertNode);
			if (selectedComponentDTO.getChildren() != null && !selectedComponentDTO.getChildren().isEmpty()) {
				removeAllChildrenNode.setText(IConstants.DELETE_ALL_ASSIGNMENTS);
				contextMenu.add(removeAllChildrenNode);
			}
		}
		else if (type.equals(ComponentTypeEnum.LINK)) {
			insertNode.setText(IConstants.INSERT_LINK);
			contextMenu.add(insertNode);
			if (selectedComponentDTO.getChildren() != null && !selectedComponentDTO.getChildren().isEmpty()) {
				removeAllChildrenNode.setText(IConstants.DELETE_ALL_LINKS);
				contextMenu.add(removeAllChildrenNode);
			}
		}
		else if (type.equals(ComponentTypeEnum.PRESENTATION)) {
			insertNode.setText(IConstants.INSERT_PRESENTATION);
			contextMenu.add(insertNode);
			if (selectedComponentDTO.getChildren() != null && !selectedComponentDTO.getChildren().isEmpty()) {
				removeAllChildrenNode.setText(IConstants.DELETE_ALL_PRESENTATIONS);
				contextMenu.add(removeAllChildrenNode);
			}
		}
		else if (type.equals(ComponentTypeEnum.TUTORIAL)) {
			insertNode.setText(IConstants.INSERT_TUTORIAL);
			contextMenu.add(insertNode);
			if (selectedComponentDTO.getChildren() != null && !selectedComponentDTO.getChildren().isEmpty()) {
				removeAllChildrenNode.setText(IConstants.DELETE_ALL_TUTORIALS);
				contextMenu.add(removeAllChildrenNode);
			}
		}
		else if (type.equals(ComponentTypeEnum.GRADE)) {
			insertNode.setText(IConstants.INSERT_GRADE);
			contextMenu.add(insertNode);
			if (selectedComponentDTO.getChildren() != null && !selectedComponentDTO.getChildren().isEmpty()) {
				removeAllChildrenNode.setText(IConstants.DELETE_ALL_GRADES);
				contextMenu.add(removeAllChildrenNode);
			}
		}
	}
	
	public void retrieveTreeModel() {
		treePanel.mask(IConstants.LOADING);
		AbstractCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				treePanel.unmask();
			}
			
			@Override
			public void onSuccess(CompositeDTO result) {
				if (result != null) {
					updateTreeModel(result);
				}
				treePanel.unmask();
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class).retrieveTreeModel(callback);
	}

	public void updateTreeModel(CompositeDTO updatedGraph) {
		store.removeAll();
		LessonsBaseTreeModel model = TreeModelFactory.get().createModel(updatedGraph);
		store.add(model.getChildren(), true);
		setGraphNodeAndHandleMenuItemsVisibility(model);
		setSelectedNode(model);
		propagateEventForGridUpdate(updatedGraph);
		treePanel.expandAll();
	}

	private void propagateEventForGridUpdate(CompositeDTO updatedGraph) {
		AppEvent event = new AppEvent(LessonsEvents.UPDATE_GRID_VIEW_EVENT_AFTER_UPDATES);
		event.setData(IConstants.GRAPH, updatedGraph);
		Dispatcher.get().dispatch(event);
	}

	public void addNewLesson(final String name, final String description) {
		treePanel.mask(IConstants.LOADING);
		AsyncCallback<CompositeDTO> callback = new AbstractCallback<CompositeDTO>() {

			@Override
			public void onFailure(Throwable caught) {
				super.onFailure(caught);
				treePanel.unmask();
			}

			@Override
			public void onSuccess(CompositeDTO createdLesson) {
				LessonsBaseTreeModel lesson = new LessonsBaseTreeModel();
				lesson.setBean(createdLesson);
				LessonsBaseTreeModel graphNode = getGraphNode();
				setSelectedNode(graphNode);
				treePanel.unmask();
				retrieveTreeModel();
			}
		};
		AsyncServiceFactory.getService(DataProviderServiceAsync.class)
				.addLesson(name, description, callback);
	}

	public CompositeDTO getParentNode() {
		return parentNode;
	}

	public void setParentNode(CompositeDTO parentNode) {
		this.parentNode = parentNode;
	}

	public CompositeDTO getPreviousParentNode() {
		return previousParentNode;
	}

	public void setPreviousParentNode(CompositeDTO previousParentNode) {
		this.previousParentNode = previousParentNode;
	}

	public CompositeDTO getNewParentNode() {
		return newParentNode;
	}

	public void setNewParentNode(CompositeDTO newParentNode) {
		this.newParentNode = newParentNode;
	}

	public List<CompositeDTO> getChildren() {
		return children;
	}

	public void setChildren(List<CompositeDTO> children) {
		this.children = children;
	}

	public LessonsBaseTreeModel getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(LessonsBaseTreeModel selectedNode) {
		this.selectedNode = selectedNode;
	}

	public LessonsBaseTreeModel getGraphNode() {
		return graphNode;
	}

	public void setGraphNodeAndHandleMenuItemsVisibility(LessonsBaseTreeModel graphNode) {
		handleMenuItemsVisibility(graphNode.getChildCount());
		this.graphNode = graphNode;
	}

	private void handleMenuItemsVisibility(int childCount) {
		expandItem.setEnabled(PermissionLogic.expandAllPermission(childCount));
		collapseItem.setEnabled(PermissionLogic.collapseAllPermission(childCount));
		addLessonItem.setEnabled(PermissionLogic.addLessonPermission());
		showDetailsItem.setEnabled(PermissionLogic.showLessonsPermission(childCount));
		removeAllChildrenItem.setEnabled(PermissionLogic.deleteLessonsPermission(childCount));
	}

	private TreePanelDropTargetExtended getDropTarget() {
		return target;
	}
}