package com.example.fnialprojectandriod;

public class Memory {
    private String greeting;
    private String imageUrl;
    private String userName;
    private long timestamp;

    public Memory() {} // חובה עבור Firebase

    public Memory(String greeting, String imageUrl, String userName, long timestamp) {
        this.greeting = greeting;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.timestamp = timestamp;
    }

    public String getGreeting() { return greeting; }
    public String getImageUrl() { return imageUrl; }
    public String getUserName() { return userName; }
    public long getTimestamp() { return timestamp; }
}
