//Karanei Kimutai Michael,183523,ICS,04/11/2024
package com.karanei.sufeeds;


import javax.swing.*;
import java.awt.*;
import java.sql.*;

public class LoginPage extends JFrame {
    private JTextField studentIdField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton signupButton;

    public LoginPage() {
        setTitle("Karanei - Login");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        studentIdField = new JTextField(20);
        passwordField = new JPasswordField(20);
        loginButton = new JButton("Login");
        signupButton = new JButton("Sign Up");

        // Layout components
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1;
        panel.add(studentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(loginButton);
        buttonPanel.add(signupButton);
        panel.add(buttonPanel, gbc);

        add(panel);

        // Add action listeners
        loginButton.addActionListener(e -> login());
        signupButton.addActionListener(e -> openSignupPage());
    }

    private void login() {
        String studentId = studentIdField.getText();
        String password = new String(passwordField.getPassword());

        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM tbl_studentinfo WHERE student_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, studentId);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                if (password.equals(rs.getString("password"))) {
                    // Login successful
                    new FeedbackPage(studentId).setVisible(true);
                    this.dispose();
                } else {
                    JOptionPane.showMessageDialog(this, "Incorrect password!");
                }
            } else {
                JOptionPane.showMessageDialog(this,
                        "Student ID not found. Please sign up first.");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());

        }
    }

    private void openSignupPage() {
        new SignupPage().setVisible(true);
        this.dispose();
    }
}
//You learn more from failure than you ever do from inaction