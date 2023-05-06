package kg.java.spring.views.admin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouteAlias;
import jakarta.annotation.security.RolesAllowed;
import kg.java.spring.core.model.entity.Customer;
import kg.java.spring.core.service.CustomerService;
import kg.java.spring.core.service.SeasonCardService;
import kg.java.spring.views.MainLayout;
import kg.java.spring.views.admin.dialog.FormDialog;

@PageTitle("Hello World")
@Route(value = "hello", layout = MainLayout.class)
@RouteAlias(value = "", layout = MainLayout.class)
@RolesAllowed("ADMIN")
public class AdminView extends VerticalLayout {
    private SeasonCardService seasonCardService;
    private CustomerService customerService;
    private TextField nameTextField;
    private TextField surenameTextField;
    private Grid<Customer> customerGrid;

    public AdminView(SeasonCardService seasonCardService, CustomerService customerService) {
        this.seasonCardService = seasonCardService;
        this.customerService = customerService;
        setupComponentUI();
    }

    private void setupComponentUI() {
        nameTextField = buildTextField();
        surenameTextField = buildSurnameTextField();
        customerGrid = buildCustomerGrid();
        Button saveButton = buildButton();

        HorizontalLayout horizontalLayout = new HorizontalLayout(nameTextField, surenameTextField, saveButton);
        Div div = new Div(horizontalLayout);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        div.setWidth("100%");
        setMargin(true);
        add(div);
        add(customerGrid);
    }

    private TextField buildSurnameTextField() {
        return new TextField("Фильтр");
    }

    private TextField buildTextField() {
        return new TextField("Фильтр");
    }

    private Grid<Customer> buildCustomerGrid() {
        Grid<Customer> grid = new Grid<>();
        grid.addColumn(Customer::getName).setHeader("Имя");
        grid.addColumn(Customer::getLastname).setHeader("Фамилия");
        grid.addColumn(c -> c.getCard().getName()).setHeader("Абонимент");
        grid.addColumn(Customer::getStartDate).setHeader("Начало абоним");
        grid.addColumn(Customer::getEndDate).setHeader("Конец абоним");

        var customers = customerService.getCustomer();
        grid.setItems(customers);
        return grid;
    }

    private Button buildButton() {
        Button button = new Button(new Icon(VaadinIcon.PLUS));
        button.addClickListener(e -> {
            new FormDialog(seasonCardService, customerService, customerGrid);
        });
        button.addClickShortcut(Key.ENTER);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }
}
