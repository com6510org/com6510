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

    public FotoData(String title, String description, String path) {
        this.title= title;
        this.description= description;
        this.path= path;
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
}
