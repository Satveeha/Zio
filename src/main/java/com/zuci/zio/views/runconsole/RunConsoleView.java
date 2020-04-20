package com.zuci.zio.views.runconsole;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;

@Route(value = "run_console", layout = MainView.class)
@PageTitle("Run Console")
@CssImport("styles/views/runconsole/run-console-view.css")
public class RunConsoleView extends Div {

    public RunConsoleView() {
        setId("run-console-view");
        add(new Label("Content placeholder"));
    }

}
