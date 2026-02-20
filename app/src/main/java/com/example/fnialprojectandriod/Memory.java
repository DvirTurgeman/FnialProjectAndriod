package com.example.fnialprojectandriod;

public class Memory {
    private String memoryId;
    private String greeting;
    private String imageUrl;
    private String userName;
    private String uploaderUid;
    private long timestamp;

    public Memory() {}

    public Memory(String memoryId, String greeting, String imageUrl, String userName, String uploaderUid, long timestamp) {
        this.memoryId = memoryId;
        this.greeting = greeting;
        this.imageUrl = imageUrl;
        this.userName = userName;
        this.uploaderUid = uploaderUid;
        this.timestamp = timestamp;
    }

    public String getMemoryId() { return memoryId; }
    public String getGreeting() { return greeting; }
    public String getImageUrl() { return imageUrl; }
    public String getUserName() { return userName; }
    public String getUploaderUid() { return uploaderUid; }
    public long getTimestamp() { return timestamp; }
}
