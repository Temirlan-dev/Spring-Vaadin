package kg.java.spring.views.admin;

import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.data.renderer.LitRenderer;
import com.vaadin.flow.data.renderer.Renderer;
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
import java.util.stream.Stream;

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
        nameTextField = buildSurnameTextField();
        customerGrid = buildCustomerGrid();
        Button saveButton = buildSaveButton();
        HorizontalLayout horizontalLayout = new HorizontalLayout(nameTextField, saveButton);
        Div div = new Div(horizontalLayout);
        horizontalLayout.setDefaultVerticalComponentAlignment(Alignment.BASELINE);
        div.setWidth("100%");
        add(div);
        add(customerGrid);
    }

    private TextField buildSurnameTextField() {
        TextField textField = new TextField();
        textField.setPlaceholder("Поиск по имени");
        textField.setPrefixComponent(new Icon(VaadinIcon.SEARCH));
        textField.setClearButtonVisible(true);
        textField.setValueChangeMode(ValueChangeMode.LAZY);
        textField.addValueChangeListener(e -> refreshGridByName());
        return textField;
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
        }).setHeader("Удалить").setFlexGrow(0).setWidth("100px");
        grid.addColumn(createToggleDetailsRenderer(grid)).setWidth("100px");

        grid.setDetailsVisibleOnClick(false);
        grid.setItemDetailsRenderer(createPersonDetailsRenderer());
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        var customers = customerService.getCustomer();
        grid.setItems(customers);
        grid.setHeight("600px");
        return grid;
    }

    private static Renderer<Customer> createToggleDetailsRenderer(
            Grid<Customer> grid) {
        return LitRenderer.<Customer> of(
                        "<vaadin-button theme=\"tertiary\" @click=\"${handleClick}\">Изменить</vaadin-button>")
                .withFunction("handleClick",
                        person -> grid.setDetailsVisible(person,
                                !grid.isDetailsVisible(person)));
    }

    private static ComponentRenderer<PersonDetailsFormLayout, Customer> createPersonDetailsRenderer() {
        return new ComponentRenderer<>(PersonDetailsFormLayout::new,
                PersonDetailsFormLayout::setPerson);
    }

    private static class PersonDetailsFormLayout extends FormLayout {
        private final TextField nameField = new TextField("Имя");
        private final TextField lastnameField = new TextField("Фамилия");
        private final TextField card = new TextField("Абонимент");
        private final TextField startDateField = new TextField("Дата начала");
        private final TextField endDateField = new TextField("Дата конец");

        public PersonDetailsFormLayout() {
            //                field.set(true);
            Stream.of(nameField, lastnameField, card, startDateField, endDateField).forEach(this::add);

            setResponsiveSteps(new ResponsiveStep("0", 6));
        }

        public void setPerson(Customer customer) {
            nameField.setValue(customer.getName());
            lastnameField.setValue(customer.getLastname());
            card.setValue(customer.getCard().getName());
            startDateField.setValue(customer.getStartDate().toString());
            endDateField.setValue(customer.getEndDate().toString());
        }
    }

    private void formDialog(Customer customerItem) throws IOException {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Подтвердите!!!");

        Button deleteButton = new Button("Удалить");
        deleteButton.addClickListener(event -> {
            customerService.delete(customerItem);
            dialog.close();
            refreshGrid();
        });
        deleteButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        Button cancelButton = new Button("Отменить", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
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
