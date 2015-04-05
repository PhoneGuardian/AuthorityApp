package it.polimi.guardian.authorityapp;

/**
 * Created by Nemanja on 16/12/2014.
 */

public class Credentials {
    private String Username;
    private String PhoneNumber;
    private String UserType;

    public String getUsername() {
        return Username;
    }

    public void setUsername(String username) {
        Username = username;
    }

    public String getPhoneNumber() {
        return PhoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        PhoneNumber = phoneNumber;
    }

    public String getUserType() { return UserType; }

    public void setUserType(String userType) { UserType = userType; }

}
