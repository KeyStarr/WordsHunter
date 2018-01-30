package com.keystarr.wordshunter.models.remote;

/**
 * Created by Cyril on 18.01.2018.
 */

public class User {
    private int age;
    private boolean gender;

    public User(int age, boolean gender) {
        this.age = age;
        this.gender = gender;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public boolean isGender() {
        return gender;
    }

    public void setGender(boolean gender) {
        this.gender = gender;
    }
}
