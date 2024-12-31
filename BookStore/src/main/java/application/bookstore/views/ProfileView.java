package application.bookstore.views;

import application.bookstore.controllers.ProfileViewEditController;
import application.bookstore.models.User;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;


public class ProfileView {
    private final User user;
    private Label first_name_label;
    private TextField surname;
    private TextField first_name;
    private Label surname_label;
    private Label email_label;
    private TextField email;
    private TextField username;
    private Label username_label;
    private Label gender_label;
    private TextField gender;
    private Label access_label;
    private TextField access;
    private GridPane pane;

    public ProfileView(User user) {
        this.user = user;
    }

    public Scene showView(Stage stage) {
        pane = new GridPane();
        pane.setAlignment(Pos.TOP_CENTER);
        pane.setHgap(30);
        pane.setVgap(30);

        first_name_label = new Label("First Name:");

        first_name = new TextField();
        first_name.setEditable(false);
        first_name.setText(user.getFirstName());

        surname_label = new Label("Last Name:");

        surname = new TextField();
        surname.setEditable(false);
        surname.setText(user.getLastName());

        email_label = new Label("Email:");

        email = new TextField();
        email.setEditable(false);
        email.setText(user.getEmail());

        username_label = new Label("Username:");

        username = new TextField();
        username.setEditable(false);
        username.setText(user.getUsername());

        gender_label = new Label("Gender:");

        gender = new TextField();
        gender.setEditable(false);
        gender.setText(user.getGender());

        Label role_label = new Label("Role:");

        TextField role = new TextField();
        role.setEditable(false);
        role.setText(user.getRoleString());

        Button edit_button = new Button("Edit");

        pane.add(first_name_label, 0, 0);
        pane.add(first_name, 1, 0);
        pane.add(surname_label, 0, 1);
        pane.add(surname, 1, 1);
        pane.add(email_label, 0, 2);
        pane.add(email, 1, 2);
        pane.add(username_label, 0, 3);
        pane.add(username, 1, 3);
        pane.add(gender_label, 0, 4);
        pane.add(gender, 1, 4);
        pane.add(role_label, 0, 5);
        pane.add(role, 1, 5);
        pane.add(edit_button, 1, 8);


        edit_button.setOnAction(e ->
        {
            if (!(user.getRoleString().equals("admin"))){
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Pending Confirmation");
                dialog.setHeaderText("Enter Access Code:");
                dialog.showAndWait().ifPresent(accessCode -> {
                    if (Integer.parseInt(accessCode) == ProfileViewEditController.accessCode) {
                        Button button = new Button("Confirm Changes");
                        pane.add(button, 1, 8);
                        first_name_label.setText("Change Name: ");
                        surname_label.setText("Change Surname: ");
                        email_label.setText("Change Email: ");
                        username_label.setText("Change Username: ");
                        gender_label.setText("Change Gender: ");
                        String old_first_name = first_name.getText();
                        String old_surname = surname.getText();
                        String old_email = email.getText();
                        String old_username = username.getText();
                        String old_gender = gender.getText();

                        first_name.setEditable(true);
                        surname.setEditable(true);
                        email.setEditable(true);
                        username.setEditable(true);
                        gender.setEditable(true);
                        Label password_label = new Label("Change Password");
                        TextField password = new TextField();
                        pane.add(password_label, 0, 6);
                        pane.add(password, 1, 6);

                        button.setOnAction(b -> {
                            if (!old_first_name.equals(first_name.getText())) {
                                ProfileViewEditController.changeName(first_name.getText(), user);
                            }
                            if (!old_surname.equals(surname.getText())) {
                                ProfileViewEditController.changeSurname(surname.getText(), user);
                            }
                            if (!old_email.equals(email.getText())) {
                                try {
                                    ProfileViewEditController.changeEmail(email.getText(), user);
                                } catch (Exception l) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(l.getMessage());
                                    alert.show();
                                }
                            }
                            if (!old_username.equals(username.getText())) {
                                try {
                                    ProfileViewEditController.changeUsername(username.getText(), user);
                                } catch (Exception l) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(l.getMessage());
                                    alert.show();
                                }
                            }
                            if (!old_gender.equals(gender.getText())) {
                                String enteredGender = gender.getText().trim(); // Remove leading/trailing spaces

                                // Check if enteredGender is equal to "M", "F", or "U"
                                if (enteredGender.equalsIgnoreCase("M") || enteredGender.equalsIgnoreCase("F") || enteredGender.equalsIgnoreCase("U")) {
                                    // Valid gender entered
                                    ProfileViewEditController.changeGender(enteredGender, user);
                                } else {
                                    // Invalid gender entered - show an error message or handle accordingly
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText("Invalid gender entered. Please enter 'M', 'F', or 'U'.");
                                    alert.show();
                                }
                            }
                            if (!(user.getPassword()).equals(password.getText()) && !(password.getText().isEmpty())) {
                                try {
                                    ProfileViewEditController.changePassword(password.getText(), user);
                                } catch (Exception l) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(l.getMessage());
                                    alert.show();
                                }
                            }
                            stage.close();
                        });
                    }else{
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setHeaderText("Wrong Access Code");
                        alert.show();
                    }
                });
            }else{
                Button button = new Button("Confirm Changes");
                pane.add(button, 1, 8);
                first_name_label.setText("Change Name: ");
                surname_label.setText("Change Surname: ");
                email_label.setText("Change Email: ");
                username_label.setText("Change Username: ");
                gender_label.setText("Change Gender: ");
                String old_first_name = first_name.getText();
                String old_surname = surname.getText();
                String old_email = email.getText();
                String old_username = username.getText();
                String old_gender = gender.getText();

                first_name.setEditable(true);
                surname.setEditable(true);
                email.setEditable(true);
                username.setEditable(true);
                gender.setEditable(true);
                TextField password = new TextField();
                Label password_label = new Label("Password");
                pane.add(password_label, 0, 6);
                pane.add(password, 1, 6);
                button.setOnAction(b -> {
                    if (!old_first_name.equals(first_name.getText())) {
                        ProfileViewEditController.changeName(first_name.getText(), user);
                    }
                    if (!old_surname.equals(surname.getText())) {
                        ProfileViewEditController.changeSurname(surname.getText(), user);
                    }
                    if (!old_email.equals(email.getText())) {
                        try {
                            ProfileViewEditController.changeEmail(email.getText(), user);
                        } catch (Exception l) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText(l.getMessage());
                            alert.show();
                        }
                    }
                        if (!old_username.equals(username.getText())) {
                            try {
                                ProfileViewEditController.changeUsername(username.getText(), user);
                            } catch (Exception l) {
                                Alert alert = new Alert(Alert.AlertType.ERROR);
                                alert.setHeaderText(l.getMessage());
                                alert.show();
                            }
                        }
                    if (!old_gender.equals(gender.getText())) {
                        String enteredGender = gender.getText().trim(); // Remove leading/trailing spaces

                        // Check if enteredGender is equal to "M", "F", or "U"
                        if (enteredGender.equalsIgnoreCase("M") || enteredGender.equalsIgnoreCase("F") || enteredGender.equalsIgnoreCase("U")) {
                            // Valid gender entered
                            ProfileViewEditController.changeGender(enteredGender, user);
                        } else {
                            // Invalid gender entered - show an error message or handle accordingly
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setHeaderText("Invalid gender entered. Please enter 'M', 'F', or 'U'.");
                            alert.show();
                        }
                    }
                            if (!(user.getPassword()).equals(password.getText()) && !(password.getText().isEmpty())) {
                                try {
                                    ProfileViewEditController.changePassword(password.getText(), user);
                                } catch (Exception l) {
                                    Alert alert = new Alert(Alert.AlertType.ERROR);
                                    alert.setHeaderText(l.getMessage());
                                    alert.show();
                                }
                            }
                            stage.close();
                        });
                }
        });
        stage.setTitle("Profile");
        return new Scene(pane, 500, 500);
    }
}
