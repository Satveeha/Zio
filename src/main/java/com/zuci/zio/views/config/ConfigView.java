package com.zuci.zio.views.config;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;

@Route(value = "1", layout = MainView.class)
@PageTitle("Config")
@CssImport("styles/views/config/config-view.css")
public class ConfigView extends Div {

    public ConfigView() {
        setId("config-view");
        add(new Label("Content placeholder"));
    }

}
