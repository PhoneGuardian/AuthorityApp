package it.polimi.guardian.authorityapp;

public class User {
   private static User instance = null;
   String username;
   String phone;
   String type;

    private User(){}

    public static User getInstance(){
        if(instance  == null){
            instance = new User();
        }
        return instance;
    }
    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPhone() {
        return this.phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getType() { return type; }

    public void setType(String type) { this.type = type; }

}
