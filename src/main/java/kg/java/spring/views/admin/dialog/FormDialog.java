package kg.java.spring.views.admin.dialog;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import kg.java.spring.core.model.ResponseDB;
import kg.java.spring.core.model.entity.Customer;
import kg.java.spring.core.model.entity.SeasonCard;
import kg.java.spring.core.model.enums.ResultDB;
import kg.java.spring.core.service.CustomerService;
import kg.java.spring.core.service.SeasonCardService;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class FormDialog extends Dialog {
    private final SeasonCardService seasonCardService;
    private final CustomerService customerService;
    private ComboBox<SeasonCard> seasonCardComboBox;
    private final Binder<Customer> binder = new Binder<>();
    private final Customer customer = new Customer();

    public FormDialog(SeasonCardService seasonCardService, CustomerService customerService) {
        this.seasonCardService = seasonCardService;
        this.customerService = customerService;
        binder.readBean(customer);
        setupComponentUI();
        binder.readBean(new Customer());
    }

    private void setupComponentUI() {
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Анкета клиента");
        VerticalLayout dialogLayout = createDialogLayout();
        dialog.add(dialogLayout);

        Button saveButton = createSaveButton(dialog);
        Button cancelButton = new Button("Отмена", e -> dialog.close());
        dialog.getFooter().add(cancelButton);
        dialog.getFooter().add(saveButton);
        dialog.open();
    }

    private VerticalLayout createDialogLayout() {
        TextField firstNameField = buildNameTextField();
        TextField lastnameField = buildLastnameTextField();

//        Locale finnishLocale = new Locale("fi", "FI");
//
//        DatePicker startDatePicker = new DatePicker("Выберите начало даты:");
//        startDatePicker.setLocale(finnishLocale);
//        startDatePicker.setValue(LocalDate.now(ZoneId.systemDefault()));
//
//        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
//        singleFormatI18n.setDateFormat("dd.MM.yyyy");
//
//        DatePicker endDatePicker = new DatePicker("Выберите конец даты:");
//        endDatePicker.setI18n(singleFormatI18n);

        seasonCardComboBox = buildSeasonCardComboBox();

        VerticalLayout dialogLayout = new VerticalLayout(firstNameField,
                lastnameField, seasonCardComboBox);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");
        return dialogLayout;
    }

    private TextField buildLastnameTextField() {
        TextField nameTextField = new TextField("Имя");
        binder.forField(nameTextField)
                .asRequired("Введите имя")
                .bind(Customer::getName, Customer::setName);
        return nameTextField;
    }

    private TextField buildNameTextField() {
        TextField lastnameTextField = new TextField("Фамилия");
        binder.forField(lastnameTextField)
                .asRequired("Введите фамилию")
                .bind(Customer::getLastname, Customer::setLastname);
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

    private Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Сохранить", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(saveCustomerListener());
        return saveButton;
    }

    private ComponentEventListener<ClickEvent<Button>> saveCustomerListener() {
        return click -> {
            try {
                binder.writeBean(customer);

                ResponseDB response = customerService.save(customer);
                if (response.getResultDB() == ResultDB.SUCCESS) {
                    notification("Успешно", NotificationVariant.LUMO_SUCCESS);
                } else if (response.getResultDB() == ResultDB.ERROR) {
                    notification("Ошибка", NotificationVariant.LUMO_ERROR);
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
}
