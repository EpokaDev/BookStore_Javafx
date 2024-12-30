package application.bookstore.models;

import javafx.beans.property.StringProperty;


public class User {

    //These attributes are set to StringProperty so that they can be put in an
    //ObservableList, so that changes to their values can automatically be reflected
    //in the UI

    /*#TODO
    In database userId
    */
    private StringProperty firstName;
    private StringProperty lastName;
    private StringProperty email;
    private StringProperty username;
    private StringProperty password;
    private StringProperty gender;
    private StringProperty role;

    //The StringProperties are used in the UsersTableView to set the CellValueFactory

    public User(){}

    public User(StringProperty firstName, StringProperty lastName,
                StringProperty email, StringProperty username, StringProperty password,
                StringProperty gender, StringProperty role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.username = username;
        this.password = password;
        this.gender = gender;
        this.role = role;
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getEmail() {
        return email.get();
    }

    public StringProperty emailProperty() {
        return email;
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public String getUsername() {
        return username.get();
    }

    public StringProperty usernameProperty() {
        return username;
    }

    public void setUsername(String username) {
        this.username.set(username);
    }

    public String getPassword() {
        return password.get();
    }

    public StringProperty passwordProperty() {
        return password;
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public String getGender() {
        return gender.get();
    }

    public StringProperty genderProperty() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender.set(gender);
    }

    public StringProperty getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role.set(role);
    }
    public String getRoleString()
    {
        return role.get();
    }

    @Override
    public String toString() {
        return "User{" +
                "firstName=" + firstName +
                ", lastName=" + lastName +
                ", email=" + email +
                ", username=" + username +
                ", password=" + password +
                ", gender=" + gender +
                ", role=" + role +
                '}';
    }

}
