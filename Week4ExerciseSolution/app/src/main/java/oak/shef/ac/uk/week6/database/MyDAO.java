package oak.shef.ac.uk.week6.database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MyDAO {
    @Insert
    void insertAll(FotoData... fotoData);

    @Insert
    void insert(FotoData fotoData);

    @Delete
    void delete(FotoData fotoData);

    @Update()
    void update(FotoData fotoData);

    @Update()
    int update(FotoData... fotoData);

    // it selects a random element
    @Query("SELECT * FROM fotoData ORDER BY RANDOM() LIMIT 1")
    LiveData<FotoData> retrieveOneFoto();

    @Query("SELECT * FROM fotoData")
    List<FotoData> retrieveAllFoto();

    @Query("SELECT id, path FROM fotoData")
    List<FotoData> retrieveAllPaths();

    @Query("SELECT * FROM fotoData WHERE path= :fotopath LIMIT 1")
    LiveData<FotoData> retrieveSelectFoto(String fotopath);

    @Query("SELECT * FROM fotoData WHERE title LIKE '%'||:title||'%' or description LIKE '%'||:desc||'%' or date LIKE '%'||:date||'%'")
    List<FotoData> SearchFotos(String title, String desc, String date);

    @Query("SELECT * FROM fotoData WHERE date LIKE '%'||:date||'%'")
    List<FotoData> SearchFotosByDate( String date);

    @Query("SELECT * FROM fotoData WHERE title LIKE '%'||:title||'%'")
    List<FotoData> SearchFotosByTitle( String title);

    @Query("SELECT * FROM fotoData WHERE description LIKE '%'||:desc||'%'")
    List<FotoData> SearchFotosByDescription( String desc);

    @Query("SELECT * FROM fotoData WHERE description LIKE '%'||:desc||'%'  or  title LIKE '%'||:title||'%'")
    List<FotoData> SearchFotosByDescTitle( String desc, String title);

    @Query("SELECT * FROM fotoData WHERE description LIKE '%'||:desc||'%'  or  title LIKE '%'||:date||'%'")
    List<FotoData> SearchFotosByDescDate( String desc, String date);

    @Query("SELECT * FROM fotoData WHERE title LIKE '%'||:title||'%'  or  title LIKE '%'||:date||'%'")
    List<FotoData> SearchFotosByTitleDate( String title, String date);

    @Delete
    void deleteAll(FotoData... fotoData);

    @Query("DELETE FROM fotodata WHERE 1")
    void deleteAllFOTO();



    @Query("SELECT COUNT(*) FROM fotoData")
    int howManyElements();


}