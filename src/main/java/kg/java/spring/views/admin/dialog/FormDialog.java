package kg.java.spring.views.admin.dialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.converter.LocalDateToDateConverter;
import kg.java.spring.core.model.ResponseDB;
import kg.java.spring.core.model.entity.Customer;
import kg.java.spring.core.model.entity.SeasonCard;
import kg.java.spring.core.model.enums.ResultDB;
import kg.java.spring.core.service.CustomerService;
import kg.java.spring.core.service.SeasonCardService;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Slf4j
public class FormDialog extends Div {
    private final SeasonCardService seasonCardService;
    private final CustomerService customerService;
    private ComboBox<SeasonCard> seasonCardComboBox;
    private final Binder<Customer> binder = new Binder<>();
    private final Customer customer = new Customer();
    private Grid<Customer> customerGrid;
    private TextField firstNameField;
    private TextField lastnameField;
    private Dialog dialog = new Dialog();
    private DatePicker startDatePicker;
    private DatePicker endDatePicker;

    public FormDialog(SeasonCardService seasonCardService,
                      CustomerService customerService,
                      Grid<Customer> customerGrid) {
        this.seasonCardService = seasonCardService;
        this.customerService = customerService;
        this.customerGrid = customerGrid;
        binder.readBean(customer);
        setupComponentUI();
        binder.readBean(new Customer());
    }

    private void setupComponentUI() {
        FormLayout dialogLayout = createDialogLayout();
        dialog.setHeaderTitle("Анкета клиента");
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton();
        Button cancelButton = createCancelButton();

        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.open();
    }

    private Button createCancelButton() {
        Button cancelButton = new Button("Отмена", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        return cancelButton;
    }

    private FormLayout createDialogLayout() {
        firstNameField = buildNameTextField();
        lastnameField = buildLastnameTextField();
        seasonCardComboBox = buildSeasonCardComboBox();
        startDatePicker = buildStartDatePicker();
        endDatePicker = buildEndDatePicker();

        FormLayout formLayout = new FormLayout();
        formLayout.add(firstNameField, startDatePicker, lastnameField,  endDatePicker,seasonCardComboBox);
        formLayout.setResponsiveSteps(
                new FormLayout.ResponsiveStep("", 2));
        formLayout.setWidth("500px");
        return formLayout;
    }

    private DatePicker buildStartDatePicker() {
        Locale finnishLocale = new Locale("fi", "FI");
        DatePicker datePicker = new DatePicker("Выберите начало даты:");
        datePicker.setLocale(finnishLocale);
        datePicker.setValue(LocalDate.now(ZoneId.systemDefault()));
        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        singleFormatI18n.setDateFormat("dd.MM.yyyy");
        customer.setStartDate(datePicker.getValue());
        return datePicker;
    }

    private DatePicker buildEndDatePicker() {
        DatePicker datePicker = new DatePicker("Выберите конец даты:");
        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        datePicker.setI18n(singleFormatI18n.setDateFormat("dd.MM.yyyy"));

        binder.forField(datePicker)
                .asRequired("Выберите дату")
                .bind(Customer::getEndDate, Customer::setEndDate);
        return datePicker;
    }

    private TextField buildNameTextField() {
        TextField nameTextField = new TextField("Имя");

        binder.forField(nameTextField)
                .asRequired("Введите имя")
                .bind(Customer::getName, Customer::setName);

        nameTextField.setRequiredIndicatorVisible(true);
        nameTextField.setAllowedCharPattern("[A-Za-zа-яёА-ЯЁ]+");
        nameTextField.setMinLength(3);
        nameTextField.setMaxLength(15);
        return nameTextField;
    }

    private TextField buildLastnameTextField() {
        TextField lastnameTextField = new TextField("Фамилия");

        binder.forField(lastnameTextField)
                .asRequired("Введите фамилию")
                .bind(Customer::getLastname, Customer::setLastname);

        lastnameTextField.setRequiredIndicatorVisible(true);
        lastnameTextField.setAllowedCharPattern("[A-Za-zа-яёА-ЯЁ]+");
        lastnameTextField.setMinLength(3);
        lastnameTextField.setMaxLength(25);
        return lastnameTextField;
    }

    private ComboBox<SeasonCard> buildSeasonCardComboBox() {
        ComboBox<SeasonCard> comboBox = new ComboBox<>("Тип абонимента");
        List<SeasonCard> seasonCardListDB = this.seasonCardService.getSeasonCards();
        comboBox.setItems(seasonCardListDB);
        comboBox.setItemLabelGenerator(SeasonCard::getName);

        binder.forField(comboBox)
                .asRequired("Выберите тип абонимента")
                .bind(Customer::getCard, Customer::setCard);

        comboBox.addValueChangeListener(e-> {
            customer.setCard(e.getValue());
        });
        return comboBox;
    }

    private Button createSaveButton() {
        Button saveButton = new Button("Сохранить");
        saveButton.setEnabled(false);
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(saveCustomerListener());

        binder.addStatusChangeListener(statusChangeEvent ->
                saveButton.setEnabled(!statusChangeEvent.hasValidationErrors()));
        return saveButton;
    }

    private ComponentEventListener<ClickEvent<Button>> saveCustomerListener() {
        return click -> {
            try {
                binder.writeBean(customer);
                customer.setEndDate(endDatePicker.getValue());

                ResponseDB response = customerService.save(customer);
                if (response.getResultDB() == ResultDB.SUCCESS) {
                    notification("Успешно сохранен в базе!!!", NotificationVariant.LUMO_SUCCESS);
                    refreshGrid();
                    dialog.close();
                } else if (response.getResultDB() == ResultDB.ERROR) {
                    notification("Ошибка!!!", NotificationVariant.LUMO_ERROR);
                }
            } catch (ValidationException e) {
                throw new RuntimeException(e);
            }
        };
    }

    private static void notification(String text, NotificationVariant notificationVariant) {
        Notification notification = new Notification(text);
        notification.setDuration(3000);
        Div statusText = new Div(new Text(text));
        HorizontalLayout layout = new HorizontalLayout(statusText);
        layout.setAlignItems(FlexComponent.Alignment.END);
        notification.add(layout);
        notification.addThemeVariants(notificationVariant);
        notification.setPosition(Notification.Position.TOP_CENTER);
        notification.open();
    }

    private void refreshGrid() {
        customerGrid.setItems(customerService.getCustomer());
    }
}
