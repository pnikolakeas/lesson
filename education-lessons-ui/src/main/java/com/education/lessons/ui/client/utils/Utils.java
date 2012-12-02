package com.education.lessons.ui.client.utils;

import java.util.ArrayList;
import java.util.List;

import com.education.lessons.viewmodel.core.CompositeDTO;
import com.education.lessons.viewmodel.type.ComponentTypeEnum;
import com.extjs.gxt.ui.client.data.BeanModel;
import com.extjs.gxt.ui.client.data.BeanModelFactory;
import com.extjs.gxt.ui.client.data.BeanModelLookup;

public class Utils {

	/**
	 * Wrap the objects contained in a list with BeanModel objects.
	 * 
	 * @param <T>
	 * @param list
	 * @param objectClass
	 * @return
	 */
	@SuppressWarnings("rawtypes") 
	public static <T> List<BeanModel> getBeanModelListStore(List<T> list, Class objectClass) {
		List<BeanModel> beanModelList = new ArrayList<BeanModel>();
		if (list != null && !list.isEmpty()) {
			BeanModelFactory factory = BeanModelLookup.get().getFactory(objectClass);
			for (T t : list) {
				BeanModel beanModel = factory.createModel(t);
				beanModelList.add(beanModel);
			}
		}
		return beanModelList;
	}

	@SuppressWarnings("rawtypes")
	public static <T> BeanModel getBeanModel(T bean, Class objectClass) {
		BeanModel beanModel=null;
		if (bean != null) {
			BeanModelFactory factory = BeanModelLookup.get().getFactory(objectClass);
			beanModel = factory.createModel(bean);
		}
		return beanModel;
	}

	public static void printUpdatedGraph(CompositeDTO removedFrom) {
		CompositeDTO root = removedFrom.getRoot();
		root.print(0);
	}

	public static boolean isEmptyOrNullString(String input) {
		return IConstants.EMPTY_STR.equals(input) || input == null;
	}

	public static boolean notEmptyAndNullString(String input) {
		return !IConstants.EMPTY_STR.equals(input) && input != null;
	}

	public static ComponentTypeEnum resolveComponentDTOType(CompositeDTO componentDTO) {
		if(componentDTO == null || componentDTO.getType() == null)
			return null;
		String valueOfType = componentDTO.getType().getValue();
		return ComponentTypeEnum.getEnumByStringValue(valueOfType);
	}

	public static String resolveComponentDTONameOfType(CompositeDTO componentDTO) {
		ComponentTypeEnum type = resolveComponentDTOType(componentDTO);
		if(type == null)
			return null;
		return type.getValue();
	}

	public static List<Integer> getListOfIDs(List<CompositeDTO> childrenToMove) {
		List<Integer> ids = new ArrayList<Integer>();
		for(CompositeDTO child : childrenToMove) {
			if(child != null && child.getId() != null)
				ids.add(child.getId());
		}
		return ids;
	}
	
	public static native boolean userExists()
	/*-{
		return $wnd.contextUser != undefined;
	}-*/;

	public static native boolean isAdminUser()
	/*-{
		return $wnd.contextUser != undefined && $wnd.contextUser.isAdmin == "true";
	}-*/;

	public static native boolean isSimpleUser()
	/*-{
		return $wnd.contextUser != undefined && $wnd.contextUser.isAdmin == "false";
	}-*/;
	
	public static native String getContextPath()
	/*-{
		return $wnd.contextPath;
	}-*/;
}
