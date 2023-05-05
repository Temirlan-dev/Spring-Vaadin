package kg.java.spring.views.admin.dialog;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import kg.java.spring.core.model.entity.SeasonCard;
import kg.java.spring.core.service.SeasonCardService;
import lombok.extern.slf4j.Slf4j;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Locale;

@Slf4j
public class FormDialog extends Dialog {
    private final SeasonCardService seasonCardService;
    private ComboBox<SeasonCard> seasonCardComboBox;

    public FormDialog(SeasonCardService seasonCardService) {
        this.seasonCardService = seasonCardService;
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

    private VerticalLayout createDialogLayout() {
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

        seasonCardComboBox = buildSeasonCardComboBox();

        VerticalLayout dialogLayout = new VerticalLayout(firstNameField,
                lastNameField, startDatePicker, endDatePicker, seasonCardComboBox);
        dialogLayout.setPadding(false);
        dialogLayout.setSpacing(false);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        dialogLayout.getStyle().set("width", "18rem").set("max-width", "100%");

        return dialogLayout;
    }

    private ComboBox<SeasonCard> buildSeasonCardComboBox() {
        ComboBox<SeasonCard> comboBox = new ComboBox<>("Тип абонимента");
        List<SeasonCard> seasonCardListDB = this.seasonCardService.getSeasonCards();
        log.info("проверка {}", seasonCardListDB);

        comboBox.setItems(seasonCardListDB);
        comboBox.setItemLabelGenerator(SeasonCard::getName);
        return comboBox;
    }

    private static Button createSaveButton(Dialog dialog) {
        Button saveButton = new Button("Сохранить", e -> dialog.close());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        return saveButton;
    }
}
