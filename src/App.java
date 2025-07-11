import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.HashMap;

public class App {

    private static final String FILE_NAME = "GeneralFolder.txt";
    private static Folder generalFolder = new Folder("General Folder");
    private static JPanel imagePanel;
    private static JScrollPane scrollPane;

    private static User currentUser = new User("default", "1234", null);
    private static final String USERS_FILE = "users.txt";
    private static HashMap<String, String> users = new HashMap<>();

    public static void main(String[] args) {
        loadUsers();

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Pinberry");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setSize(900, 600);
            frame.setLocationRelativeTo(null);
            frame.setLayout(new BorderLayout());

            showLoginRegister(frame);

            if (currentUser == null || currentUser.getName().equals("default")) {
                System.exit(0);
            }

            JPanel buttonPanel = new JPanel(new GridLayout(3, 1, 10, 10));
            buttonPanel.setPreferredSize(new Dimension(200, 600));

            JButton resetScrollBtn = new JButton("🔁 Reset Scroll");
            JButton addMediaBtn = new JButton("➕ Add Media");
            JButton profileBtn = new JButton("👤 Go to Profile");

            buttonPanel.add(resetScrollBtn);
            buttonPanel.add(addMediaBtn);
            buttonPanel.add(profileBtn);
            frame.add(buttonPanel, BorderLayout.WEST);

            imagePanel = new JPanel(new GridLayout(0, 3, 10, 10));
            scrollPane = new JScrollPane(imagePanel);
            frame.add(scrollPane, BorderLayout.CENTER);

            resetScrollBtn.addActionListener(e ->
                scrollPane.getVerticalScrollBar().setValue(0)
            );

            addMediaBtn.addActionListener(e -> {
                JFileChooser fileChooser = new JFileChooser();
                int option = fileChooser.showOpenDialog(frame);
                if (option == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String path = file.getAbsolutePath();
                    String name = file.getName();
                    int size = (int) file.length();
                    LocalDate date = LocalDate.now();
                    Multimedia media = createMediaFromExtension(name, path, size, date);
                    if (media != null) {
                        generalFolder.addMultimedia(media);
                        saveMediaToFile(media);
                        showMedia(media);
                        if (currentUser != null) {
                            currentUser.uploadMultimedia(media);
                            }
                        }                }
            });

            profileBtn.addActionListener(e -> {
                UserFrame userFrame = new UserFrame();
                userFrame.showUserFrame();
            });

            loadMediaFromFile();

            frame.setVisible(true);
        });
    }

    private static Multimedia createMediaFromExtension(String name, String path, int size, LocalDate date) {
        String lowerName = name.toLowerCase();
        if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg") || lowerName.endsWith(".png")) {
            return new Photo(name, path, size, date);
        } else if (lowerName.endsWith(".gif")) {
            return new Gif(name, path, size, date);
        } else if (lowerName.endsWith(".mp4") || lowerName.endsWith(".avi") || lowerName.endsWith(".mov")) {
            return new Video(name, path, size, date);
        }
        return null;
    }

    private static void showLoginRegister(JFrame frame) {
        boolean loggedIn = false;
        while (!loggedIn) {
            JPanel panel = new JPanel(new GridLayout(3, 2, 10, 10));
            JTextField usernameField = new JTextField();
            JPasswordField passwordField = new JPasswordField();

            panel.add(new JLabel("Username:"));
            panel.add(usernameField);
            panel.add(new JLabel("Password:"));
            panel.add(passwordField);

            int option = JOptionPane.showOptionDialog(
                frame,
                panel,
                "Login or Register",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE,
                null,
                new String[]{"Login", "Register"},
                "Login"
            );

            if (option == JOptionPane.CLOSED_OPTION) {
                currentUser = null;
                break;
            }

            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Username and password cannot be empty.");
                continue;
            }

            if (option == 1) { // Register
                if (users.containsKey(username)) {
                    JOptionPane.showMessageDialog(frame, "Username '" + username + "' already exists.");
                    continue;
                }
                users.put(username, password);
                saveUsers();
                currentUser = new User(username, password, null);
                new File("users/" + username).mkdirs();
                try {
                    new File("users/" + username + "/folders.txt").createNewFile();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(frame, "Error creating user folder file: " + ex.getMessage());
                    continue;
                }
                JOptionPane.showMessageDialog(frame, "Registration successful. You are now logged in.");
                loggedIn = true;
            } else if (option == 0) { // Login
                if (!users.containsKey(username) || !users.get(username).equals(password)) {
                    JOptionPane.showMessageDialog(frame, "Invalid username or password.");
                    continue;
                }
                currentUser = new User(username, password, null);
                loggedIn = true;
            }
        }
    }

    private static void loadUsers() {
        File file = new File(USERS_FILE);
        if (!file.exists()) return;
        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] parts = scanner.nextLine().split(";");
                if (parts.length == 2) {
                    users.put(parts[0], parts[1]);
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
    }

    private static void saveUsers() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(USERS_FILE))) {
            for (String username : users.keySet()) {
                writer.write(username + ";" + users.get(username));
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    private static void saveMediaToFile(Multimedia media) {
        String type = "PHOTO";
        if (media instanceof Gif) type = "GIF";
        else if (media instanceof Video) type = "VIDEO";

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_NAME, true))) {
            writer.write(type + ";" + media.getName() + ";" + media.getRoute() + ";" + media.getSize() + ";" + media.getDate());
            writer.newLine();
        } catch (IOException e) {
            System.out.println("Error saving media: " + e.getMessage());
        }

        if (media instanceof Photo && currentUser != null) {
            copyPhotoToUserFolder((Photo) media, currentUser.getName());
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("User.txt", true))) {
                writer.write("PHOTO;" + media.getName() + ";" + media.getRoute() + ";" + media.getSize() + ";" + media.getDate());
                writer.newLine();
            } catch (IOException ex) {
                System.out.println("Error saving user photo: " + ex.getMessage());
            }
        }
    }

    private static void copyPhotoToUserFolder(Photo photo, String username) {
        File userDir = new File("users/" + username);
        if (!userDir.exists()) userDir.mkdirs();
        File destFile = new File(userDir, photo.getName());
        try {
            Files.copy(new File(photo.getRoute()).toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            System.out.println("Error copying photo to user folder: " + e.getMessage());
        }
    }

    private static void loadMediaFromFile() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return;

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                String[] data = scanner.nextLine().split(";");
                if (data.length == 5) {
                    String type = data[0];
                    String name = data[1];
                    String path = data[2];
                    int size = Integer.parseInt(data[3]);
                    LocalDate date = LocalDate.parse(data[4]);

                    File f = new File(path);
                    if (!f.exists()) continue;

                    Multimedia media = null;
                    switch (type) {
                        case "PHOTO":
                            media = new Photo(name, path, size, date);
                            break;
                        case "GIF":
                            media = new Gif(name, path, size, date);
                            break;
                        case "VIDEO":
                            media = new Video(name, path, size, date);
                            break;
                    }

                    if (media != null) {
                        generalFolder.addMultimedia(media);
                        showMedia(media);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading media: " + e.getMessage());
        }
    }

    private static void showMedia(Multimedia media) {
        if (media instanceof Video) {
            JLabel label = new JLabel("🎬 " + media.getName());
            label.setHorizontalAlignment(SwingConstants.CENTER);
            imagePanel.add(label);
        } else {
            ImageIcon img = new ImageIcon(media.getRoute());
            Image scaled = img.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
            JLabel label = new JLabel(new ImageIcon(scaled));
            label.setToolTipText(media.getName());

            JButton saveBtn = new JButton("Save");
            saveBtn.addActionListener(e -> {
                new UserFrame().openPhotoActionsDialog((Photo) media, imagePanel);
            });

            JPanel container = new JPanel(new BorderLayout());
            container.add(label, BorderLayout.CENTER);
            container.add(saveBtn, BorderLayout.SOUTH);

            File photoFile = new File(media.getRoute());
            File userPhoto = new File("users/" + currentUser.getName() + "/" + media.getName());
            if (userPhoto.exists()) {
                // No agregar ningún botón de eliminar aquí
            }

            imagePanel.add(container);
        }
        imagePanel.revalidate();
        imagePanel.repaint();
    }

    private static void removePhotoFromGeneral(String photoName, String photoPath) {
        File inputFile = new File(FILE_NAME);
        File tempFile = new File("GeneralFolder_temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split(";");
                if (data.length < 3 || !(data[1].equals(photoName) && data[2].equals(photoPath))) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private static void removePhotoFromGallery(String photoName) {
        for (Component comp : imagePanel.getComponents()) {
            if (comp instanceof JPanel) {
                JPanel panel = (JPanel) comp;
                for (Component inner : panel.getComponents()) {
                    if (inner instanceof JLabel) {
                        JLabel label = (JLabel) inner;
                        if (photoName.equals(label.getToolTipText())) {
                            imagePanel.remove(panel);
                            imagePanel.revalidate();
                            imagePanel.repaint();
                            return;
                        }
                    }
                }
            }
        }
    }

    private static void removePhotoFromUserFile(String photoName) {
        File inputFile = new File("User.txt");
        File tempFile = new File("User_temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.contains("PHOTO;" + photoName + ";")) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private static void removePhotoFromFolderFile(String photoName, String photoPath, String folderName) {
        File inputFile = new File("users/" + currentUser.getName() + "/folders.txt");
        File tempFile = new File("users/" + currentUser.getName() + "/folders_temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!(line.startsWith("PHOTO;") && line.contains(";" + photoName + ";") && line.contains(";" + photoPath + ";") && line.endsWith(";" + folderName))) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    private static void removePhotoFromAllFolders(String photoName, String photoPath) {
        File inputFile = new File("users/" + currentUser.getName() + "/folders.txt");
        File tempFile = new File("users/" + currentUser.getName() + "/folders_temp.txt");
        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!(line.startsWith("PHOTO;") && line.contains(";" + photoName + ";") && line.contains(";" + photoPath + ";"))) {
                    writer.write(line);
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        inputFile.delete();
        tempFile.renameTo(inputFile);
    }

    // --- UserFrame class ---
    public static class UserFrame {
        private Folder userFolder = new Folder("User");
        private List<Folder> userFolders = new ArrayList<>();

        public void showUserFrame() {
            JFrame frame = new JFrame(currentUser.getName() + " profile");
            frame.setSize(800, 600);
            frame.setLayout(new BorderLayout());
            frame.setLocationRelativeTo(null);

            JPanel imagePanel = new JPanel(new GridLayout(0, 3, 10, 10));
            JScrollPane scrollPane = new JScrollPane(imagePanel);
            frame.add(scrollPane, BorderLayout.CENTER);
            loadUserPhotos(imagePanel);
            frame.setVisible(true);
        }

        private void loadUserPhotos(JPanel panel) {
            String userDirPath = "users/" + currentUser.getName();
            File userDir = new File(userDirPath);
            if (!userDir.exists()) return;
            File[] files = userDir.listFiles();
            if (files == null) return;
            for (File file : files) {
                String name = file.getName().toLowerCase();
                if (!(name.endsWith(".jpg") || name.endsWith(".jpeg") || name.endsWith(".png") || name.endsWith(".gif"))) continue;
                String path = file.getAbsolutePath();
                int size = (int) file.length();
                LocalDate date = LocalDate.now();
                Photo photo = new Photo(name, path, size, date);
                userFolder.addMultimedia(photo);

                ImageIcon img = new ImageIcon(photo.getRoute());
                Image scaled = img.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                JLabel label = new JLabel(new ImageIcon(scaled));
                label.setToolTipText(photo.getName());

                JButton saveBtn = new JButton("Save");
                saveBtn.addActionListener(e -> openPhotoActionsDialog(photo, panel));

                JPanel container = new JPanel(new BorderLayout());
                container.add(label, BorderLayout.CENTER);
                container.add(saveBtn, BorderLayout.SOUTH);

                File photoFile = new File(photo.getRoute());
                File userPhoto = new File("users/" + currentUser.getName() + "/" + photo.getName());
                if (userPhoto.exists()) {
                    // No agregar ningún botón de eliminar aquí
                }

                panel.add(container);
            }
        }

        private void openPhotoActionsDialog(Photo photo, JPanel panel) {
            JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(panel), "Photo Actions", false);
            dialog.setSize(400, 300);
            dialog.setLayout(new GridLayout(5, 1));
            JButton newFolderBtn = new JButton("📂 Create Folder");
            JButton addToFolderBtn = new JButton("➕ Add to Folder");
            JButton viewFolderBtn = new JButton("👁️ View Folder");
            JButton deletePhotoBtn = new JButton("🗑️ Delete Photo");
            JButton deleteFolderBtn = new JButton("🗑️ Delete Folder");

            newFolderBtn.addActionListener(e -> {
                String folderName = JOptionPane.showInputDialog(dialog, "Enter folder name:");
                if (folderName != null && !folderName.trim().isEmpty()) {
                    saveFolder(folderName);
                    JOptionPane.showMessageDialog(dialog, "Folder created: " + folderName);
                }
            });

            addToFolderBtn.addActionListener(e -> {
                DefaultListModel<String> folderModel = new DefaultListModel<>();
                loadUserFolders(folderModel);
                String folderName = (String) JOptionPane.showInputDialog(dialog, "Select folder:", "Add to Folder",
                        JOptionPane.PLAIN_MESSAGE, null, folderModel.toArray(), null);
                if (folderName != null) {
                    copyPhotoToFolder(photo, folderName);
                    JOptionPane.showMessageDialog(dialog, "Photo added to folder: " + folderName);
                }
            });

            viewFolderBtn.addActionListener(e -> {
                DefaultListModel<String> folderModel = new DefaultListModel<>();
                loadUserFolders(folderModel);
                String folderName = (String) JOptionPane.showInputDialog(dialog, "Select folder:", "View Folder",
                        JOptionPane.PLAIN_MESSAGE, null, folderModel.toArray(), null);
                if (folderName != null) {
                    showPhotosInFolder(folderName);
                }
            });

            deletePhotoBtn.addActionListener(e -> {
                File photoFile = new File("users/" + currentUser.getName() + "/" + photo.getName());
                if (photoFile.exists()) {
                    int confirm = JOptionPane.showConfirmDialog(dialog, "Delete this photo permanently?", "Confirm", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        boolean deleted = photoFile.delete();
                        if (deleted) {
                            removePhotoFromUserFile(photo.getName());
                            removePhotoFromGeneral(photo.getName(), photo.getRoute());
                            removePhotoFromAllFolders(photo.getName(), photo.getRoute());
                            removePhotoFromGallery(photo.getName());
                            panel.remove(SwingUtilities.getAncestorOfClass(JPanel.class, (Component) e.getSource()));
                            panel.revalidate();
                            panel.repaint();
                            JOptionPane.showMessageDialog(dialog, "Photo deleted.");
                            dialog.dispose();
                        } else {
                            JOptionPane.showMessageDialog(dialog, "Could not delete the photo.");
                        }
                    }
                } else {
                    JOptionPane.showMessageDialog(dialog, "You can only delete your own photos.");
                }
            });

            deleteFolderBtn.addActionListener(e -> {
                DefaultListModel<String> folderModel = new DefaultListModel<>();
                loadUserFolders(folderModel);
                String folderName = (String) JOptionPane.showInputDialog(dialog, "Select folder to delete:", "Delete Folder",
                        JOptionPane.PLAIN_MESSAGE, null, folderModel.toArray(), null);
                if (folderName != null) {
                    deleteFolder(folderName);
                    JOptionPane.showMessageDialog(dialog, "Folder deleted: " + folderName);
                }
            });

            dialog.add(newFolderBtn);
            dialog.add(addToFolderBtn);
            dialog.add(viewFolderBtn);
            dialog.add(deletePhotoBtn);
            dialog.add(deleteFolderBtn);
            dialog.setLocationRelativeTo(panel);
            dialog.setVisible(true);
        }

        private void loadUserFolders(DefaultListModel<String> model) {
            File file = new File(getUserFoldersFile());
            if (!file.exists()) return;
            try (Scanner scanner = new Scanner(file)) {
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if (line.startsWith("FOLDER;")) {
                        String folderName = line.split(";")[1];
                        model.addElement(folderName);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        private void saveFolder(String folderName) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserFoldersFile(), true))) {
                writer.write("FOLDER;" + folderName);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void copyPhotoToFolder(Photo photo, String folderName) {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(getUserFoldersFile(), true))) {
                writer.write("PHOTO;" + photo.getName() + ";" + photo.getRoute() + ";" + photo.getSize() + ";" + photo.getDate() + ";" + folderName);
                writer.newLine();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void deleteFolder(String folderName) {
            File file = new File(getUserFoldersFile());
            File tempFile = new File("UserFolders_temp.txt");
            try (BufferedReader reader = new BufferedReader(new FileReader(file));
                 BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.equals("FOLDER;" + folderName)) continue;
                    if (line.startsWith("PHOTO;") && line.endsWith(";" + folderName)) continue;
                    writer.write(line);
                    writer.newLine();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file.delete()) {
                tempFile.renameTo(file);
            }
        }

        private String getUserFoldersFile() {
            return "users/" + currentUser.getName() + "/folders.txt";
        }

        private void showPhotosInFolder(String folderName) {
            JFrame photosFrame = new JFrame("Photos in " + folderName);
            photosFrame.setSize(600, 400);
            photosFrame.setLayout(new BorderLayout());
            photosFrame.setLocationRelativeTo(null);

            JPanel panel = new JPanel(new GridLayout(0, 3, 10, 10));
            File file = new File(getUserFoldersFile());
            if (file.exists()) {
                try (Scanner scanner = new Scanner(file)) {
                    while (scanner.hasNextLine()) {
                        String line = scanner.nextLine();
                        if (line.startsWith("PHOTO;")) {
                            String[] data = line.split(";");
                            if (data.length == 6 && data[5].equals(folderName)) {
                                String name = data[1];
                                String path = data[2];
                                int size = Integer.parseInt(data[3]);
                                LocalDate date = LocalDate.parse(data[4]);
                                Photo photo = new Photo(name, path, size, date);

                                ImageIcon img = new ImageIcon(photo.getRoute());
                                Image scaled = img.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH);
                                JLabel label = new JLabel(new ImageIcon(scaled));
                                label.setToolTipText(photo.getName());

                                File photoFile = new File(photo.getRoute());
                                JPanel container = new JPanel(new BorderLayout());
                                container.add(label, BorderLayout.CENTER);

                                File userPhoto = new File("users/" + currentUser.getName() + "/" + photo.getName());
                                if (userPhoto.exists()) {
                                    // No agregar ningún botón de eliminar aquí
                                }

                                panel.add(container);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            photosFrame.add(new JScrollPane(panel), BorderLayout.CENTER);
            photosFrame.setVisible(true);
            photosFrame.toFront();
            photosFrame.requestFocus();
            photosFrame.setAlwaysOnTop(false);
        }
    }
}
