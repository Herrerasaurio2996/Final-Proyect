import java.time.LocalDate;

public class Photo implements Multimedia{
    private String name = "";
    private String route = "";
    private Integer size = 0; // Size of the photo file, default is 0
    private LocalDate date = LocalDate.now(); // Date of the photo file, default is LocalDate.MIN

    public Photo(String name, String route, Integer size, LocalDate date) {
        this.name = name;
        this.route = route;
        this.size = size;
        this.date = date; // Use the received parameter
    }

    public String getName() {
        return this.name;
    }

    public String getRoute() {
        return this.route;
    }

    public Integer getSize() {
        return this.size; // Returns the size of the Photo file
    }

    public LocalDate getDate() {
        return this.date; // Returns the date of the Photo file
    }

    @Override
    public String toString() {
        return "Photo{name='" + name + "', route='" + route + "', size=" + size + "KB, date=" + date.toString() + "}";
    }


}