package com.the21codes.do_it;

public class User {
    public String userName, userEmail, userPassword, userDatabase;


    public User(String userName, String userEmail, String userPassword, String userDatabase) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
        this.userDatabase = userDatabase;
    }

    public User(){

    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserPassword() {
        return userPassword;
    }

    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

    public String getUserDatabase() {
        return userDatabase;
    }

    public void setUserDatabase(String userDatabase) {
        this.userDatabase = userDatabase;
    }
}
