package com.example.ihelpou.models;

import java.io.Serializable;

public class User implements Serializable {

    //int key, age;
    //String  name, username, password, surname, address, phone, email;
    //private Bitmap avatar;

    String username, password;

    public User() {
    }

    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }
 /*
    public User(String name, String username, String password) {
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(int id, int age, String name, String username, String password, String surname, String address, String phone, String email) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }

   public User(int id, int age, String name, String username, String password, String surname, String address, String phone, String email, Bitmap avatar) {
        this.id = id;
        this.age = age;
        this.name = name;
        this.username = username;
        this.password = password;
        this.surname = surname;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.avatar = avatar;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/

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

    /*public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Bitmap getAvatar() {
        return avatar;
    }

    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }*/
}
