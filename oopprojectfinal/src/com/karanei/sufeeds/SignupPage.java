//Welcome to KaraneiVille. Enjoy your stay😭🙃
package com.karanei.sufeeds;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

public class SignupPage extends JFrame {
    private JTextField studentIdField;
    private JPasswordField passwordField;
    private JComboBox<String> courseComboBox;
    private JLabel unitsLabel;
    private JTextArea unitsTextArea;
    private JCheckBox unitsCheckBox;
    private JButton signupButton;
    private JButton backButton;
    private Map<String, String[]> courseSubjects;
    private Map<String, Integer> courseUnits;
    private Map<String, String[]> courseUnitNames;

    public SignupPage() {
        setTitle("Karanei - Sign Up");
        setSize(650, 650);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        initializeCourseSubjects();
        initializeCourseUnits();
        initializeCourseUnitNames();

        // Create components
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        studentIdField = new JTextField(30);
        passwordField = new JPasswordField(30);
        courseComboBox = new JComboBox<>(courseSubjects.keySet().toArray(new String[0]));
        unitsLabel = new JLabel();
        unitsTextArea = new JTextArea(7, 50);
        unitsTextArea.setEditable(false);
        unitsTextArea.setLineWrap(true);
        unitsTextArea.setWrapStyleWord(true);
        unitsCheckBox = new JCheckBox("Enroll in Units");
        signupButton = new JButton("Sign Up");
        backButton = new JButton("Back to Login");

        // GUI elements
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(new JLabel("Student ID:"), gbc);

        gbc.gridx = 1; gbc.gridy = 0;
        panel.add(studentIdField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(new JLabel("Password:"), gbc);

        gbc.gridx = 1; gbc.gridy = 1;
        panel.add(passwordField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(new JLabel("Course, Year and Semester:"), gbc);

        gbc.gridx = 1; gbc.gridy = 2;
        panel.add(courseComboBox, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(new JLabel("Units:"), gbc);

        gbc.gridx = 1; gbc.gridy = 3;
        panel.add(unitsLabel, gbc);

        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(new JLabel("Unit Names:"), gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        panel.add(new JScrollPane(unitsTextArea), gbc);

        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(unitsCheckBox, gbc);

        // Add buttons to a panel for better layout
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(signupButton);
        buttonPanel.add(backButton);

        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        add(panel);

        // Add action listeners
        signupButton.addActionListener(e -> signup());
        backButton.addActionListener(e -> backToLogin());
        courseComboBox.addActionListener(e -> updateUnitsInfo());
    }

    private void initializeCourseSubjects() {
        courseSubjects = new HashMap<>();
        courseSubjects.put("ICSYear1Sem2", new String[]{
                "Object Oriented Programming", "Database Systems", "Computer Networks",
                "Communication Skills 2", "Management",
                "Integral calculus", "Linear Algebra", "Maisha"
        });
        courseSubjects.put("BBITYear1Sem2",new String[]{"Database Systems","Accounting","Business Finance","Differential calculus","Philosophical anthropology"});
        // Add more courses and their subjects as needed
    }

    private void initializeCourseUnits() {
        courseUnits = new HashMap<>();
        courseUnits.put("ICSYear1Sem2", 8);
        courseUnits.put("BBITYear1Sem2", 5);
    }

    private void initializeCourseUnitNames() {
        courseUnitNames = new HashMap<>();
        courseUnitNames.put("ICSYear1Sem2", new String[]{
                "Object Oriented Programming", "Database Systems", "Computer Networks",
                "Communication Skills 2", "Management",
                "Integral calculus", "Linear Algebra", "Maisha"
        });
        courseUnitNames.put("BBITYear1Sem2", new String[]{"Database Systems","Accounting","Business Finance","Differential calculus","Philosophical anthropology"});
        //add more courses if you'd like and the subjects associated with them
    }

    private void updateUnitsInfo() {
        String selectedCourse = (String) courseComboBox.getSelectedItem();
        int units = courseUnits.get(selectedCourse);
        String[] unitNames = courseUnitNames.get(selectedCourse);

        unitsLabel.setText(String.valueOf(units));
        unitsTextArea.setText(String.join("\n", unitNames));
    }

    private void signup() {
        String studentId = studentIdField.getText();
        String password = new String(passwordField.getPassword());
        String course = (String) courseComboBox.getSelectedItem();
        boolean enrollInUnits = unitsCheckBox.isSelected();
        int units = courseUnits.get(course);
        String[] unitNames = courseUnitNames.get(course);

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if student ID already exists
            String checkQuery = "SELECT student_id FROM tbl_studentinfo WHERE student_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, studentId);

            if (checkStmt.executeQuery().next()) {
                JOptionPane.showMessageDialog(this, "Student ID already exists!");
                return;
            }

            if (studentIdField.getText().isEmpty()){
                JOptionPane.showMessageDialog(this,"Student ID needed");
                new SignupPage().setVisible(true);
                this.dispose();
            }
            else if (passwordField.getPassword().length==0){
                JOptionPane.showMessageDialog(this,"Password needed");
                new SignupPage().setVisible(true);
                this.dispose();
            }
            else {
                // Insert new student
                String insertQuery = "INSERT INTO tbl_studentinfo (student_id, password, course, units, units_enrolled, unit_names) VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement pstmt = conn.prepareStatement(insertQuery);
                pstmt.setString(1, studentId);
                pstmt.setString(2, password);
                pstmt.setString(3, course);
                pstmt.setInt(4, units);
                pstmt.setBoolean(5, enrollInUnits);
                pstmt.setString(6, String.join(",", unitNames));

                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(this, "Sign up successful!");
                new com.karanei.sufeeds.LoginPage().setVisible(true);
                this.dispose();
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Database error: " + ex.getMessage());
        }
    }

    private void backToLogin() {
        new LoginPage().setVisible(true);
        this.dispose();
    }
}