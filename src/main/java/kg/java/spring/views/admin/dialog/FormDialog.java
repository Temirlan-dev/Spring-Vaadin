package kg.java.spring.views.admin.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Locale;

public class FormDialog extends Dialog {

    public FormDialog() {
        setupComponentUI();
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

    private static VerticalLayout createDialogLayout() {
        TextField firstNameField = new TextField("Фамилия");
        TextField lastNameField = new TextField("Имя");

        Locale finnishLocale = new Locale("fi", "FI");

        DatePicker startDatePicker = new DatePicker("Выберите начало даты:");
        startDatePicker.setLocale(finnishLocale);
        startDatePicker.setValue(LocalDate.now(ZoneId.systemDefault()));

        DatePicker.DatePickerI18n singleFormatI18n = new DatePicker.DatePickerI18n();
        singleFormatI18n.setDateFormat("dd.MM.yyyy");

        DatePicker endDatePicker = new DatePicker("Выберите конец даты:");
        endDatePicker.setI18n(singleFormatI18n);

        VerticalLayout dialogLayout = new VerticalLayout(firstNameField,
                lastNameField, startDatePicker, endDatePicker);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private static Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Сохранить", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }
}
