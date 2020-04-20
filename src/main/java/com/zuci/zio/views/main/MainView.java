package com.zuci.zio.views.main;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.HasComponents;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.TabVariant;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.component.tabs.TabsVariant;
import com.vaadin.flow.router.RouteConfiguration;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.Theme;import com.vaadin.flow.theme.lumo.Lumo;

import com.zuci.zio.views.main.MainView;
import com.zuci.zio.views.define.DefineView;
import com.zuci.zio.views.edit.EditView;
import com.zuci.zio.views.config.ConfigView;
import com.zuci.zio.views.commonconfigview.CommonConfigView;
import com.zuci.zio.views.pipeline.PipelineView;
import com.zuci.zio.views.managerun.ManageRunView;
import com.zuci.zio.dao.CommonConfigDao;
import com.zuci.zio.model.CommonConfig;
import com.zuci.zio.views.audittrail.AuditTrailView;
import com.zuci.zio.views.channel.ChannelView;
import com.zuci.zio.views.runconsole.RunConsoleView;

/**
 * The main view is a top-level placeholder for other views.
 */
@JsModule("./styles/shared-styles.js")
@PWA(name = "Zio", shortName = "Zio")
@Theme(value = Lumo.class, variant = Lumo.LIGHT)
public class MainView extends AppLayout {

    private final Tabs menu;

    public MainView(CommonConfigDao commonConfigDao) {
        setPrimarySection(Section.DRAWER);
        addToNavbar(true, new DrawerToggle());
        menu = createMenuTabs();
        addToDrawer(menu);
    }

    private static Tabs createMenuTabs() {
        final Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.addThemeVariants(TabsVariant.LUMO_MINIMAL);
        tabs.setId("tabs");
        tabs.add(getAvailableTabs());
        //tabs.addComponentAtIndex(0, new Label("Define"));
        //tabs.addComponentAtIndex(2, new Label("Config"));
        //tabs.addComponentAtIndex(6, new Label("Manage/Run"));
        return tabs;
    }

    private static Tab[] getAvailableTabs() {
        final List<Tab> tabs = new ArrayList<>();
        tabs.add(createTab("Define", DefineView.class));
        tabs.add(createTab("Edit", EditView.class));
        tabs.add(createTab("Config", ConfigView.class));
        tabs.add(createTab("Commons", CommonConfigView.class));
        tabs.add(createTab("Pipeline", PipelineView.class));
        tabs.add(createTab("Channels", ChannelView.class));
        tabs.add(createTab("Manage/Run", ManageRunView.class));
        tabs.add(createTab("Audit Trail", AuditTrailView.class));
        tabs.add(createTab("Run Console", RunConsoleView.class));
        return tabs.toArray(new Tab[tabs.size()]);
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
        /*String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));*/
        
        String target = RouteConfiguration.forSessionScope().getUrl(getContent().getClass());
        Optional<Component> tabToSelect = menu.getChildren().filter(tab -> {
            Component child = tab.getChildren().findFirst().get().getChildren().findFirst().get();
            return child instanceof RouterLink && ((RouterLink) child).getHref().equals(target);
        }).findFirst();
        tabToSelect.ifPresent(tab -> menu.setSelectedTab((Tab) tab));
    }
}
