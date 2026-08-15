package com.xiaojiaoyin.baby.data.db;

import androidx.annotation.NonNull;
import androidx.room.InvalidationTracker;
import androidx.room.RoomOpenDelegate;
import androidx.room.migration.AutoMigrationSpec;
import androidx.room.migration.Migration;
import androidx.room.util.DBUtil;
import androidx.room.util.TableInfo;
import androidx.sqlite.SQLite;
import androidx.sqlite.SQLiteConnection;
import com.xiaojiaoyin.baby.data.db.dao.BabyDao;
import com.xiaojiaoyin.baby.data.db.dao.BabyDao_Impl;
import com.xiaojiaoyin.baby.data.db.dao.RecordDao;
import com.xiaojiaoyin.baby.data.db.dao.RecordDao_Impl;
import com.xiaojiaoyin.baby.data.db.dao.TodoDao;
import com.xiaojiaoyin.baby.data.db.dao.TodoDao_Impl;
import java.lang.Class;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.processing.Generated;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class AppDatabase_Impl extends AppDatabase {
  private volatile BabyDao _babyDao;

  private volatile RecordDao _recordDao;

  private volatile TodoDao _todoDao;

  @Override
  @NonNull
  protected RoomOpenDelegate createOpenDelegate() {
    final RoomOpenDelegate _openDelegate = new RoomOpenDelegate(1, "8175f1f0c0daeda883bb8dd2bbaa206a", "de0993c91a9de4ae051bdea57d2c6e47") {
      @Override
      public void createAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `baby` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `nickname` TEXT NOT NULL, `gender` TEXT NOT NULL, `birthDateTime` INTEGER NOT NULL, `avatarColorIndex` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `record` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `babyId` INTEGER NOT NULL, `type` TEXT NOT NULL, `occurredAt` INTEGER NOT NULL, `detailJson` TEXT NOT NULL, `note` TEXT NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS `todo` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `babyId` INTEGER NOT NULL, `title` TEXT NOT NULL, `timeAt` INTEGER NOT NULL, `remindEnabled` INTEGER NOT NULL, `completed` INTEGER NOT NULL, `createdAt` INTEGER NOT NULL)");
        SQLite.execSQL(connection, "CREATE TABLE IF NOT EXISTS room_master_table (id INTEGER PRIMARY KEY,identity_hash TEXT)");
        SQLite.execSQL(connection, "INSERT OR REPLACE INTO room_master_table (id,identity_hash) VALUES(42, '8175f1f0c0daeda883bb8dd2bbaa206a')");
      }

      @Override
      public void dropAllTables(@NonNull final SQLiteConnection connection) {
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `baby`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `record`");
        SQLite.execSQL(connection, "DROP TABLE IF EXISTS `todo`");
      }

      @Override
      public void onCreate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      public void onOpen(@NonNull final SQLiteConnection connection) {
        internalInitInvalidationTracker(connection);
      }

      @Override
      public void onPreMigrate(@NonNull final SQLiteConnection connection) {
        DBUtil.dropFtsSyncTriggers(connection);
      }

      @Override
      public void onPostMigrate(@NonNull final SQLiteConnection connection) {
      }

      @Override
      @NonNull
      public RoomOpenDelegate.ValidationResult onValidateSchema(
          @NonNull final SQLiteConnection connection) {
        final Map<String, TableInfo.Column> _columnsBaby = new HashMap<String, TableInfo.Column>(7);
        _columnsBaby.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaby.put("name", new TableInfo.Column("name", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaby.put("nickname", new TableInfo.Column("nickname", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaby.put("gender", new TableInfo.Column("gender", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaby.put("birthDateTime", new TableInfo.Column("birthDateTime", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaby.put("avatarColorIndex", new TableInfo.Column("avatarColorIndex", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsBaby.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysBaby = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesBaby = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoBaby = new TableInfo("baby", _columnsBaby, _foreignKeysBaby, _indicesBaby);
        final TableInfo _existingBaby = TableInfo.read(connection, "baby");
        if (!_infoBaby.equals(_existingBaby)) {
          return new RoomOpenDelegate.ValidationResult(false, "baby(com.xiaojiaoyin.baby.data.db.entity.BabyEntity).\n"
                  + " Expected:\n" + _infoBaby + "\n"
                  + " Found:\n" + _existingBaby);
        }
        final Map<String, TableInfo.Column> _columnsRecord = new HashMap<String, TableInfo.Column>(7);
        _columnsRecord.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecord.put("babyId", new TableInfo.Column("babyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecord.put("type", new TableInfo.Column("type", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecord.put("occurredAt", new TableInfo.Column("occurredAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecord.put("detailJson", new TableInfo.Column("detailJson", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecord.put("note", new TableInfo.Column("note", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsRecord.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysRecord = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesRecord = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoRecord = new TableInfo("record", _columnsRecord, _foreignKeysRecord, _indicesRecord);
        final TableInfo _existingRecord = TableInfo.read(connection, "record");
        if (!_infoRecord.equals(_existingRecord)) {
          return new RoomOpenDelegate.ValidationResult(false, "record(com.xiaojiaoyin.baby.data.db.entity.RecordEntity).\n"
                  + " Expected:\n" + _infoRecord + "\n"
                  + " Found:\n" + _existingRecord);
        }
        final Map<String, TableInfo.Column> _columnsTodo = new HashMap<String, TableInfo.Column>(7);
        _columnsTodo.put("id", new TableInfo.Column("id", "INTEGER", true, 1, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodo.put("babyId", new TableInfo.Column("babyId", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodo.put("title", new TableInfo.Column("title", "TEXT", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodo.put("timeAt", new TableInfo.Column("timeAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodo.put("remindEnabled", new TableInfo.Column("remindEnabled", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodo.put("completed", new TableInfo.Column("completed", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        _columnsTodo.put("createdAt", new TableInfo.Column("createdAt", "INTEGER", true, 0, null, TableInfo.CREATED_FROM_ENTITY));
        final Set<TableInfo.ForeignKey> _foreignKeysTodo = new HashSet<TableInfo.ForeignKey>(0);
        final Set<TableInfo.Index> _indicesTodo = new HashSet<TableInfo.Index>(0);
        final TableInfo _infoTodo = new TableInfo("todo", _columnsTodo, _foreignKeysTodo, _indicesTodo);
        final TableInfo _existingTodo = TableInfo.read(connection, "todo");
        if (!_infoTodo.equals(_existingTodo)) {
          return new RoomOpenDelegate.ValidationResult(false, "todo(com.xiaojiaoyin.baby.data.db.entity.TodoEntity).\n"
                  + " Expected:\n" + _infoTodo + "\n"
                  + " Found:\n" + _existingTodo);
        }
        return new RoomOpenDelegate.ValidationResult(true, null);
      }
    };
    return _openDelegate;
  }

  @Override
  @NonNull
  protected InvalidationTracker createInvalidationTracker() {
    final Map<String, String> _shadowTablesMap = new HashMap<String, String>(0);
    final Map<String, Set<String>> _viewTables = new HashMap<String, Set<String>>(0);
    return new InvalidationTracker(this, _shadowTablesMap, _viewTables, "baby", "record", "todo");
  }

  @Override
  public void clearAllTables() {
    super.performClear(false, "baby", "record", "todo");
  }

  @Override
  @NonNull
  protected Map<Class<?>, List<Class<?>>> getRequiredTypeConverters() {
    final Map<Class<?>, List<Class<?>>> _typeConvertersMap = new HashMap<Class<?>, List<Class<?>>>();
    _typeConvertersMap.put(BabyDao.class, BabyDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(RecordDao.class, RecordDao_Impl.getRequiredConverters());
    _typeConvertersMap.put(TodoDao.class, TodoDao_Impl.getRequiredConverters());
    return _typeConvertersMap;
  }

  @Override
  @NonNull
  public Set<Class<? extends AutoMigrationSpec>> getRequiredAutoMigrationSpecs() {
    final Set<Class<? extends AutoMigrationSpec>> _autoMigrationSpecsSet = new HashSet<Class<? extends AutoMigrationSpec>>();
    return _autoMigrationSpecsSet;
  }

  @Override
  @NonNull
  public List<Migration> getAutoMigrations(
      @NonNull final Map<Class<? extends AutoMigrationSpec>, AutoMigrationSpec> autoMigrationSpecs) {
    final List<Migration> _autoMigrations = new ArrayList<Migration>();
    return _autoMigrations;
  }

  @Override
  public BabyDao babyDao() {
    if (_babyDao != null) {
      return _babyDao;
    } else {
      synchronized(this) {
        if(_babyDao == null) {
          _babyDao = new BabyDao_Impl(this);
        }
        return _babyDao;
      }
    }
  }

  @Override
  public RecordDao recordDao() {
    if (_recordDao != null) {
      return _recordDao;
    } else {
      synchronized(this) {
        if(_recordDao == null) {
          _recordDao = new RecordDao_Impl(this);
        }
        return _recordDao;
      }
    }
  }

  @Override
  public TodoDao todoDao() {
    if (_todoDao != null) {
      return _todoDao;
    } else {
      synchronized(this) {
        if(_todoDao == null) {
          _todoDao = new TodoDao_Impl(this);
        }
        return _todoDao;
      }
    }
  }
}
