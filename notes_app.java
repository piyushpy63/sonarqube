import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SecureNotesApp extends JFrame {

    // FIX: Use a Logger instead of System.out.println
    private static final Logger LOGGER = Logger.getLogger(SecureNotesApp.class.getName());

    // FIX: Constants for UI dimensions (Magic Numbers)
    private static final int WINDOW_WIDTH = 400;
    private static final int WINDOW_HEIGHT = 300;
    private static final int FIELD_COLUMNS = 15;

    // FIX: Load credentials from Environment Variables (Never hardcode!)
    private static final String DB_URL = System.getenv("DB_URL"); // e.g., jdbc:mysql://localhost:3306/notes_db
    private static final String DB_USER = System.getenv("DB_USER");
    private static final String DB_PASSWORD = System.getenv("DB_PASSWORD");

    private final JTextArea noteArea;
    private final JTextField filenameField;

    public SecureNotesApp() {
        setTitle("Secure Notes App");
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        
        // FIX: Explicit static access (SonarQube Maintainability issue)
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        noteArea = new JTextArea();
        add(new JScrollPane(noteArea), BorderLayout.CENTER);

        JPanel panel = new JPanel();
        filenameField = new JTextField(FIELD_COLUMNS);
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

        // FIX: Path Traversal Vulnerability
        // Validate that the filename does not contain ".." or slashes
        if (filename == null || filename.trim().isEmpty() || filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            JOptionPane.showMessageDialog(this, "Invalid filename!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Use standard directory
        Path basePath = Paths.get(System.getProperty("user.home"), "notes");
        Path filePath = basePath.resolve(filename);

        // FIX: Try-with-resources (Closes the writer automatically)
        try {
            // Ensure directory exists
            Files.createDirectories(basePath);
            
            try (BufferedWriter writer = Files.newBufferedWriter(filePath)) {
                writer.write(content);
                LOGGER.info(() -> "File saved successfully to " + filePath);
                JOptionPane.showMessageDialog(this, "Saved!");
            }
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save file", e);
            JOptionPane.showMessageDialog(this, "Error saving file.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void adminLogin() {
        String user = JOptionPane.showInputDialog("Enter Admin Username:");
        if (user == null || user.trim().isEmpty()) return;

        // FIX: Check if DB Env vars are set before crashing
        if (DB_URL == null || DB_USER == null || DB_PASSWORD == null) {
            JOptionPane.showMessageDialog(this, "Database configuration missing (Env Vars).", "Config Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // FIX: Try-with-resources for Connection and PreparedStatement
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            
            // FIX: SQL Injection (Use PreparedStatement instead of concatenating strings)
            String query = "SELECT id FROM users WHERE username = ?";
            
            try (PreparedStatement pstmt = conn.prepareStatement(query)) {
                pstmt.setString(1, user); // Safely insert user input
                
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        LOGGER.info("Login Successful for user: " + user);
                        JOptionPane.showMessageDialog(this, "Login Successful!");
                    } else {
                        LOGGER.warning("Login Failed for user: " + user);
                        JOptionPane.showMessageDialog(this, "Invalid Username.");
                    }
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error", e);
            JOptionPane.showMessageDialog(this, "Database Error", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SecureNotesApp().setVisible(true));
    }
}
