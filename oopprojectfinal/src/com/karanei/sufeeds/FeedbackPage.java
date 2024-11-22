//Karanei Kimutai Michael,183523,ICS,04/11/2024
package com.karanei.sufeeds;

//Import Java libraries for the GUI
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FeedbackPage extends JFrame {
    private String currentStudentId;
    private String currentCourse;
    private JTextArea feedbackArea;
    private JTextArea newFeedbackArea;
    private JButton submitButton;
    private JButton updateButton;
    private JButton deleteButton;
    private JButton backButton;  // New back button
    private List<com.karanei.sufeeds.Feedback> feedbackList;
    private JLabel courseLabel;

    //Constructor to launch the feedback page
    public FeedbackPage(String studentId) {
        this.currentStudentId = studentId;
        this.feedbackList = new ArrayList<>();

        fetchUserCourse();//Function defined further onward

        setTitle("Karanei - Subject logs,comments and Feedback");
        setSize(600, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JPanel mainPanel = new JPanel(new BorderLayout());

        // Create header panel with course label and back button
        JPanel headerPanel = new JPanel(new BorderLayout());
        courseLabel = new JLabel("Course: " + currentCourse);
        courseLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        backButton = new JButton("Back to Login");

        headerPanel.add(courseLabel, BorderLayout.WEST);
        headerPanel.add(backButton, BorderLayout.EAST);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        //Add header panel to the main panel on the upper side
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Feedback display area
        feedbackArea = new JTextArea();
        feedbackArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(feedbackArea);

        //Feedback input area
        JPanel inputPanel = new JPanel(new BorderLayout());
        newFeedbackArea = new JTextArea(3, 40);
        submitButton = new JButton("Submit");
        updateButton = new JButton("Update");
        deleteButton = new JButton("Delete");
       //Panel for the Submit, Update and Delete buttons at the bottom of the main panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(submitButton);
        buttonPanel.add(updateButton);
        buttonPanel.add(deleteButton);

        inputPanel.add(new JScrollPane(newFeedbackArea), BorderLayout.CENTER);
        inputPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(inputPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Add action listeners to the buttons
        submitButton.addActionListener(e -> submitFeedback());
        updateButton.addActionListener(e -> updateFeedback());
        deleteButton.addActionListener(e -> deleteFeedback());
        backButton.addActionListener(e -> backToLogin());  // New action listener

        // load feedback
        loadFeedback();
    }

    private void fetchUserCourse() {
        try (Connection conn = com.karanei.sufeeds.DatabaseConnection.getConnection()) {
            String query = "SELECT course FROM tbl_studentinfo WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentStudentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                currentCourse = rs.getString("course");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Course not regitered for during sign up,please contact the administrator.Error fetching user course: " + ex.getMessage());
        }
    }

    private void loadFeedback() {
        feedbackList.clear();
        feedbackArea.setText("");

        try (Connection conn = com.karanei.sufeeds.DatabaseConnection.getConnection()) {
            // Modified query to only show feedback from students in the same course
            String query = """
                SELECT f.*, s.course 
                FROM tbl_feedback f 
                JOIN tbl_studentinfo s ON f.student_id = s.student_id 
                WHERE s.course = ? 
                ORDER BY f.timestamp DESC
            """;

            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentCourse);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                com.karanei.sufeeds.Feedback feedback = new com.karanei.sufeeds.Feedback(
                        rs.getInt("feedback_id"),
                        rs.getString("student_id"),
                        rs.getString("content"),
                        rs.getString("timestamp")
                );
                feedbackList.add(feedback);

                // Display feedback with edit/delete options for own posts
                boolean isOwnFeedback = feedback.getStudentId().equals(currentStudentId);
                String controls = isOwnFeedback ?
                        "\n[To edit or delete, tap on the feedback then click the respective button at the bottom of the page]" : "";

                feedbackArea.append(String.format(
                        "Student ID: %s%nTime: %s%n%s%s%n%s%n%n",
                        feedback.getStudentId(),
                        feedback.getTimestamp(),
                        feedback.getContent(),
                        controls,
                        "-".repeat(50)
                ));
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading feedback: " + ex.getMessage());
        }
    }

    private void submitFeedback() {
        String content = newFeedbackArea.getText().trim();
        if (content.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Enter the feedback you want to submit");
            return;
        }

        try (Connection conn = com.karanei.sufeeds.DatabaseConnection.getConnection()) {
            String query = "INSERT INTO tbl_feedback (student_id, content) VALUES (?, ?)";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, currentStudentId);
            pstmt.setString(2, content);

            pstmt.executeUpdate();

            newFeedbackArea.setText("");
            loadFeedback();

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error submitting feedback: " + ex.getMessage());
        }
    }

    private void updateFeedback() {
        // Get selected feedback
        int selectedIndex = feedbackArea.getCaretPosition();
        com.karanei.sufeeds.Feedback selectedFeedback = null;

        // Find the feedback entry that contains the cursor position
        int currentPosition = 0;
        for (com.karanei.sufeeds.Feedback feedback : feedbackList) {
            String feedbackText = String.format(
                    "Student ID: %s%nTime: %s%n%s%n%s%n%n",
                    feedback.getStudentId(),
                    feedback.getTimestamp(),
                    feedback.getContent(),
                    "-".repeat(50)
            );

            int entryLength = feedbackText.length();
            if (selectedIndex >= currentPosition &&
                    selectedIndex < currentPosition + entryLength) {
                selectedFeedback = feedback;
                break;
            }
            currentPosition += entryLength;
        }

        if (selectedFeedback != null &&
                selectedFeedback.getStudentId().equals(currentStudentId)) {

            String newContent = JOptionPane.showInputDialog(
                    this,
                    "Update your feedback:",
                    selectedFeedback.getContent()
            );

            if (newContent != null && !newContent.trim().isEmpty()) {
                try (Connection conn = com.karanei.sufeeds.DatabaseConnection.getConnection()) {
                    String query = """
                        UPDATE tbl_feedback 
                        SET content = ? 
                        WHERE feedback_id = ? AND student_id = ?
                    """;

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setString(1, newContent.trim());
                    pstmt.setInt(2, selectedFeedback.getFeedbackId());
                    pstmt.setString(3, currentStudentId);

                    if (pstmt.executeUpdate() > 0) {
                        loadFeedback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error updating feedback: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select your own feedback to update.");
        }
    }

    private void deleteFeedback() {
        // Get selected feedback
        int selectedIndex = feedbackArea.getCaretPosition();
        com.karanei.sufeeds.Feedback selectedFeedback = null;

        // Find the feedback entry that contains the cursor position
        int currentPosition = 0;
        for (Feedback feedback: feedbackList) {
            String feedbackText = String.format(
                    "Student ID: %s%nTime: %s%n%s%n%s%n%n",
                    feedback.getStudentId(),
                    feedback.getTimestamp(),
                    feedback.getContent(),
                    "-".repeat(50)
            );

            int entryLength = feedbackText.length();
            if (selectedIndex >= currentPosition &&
                    selectedIndex < currentPosition + entryLength) {
                selectedFeedback = feedback;
                break;
            }
            currentPosition += entryLength;
        }

        if (selectedFeedback != null &&
                selectedFeedback.getStudentId().equals(currentStudentId)) {

            int confirm = JOptionPane.showConfirmDialog(
                    this,
                    "Are you sure you want to delete this feedback?",
                    "Confirm Delete",
                    JOptionPane.YES_NO_OPTION
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try (Connection conn = DatabaseConnection.getConnection()) {
                    String query = """
                        DELETE FROM tbl_feedback 
                        WHERE feedback_id = ? AND student_id = ?
                    """;

                    PreparedStatement pstmt = conn.prepareStatement(query);
                    pstmt.setInt(1, selectedFeedback.getFeedbackId());
                    pstmt.setString(2, currentStudentId);

                    if (pstmt.executeUpdate() > 0) {
                        loadFeedback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                    JOptionPane.showMessageDialog(this,
                            "Error deleting feedback: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Please select your own feedback to delete.");
        }
    }

    private void backToLogin() {
        int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            new LoginPage().setVisible(true);
            dispose();
        }
    }
}


//This page has content😭😭

