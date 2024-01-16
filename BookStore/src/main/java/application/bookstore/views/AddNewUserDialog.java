package application.bookstore.views;

import application.bookstore.auxiliaries.Alerts;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.models.User;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.util.Callback;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class AddNewUserDialog extends Dialog<User> implements DatabaseConnector {
    private User user;
    private Label firstNameL;
    private TextField firstNameField;
    private Label LastNameL;
    private TextField LastNameField;
    private Label EmailL;
    private TextField EmailField;
    private Label Username;
    private TextField UsernameField;
    private Label PasswordL;
    private PasswordField passF;
    private Label VPasswordL;
    private PasswordField VpassF;
    private Label genderL;
    private Label role;
    private RadioButton male;
    private RadioButton female;
    private RadioButton other;
    private RadioButton admin;
    private RadioButton manager;
    private RadioButton librarian;
    private ToggleGroup genderToggleGroup;
    private ToggleGroup roleToggleGroup;

    public AddNewUserDialog(User user) {
        super();
        this.setTitle("Add User");
        this.user = user;
        buildUI();
        setPropertyBindings();
        setResultConverter();
    }

    private void buildUI() {
        GridPane pane = createPane();
        getDialogPane().setContent(pane);

        getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Button button = (Button) getDialogPane().lookupButton(ButtonType.OK);
        button.addEventFilter(ActionEvent.ACTION, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if (!validateDialog()) {
                    event.consume();
                }
            }

            private boolean validateDialog() {
                if (firstNameField.getText().isEmpty() || LastNameField.getText().isEmpty() ||
                        EmailField.getText().isEmpty() || UsernameField.getText().isEmpty() ||
                        passF.getText().isEmpty() || VpassF.getText().isEmpty() ||
                        (genderToggleGroup.getSelectedToggle() == null) ||
                        (roleToggleGroup.getSelectedToggle() == null)) {

                    Alerts.showAlert(Alert.AlertType.ERROR,"Empty feilds","All fields must be completed");
                    return false;
                }
                else if(!(firstNameField.getText().matches("[a-zA-Z ]{1,24}")&&
                        LastNameField.getText().matches("[a-zA-Z ]{1,24}")))
                {
                    Alerts.showAlert(Alert.AlertType.ERROR,"Error in name","The name and surname can only " +
                            "include letters and spaces and cannot be longer than 25 characters.");
                    return false;
                }
                else if(!(passF.getText().matches("^(?=.*[A-Za-z])(?=.*[\\d])[A-Za-z\\d]{8,}$")))
                {
                    Alerts.showAlert(Alert.AlertType.ERROR,"Error in password","The password must contain " +
                            "a minimum of eight characters, at least one letter and one number and no special characters.");
                    return false;
                }
                return true;
            }
        });
    }

    private void setPropertyBindings() {
        firstNameField.textProperty().bindBidirectional(user.firstNameProperty());
        LastNameField.textProperty().bindBidirectional(user.lastNameProperty());
        EmailField.textProperty().bindBidirectional(user.emailProperty());
        UsernameField.textProperty().bindBidirectional(user.usernameProperty());
        passF.textProperty().bindBidirectional(user.passwordProperty());

        //Binding the toggle group results

        BooleanProperty isMaleSelected = male.selectedProperty();
        BooleanProperty isFemaleSelected = female.selectedProperty();

        user.genderProperty().bind(Bindings.when(isMaleSelected).then("male")
                .otherwise(Bindings.when(isFemaleSelected).then("female").otherwise("other")));

        //Conditional Binding
        BooleanProperty isAdminSelected = admin.selectedProperty();
        BooleanProperty isManagerSelected = manager.selectedProperty();

        user.getRole().bind(Bindings.when(isAdminSelected).then("admin")
                .otherwise(Bindings.when(isManagerSelected).then("manager")
                        .otherwise("librarian")));

    }

    private void setResultConverter() {
        Callback<ButtonType, User> personResultConverter = new Callback<ButtonType, User>() {
            @Override
            public User call(ButtonType param) {
                if (param == ButtonType.OK) {
                    return user;
                } else {
                    return null;
                }
            }
        };
        setResultConverter(personResultConverter);
    }

    public GridPane createPane()
    {
        GridPane gridPane=new GridPane();
        gridPane.setHgap(10);
        gridPane.setVgap(10);

        firstNameL = new Label("First Name");
        firstNameField = new TextField();
        firstNameL.setFont(Font.font(25));
        firstNameField.setFont(Font.font(25));
        gridPane.add(firstNameL, 0, 0);
        gridPane.add(firstNameField, 1, 0);

        LastNameL = new Label("Last Name");
        LastNameField = new TextField();
        LastNameL.setFont(Font.font(25));
        LastNameField.setFont(Font.font(25));
        gridPane.add(LastNameL, 0, 1);
        gridPane.add(LastNameField, 1, 1);

        EmailL = new Label("Email");
        EmailField = new TextField();
        EmailL.setFont(Font.font(25));
        EmailField.setFont(Font.font(25));
        gridPane.add(EmailL, 0, 2);
        gridPane.add(EmailField, 1, 2);

        Username=new Label("Username");
        UsernameField=new TextField();
        Username.setFont(Font.font(25));
        UsernameField.setFont(Font.font(25));
        gridPane.add(Username,0,3);
        gridPane.add(UsernameField,1,3);

        PasswordL = new Label("Password");
        passF = new PasswordField();
        PasswordL.setFont(Font.font(25));
        passF.setFont(Font.font(25));
        gridPane.add(PasswordL, 0, 4);
        gridPane.add(passF, 1, 4);

        VPasswordL = new Label("Verify Password");
        VpassF = new PasswordField();
        VPasswordL.setFont(Font.font(25));
        VpassF.setFont(Font.font(25));
        gridPane.add(VPasswordL, 0, 5);
        gridPane.add(VpassF, 1, 5);

        genderToggleGroup = new ToggleGroup();
        genderL = new Label("Gender");
        male = new RadioButton("Male");
        female = new RadioButton("Female");
        other = new RadioButton("Other");
        genderL.setFont(Font.font(25));
        male.setFont(Font.font(22));
        female.setFont(Font.font(22));
        other.setFont(Font.font(22));
        male.setToggleGroup(genderToggleGroup);
        female.setToggleGroup(genderToggleGroup);
        other.setToggleGroup(genderToggleGroup);

        gridPane.add(genderL, 0, 6);
        HBox genderButtons = new HBox(male,female,other);
        genderButtons.setSpacing(10);
        gridPane.add(genderButtons, 1, 6);

        role = new Label("Role");
        role.setFont(Font.font(25));
        roleToggleGroup= new ToggleGroup();
        admin = new RadioButton("Admin");
        manager = new RadioButton("Manager");
        librarian = new RadioButton("Librarian");
        admin.setFont(Font.font(22));
        manager.setFont(Font.font(22));
        librarian.setFont(Font.font(22));
        admin.setToggleGroup(roleToggleGroup);
        manager.setToggleGroup(roleToggleGroup);
        librarian.setToggleGroup(roleToggleGroup);

        gridPane.add(role,0,7);
        HBox roleButtons=new HBox(admin,manager,librarian);
        roleButtons.setSpacing(10);
        gridPane.add(roleButtons,1,7);

        return gridPane;
    }

}
