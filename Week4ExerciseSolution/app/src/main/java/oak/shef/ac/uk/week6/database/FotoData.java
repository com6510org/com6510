package oak.shef.ac.uk.week6.database;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

@Entity()
public class FotoData {
    @PrimaryKey(autoGenerate = true)
    @android.support.annotation.NonNull
    private int id=0;
    private String title;
    private String description;
    private String path;
    private String date;
    private Double latitude;
    private Double longitude;



    public FotoData(String title, String description, String path, String date,Double latitude,Double longitude) {
        this.title= title;
        this.description= description;
        this.path= path;
        this.date=date;
        this.latitude=latitude;
        this.longitude=longitude;
    }

    @android.support.annotation.NonNull
    public int getId() {
        return id;
    }
    public void setId(@android.support.annotation.NonNull int id) {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }
    public void setTitle(String title) {
        this.title= title;
    }

    public String getDescription()
    {
        return description;
    }
    public void setDescription(String description) {
        this.description= description;
    }

    public String getPath()
    {
        return path;
    }
    public void setPath(String path) {
        this.path= path;
    }


    public String getDate()
    {
        return date;
    }
    public void setDate(String date) {
        this.date= date;
    }

    public Double getLatitude()
    {
        return latitude;
    }
    public void setLatitude(Double latitude) {
        this.latitude= latitude;
    }

    public Double getLongitude()
    {
        return longitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude= longitude;
    }


}
