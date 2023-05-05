package kg.java.spring.views.admin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import kg.java.spring.views.MainLayout;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends HorizontalLayout {
    private TextField nameTextField;
    private TextField surenameTextField;

    public AdminView() {
        setupComponentUI();
    }

    private void setupComponentUI() {
        nameTextField = buildTextField();
        surenameTextField = buildSurnameTextField();
        Button saveButton = buildButton();

        setMargin(true);

        HorizontalLayout horizontalLayout = new HorizontalLayout(nameTextField, surenameTextField, saveButton);
        Div div = new Div(horizontalLayout);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        div.setWidth("100%");
        add(div);
    }

    private Button buildButton() {
        Button button = new Button("save");
        button.addClickListener(e -> {
            Notification.show("Hello " + nameTextField.getValue());
        });
        button.addClickShortcut(Key.ENTER);
        button.setThemeName("primary");
        return button;
    }

    private TextField buildSurnameTextField() {
        return new TextField("Your surname");
    }

    private TextField buildTextField() {
        return new TextField("Your name");
    }
}
