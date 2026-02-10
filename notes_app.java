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

    // FIX: Use Logger instead of System.out.println (SonarQube "Code Smell")
    private static final Logger LOGGER = Logger.getLogger(SecureNotesApp.class.getName());

    public JTextArea noteArea;
    public JTextField filenameField;

    public SecureNotesApp() {
        // FIX: "Use static access with WindowConstants"
        // Old: setDefaultCloseOperation(EXIT_ON_CLOSE);
        // New: Explicitly referencing the class avoids ambiguity.
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        setSize(400, 300);
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

        // FIX: Path Traversal (Security)
        if (filename.contains("..") || filename.contains("/") || filename.contains("\\")) {
            JOptionPane.showMessageDialog(this, "Invalid Filename!");
            return;
        }

        Path file = Paths.get(System.getProperty("user.home"), "notes", filename);

        // FIX: "Use try-with-resources"
        // This ensures the file is closed automatically, even if an error occurs.
        try (BufferedWriter writer = Files.newBufferedWriter(file)) {
            writer.write(content);
            LOGGER.info("File saved to: " + file);
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to save file", e);
        }
    }

    private void adminLogin() {
        // FIX: "Revoke and change this password"
        // NEVER hardcode passwords. We now read from Environment Variables.
        String dbUrl = System.getenv("DB_URL");
        String dbUser = System.getenv("DB_USER");
        String dbPass = System.getenv("DB_PASSWORD");

        if (dbUrl == null || dbPass == null) {
            JOptionPane.showMessageDialog(this, "Database config missing in Env Vars!");
            return;
        }

        String inputUser = JOptionPane.showInputDialog("Enter Admin Username:");

        // FIX: "Use try-with-resources... Connection"
        try (Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPass)) {
            
            // FIX: SQL Injection (Critical)
            // Using PreparedStatement (?) instead of concatenating strings
            String query = "SELECT id FROM users WHERE username = ?";
            
            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, inputUser);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    JOptionPane.showMessageDialog(this, "Login Successful!");
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid User");
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error", e);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SecureNotesApp().setVisible(true));
    }
}
