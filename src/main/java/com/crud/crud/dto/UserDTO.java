package com.crud.crud.dto;

public class UserDTO {

    private String username;
    private String password;
    private String samAccountName;
    private String userPrincipalName;
    private String firstName;
    private String lastName;  // assuming this is already there
    private String email;

    // Constructors, Getters, and Setters
    public UserDTO() {}

    public UserDTO(String username, String password, String samAccountName,
                   String userPrincipalName, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.samAccountName = samAccountName;
        this.userPrincipalName = userPrincipalName;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    // Getters and Setters for all fields


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSamAccountName() {
        return samAccountName;
    }

    public void setSamAccountName(String samAccountName) {
        this.samAccountName = samAccountName;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
