package com.education.lessons.ui.client.mvc;

import com.education.lessons.ui.client.utils.IConstants;
import com.education.lessons.ui.client.utils.Utils;
import com.extjs.gxt.themes.client.Slate;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.util.ThemeManager;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HtmlContainer;
import com.extjs.gxt.ui.client.widget.Viewport;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.ui.RootPanel;

public class LessonsView extends View {

	private Viewport viewport;
	private HtmlContainer northPanel;
	private ContentPanel centerPanel;
	private ContentPanel westPanel;

	public LessonsView(Controller controller) {
		super(controller);
	}

	@Override
	protected void initialize() {
		viewport = new Viewport();
		viewport.setLayoutOnChange(true);
		viewport.setLayout(new BorderLayout());
		Registry.register(IConstants.VIEWPORT, viewport);
		ThemeManager.register(Slate.SLATE);
	
		createNorth();
		createWest();
		createCenter();

		RootPanel.get().add(viewport);
	}

	private void createCenter() {
		centerPanel = new ContentPanel();
		centerPanel.setBorders(false);
		centerPanel.setHeaderVisible(false);
		centerPanel.setLayout(new FitLayout());
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.CENTER);
		data.setMargins(new Margins(5, 5, 5, 0));
		viewport.add(centerPanel, data);
		Registry.register(IConstants.CENTER_PANEL, centerPanel);
	}

	private void createWest() {
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.WEST, 228, 150, 320);
		data.setMargins(new Margins(5, 5, 5, 5));
		data.setCollapsible(true);
		westPanel = new ContentPanel();
		viewport.add(westPanel, data);
		Registry.register(IConstants.WEST_PANEL, westPanel);
	}

	private void createNorth() {
		StringBuilder sb = new StringBuilder();
		sb.append("<table width='100%'><tr><td width='80%'><div class=\"lessons-title\">")
				.append("Lessons Management")
				.append("</div></td><td>"+getDescription()+"<td><div class=\"lessons-title\"><a style='color:white; text-decoration:underline;' href='"+Utils.getContextPath()+"/app/login/openid/logout'>logout</a></div></td></tr></table>");
		northPanel = new HtmlContainer(sb.toString());
		northPanel.setStateful(false);
		northPanel.addStyleName("x-small-editor");
		BorderLayoutData data = new BorderLayoutData(LayoutRegion.NORTH, 33);
		data.setMargins(new Margins());
		viewport.add(northPanel, data);
		Registry.register(IConstants.NORTH_PANEL, northPanel);
	}

	private String getDescription() {
		StringBuffer sb = new StringBuffer();
		if(Utils.isAdminUser()) 
		{
			sb.append("<td><div class=\"lessons-title\">")
			  .append(" Login as <b>ADMIN</b> ")
			  .append("</div></td>");
		}
		else if(Utils.userExists() && !Utils.isAdminUser()) {
			sb.append("<td><div class=\"lessons-title\">")
			  .append(" Login as <b>USER</b> ")
			  .append("</div></td>");
		}
		if(sb.length() > 0) return sb.toString();
		return " ";
	}

	@Override
	protected void handleEvent(AppEvent event) { }
}
