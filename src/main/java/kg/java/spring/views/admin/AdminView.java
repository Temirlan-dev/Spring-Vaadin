package kg.java.spring.views.admin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.RolesAllowed;
import kg.java.spring.core.model.entity.Customer;
import kg.java.spring.core.service.CustomerService;
import kg.java.spring.core.service.SeasonCardService;
import kg.java.spring.views.MainLayout;
import kg.java.spring.views.admin.dialog.FormDialog;
import lombok.extern.slf4j.Slf4j;
import java.io.IOException;

@Slf4j
@PageTitle("admin_view")
@Route(value = "admin_view", layout = MainLayout.class)
@RolesAllowed({"ADMIN"})
public class AdminView extends VerticalLayout {
    private final SeasonCardService seasonCardService;
    private final CustomerService customerService;
    private Grid<Customer> customerGrid;
    private TextField nameTextField;

    public AdminView(SeasonCardService seasonCardService,
                     CustomerService customerService) {
        this.seasonCardService = seasonCardService;
        this.customerService = customerService;
        setupComponentUI();
    }

    private void setupComponentUI() {
        nameTextField = buildTextField();
        TextField surenameTextField = buildSurnameTextField();
        customerGrid = buildCustomerGrid();
        Button saveButton = buildSaveButton();

        HorizontalLayout horizontalLayout = new HorizontalLayout(nameTextField, surenameTextField, saveButton);
        Div div = new Div(horizontalLayout);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        div.setWidth("100%");
        add(div);
        add(customerGrid);
    }

    private TextField buildSurnameTextField() {
//        nameTextField = new TextField("Поиск по имени");
        nameTextField.setPlaceholder("Поиск по имени");
        nameTextField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        nameTextField.setClearButtonVisible(true);
        nameTextField.setValueChangeMode(ValueChangeMode.LAZY);
        nameTextField.addValueChangeListener(e -> refreshGridByName());
        return nameTextField;
    }

    private TextField buildTextField() {
        return new TextField("Фильтр");
    }

    private Button buildSaveButton() {
        Button button = new Button(new Icon(VaadinIcon.PLUS));
        button.addClickListener(e -> {
            new FormDialog(seasonCardService, customerService, customerGrid);
        });
        button.addClickShortcut(Key.ENTER);
        button.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return button;
    }

    private Grid<Customer> buildCustomerGrid() {
        Grid<Customer> grid = new Grid<>();
        grid.addColumn(Customer::getName).setHeader("Имя");
        grid.addColumn(Customer::getLastname).setHeader("Фамилия");
        grid.addColumn(c -> c.getCard().getName()).setHeader("Абонимент");
        grid.addColumn(Customer::getStartDate).setHeader("Начало абоним");
        grid.addColumn(Customer::getEndDate).setHeader("Конец абоним");
        grid.addComponentColumn(item -> {
            try {
                return deleteFile(item);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).setHeader("Удалить").setFlexGrow(0).setWidth("150px");

        var customers = customerService.getCustomer();
        grid.setItems(customers);
        return grid;
    }

    private void formDialog(Customer customerItem) throws IOException {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Подтвердите!");

        Button deleteButton = new Button("Удалить");
        deleteButton.addClickListener(event -> {
            customerService.delete(customerItem);
            dialog.close();
            refreshGrid();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отменить", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(deleteButton);
        dialog.open();
    }

    private Button deleteFile(Customer customerItem) throws IOException {
        Button deleteButton = new Button("");
        deleteButton.addClickListener(event -> {
            try {
                formDialog(customerItem);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_ICON,
                ButtonVariant.LUMO_ERROR,
                ButtonVariant.LUMO_TERTIARY);
        deleteButton.setIcon(new Icon(VaadinIcon.TRASH));
        return deleteButton;
    }

    private void refreshGridByName() {
        customerGrid.setItems(customerService.findAllCustomer(nameTextField.getValue()));
    }

    private void refreshGrid() {
        customerGrid.setItems(customerService.getCustomer());
    }
}
