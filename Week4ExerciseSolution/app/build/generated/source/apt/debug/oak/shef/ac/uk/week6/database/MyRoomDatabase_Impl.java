package oak.shef.ac.uk.week6.database;

import android.arch.persistence.db.SupportSQLiteDatabase;
import android.arch.persistence.db.SupportSQLiteOpenHelper;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Callback;
import android.arch.persistence.db.SupportSQLiteOpenHelper.Configuration;
import android.arch.persistence.room.DatabaseConfiguration;
import android.arch.persistence.room.InvalidationTracker;
import android.arch.persistence.room.RoomOpenHelper;
import android.arch.persistence.room.RoomOpenHelper.Delegate;
import android.arch.persistence.room.util.TableInfo;
import android.arch.persistence.room.util.TableInfo.Column;
import android.arch.persistence.room.util.TableInfo.ForeignKey;
import android.arch.persistence.room.util.TableInfo.Index;
import java.lang.IllegalStateException;
import java.lang.Override;
import java.lang.String;
import java.util.HashMap;
import java.util.HashSet;

public class MyRoomDatabase_Impl extends MyRoomDatabase {
  private volatile MyDAO _myDAO;

  protected SupportSQLiteOpenHelper createOpenHelper(DatabaseConfiguration configuration) {
    final SupportSQLiteOpenHelper.Callback _openCallback = new RoomOpenHelper(configuration, new RoomOpenHelper.Delegate(1) {
      public void createAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("CREATE TABLE IF NOT EXISTS `FotoData` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `title` TEXT, `description` TEXT, `path` TEXT, `date` TEXT, `latitude` REAL, `longitude` REAL)");
        _db.execSQL("CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        _db.execSQL("INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, \"f3be8a3a22a330b63ff8608ebd0d942f\")");
      }

      public void dropAllTables(SupportSQLiteDatabase _db) {
        _db.execSQL("DROP TABLE IF EXISTS `FotoData`");
      }

      protected void onCreate(SupportSQLiteDatabase _db) {
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onCreate(_db);
          }
        }
      }

      public void onOpen(SupportSQLiteDatabase _db) {
        mDatabase = _db;
        internalInitInvalidationTracker(_db);
        if (mCallbacks != null) {
          for (int _i = 0, _size = mCallbacks.size(); _i < _size; _i++) {
            mCallbacks.get(_i).onOpen(_db);
          }
        }
      }

      protected void validateMigration(SupportSQLiteDatabase _db) {
        final HashMap<String, TableInfo.Column> _columnsFotoData = new HashMap<String, TableInfo.Column>(7);
        _columnsFotoData.put("id", new TableInfo.Column("id", "INTEGER", true, 1));
        _columnsFotoData.put("title", new TableInfo.Column("title", "TEXT", false, 0));
        _columnsFotoData.put("description", new TableInfo.Column("description", "TEXT", false, 0));
        _columnsFotoData.put("path", new TableInfo.Column("path", "TEXT", false, 0));
        _columnsFotoData.put("date", new TableInfo.Column("date", "TEXT", false, 0));
        _columnsFotoData.put("latitude", new TableInfo.Column("latitude", "REAL", false, 0));
        _columnsFotoData.put("longitude", new TableInfo.Column("longitude", "REAL", false, 0));
        final HashSet<TableInfo.ForeignKey> _foreignKeysFotoData = new HashSet<TableInfo.ForeignKey>(0);
        final HashSet<TableInfo.Index> _indicesFotoData = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoFotoData = new TableInfo("FotoData", _columnsFotoData, _foreignKeysFotoData, _indicesFotoData);
        final TableInfo _existingFotoData = TableInfo.read(_db, "FotoData");
        if (! _infoFotoData.equals(_existingFotoData)) {
          throw new IllegalStateException("Migration didn't properly handle FotoData(oak.shef.ac.uk.week6.database.FotoData).\n"
                  + " Expected:\n" + _infoFotoData + "\n"
                  + " Found:\n" + _existingFotoData);
        }
      }
    }, "f3be8a3a22a330b63ff8608ebd0d942f");
    final SupportSQLiteOpenHelper.Configuration _sqliteConfig = SupportSQLiteOpenHelper.Configuration.builder(configuration.context)
        .name(configuration.name)
        .callback(_openCallback)
        .build();
    final SupportSQLiteOpenHelper _helper = configuration.sqliteOpenHelperFactory.create(_sqliteConfig);
    return _helper;
  }

  @Override
  protected InvalidationTracker createInvalidationTracker() {
    return new InvalidationTracker(this, "FotoData");
  }

  @Override
  public MyDAO myDao() {
    if (_myDAO != null) {
      return _myDAO;
    } else {
      synchronized(this) {
        if(_myDAO == null) {
          _myDAO = new MyDAO_Impl(this);
        }
        return _myDAO;
      }
    }
  }
}
