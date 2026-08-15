package com.xiaojiaoyin.baby.data.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.xiaojiaoyin.baby.data.db.entity.TodoEntity;
import java.lang.Class;
import java.lang.Long;
import java.lang.NullPointerException;
import java.lang.Object;
import java.lang.Override;
import java.lang.String;
import java.lang.SuppressWarnings;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.processing.Generated;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class TodoDao_Impl implements TodoDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<TodoEntity> __insertAdapterOfTodoEntity;

  private final EntityDeleteOrUpdateAdapter<TodoEntity> __deleteAdapterOfTodoEntity;

  private final EntityDeleteOrUpdateAdapter<TodoEntity> __updateAdapterOfTodoEntity;

  public TodoDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfTodoEntity = new EntityInsertAdapter<TodoEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `todo` (`id`,`babyId`,`title`,`timeAt`,`remindEnabled`,`completed`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TodoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBabyId());
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        statement.bindLong(4, entity.getTimeAt());
        final int _tmp = entity.getRemindEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.getCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__deleteAdapterOfTodoEntity = new EntityDeleteOrUpdateAdapter<TodoEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `todo` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TodoEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfTodoEntity = new EntityDeleteOrUpdateAdapter<TodoEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `todo` SET `id` = ?,`babyId` = ?,`title` = ?,`timeAt` = ?,`remindEnabled` = ?,`completed` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final TodoEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBabyId());
        if (entity.getTitle() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getTitle());
        }
        statement.bindLong(4, entity.getTimeAt());
        final int _tmp = entity.getRemindEnabled() ? 1 : 0;
        statement.bindLong(5, _tmp);
        final int _tmp_1 = entity.getCompleted() ? 1 : 0;
        statement.bindLong(6, _tmp_1);
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final TodoEntity todo, final Continuation<? super Long> arg1) {
    if (todo == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfTodoEntity.insertAndReturnId(_connection, todo);
    }, arg1);
  }

  @Override
  public Object delete(final TodoEntity todo, final Continuation<? super Unit> arg1) {
    if (todo == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfTodoEntity.handle(_connection, todo);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object update(final TodoEntity todo, final Continuation<? super Unit> arg1) {
    if (todo == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfTodoEntity.handle(_connection, todo);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Flow<List<TodoEntity>> observeAll(final long babyId) {
    final String _sql = "SELECT * FROM todo WHERE babyId = ? ORDER BY timeAt ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"todo"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, babyId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfBabyId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "babyId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfTimeAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timeAt");
        final int _columnIndexOfRemindEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "remindEnabled");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<TodoEntity> _result = new ArrayList<TodoEntity>();
        while (_stmt.step()) {
          final TodoEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpBabyId;
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpTimeAt;
          _tmpTimeAt = _stmt.getLong(_columnIndexOfTimeAt);
          final boolean _tmpRemindEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfRemindEnabled));
          _tmpRemindEnabled = _tmp != 0;
          final boolean _tmpCompleted;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_1 != 0;
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new TodoEntity(_tmpId,_tmpBabyId,_tmpTitle,_tmpTimeAt,_tmpRemindEnabled,_tmpCompleted,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object pendingReminders(final long now,
      final Continuation<? super List<TodoEntity>> arg1) {
    final String _sql = "SELECT * FROM todo WHERE remindEnabled = 1 AND completed = 0 AND timeAt > ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, now);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfBabyId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "babyId");
        final int _columnIndexOfTitle = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "title");
        final int _columnIndexOfTimeAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "timeAt");
        final int _columnIndexOfRemindEnabled = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "remindEnabled");
        final int _columnIndexOfCompleted = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "completed");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<TodoEntity> _result = new ArrayList<TodoEntity>();
        while (_stmt.step()) {
          final TodoEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpBabyId;
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId);
          final String _tmpTitle;
          if (_stmt.isNull(_columnIndexOfTitle)) {
            _tmpTitle = null;
          } else {
            _tmpTitle = _stmt.getText(_columnIndexOfTitle);
          }
          final long _tmpTimeAt;
          _tmpTimeAt = _stmt.getLong(_columnIndexOfTimeAt);
          final boolean _tmpRemindEnabled;
          final int _tmp;
          _tmp = (int) (_stmt.getLong(_columnIndexOfRemindEnabled));
          _tmpRemindEnabled = _tmp != 0;
          final boolean _tmpCompleted;
          final int _tmp_1;
          _tmp_1 = (int) (_stmt.getLong(_columnIndexOfCompleted));
          _tmpCompleted = _tmp_1 != 0;
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new TodoEntity(_tmpId,_tmpBabyId,_tmpTitle,_tmpTimeAt,_tmpRemindEnabled,_tmpCompleted,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg1);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }
}
