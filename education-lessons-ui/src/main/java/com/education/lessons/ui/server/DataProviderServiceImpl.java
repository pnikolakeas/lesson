package com.education.lessons.ui.server;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;

import com.education.lessons.dao.exception.DataException;
import com.education.lessons.dao.model.composite.Composite;
import com.education.lessons.dao.model.composite.CompositeTypeEnum;
import com.education.lessons.dao.service.composite.CompositeService;
import com.education.lessons.ui.client.services.DataProviderService;
import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.viewmodel.core.CompositeDTO;
import com.education.lessons.viewmodel.paging.PaginationInfoDTO;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings({ "serial" })
public class DataProviderServiceImpl extends RemoteServiceServlet implements DataProviderService {

	protected WebApplicationContext applicationContext;
	protected CompositeService compositeService;

	@Override
	public void init() throws ServletException {
		super.init();
		applicationContext = WebApplicationContextUtils.getWebApplicationContext(getServletContext());
		compositeService = (CompositeService) applicationContext.getBean("compositeService");
	}

	@Override
	public CompositeDTO retrieveTreeModel() {
		Composite root = compositeService.getRoot();
		return createCompositeDTO(root);
	}

	@Override
	public CompositeDTO addTreeContent(Integer parentID) {
		Composite root = null;
		try {
			root = compositeService.addTreeContent(parentID);
		} catch(DataException de) {
			root = compositeService.getRoot();
			de.printStackTrace();
		}
		return createCompositeDTO(root);
	}

	@Override
	public CompositeDTO removeTreeContent(Integer childID) {
		try {
			compositeService.removeTreeContent(childID);
		} catch(DataException de) {
			de.printStackTrace();
		}
		return createCompositeDTO(compositeService.getRoot());
	}
	
	@Override
	public CompositeDTO removeAllChildrenTreeContent(Integer parentID) {
		try {
			compositeService.removeAllChildrenTreeContent(parentID);
		} catch(DataException de) {
			de.printStackTrace();
		}
		return createCompositeDTO(compositeService.getRoot());
	}

	@Override
	public PagingLoadResult<CompositeDTO> retrieveGridModel(PagingLoadConfig loadConfig, Integer compositeID) {
		
		Composite selectedFolder = compositeService.getComposite(compositeID);
		CompositeDTO selectedFolderDTO = createCompositeDTO(selectedFolder);
		
		PaginationInfoDTO pagingInfoDTO = new PaginationInfoDTO();
		pagingInfoDTO.setFirstRow(loadConfig.getOffset());
		pagingInfoDTO.setMaxResults(loadConfig.getLimit());
		pagingInfoDTO.setSortingColumn(loadConfig.getSortField());
		pagingInfoDTO.setSortAscending(loadConfig.getSortDir().toString().equalsIgnoreCase("ASC"));

		return new BasePagingLoadResult<CompositeDTO>(selectedFolderDTO.getChildren(), loadConfig.getOffset(), selectedFolderDTO.getChildren().size());
	}

	@Override
	public CompositeDTO moveTreeContent(Integer newParentID, List<Integer> childrenToMove) {
		Composite root = null;
		try {
			root = compositeService.moveTreeContent(newParentID, childrenToMove);
		}catch(DataException de) {
			root = compositeService.getRoot();
			de.printStackTrace();
		}
		return createCompositeDTO(root);
	}

	@Override
	public String displayNode(Integer compositeID) {
		String value = IConstants.EMPTY_STRING;
		try {
			value = compositeService.displayNode(compositeID);
		} catch(DataException de) {
			de.printStackTrace();
		}
		return value;
	}
	
	@Override
	public CompositeDTO assosiateUploadedFile(Integer compositeID, String keyword, String filepath) {
		Composite root = null;
		try {
			root = compositeService.assosiateUploadedFile(compositeID, keyword, filepath);
		} catch(DataException de) {
			root = compositeService.getRoot();
			de.printStackTrace();
		}
		return createCompositeDTO(root);
	}
	
	@Override
	public CompositeDTO updateTreeContent(Integer parentID, List<CompositeDTO> children) {
		List<Composite> childrenEntities = new ArrayList<Composite>();
		for(CompositeDTO compositeDTO : children) {
			childrenEntities.add(createComposite(compositeDTO));
		}
		Composite root = null;
		try{
			root = compositeService.updateComposites(parentID, childrenEntities);
		}catch(DataException de) {
			root = compositeService.getRoot();
			de.printStackTrace();
		}
		return createCompositeDTO(root);
	}

	@Override
	public CompositeDTO addLesson(String name, String description) {
		Composite lesson = compositeService.createLesson(name, description);
		return createCompositeDTO(lesson);
	}


	public static CompositeDTO createCompositeDTO(Composite composite) {
		CompositeDTO dto = new CompositeDTO();
		dto.setId(composite.getId());

		if (composite.getType() != null) {
			String typeEnumName = composite.getType().name();
			ComponentTypeEnum typeEnum = ComponentTypeEnum.valueOf(typeEnumName);
			dto.setType(typeEnum);
		}

		dto.setTitle(composite.getTitle());
		dto.setDescription(composite.getDescription());
		dto.setTerminal(composite.isTerminal());
		dto.setKeyword(composite.getKeyword());
		dto.setFilePath(composite.getFilePath());
		dto.setPresentedBy(composite.getPresentedBy());
		dto.setAssignedTo(composite.getAssignedTo());
		dto.setUrl(composite.getUrl());

		for (Composite child : composite.getChildren()) {
			CompositeDTO childDto = createCompositeDTO(child);
			childDto.setParent(dto);
			dto.getChildren().add(childDto);
		}
		
		return dto;
	}
	
	public static Composite createComposite(CompositeDTO compositeDTO) {
		Composite composite = new Composite();
		composite.setId(compositeDTO.getId());

		if (compositeDTO.getType() != null) {
			String typeEnumName = compositeDTO.getType().name();
			CompositeTypeEnum typeEnum = CompositeTypeEnum.valueOf(typeEnumName);
			composite.setType(typeEnum);
		}

		composite.setTitle(compositeDTO.getTitle());
		composite.setDescription(compositeDTO.getDescription());
		composite.setTerminal(compositeDTO.isTerminal());
		composite.setKeyword(compositeDTO.getKeyword());
		composite.setFilePath(compositeDTO.getFilePath());
		composite.setPresentedBy(compositeDTO.getPresentedBy());
		composite.setAssignedTo(compositeDTO.getAssignedTo());
		composite.setUrl(compositeDTO.getUrl());

		for (CompositeDTO childDTO : compositeDTO.getChildren()) {
			Composite child = createComposite(childDTO);
			child.setParent(composite);
			composite.getChildren().add(child);
		}

		return composite;
	}
}
