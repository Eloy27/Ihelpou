package com.example.ihelpou.models;

import java.io.Serializable;

public class User implements Serializable {
    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", surname='" + surname + '\'' +
                ", phone='" + phone + '\'' +
                ", address='" + address + '\'' +
                ", age=" + age +
                '}';
    }

    private String email, name, username, password, surname, phone, address;
    private int age;
    //private Bitmap avatar;

    public User() {
    }

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }


    public User(String email, String name, String username, String password, String surname, String phone, String address, int age) {
        this.name = name;
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.phone = phone;
        this.address = address;
        this.age = age;
        this.email = email;
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
