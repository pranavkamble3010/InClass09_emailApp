package com.example.inclass09;

class Email {
    String subject;
    String created_at;
    String id;
    String senderFname;
    String senderLname;
    String message;
    String senderId;

    @Override
    public String toString() {
        return "Email{" +
                "subject='" + subject + '\'' +
                ", created_at='" + created_at + '\'' +
                ", id='" + id + '\'' +
                ", senderFname='" + senderFname + '\'' +
                ", senderLname='" + senderLname + '\'' +
                ", message='" + message + '\'' +
                ", senderId='" + senderId + '\'' +
                '}';
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderFname() {
        return senderFname;
    }

    public void setSenderFname(String senderFname) {
        this.senderFname = senderFname;
    }

    public String getSenderLname() {
        return senderLname;
    }

    public void setSenderLname(String senderLname) {
        this.senderLname = senderLname;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }
}
