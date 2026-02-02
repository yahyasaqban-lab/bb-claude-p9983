package com.bbclaude.chat;

public class Message {
    public long id;
    public String role;
    public String content;
    public long timestamp;

    public Message(long id, String role, String content, long timestamp) {
        this.id = id;
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }
}
