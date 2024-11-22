//Karanei Kimutai Michael,ICS,183523,04/11/24
package com.karanei.sufeeds;

public class Feedback {
    private int feedbackId;
    private String studentId;
    private String content;
    private String timestamp;

    // Constructor, getters, and setters
    public Feedback(int feedbackId, String studentId, String content, String timestamp) {
        this.feedbackId = feedbackId;
        this.studentId = studentId;
        this.content = content;
        this.timestamp = timestamp;
    }

    // Getters and setters
    public int getFeedbackId() { return feedbackId; }
    public String getStudentId() { return studentId; }
    public String getContent() { return content; }
    public String getTimestamp() { return timestamp; }
}

/*This attempt was brought to life by Karanei😭*/

