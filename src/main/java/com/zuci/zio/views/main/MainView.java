package com.zuci.zio.views.main;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.views.audittrail.AuditTrailView;
import com.zuci.zio.views.channel.ChannelView;
import com.zuci.zio.views.commonconfigview.CommonConfigView;
import com.zuci.zio.views.edit.EditView;
import com.zuci.zio.views.pipeline.PipelineView;
import com.zuci.zio.views.runconsole.RunConsoleView;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "Zio", shortName = "Zio")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
@CssImport(value = "./styles/app-layout.css", themeFor = "vaadin-app-layout")
//@CssImport(value = "./styles/my-app-layout.css", themeFor = "vaadin-app-layout")
@CssImport(value = "./styles/navigation-view-layout.css")
@CssImport(value = "./styles/menubar.css", themeFor = "vaadin-menu-bar")
public class MainView extends AppLayout {

	public MainView(CommonConfigDao commonConfigDao) {

		setPrimarySection(Section.DRAWER);
		// addToNavbar(true, new DrawerToggle());

		setId("main-view");
		Label logoLabel = new Label();
		logoLabel.setText("ZIO");
		addToNavbar(true, logoLabel);
//		logoLabel.addClassName("nav-drawer-menu");
		logoLabel.getStyle().set("text-align", "center");
		logoLabel.getStyle().set("margin-left", "auto");
		logoLabel.getStyle().set("margin-right", "auto");
		logoLabel.getStyle().set("color", "#4b483f !important");
	    logoLabel.getStyle().set("font-weight", "bold");

		addToDrawer(getMenuBar());
	}

