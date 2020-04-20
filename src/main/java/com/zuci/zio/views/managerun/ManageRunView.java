package com.zuci.zio.views.managerun;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;

@Route(value = "2", layout = MainView.class)
@PageTitle("Manage/Run")
@CssImport("styles/views/managerun/manage-run-view.css")
public class ManageRunView extends Div {

    public ManageRunView() {
        setId("manage-run-view");
        add(new Label("Content placeholder"));
    }

}
