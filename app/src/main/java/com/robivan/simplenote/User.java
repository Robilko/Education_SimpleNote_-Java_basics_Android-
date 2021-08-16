package com.robivan.simplenote;

public class User {

    private static String nameUser;

    private static final User userData = new User();

    public User() {

    }

    public static User getUserData(String name) {
        nameUser = name;
        return userData;
    }

    public static String getNameUser() {
        return nameUser;
    }
}
