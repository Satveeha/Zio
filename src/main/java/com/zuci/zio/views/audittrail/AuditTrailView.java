package com.zuci.zio.views.audittrail;

import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.zuci.zio.views.main.MainView;

@Route(value = "audit_trail", layout = MainView.class)
@PageTitle("Audit Trail")
@CssImport("styles/views/audittrail/audit-trail-view.css")
public class AuditTrailView extends Div {

    public AuditTrailView() {
        setId("audit-trail-view");
        add(new Label("Content placeholder"));
    }

}
