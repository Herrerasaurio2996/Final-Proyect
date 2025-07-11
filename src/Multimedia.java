import java.time.LocalDate;
/**
 * Interface representing a multimedia item.
 * It provides methods to retrieve the route, name, size, and date of the multimedia item.
 */

public interface  Multimedia {

    public abstract String getRoute();
    public abstract String getName();
    public abstract Integer getSize();
    public abstract LocalDate getDate();
    /**
     * public abstract String toString(); */
    
}
