package com.zuci.zio.views.main;

import org.springframework.beans.factory.annotation.Autowired;

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
import com.zuci.zio.dao.ChannelConfigDao;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.dao.PipelineConfigDao;
import com.zuci.zio.views.audittrail.AuditTrailView;
import com.zuci.zio.views.channel.ChannelView;
import com.zuci.zio.views.common.CommonView;
import com.zuci.zio.views.pipeline.PipelineView;
import com.zuci.zio.views.pipelineDefinition.PipelineDefinitionView;
import com.zuci.zio.views.runconsole.RunConsoleView;
import com.zuci.zio.views.upload.UploadScreenOne;
import com.zuci.zio.views.upload.UploadScreenTwo;

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

	@Autowired
	private static PipelineConfigDao pipelineConfigDao;
	
	@Autowired
	private static CommonConfigDao commonConfigDao;
	
	@Autowired
	private static ChannelConfigDao channelConfigDao;
	
	public MainView(PipelineConfigDao pipelineConfigDao, CommonConfigDao commonConfigDao, ChannelConfigDao channelConfigDao) {
		
		this.pipelineConfigDao = pipelineConfigDao;
		this.commonConfigDao = commonConfigDao;
		this.channelConfigDao = channelConfigDao;

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

		VerticalLayout logoName = new VerticalLayout();
		Label logoLabel = new Label();
		logoLabel.setText("ZIO");
		logoLabel.getStyle().set("color", "#f8ca34");
		logoLabel.getStyle().set("font-size", "30px");
		logoLabel.getStyle().set("font-weight", "1000");
		logoLabel.getStyle().set("text-align", "center");
		logoLabel.getStyle().set("justify-content", "center");
		logoLabel.getStyle().set("margin-left", "auto");
		logoLabel.getStyle().set("margin-right", "auto");
		logoLabel.getStyle().set("margin-top", "-10px");

		Image sidenavimage = new Image();
		sidenavimage.setSrc("/icons/globe.svg");
		sidenavimage.setWidth("26%");
		sidenavimage.setHeight("52%");
		sidenavimage.getStyle().set("margin-left", "auto");
		sidenavimage.getStyle().set("margin-right", "auto");
		sidenavimage.getStyle().set("border-radius", "50%");
		sidenavimage.getStyle().set("padding", "8px");
		image_layout.add(sidenavimage);
		logoName.add(image_layout,logoLabel);

		Label defineLabel = new Label();
		defineLabel.setText("Make Pipeline");
		defineLabel.getStyle().set("font-size", "18px");
		defineLabel.addClassName("nav-drawer-menus");

		Label create = new Label();
		create.setText("Create");
		create.getStyle().set("font-size", "15px");
		create.getStyle().set("cursor", "pointer");
		create.getStyle().set("margin-top", "8px");
		create.getStyle().set("margin-left", "50px");
		create.addClassName("nav-drawer-menus");
		
		
		Label upload = new Label(); 
		upload.setText("Upload");
		upload.getStyle().set("font-size", "15px");
		upload.getStyle().set("cursor", "pointer");
		upload.getStyle().set("margin-top", "8px");
		upload.getStyle().set("margin-left", "50px");
		upload.addClassName("nav-drawer-menus");
		 
		
		Label drafts = new Label();
		drafts.setText("Drafts");
		drafts.getStyle().set("font-size", "15px");
		drafts.getStyle().set("cursor", "pointer");
		drafts.getStyle().set("margin-top", "8px");
		drafts.getStyle().set("margin-left", "50px");
		drafts.addClassName("nav-drawer-menus");
		
		Label browse = new Label();
		browse.setText("Browse");
		browse.getStyle().set("font-size", "15px");
		browse.getStyle().set("cursor", "pointer");
		browse.getStyle().set("margin-top", "8px");
		browse.getStyle().set("margin-left", "50px");
		browse.addClassName("nav-drawer-menus");
		
		Label configLabel = new Label();
		configLabel.setText("Config");
		configLabel.getStyle().set("font-size", "18px");
		configLabel.getStyle().set("margin-top", "8px");
		configLabel.getStyle().set("cursor", "pointer");
		configLabel.addClassName("nav-drawer-menus");

		Label commonsLabel = new Label();
		commonsLabel.setText("Commons");
		commonsLabel.getStyle().set("font-size", "15px");
		commonsLabel.getStyle().set("margin-top", "8px");
		commonsLabel.getStyle().set("margin-left", "50px");
		commonsLabel.getStyle().set("cursor", "pointer");
		commonsLabel.addClassName("nav-drawer-menus");

		Label pipelineLabel = new Label();
		pipelineLabel.setText("Pipeline");
		pipelineLabel.getStyle().set("font-size", "15px");
		pipelineLabel.getStyle().set("margin-top", "8px");
		pipelineLabel.getStyle().set("margin-left", "50px");
		pipelineLabel.getStyle().set("cursor", "pointer");
		pipelineLabel.addClassName("nav-drawer-menus");

		Label ChannelsLabel = new Label();
		ChannelsLabel.setText("Channels");
		ChannelsLabel.getStyle().set("font-size", "15px");
		ChannelsLabel.getStyle().set("margin-top", "8px");
		ChannelsLabel.getStyle().set("margin-left", "50px");
		ChannelsLabel.getStyle().set("cursor", "pointer");
		ChannelsLabel.addClassName("nav-drawer-menus");

		Label manageResourceLabel = new Label();
		manageResourceLabel.setText("Manage Resource");
		manageResourceLabel.getStyle().set("font-size", "18px");
		manageResourceLabel.getStyle().set("margin-top", "8px");
		manageResourceLabel.getStyle().set("cursor", "pointer");
		manageResourceLabel.addClassName("nav-drawer-menus");

		Label addOrModifyRDBMSLabel = new Label();
		addOrModifyRDBMSLabel.setText("Add/Modify RDBMS");
		addOrModifyRDBMSLabel.getStyle().set("font-size", "15px");
		addOrModifyRDBMSLabel.getStyle().set("margin-top", "8px");
		addOrModifyRDBMSLabel.getStyle().set("margin-left", "50px");
		addOrModifyRDBMSLabel.getStyle().set("cursor", "pointer");
		addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");

		Label manageOrRunChannelLabel = new Label();
		manageOrRunChannelLabel.setText("Manage/Run Channel");
		manageOrRunChannelLabel.getStyle().set("font-size", "18px");
		manageOrRunChannelLabel.getStyle().set("margin-top", "8px");
		manageOrRunChannelLabel.getStyle().set("cursor", "pointer");
		manageOrRunChannelLabel.addClassName("nav-drawer-menus");

		Label auditTrailLabel = new Label();
		auditTrailLabel.setText("Audit Trail");
		auditTrailLabel.getStyle().set("font-size", "15px");
		auditTrailLabel.getStyle().set("margin-top", "8px");
		auditTrailLabel.getStyle().set("margin-left", "50px");
		auditTrailLabel.getStyle().set("cursor", "pointer");
		auditTrailLabel.addClassName("nav-drawer-menus");

		Label runConsoleLabel = new Label();
		runConsoleLabel.setText("Run Console");
		runConsoleLabel.getStyle().set("font-size", "15px");
		runConsoleLabel.getStyle().set("margin-top", "8px");
		runConsoleLabel.getStyle().set("margin-left", "50px");
		runConsoleLabel.getStyle().set("cursor", "pointer");
		runConsoleLabel.addClassName("nav-drawer-menus");

		Div createDiv = new Div(create);
		createDiv.setWidthFull();
		createDiv.getStyle().set("margin-top", "8px");
		createDiv.addClickListener(event -> {
			try {
				create.addClassName("nav-drawer-menu");
				create.removeClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				upload.removeClassName("nav-drawer-menu");
				drafts.addClassName("nav-drawer-menus");
				drafts.removeClassName("nav-drawer-menu");
				browse.addClassName("nav-drawer-menus");
				browse.removeClassName("nav-drawer-menu");
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
				getUI().get().navigate(PipelineDefinitionView.class);
				// setContent(new EditView().gridView());

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		Div uploadDiv = new Div(upload);
		uploadDiv.setWidthFull();
		uploadDiv.getStyle().set("margin-top", "8px");
		uploadDiv.addClickListener(event -> {
			try {
				upload.addClassName("nav-drawer-menu");
				upload.removeClassName("nav-drawer-menus");
				create.addClassName("nav-drawer-menus");
				create.removeClassName("nav-drawer-menu");
				drafts.addClassName("nav-drawer-menus");
				drafts.removeClassName("nav-drawer-menu");
				browse.addClassName("nav-drawer-menus");
				browse.removeClassName("nav-drawer-menu");
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
				setContent(new UploadScreenOne(this.pipelineConfigDao,this.commonConfigDao,this.channelConfigDao,""));
				//getUI().get().navigate(UploadScreenOne.class);
				// setContent(new EditView().gridView());

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		Div draftsDiv = new Div(drafts);
		draftsDiv.setWidthFull();
		draftsDiv.getStyle().set("margin-top", "8px");
		draftsDiv.addClickListener(event -> {
			try {
				drafts.addClassName("nav-drawer-menu");
				drafts.removeClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				upload.removeClassName("nav-drawer-menu");
				create.addClassName("nav-drawer-menus");
				create.removeClassName("nav-drawer-menu");
				browse.addClassName("nav-drawer-menus");
				browse.removeClassName("nav-drawer-menu");
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
				getUI().get().navigate(PipelineDefinitionView.class);
				// setContent(new EditView().gridView());

			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		
		Div browseDiv = new Div(browse);
		browseDiv.setWidthFull();
		browseDiv.getStyle().set("margin-top", "8px");
		browseDiv.addClickListener(event -> {
			try {
				browse.addClassName("nav-drawer-menu");
				browse.removeClassName("nav-drawer-menus");
				drafts.addClassName("nav-drawer-menus");
				drafts.removeClassName("nav-drawer-menu");
				upload.addClassName("nav-drawer-menus");
				upload.removeClassName("nav-drawer-menu");
				create.addClassName("nav-drawer-menus");
				create.removeClassName("nav-drawer-menu");
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
				getUI().get().navigate(PipelineDefinitionView.class);
				// setContent(new EditView().gridView());

			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div configCommonsDiv = new Div(commonsLabel);
		configCommonsDiv.setWidthFull();
		configCommonsDiv.getStyle().set("margin-top", "8px");
		configCommonsDiv.addClickListener(event -> {
			try {
				configLabel.addClassName("nav-drawer-menu");
				configLabel.removeClassName("nav-drawer-menus");
				commonsLabel.addClassName("nav-drawer-menu");
				commonsLabel.removeClassName("nav-drawer-menus");
				create.addClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				drafts.addClassName("nav-drawer-menus");
				browse.addClassName("nav-drawer-menus");
				defineLabel.addClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menus");
				manageResourceLabel.addClassName("nav-drawer-menus");
				addOrModifyRDBMSLabel.addClassName("nav-drawer-menus");
				manageOrRunChannelLabel.addClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menus");
				getUI().get().navigate(CommonView.class);
				// setContent(new CommonView(commonConfigDao));
			} catch (Exception e) {
				e.printStackTrace();
			}
		});

		Div configPipelineDiv = new Div(pipelineLabel);
		configPipelineDiv.setWidthFull();
		configPipelineDiv.getStyle().set("margin-top", "8px");
		configPipelineDiv.addClickListener(event -> {
			try {
				configLabel.addClassName("nav-drawer-menu");
				configLabel.removeClassName("nav-drawer-menus");
				pipelineLabel.addClassName("nav-drawer-menu");
				pipelineLabel.removeClassName("nav-drawer-menus");
				create.addClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				drafts.addClassName("nav-drawer-menus");
				browse.addClassName("nav-drawer-menus");
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
		configChannelDiv.getStyle().set("margin-top", "8px");
		configChannelDiv.addClickListener(event -> {
			try {
				configLabel.addClassName("nav-drawer-menu");
				configLabel.removeClassName("nav-drawer-menus");
				ChannelsLabel.addClassName("nav-drawer-menu");
				ChannelsLabel.removeClassName("nav-drawer-menus");
				create.addClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				drafts.addClassName("nav-drawer-menus");
				browse.addClassName("nav-drawer-menus");
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
		auditTrailDiv.getStyle().set("margin-top", "8px");
		auditTrailDiv.addClickListener(event -> {
			try {
				manageOrRunChannelLabel.addClassName("nav-drawer-menu");
				manageOrRunChannelLabel.removeClassName("nav-drawer-menus");
				auditTrailLabel.addClassName("nav-drawer-menu");
				auditTrailLabel.removeClassName("nav-drawer-menus");
				create.addClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				drafts.addClassName("nav-drawer-menus");
				browse.addClassName("nav-drawer-menus");
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
		runConsoleDiv.getStyle().set("margin-top", "8px");
		runConsoleDiv.addClickListener(event -> {
			try {
				manageOrRunChannelLabel.addClassName("nav-drawer-menu");
				manageOrRunChannelLabel.removeClassName("nav-drawer-menus");
				runConsoleLabel.addClassName("nav-drawer-menu");
				runConsoleLabel.removeClassName("nav-drawer-menus");
				create.addClassName("nav-drawer-menus");
				upload.addClassName("nav-drawer-menus");
				drafts.addClassName("nav-drawer-menus");
				browse.addClassName("nav-drawer-menus");
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

		scrollableLayout.add(logoName, defineLabel, createDiv, uploadDiv, draftsDiv, browseDiv, configLabel, configCommonsDiv,
				configPipelineDiv, configChannelDiv, manageResourceLabel, addOrModifyRDBMSLabel,
				manageOrRunChannelLabel, auditTrailDiv, runConsoleDiv);
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