	private Component getMenuBar() {
		VerticalLayout scrollableLayout = new VerticalLayout();
		scrollableLayout.setHeight("100%");
		scrollableLayout.addClassName("nav-drawer");
		scrollableLayout.setHeightFull();
		scrollableLayout.setPadding(false);
		scrollableLayout.setSpacing(false);
		

		VerticalLayout image_layout = new VerticalLayout();
		image_layout.setPadding(false);
		image_layout.setMargin(false);
		image_layout.getStyle().set("margin-bottom", "-50px");
		Image sidenavimage = new Image();
		sidenavimage.setSrc("/icons/zio.png");
		sidenavimage.setWidth("55%");
		sidenavimage.setHeight("60%");
		sidenavimage.getStyle().set("margin-left", "25px");
		sidenavimage.getStyle().set("border-radius", "50%");
		sidenavimage.getStyle().set("padding", "15px");
		image_layout.add(sidenavimage);

		Label packageLabel = new Label();
		Label defineLabel = new Label();
		defineLabel.setText("Define");
		defineLabel.addClassName("nav-drawer-menus");

		Label editLabel = new Label();
		editLabel.setText("Edit");
		editLabel.getStyle().set("cursor", "pointer");
		editLabel.getStyle().set("margin-top", "10px");
		editLabel.getStyle().set("margin-left", "50px");
		editLabel.addClassName("nav-drawer-menus");

		Label configLabel = new Label();
		configLabel.setText("Config");
		configLabel.getStyle().set("margin-top", "10px");
		configLabel.getStyle().set("cursor", "pointer");
		configLabel.addClassName("nav-drawer-menus");

		Label commonsLabel = new Label();
		commonsLabel.setText("Commons");
		commonsLabel.getStyle().set("margin-top", "10px");
		commonsLabel.getStyle().set("margin-left", "50px");
		commonsLabel.getStyle().set("cursor", "pointer");
		commonsLabel.addClassName("nav-drawer-menus");

		Label pipelineLabel = new Label();
		pipelineLabel.setText("Pipeline");
		pipelineLabel.getStyle().set("margin-top", "10px");
		pipelineLabel.getStyle().set("margin-left", "50px");
		pipelineLabel.getStyle().set("cursor", "pointer");
		pipelineLabel.addClassName("nav-drawer-menus");

		Label ChannelsLabel = new Label();
		ChannelsLabel.setText("Channels");
		ChannelsLabel.getStyle().set("margin-top", "10px");
		ChannelsLabel.getStyle().set("margin-left", "50px");
		ChannelsLabel.getStyle().set("cursor", "pointer");
		ChannelsLabel.addClassName("nav-drawer-menus");

		Label manageResourceLabel = new Label();
		manageResourceLabel.setText("Manage Resource");
		ChannelsLabel.getStyle().set("margin-top", "10px");
		manageResourceLabel.getStyle().set("cursor", "pointer");
		manageResourceLabel.addClassName("nav-drawer-menus");

		Label addOrModifyRDBMSLabel = new Label();
		addOrModifyRDBMSLabel.setText("Add/Modify RDBMS");
		addOrModifyRDBMSLabel.getStyle().set("margin-top", "10px");
		addOrModifyRDBMSLabel.getStyle().set("margin-left", "50px");
		addOrModifyRDBMSLabel.getStyle().set("cursor", "pointer");
		addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");

		Label manageOrRunChannelLabel = new Label();
		manageOrRunChannelLabel.setText("Manage/Run Channel");
		manageOrRunChannelLabel.getStyle().set("margin-top", "10px");
		manageOrRunChannelLabel.getStyle().set("cursor", "pointer");
		manageOrRunChannelLabel.addClassName("nav-drawer-menus");

		Label auditTrailLabel = new Label();
		auditTrailLabel.setText("Audit Trail");
		auditTrailLabel.getStyle().set("margin-top", "10px");
		auditTrailLabel.getStyle().set("margin-left", "50px");
		auditTrailLabel.getStyle().set("cursor", "pointer");
		auditTrailLabel.addClassName("nav-drawer-menus");

		Label runConsoleLabel = new Label();
		runConsoleLabel.setText("Run Console");
		runConsoleLabel.getStyle().set("margin-top", "10px");
		runConsoleLabel.getStyle().set("margin-left", "50px");
		runConsoleLabel.getStyle().set("cursor", "pointer");
		runConsoleLabel.addClassName("nav-drawer-menus");

		Div edit_div = new Div(editLabel);
		edit_div.setWidthFull();
		edit_div.getStyle().set("margin-top", "15px");
		edit_div.addClickListener(event -> {
			try {
				editLabel.addClassName("nav-drawer-menu");
				editLabel.removeClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menu");
				defineLabel.removeClassName("nav-drawer-menus");
				configLabel.addClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				manageOrRunChannelLabel.addClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(EditView.class);
				// setContent(new EditView().gridView());

			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div configCommonsDiv = new Div(commonsLabel);
		configCommonsDiv.setWidthFull();
		configCommonsDiv.getStyle().set("margin-top", "15px");
		configCommonsDiv.addClickListener(event -> {
			try {
				configLabel.addClassName("nav-drawer-menu");
				configLabel.removeClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menu");
				commonsLabel.removeClassName("nav-drawer-menus");
				editLabel.addClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				manageOrRunChannelLabel.addClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(CommonConfigView.class);
				// setContent(new CommonConfigView(commonConfigDao));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div configPipelineDiv = new Div(pipelineLabel);
		configPipelineDiv.setWidthFull();
		configPipelineDiv.getStyle().set("margin-top", "15px");
		configPipelineDiv.addClickListener(event -> {
			try {
				configLabel.addClassName("nav-drawer-menu");
				configLabel.removeClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menu");
				pipelineLabel.removeClassName("nav-drawer-menus");
				editLabel.addClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				manageOrRunChannelLabel.addClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(PipelineView.class);
				// setContent(new PipelineView(pipelineConfigDao).pipeLineComponent());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div configChannelDiv = new Div(ChannelsLabel);
		configChannelDiv.setWidthFull();
		configChannelDiv.getStyle().set("margin-top", "15px");
		configChannelDiv.addClickListener(event -> {
			try {
				configLabel.addClassName("nav-drawer-menu");
				configLabel.removeClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menu");
				ChannelsLabel.removeClassName("nav-drawer-menus");
				editLabel.addClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				manageOrRunChannelLabel.addClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(ChannelView.class);
				// setContent(new ChannelView(channelConfigDao).channelViewComponent());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div auditTrailDiv = new Div(auditTrailLabel);
		auditTrailDiv.setWidthFull();
		auditTrailDiv.getStyle().set("margin-top", "15px");
		auditTrailDiv.addClickListener(event -> {
			try {
				manageOrRunChannelLabel.addClassName("nav-drawer-menu");
				manageOrRunChannelLabel.removeClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menu");
				auditTrailLabel.removeClassName("nav-drawer-menus");
				editLabel.addClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				configLabel.addClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(AuditTrailView.class);
				// setContent(new AuditTrailView().auditTrailView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div runConsoleDiv = new Div(runConsoleLabel);
		runConsoleDiv.setWidthFull();
		runConsoleDiv.getStyle().set("margin-top", "15px");
		runConsoleDiv.addClickListener(event -> {
			try {
				manageOrRunChannelLabel.addClassName("nav-drawer-menu");
				manageOrRunChannelLabel.removeClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menu");
				runConsoleLabel.removeClassName("nav-drawer-menus");
				editLabel.addClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				configLabel.addClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(RunConsoleView.class);
				// setContent(new RunConsoleView().runConsoleView());
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		scrollableLayout.add(image_layout, defineLabel, edit_div, configLabel, configCommonsDiv, configPipelineDiv,
				configChannelDiv, manageResourceLabel, addOrModifyRDBMSLabel, manageOrRunChannelLabel, auditTrailDiv,
				runConsoleDiv);
		return scrollableLayout;

	}

	private static Tab createTab(String title, Class<? extends Component> viewClass) {
		return createTab(populateLink(new RouterLink(null, viewClass), title));
	}

	private static Tab createTab(Component content) {
		final Tab tab = new Tab();
		tab.add(content);
		return tab;
	}

	private static <T extends HasComponents> T populateLink(T a, String title) {
		a.add(title);
		return a;
	}

	@Override
	protected void afterNavigation() {
		super.afterNavigation();
		selectTab();
	}

	private void selectTab() {
		/*
		 * String target =
		 * RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
		 * Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
		 * Component child = tab.getChildren().findFirst().get(); return child
		 * instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
		 * }).findFirst(); tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
		 */

//		String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
//		Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
//			Component child = tab.getChildren().findFirst().get().getChildren().findFirst().get();
//			return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
//		}).findFirst();
//		tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
	}
}