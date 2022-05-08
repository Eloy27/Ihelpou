package com.example.ihelpou.models;

import java.io.Serializable;

public class User implements Serializable {

    String key;
    String  name, username, password, surname, phone, address;
    int age;
    String email;
    //private Bitmap avatar;

    public User() {
    }

    public User(String name, String username, String password, String surname, String phone, String address, int age, String email) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.email = email;
    }

    public User(String key, String name, String username, String password, String surname, String phone, String address, int age, String email) {
        this.key = key;
        this.name = name;
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.email = email;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
