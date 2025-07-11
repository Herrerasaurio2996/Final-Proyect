import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;

public class Folder {
    private String folderName = "";
    private List<Multimedia> files = new ArrayList<>();

    public Folder(String folderName) {
        this.folderName = folderName;
        this.files = new ArrayList<>();
        
    }

    public void addMultimedia(Multimedia multimedia) {
        files.add(multimedia);
        System.out.println("Multimedia added to folder: " + folderName);
    }

    public void removeMultimedia(Multimedia multimedia) {
        if (files.remove(multimedia)) {
            System.out.println("Multimedia removed from folder: " + folderName);
        } else {
            System.out.println("Multimedia not found in folder: " + folderName);
        }
    }

    public void saveMultimedia(Multimedia multimedia) {
        System.out.println("Multimedia saved in folder: " + folderName);
        // Aquí puedes implementar lógica adicional si es distinta a agregar
    }

    public String getName() {
        return folderName;
    }

    public void setName(String folderName) {
        this.folderName = folderName;
    }

    public List<Multimedia> getFiles() {
        return files;
    }

    // Copies the physical image file to the user's personal directory.
    // This supports the separation of media files per user in the filesystem.
    public void copyPhotoToUserFolder(Photo photo, String username) {
        File userDir = new File("users/" + username);
        if (!userDir.exists()) {
            userDir.mkdirs(); // Create directory if it doesn't exist
    }

    File destFile = new File(userDir, photo.getName());

    try {
        Files.copy(
            new File(photo.getRoute()).toPath(),
            destFile.toPath(),
            StandardCopyOption.REPLACE_EXISTING
        );
    } catch (IOException e) {
        System.out.println("Error copying photo to user folder: " + e.getMessage());
    }
}


    public void saveMultimediaToFile(Multimedia media, User user) {
        String type = "PHOTO";
        if (media instanceof Gif) type = "GIF";
        else if (media instanceof Video) type = "VIDEO";

        // Guardar en General.txt
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("General.txt", true))) {
        writer.write(type + ";" + media.getName() + ";" + media.getRoute() + ";" + media.getSize() + ";" + media.getDate());
        writer.newLine();
        } catch (IOException e) {
        System.out.println("Error saving media: " + e.getMessage());
        }

        // Guardar en carpeta de usuario (si es foto)
            if (media instanceof Photo && user != null) {
                this.copyPhotoToUserFolder((Photo) media, user.getName());

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("User.txt", true))) {
                writer.write("PHOTO;" + media.getName() + ";" + media.getRoute() + ";" + media.getSize() + ";" + media.getDate());
                writer.newLine();
            } catch (IOException ex) {
            System.out.println("Error saving user photo: " + ex.getMessage());
            }
    }
}


    public void generalFolder() {
        this.folderName = "General Folder";
    }

    public void miniFolder(String customName) {
        this.folderName = "Mini Folder - " + customName;
    }

    public void userFolder(User name) {
        this.folderName =  "User Folder  - " + name.getName();
    }

    public void getMultimediaDevice() {
        //This method should be able to get the multimedia from the device
    }

    public void getMultimediaGeneral() {
        //This method should be able to get the multimedia from the general folder
    }
}