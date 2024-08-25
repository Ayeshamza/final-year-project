package com.example.planandmeet;

import java.util.ArrayList;

public class Event {
    private String name;
    private String startDate;
    private String endDate;
    private String mode;
    private String venue;
    private Long creationDate;
    private ArrayList<String> userID;
    private String meetingOccurence;

    public Event() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String spinner) {
        this.mode = spinner;
    }

    public ArrayList<String> getUserID() {
        return userID;
    }

    public void setUserID(ArrayList<String> userID) {
        if (userID.size() > 0) {
            this.userID = userID;
        }
    }

    public String getMeetingOccurence() {
        return meetingOccurence;
    }

    public void setMeetingOccurence(String meetingOccurence) {
        this.meetingOccurence = meetingOccurence;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public Long getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Long creationDate) {
        this.creationDate = creationDate;
    }
}
