package com.crud.crud.dto;

import java.util.List;
import java.util.Map;

public class UserDetailsDTO {
    private String samAccountName;
    private String distinguishedName;
    private String userPrincipalName;
    private String objectGUID;
    private String firstName;
    private String lastName;
    private String email;
    private List<Map<String, String>> memberOf;
    private String userAccountControl;

    public UserDetailsDTO(String samAccountName, String distinguishedName, String userPrincipalName, String objectGUID, String firstName, String lastName, String email, List<Map<String, String>> memberOf, String userAccountControl) {
        this.samAccountName = samAccountName;
        this.distinguishedName = distinguishedName;
        this.userPrincipalName = userPrincipalName;
        this.objectGUID = objectGUID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.memberOf = memberOf;
        this.userAccountControl = userAccountControl;
    }

    public String getSamAccountName() {
        return samAccountName;
    }

    public void setSamAccountName(String samAccountName) {
        this.samAccountName = samAccountName;
    }

    public String getDistinguishedName() {
        return distinguishedName;
    }

    public void setDistinguishedName(String distinguishedName) {
        this.distinguishedName = distinguishedName;
    }

    public String getUserPrincipalName() {
        return userPrincipalName;
    }

    public void setUserPrincipalName(String userPrincipalName) {
        this.userPrincipalName = userPrincipalName;
    }

    public String getObjectGUID() {
        return objectGUID;
    }

    public void setObjectGUID(String objectGUID) {
        this.objectGUID = objectGUID;
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

    public List<Map<String, String>> getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(List<Map<String, String>> memberOf) {
        this.memberOf = memberOf;
    }

    public String getUserAccountControl() {
        return userAccountControl;
    }

    public void setUserAccountControl(String userAccountControl) {
        this.userAccountControl = userAccountControl;
    }
}
