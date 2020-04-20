package com.zuci.zio.views.define;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import com.zuci.zio.views.main.MainView;

@Route(value = "3", layout = MainView.class)
@RouteAlias(value = "", layout = MainView.class)
@PageTitle("Define")
@CssImport("styles/views/define/define-view.css")
public class DefineView extends Div {

    public DefineView() {
        setId("define-view");
        add(new Label("Content placeholder"));
    }

}
