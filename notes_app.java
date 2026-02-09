import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.sql.*;

public class notes_app extends JFrame {

    // VULNERABILITY: Hardcoded Credentials (Security Hotspot/Critical)
    private static final String DB_URL = "jdbc:mysql://localhost:3306/notes_db";
    private static final String DB_USER = "admin";
    private static final String DB_PASSWORD = "password123"; 

    public JTextArea noteArea;
    public JTextField filenameField;

    public notes_app() {
        // CODE SMELL: Magic Number (Maintainability)
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        noteArea = new JTextArea();
        add(new JScrollPane(noteArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        filenameField = new JTextField(15);
        JButton saveButton = new JButton("Save Note");
        JButton loginButton = new JButton("Admin Login");

        panel.add(new JLabel("Filename:"));
        panel.add(filenameField);
        panel.add(saveButton);
        panel.add(loginButton);
        add(panel, BorderLayout.SOUTH);

        saveButton.addActionListener(e -> saveToFile());
        loginButton.addActionListener(e -> adminLogin());
    }

    private void saveToFile() {
        String filename = filenameField.getText();
        String content = noteArea.getText();

        // VULNERABILITY: Path Traversal (Security)
        // User can enter "../../../etc/passwd" as filename
        File file = new File("c:\\notes\\" + filename); 

        try {
            // VULNERABILITY: Resource Leak (Reliability)
            // FileWriter is not closed in a finally block or try-with-resources
            FileWriter writer = new FileWriter(file);
            writer.write(content);
            
            // CODE SMELL: System.out.println (Maintainability)
            // Should use a Logger
            System.out.println("File saved successfully to " + file.getAbsolutePath());
            
        } catch (IOException e) {
            // VULNERABILITY: Empty Catch Block (Reliability)
            // Swallowing the exception helps no one
        }
    }

    private void adminLogin() {
        String user = JOptionPane.showInputDialog("Enter Admin Username:");
        
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            Statement stmt = conn.createStatement();
            
            // VULNERABILITY: SQL Injection (Critical Security)
            // Concatenating user input directly into the query
            String query = "SELECT * FROM users WHERE username = '" + user + "'";
            ResultSet rs = stmt.executeQuery(query);
            
            if(rs.next()) {
                System.out.println("Login Successful");
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        // CODE SMELL: Unused variable
        int unused = 10;
        
        SwingUtilities.invokeLater(() -> {
            new notes_app().setVisible(true);
        });
    }
}