//Karanei Kimutai Michael,183523,ICS,04/11/2024
package com.karanei.sufeeds;

public class Student {
    private String studentId;
    private String password;
    private String course;
    private String[] subjects;

    // Constructor, getters, and setters
    public Student(String studentId, String password, String course, String[] subjects) {
        this.studentId = studentId;
        this.password = password;
        this.course = course;
        this.subjects = subjects;
    }

    // Getters and setters
    public String getStudentId() { return studentId; }
    public String getPassword() { return password; }
    public String getCourse() { return course; }
    public String[] getSubjects() { return subjects; }
}
//"Oh Antonio, my Antonio, do you ever wonder why or where I am now, ddo you ever feel ashamed?"