package com.example.fnialprojectandriod;

public class Event {
    private String eventName;
    private String eventDate;
    private String eventCode;
    private String creatorUid;

    public Event() {} // חובה עבור Firebase

    public Event(String eventName, String eventDate, String eventCode, String creatorUid) {
        this.eventName = eventName;
        this.eventDate = eventDate;
        this.eventCode = eventCode;
        this.creatorUid = creatorUid;
    }

    public String getEventName() { return eventName; }
    public String getEventDate() { return eventDate; }
    public String getEventCode() { return eventCode; }
    public String getCreatorUid() { return creatorUid; }
}
