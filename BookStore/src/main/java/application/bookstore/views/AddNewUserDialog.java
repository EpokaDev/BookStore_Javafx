package application.bookstore.views;

import application.bookstore.Exceptions.EmailAlreadyExistsException;
import application.bookstore.Exceptions.PasswordAlreadyExistsException;
import application.bookstore.Exceptions.UsernameAlreadyExistsException;
import application.bookstore.auxiliaries.Alerts;
import application.bookstore.auxiliaries.DatabaseConnector;
import application.bookstore.controllers.AddNewUserController;
import application.bookstore.models.User;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.event.ActionEvent;
import javafx.util.Callback;


public class AddNewUserDialog extends Dialog<User> implements DatabaseConnector {
    private final User user;
    private TextField firstNameField;
    private TextField LastNameField;
    private TextField EmailField;
    private TextField UsernameField;
    private PasswordField passF;
    private PasswordField VpassF;
    private RadioButton male;
    private RadioButton female;
    private RadioButton admin;
    private RadioButton manager;
    private ToggleGroup genderToggleGroup;
    private ToggleGroup roleToggleGroup;

    public AddNewUserDialog(User user) {
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
        button.setId("okButton"); // Set fx:id for OK button

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

                    Alerts.showAlert(Alert.AlertType.ERROR,"Empty fields","All fields must be completed");
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
                else if(!(passF.getText().equals(VpassF.getText()))) {
                    Alerts.showAlert(Alert.AlertType.ERROR,"Error in password","Password and Verify Password fields do not match");
                    return false;
                }

                try
                {
                    AddNewUserController.allValuesUnique(getUsername(),getEmail());
                }catch (UsernameAlreadyExistsException e)
                {
                    Alerts.showAlert(Alert.AlertType.ERROR,"Username exists","The entered username already exists");
                    return false;
                }catch (EmailAlreadyExistsException ex)
                {
                    Alerts.showAlert(Alert.AlertType.ERROR,"Email exists","The entered email already exists");
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

        Label firstNameL = new Label("First Name");
        firstNameField = new TextField();
        firstNameField.setId("firstNameField"); // Set fx:id
        firstNameL.setFont(Font.font(25));
        firstNameField.setFont(Font.font(25));
        gridPane.add(firstNameL, 0, 0);
        gridPane.add(firstNameField, 1, 0);

        Label lastNameL = new Label("Last Name");
        LastNameField = new TextField();
        LastNameField.setId("lastNameField"); // Set fx:id
        lastNameL.setFont(Font.font(25));
        LastNameField.setFont(Font.font(25));
        gridPane.add(lastNameL, 0, 1);
        gridPane.add(LastNameField, 1, 1);

        Label emailL = new Label("Email");
        EmailField = new TextField();
        EmailField.setId("emailField"); // Set fx:id
        emailL.setFont(Font.font(25));
        EmailField.setFont(Font.font(25));
        gridPane.add(emailL, 0, 2);
        gridPane.add(EmailField, 1, 2);

        Label username = new Label("Username");
        UsernameField=new TextField();
        UsernameField.setId("usernameField"); // Set fx:id
        username.setFont(Font.font(25));
        UsernameField.setFont(Font.font(25));
        gridPane.add(username,0,3);
        gridPane.add(UsernameField,1,3);

        Label passwordL = new Label("Password");
        passF = new PasswordField();
        passF.setId("passwordField"); // Set fx:id
        passwordL.setFont(Font.font(25));
        passF.setFont(Font.font(25));
        gridPane.add(passwordL, 0, 4);
        gridPane.add(passF, 1, 4);

        Label VPasswordL = new Label("Verify Password");
        VpassF = new PasswordField();
        VpassF.setId("verifyPasswordField"); // Set fx:id
        VPasswordL.setFont(Font.font(25));
        VpassF.setFont(Font.font(25));
        gridPane.add(VPasswordL, 0, 5);
        gridPane.add(VpassF, 1, 5);

        genderToggleGroup = new ToggleGroup();
        Label genderL = new Label("Gender");
        male = new RadioButton("Male");
        male.setId("maleRadio"); // Unique fx:id
        female = new RadioButton("Female");
        female.setId("genderField"); // Set fx:id
        female.setId("femaleRadio"); // Unique fx:id
        RadioButton other = new RadioButton("Other");
        other.setId("otherRadio"); // Unique fx:id
        genderL.setFont(Font.font(25));
        male.setFont(Font.font(22));
        female.setFont(Font.font(22));
        other.setFont(Font.font(22));
        male.setToggleGroup(genderToggleGroup);
        female.setToggleGroup(genderToggleGroup);
        other.setToggleGroup(genderToggleGroup);

        gridPane.add(genderL, 0, 6);
        HBox genderButtons = new HBox(male,female, other);
        genderButtons.setSpacing(10);
        gridPane.add(genderButtons, 1, 6);

        Label role = new Label("Role");
        role.setFont(Font.font(25));
        roleToggleGroup= new ToggleGroup();
        admin = new RadioButton("Admin");
        admin.setId("adminRadio"); // Set fx:id
        manager = new RadioButton("Manager");
        manager.setId("managerRadio"); // Set fx:id
        RadioButton librarian = new RadioButton("Librarian");
        librarian.setId("librarianRadio"); // Set fx:id
        admin.setFont(Font.font(22));
        manager.setFont(Font.font(22));
        librarian.setFont(Font.font(22));
        admin.setToggleGroup(roleToggleGroup);
        manager.setToggleGroup(roleToggleGroup);
        librarian.setToggleGroup(roleToggleGroup);

        gridPane.add(role,0,7);
        HBox roleButtons=new HBox(admin,manager, librarian);
        roleButtons.setSpacing(10);
        gridPane.add(roleButtons,1,7);

        return gridPane;
    }

    public String getUsername()
    {
        return UsernameField.getText();
    }
    public String getEmail()
    {
        return this.EmailField.getText();
    }
    public String getPassword()
    {
        return this.passF.getText();
    }

    public void setUsernameField(String text) {
        this.UsernameField.setText(text);
    }

    public void setEmailField(String text) {
        this.EmailField.setText(text);
    }

    public void setPassField(String text) {
        this.passF.setText(text);
    }



}
