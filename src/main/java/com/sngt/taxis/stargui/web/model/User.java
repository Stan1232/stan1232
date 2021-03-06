package com.sngt.taxis.stargui.web.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by skiss on 08/09/2015.
 */
@Entity
public class User {

    public static final Logger LOGGER = LoggerFactory.getLogger(User.class);

    @Id
    @GeneratedValue
    public Integer userId;

    public String birthDate;
    public long creationDate;

    @Column(unique = true, nullable = false)
    public String login;

    public String surName;
    public String firstName;

    @Column(unique = true, nullable = false)
    public String mail;

    public String location;
    public String departement;
    public String region;
    public String country;
    public String description;
    public String age;
    public String inscription;
    public String picture;
    public String pass;
    public String gender;

    //@JoinTable(name="user_liste_discussion",
    //joinColumns=@JoinColumn(name="user_userid"),
    //inverseJoinColumns=@JoinColumn(name="liste_discussion_discid"))
    @ManyToMany
    public List<Discussion> listeDiscussion;

    public List<Discussion> getListeDiscussion() {
        return listeDiscussion;
    }

    public void setListeDiscussion(List<Discussion> listeDiscussion) {
        this.listeDiscussion = listeDiscussion;
    }

    public String getDepartement() {
        return departement;
    }

    public void setDepartement(String departement) {
        this.departement = departement;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @Override
    public int hashCode() {
        return userId;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getInscription() {
        return inscription;
    }

    public void setInscription(String inscription) {
        this.inscription = inscription;
    }


    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }


    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }


    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }


    public String getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(String birthDate) {
        this.birthDate = birthDate;
    }

    public long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(long creationDate) {
        this.creationDate = creationDate;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getSurName() {
        return surName;
    }

    public void setSurName(String surName) {
        this.surName = surName;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", birthDate='" + birthDate + '\'' +
                ", creationDate=" + creationDate +
                ", login='" + login + '\'' +
                ", surName='" + surName + '\'' +
                ", firstName='" + firstName + '\'' +
                ", mail='" + mail + '\'' +
                ", location='" + location + '\'' +
                ", description='" + description + '\'' +
                ", age='" + age + '\'' +
                ", inscription='" + inscription + '\'' +
                ", pass='" + pass + '\'' +
                ", gender='" + gender + '\'' +
                ", picture='" + picture + '\'' +
                '}';
    }

    public void initTest1() {
        userId = 12345678;
        birthDate = "2005-05-02";
        creationDate = System.currentTimeMillis();
        login = "Login";
        surName = "Sur Name";
        firstName = "First Name";
        mail = "mail@mail.com";
        location = "Paris";
        gender = "Female";
        age = "25";
        inscription = "June 2015";
        description = "Hello everybody. I am Aigul, from Kazakhstan and l am going to visit Paris with my friend." +
                "(She is my colleague). I hope I can find some cool locals, singles to families, everything highly" +
                "appreciated to get an amazing experience in Paris :) ";
    }

    public void initTest2() {
        userId = 123456782;
        birthDate = "2005-05-02";
        creationDate = System.currentTimeMillis();
        login = "Login2";
        surName = "Sur Name2";
        firstName = "First Name2";
        mail = "mail@mail.com2";
        location = "Paris2";
        gender = "Female2";
        age = "252";
        inscription = "June 2015";
        description = "22Hello everybody. I am Aigul, from Kazakhstan and l am going to visit Paris with my friend." +
                "(She is my colleague). I hope I can find some cool locals, singles to families, everything highly" +
                "appreciated to get an amazing experience in Paris :) 22";
    }

    public void mergeModify(User userJSON) {
        setFirstName(userJSON.getFirstName());
        setSurName(userJSON.getSurName());
        setLocation(userJSON.getLocation());
        setBirthDate(userJSON.getBirthDate());
        setDescription(userJSON.getDescription());
    }

    public void addDiscussion(Discussion discussion) {
        if (listeDiscussion == null) {
            listeDiscussion = new ArrayList<Discussion>();
            LOGGER.info("!!!!!! CREATION D4UNE NOUVELLE DISCUSSION");
        }
        listeDiscussion.add(discussion);
    }
}