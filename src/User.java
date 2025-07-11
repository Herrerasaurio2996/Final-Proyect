import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JOptionPane;

public class User {
    private String name = "";
    private String password = "";
    private List<Folder> folders = new ArrayList<>();
    private static final String USER_FILE = "users.txt";
    private static final Map<String, String> credentials = new HashMap<>();

// Load users from file when the class is first loaded
    static {
        loadCredentialsFromFile();
    }

    public User(String name, String password, List<Folder> folders) {
        this.name = name;
        this.password = password;
        this.folders = (folders != null) ? folders : new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public String getPassword() {
        return this.password;
    }

    public static boolean register(String name, String password) {
        if (credentials.containsKey(name)) {
            System.out.println("This username is already registered.");
            return false;
        }

        credentials.put(name, password);
        appendCredentialToFile(name, password);
        System.out.println("User registered successfully.");
        return true;
    }

    public boolean login(String name, String password) {
        if (credentials.containsKey(name) && credentials.get(name).equals(password)) {
            this.name = name;
            this.password = password;
            System.out.println("Login successful.");
            return true;
        } else {
            System.out.println("Invalid credentials.");
            return false;
        }
    }

    private static void loadCredentialsFromFile() {
        File file = new File(USER_FILE);

        if (!file.exists()) {
            return; // No users registered yet
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;

            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 2) {
                    credentials.put(parts[0], parts[1]);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users from file: " + e.getMessage());
        }
    }

    private static void appendCredentialToFile(String name, String password) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USER_FILE, true))) {
            writer.write(name + "," + password);
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error writing user to file: " + e.getMessage());
        }
    }

    // Helper method to create a Multimedia object based on the file extension
    // Returns a Photo, Gif, or Video object, or null if the file type is unsupported
    private static Multimedia createMediaFromExtension(String name, String path, int size, LocalDate date) {
        String ext = name.toLowerCase();
        // If the file is a photo (jpg, jpeg, png)
        if (ext.endsWith(".jpg") || ext.endsWith(".jpeg") || ext.endsWith(".png")) {
            return new Photo(name, path, size, date);
        // If the file is a gif
        } else if (ext.endsWith(".gif")) {
            return new Gif(name, path, size, date);
        // If the file is a video (mp4, mov, avi)
        } else if (ext.endsWith(".mp4") || ext.endsWith(".mov") || ext.endsWith(".avi")) {
            return new Video(name, path, size, date);
        // If the file type is not supported, show a dialog and return null
        } else {
            return null;
        }
    }

    public void uploadMultimedia(Multimedia multimedia) {
        if (folders != null && !folders.isEmpty()) {
            folders.get(0).addMultimedia(multimedia);
        } else {
            System.out.println("No folders available to add multimedia.");
        }
    }

    public void deleteMultimedia(Multimedia multimedia) {
        if (folders != null && !folders.isEmpty()) {
            folders.get(0).removeMultimedia(multimedia);
        } else {
            System.out.println("No folders available to delete multimedia.");
        }
    }

    public void downloadMultimedia(Multimedia multimedia) {
        if (folders != null && !folders.isEmpty()) {
            folders.get(0).saveMultimedia(multimedia);
        } else {
            System.out.println("No folders available to save multimedia.");
        }
    }
}
