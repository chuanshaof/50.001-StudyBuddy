package com.example.myapplication2.objectmodel;

import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;

public class ProfileModel {

    private static final String TAG = "Profile Model";
    private ArrayList<DocumentReference> eventsCreated;
    private ArrayList<DocumentReference> eventsJoined;
    private DocumentReference imagePath;
    private ArrayList<DocumentReference> modules;
    private String name;
    private String pillar;
    private Date profileCreated;
    private Date profileUpdated;
    private int term;
    private DocumentReference userId;

    ProfileModel() {
    }

    ProfileModel(ArrayList<DocumentReference> eventsCreated, ArrayList<DocumentReference> eventsJoined,
                 DocumentReference imagePath, ArrayList<DocumentReference> modules, String name, String pillar,
                 Date profileCreated, Date profileUpdated, int term, DocumentReference userId) {
        this.eventsCreated = eventsCreated;
        this.eventsJoined = eventsJoined;
        this.imagePath = imagePath;
        this.modules = modules;
        this.name = name;
        this.pillar = pillar;
        this.profileCreated = profileCreated;
        this.profileUpdated = profileUpdated;
        this.term = term;
        this.userId = userId;
    }


    public ArrayList<DocumentReference> getEventsCreated() {
        return eventsCreated;
    }

    public void setEventsCreated(ArrayList<DocumentReference> eventsCreated) {
        this.eventsCreated = eventsCreated;
    }

    public ArrayList<DocumentReference> getEventsJoined() {
        return eventsJoined;
    }

    public void setEventsJoined(ArrayList<DocumentReference> eventsJoined) {
        this.eventsJoined = eventsJoined;
    }

    public DocumentReference getImagePath() {
        return imagePath;
    }

    public void setImagePath(DocumentReference imagePath) {
        this.imagePath = imagePath;
    }

    public ArrayList<DocumentReference> getModules() {
        return modules;
    }

    public void setModules(ArrayList<DocumentReference> modules) {
        this.modules = modules;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPillar() {
        return pillar;
    }

    public void setPillar(String pillar) {
        this.pillar = pillar;
    }

    public Date getProfileCreated() {
        return profileCreated;
    }

    public void setProfileCreated(Date profileCreated) {
        this.profileCreated = profileCreated;
    }

    public Date getProfileUpdated() {
        return profileUpdated;
    }

    public void setProfileUpdated(Date profileUpdated) {
        this.profileUpdated = profileUpdated;
    }

    public int getTerm() {
        return term;
    }

    public void setTerm(int term) {
        this.term = term;
    }

    public DocumentReference getUserId() {
        return userId;
    }

    public void setUserId(DocumentReference userId) {
        this.userId = userId;
    }
}