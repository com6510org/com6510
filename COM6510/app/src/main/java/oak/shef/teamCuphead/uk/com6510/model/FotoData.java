package oak.shef.teamCuphead.uk.com6510.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

/**
 * This is the model data type for the photo which used to store the different information
 * In this model data, have the primarykey for id. id is automatic generate.
 * The FotoDate includes:
 * <ul>
 * <li>The photo title
 * <li>The photo description
 * <li>The photo path
 * <li>The photo date
 * <li>The photo latitude
 * <li>The photo longitude
 * <li>The photo type
 * </ul>
 */
@Entity()
public class FotoData {
    @PrimaryKey(autoGenerate = true)
    @android.support.annotation.NonNull
    private int id = 0;
    private String title;
    private String description;
    private String path;
    private String date;
    private Double latitude;
    private Double longitude;
    //easyimage photo :1 gallery photo :2
    private Double fototype;

    /**
     * Class constructor.
     *
     * @param title       Title is used to return title of the photo.
     * @param description Description is used to return the description of the photo.
     * @param path        The path is where to get from the device storage.
     * @param date        The date is when the photo taken
     * @param latitude    The latitude return the latitude where the photo taken.
     * @param longitude   The longitude return the longitude where the photo taken.
     * @param fototype    The fototype is used to declare where the photo store: gallery or cache.
     */
    public FotoData(String title, String description, String path, String date, Double latitude, Double longitude, Double fototype) {
        this.title = title;
        this.description = description;
        this.path = path;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
        this.fototype = fototype;
    }

    @android.support.annotation.NonNull
    /**
     * @return return the photo id
     */
    public int getId() {
        return id;
    }

    /**
     * This method used to get the id.
     *
     * @param id set the id
     */
    public void setId(@android.support.annotation.NonNull int id) {
        this.id = id;
    }

    /**
     * @return return the photo title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title set the title.
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * This method used to get the description.
     *
     * @return return the photo description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description set the description.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * This method used to get the path.
     *
     * @return return the photo path
     */
    public String getPath() {
        return path;
    }

    /**
     * @param path set the path.
     */
    public void setPath(String path) {
        this.path = path;
    }

    /**
     * This method used to get the date when the photo taken.
     *
     * @return return the photo date
     */
    public String getDate() {
        return date;
    }

    /**
     * @param date set the date.
     */
    public void setDate(String date) {
        this.date = date;
    }

    /**
     * This method used to get the latitude.
     *
     * @return return the photo latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     * @param latitude set the latitude.
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     * This method used to get the longitude.
     *
     * @return return the photo longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     * @param longitude set the longitude.
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * This method used to get the photo type.
     *
     * @return return the photo type
     */
    public Double getFototype() {
        return fototype;
    }

    /**
     * @param fototype set the fototype.
     */
    public void setFototype(Double fototype) {
        this.fototype = fototype;
    }

    /**
     * This method used to get the photo information.
     *
     * @return return the photo information
     */
    public String toString() {
        return "Fotadata: " +
                "id: " + id +
                " title:" + title +
                " des: " + description +
                " path: " + path +
                " date: " + date +
                " latitude: " + latitude +
                " longitude:" + longitude;
    }

}
