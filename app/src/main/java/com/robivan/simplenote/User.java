package com.robivan.simplenote;

public class User {

    private static String nameUser;
    private static String emailUser;

    private static final User userData = new User();

    public User() {

    }

    public static User getUserData(String name, String email) {
        nameUser = name;
        emailUser = email;
        return userData;
    }

    public static String getNameUser() {
        return nameUser;
    }

    public static String getEmailUser() {
        return emailUser;
    }
}
