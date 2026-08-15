package com.xiaojiaoyin.baby.data.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.xiaojiaoyin.baby.data.db.entity.RecordEntity;
import com.xiaojiaoyin.baby.data.db.entity.RecordType;
import java.lang.Class;
import java.lang.IllegalArgumentException;
import java.lang.Integer;
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
import kotlin.coroutines.Continuation;
import kotlinx.coroutines.flow.Flow;

@Generated("androidx.room.RoomProcessor")
@SuppressWarnings({"unchecked", "deprecation", "removal"})
public final class RecordDao_Impl implements RecordDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<RecordEntity> __insertAdapterOfRecordEntity;

  public RecordDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfRecordEntity = new EntityInsertAdapter<RecordEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `record` (`id`,`babyId`,`type`,`occurredAt`,`detailJson`,`note`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final RecordEntity entity) {
        statement.bindLong(1, entity.getId());
        statement.bindLong(2, entity.getBabyId());
        statement.bindText(3, __RecordType_enumToString(entity.getType()));
        statement.bindLong(4, entity.getOccurredAt());
        if (entity.getDetailJson() == null) {
          statement.bindNull(5);
        } else {
          statement.bindText(5, entity.getDetailJson());
        }
        if (entity.getNote() == null) {
          statement.bindNull(6);
        } else {
          statement.bindText(6, entity.getNote());
        }
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
  }

  @Override
  public Object insert(final RecordEntity record, final Continuation<? super Long> $completion) {
    if (record == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfRecordEntity.insertAndReturnId(_connection, record);
    }, $completion);
  }

  @Override
  public Flow<List<RecordEntity>> observeRecent(final long babyId, final int limit) {
    final String _sql = "SELECT * FROM record WHERE babyId = ? ORDER BY occurredAt DESC LIMIT ?";
    return FlowUtil.createFlow(__db, false, new String[] {"record"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, babyId);
        _argIndex = 2;
        _stmt.bindLong(_argIndex, limit);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfBabyId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "babyId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfOccurredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "occurredAt");
        final int _columnIndexOfDetailJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "detailJson");
        final int _columnIndexOfNote = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "note");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<RecordEntity> _result = new ArrayList<RecordEntity>();
        while (_stmt.step()) {
          final RecordEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpBabyId;
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId);
          final RecordType _tmpType;
          _tmpType = __RecordType_stringToEnum(_stmt.getText(_columnIndexOfType));
          final long _tmpOccurredAt;
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt);
          final String _tmpDetailJson;
          if (_stmt.isNull(_columnIndexOfDetailJson)) {
            _tmpDetailJson = null;
          } else {
            _tmpDetailJson = _stmt.getText(_columnIndexOfDetailJson);
          }
          final String _tmpNote;
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null;
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new RecordEntity(_tmpId,_tmpBabyId,_tmpType,_tmpOccurredAt,_tmpDetailJson,_tmpNote,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Flow<List<RecordEntity>> observeAll(final long babyId) {
    final String _sql = "SELECT * FROM record WHERE babyId = ? ORDER BY occurredAt DESC";
    return FlowUtil.createFlow(__db, false, new String[] {"record"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, babyId);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfBabyId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "babyId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfOccurredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "occurredAt");
        final int _columnIndexOfDetailJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "detailJson");
        final int _columnIndexOfNote = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "note");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<RecordEntity> _result = new ArrayList<RecordEntity>();
        while (_stmt.step()) {
          final RecordEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpBabyId;
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId);
          final RecordType _tmpType;
          _tmpType = __RecordType_stringToEnum(_stmt.getText(_columnIndexOfType));
          final long _tmpOccurredAt;
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt);
          final String _tmpDetailJson;
          if (_stmt.isNull(_columnIndexOfDetailJson)) {
            _tmpDetailJson = null;
          } else {
            _tmpDetailJson = _stmt.getText(_columnIndexOfDetailJson);
          }
          final String _tmpNote;
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null;
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new RecordEntity(_tmpId,_tmpBabyId,_tmpType,_tmpOccurredAt,_tmpDetailJson,_tmpNote,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object countByTypeSince(final long babyId, final RecordType type, final long since,
      final Continuation<? super List<RecordEntity>> $completion) {
    final String _sql = "SELECT * FROM record WHERE babyId = ? AND type = ? AND occurredAt >= ? ORDER BY occurredAt DESC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, babyId);
        _argIndex = 2;
        _stmt.bindText(_argIndex, __RecordType_enumToString(type));
        _argIndex = 3;
        _stmt.bindLong(_argIndex, since);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfBabyId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "babyId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfOccurredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "occurredAt");
        final int _columnIndexOfDetailJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "detailJson");
        final int _columnIndexOfNote = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "note");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<RecordEntity> _result = new ArrayList<RecordEntity>();
        while (_stmt.step()) {
          final RecordEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpBabyId;
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId);
          final RecordType _tmpType;
          _tmpType = __RecordType_stringToEnum(_stmt.getText(_columnIndexOfType));
          final long _tmpOccurredAt;
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt);
          final String _tmpDetailJson;
          if (_stmt.isNull(_columnIndexOfDetailJson)) {
            _tmpDetailJson = null;
          } else {
            _tmpDetailJson = _stmt.getText(_columnIndexOfDetailJson);
          }
          final String _tmpNote;
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null;
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new RecordEntity(_tmpId,_tmpBabyId,_tmpType,_tmpOccurredAt,_tmpDetailJson,_tmpNote,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object lastOfType(final long babyId, final RecordType type,
      final Continuation<? super RecordEntity> $completion) {
    final String _sql = "SELECT * FROM record WHERE babyId = ? AND type = ? ORDER BY occurredAt DESC LIMIT 1";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, babyId);
        _argIndex = 2;
        _stmt.bindText(_argIndex, __RecordType_enumToString(type));
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfBabyId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "babyId");
        final int _columnIndexOfType = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "type");
        final int _columnIndexOfOccurredAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "occurredAt");
        final int _columnIndexOfDetailJson = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "detailJson");
        final int _columnIndexOfNote = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "note");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final RecordEntity _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final long _tmpBabyId;
          _tmpBabyId = _stmt.getLong(_columnIndexOfBabyId);
          final RecordType _tmpType;
          _tmpType = __RecordType_stringToEnum(_stmt.getText(_columnIndexOfType));
          final long _tmpOccurredAt;
          _tmpOccurredAt = _stmt.getLong(_columnIndexOfOccurredAt);
          final String _tmpDetailJson;
          if (_stmt.isNull(_columnIndexOfDetailJson)) {
            _tmpDetailJson = null;
          } else {
            _tmpDetailJson = _stmt.getText(_columnIndexOfDetailJson);
          }
          final String _tmpNote;
          if (_stmt.isNull(_columnIndexOfNote)) {
            _tmpNote = null;
          } else {
            _tmpNote = _stmt.getText(_columnIndexOfNote);
          }
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _result = new RecordEntity(_tmpId,_tmpBabyId,_tmpType,_tmpOccurredAt,_tmpDetailJson,_tmpNote,_tmpCreatedAt);
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @Override
  public Object countAll(final Continuation<? super Integer> $completion) {
    final String _sql = "SELECT COUNT(*) FROM record";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final Integer _result;
        if (_stmt.step()) {
          final Integer _tmp;
          if (_stmt.isNull(0)) {
            _tmp = null;
          } else {
            _tmp = (int) (_stmt.getLong(0));
          }
          _result = _tmp;
        } else {
          _result = null;
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, $completion);
  }

  @NonNull
  public static List<Class<?>> getRequiredConverters() {
    return Collections.emptyList();
  }

  private String __RecordType_enumToString(@NonNull final RecordType _value) {
    switch (_value) {
      case FEEDING: return "FEEDING";
      case CRYING: return "CRYING";
      case GROWTH: return "GROWTH";
      default: throw new IllegalArgumentException("Can't convert enum to string, unknown enum value: " + _value);
    }
  }

  private RecordType __RecordType_stringToEnum(@NonNull final String _value) {
    switch (_value) {
      case "FEEDING": return RecordType.FEEDING;
      case "CRYING": return RecordType.CRYING;
      case "GROWTH": return RecordType.GROWTH;
      default: throw new IllegalArgumentException("Can't convert value to enum, unknown value: " + _value);
    }
  }
}
