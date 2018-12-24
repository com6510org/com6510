package oak.shef.ac.uk.week6.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

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

    @Query("SELECT * FROM fotoData")
    List<FotoData> retrieveAllFoto();

    @Query("SELECT * FROM fotoData WHERE path= :fotopath LIMIT 1")
    LiveData<FotoData> retrieveSelectFoto(String fotopath);

    @Delete
    void deleteAll(FotoData... fotoData);

    @Query("DELETE FROM fotodata WHERE 1")
    void deleteAllFOTO();

    @Query("SELECT COUNT(*) FROM fotoData")
    int howManyElements();
}