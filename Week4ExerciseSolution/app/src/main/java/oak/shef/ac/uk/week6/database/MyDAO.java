package oak.shef.ac.uk.week6.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

@Dao
public interface MyDAO {
    @Insert
    void insertAll(FotoData... fotoData);

    @Insert
    void insert(FotoData fotoData);

    @Delete
    void delete(FotoData fotoData);

    // it selects a random element
    @Query("SELECT * FROM fotoData ORDER BY RANDOM() LIMIT 1")
    LiveData<FotoData> retrieveOneFoto();

    @Delete
    void deleteAll(FotoData... fotoData);

    @Query("SELECT COUNT(*) FROM fotoData")
    int howManyElements();
}