package com.xiaojiaoyin.baby.data.db.dao;

import androidx.annotation.NonNull;
import androidx.room.EntityDeleteOrUpdateAdapter;
import androidx.room.EntityInsertAdapter;
import androidx.room.RoomDatabase;
import androidx.room.coroutines.FlowUtil;
import androidx.room.util.DBUtil;
import androidx.room.util.SQLiteStatementUtil;
import androidx.sqlite.SQLiteStatement;
import com.xiaojiaoyin.baby.data.db.entity.BabyEntity;
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
public final class BabyDao_Impl implements BabyDao {
  private final RoomDatabase __db;

  private final EntityInsertAdapter<BabyEntity> __insertAdapterOfBabyEntity;

  private final EntityDeleteOrUpdateAdapter<BabyEntity> __deleteAdapterOfBabyEntity;

  private final EntityDeleteOrUpdateAdapter<BabyEntity> __updateAdapterOfBabyEntity;

  public BabyDao_Impl(@NonNull final RoomDatabase __db) {
    this.__db = __db;
    this.__insertAdapterOfBabyEntity = new EntityInsertAdapter<BabyEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "INSERT OR ABORT INTO `baby` (`id`,`name`,`nickname`,`gender`,`birthDateTime`,`avatarColorIndex`,`createdAt`) VALUES (nullif(?, 0),?,?,?,?,?,?)";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final BabyEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getNickname() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getNickname());
        }
        if (entity.getGender() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getGender());
        }
        statement.bindLong(5, entity.getBirthDateTime());
        statement.bindLong(6, entity.getAvatarColorIndex());
        statement.bindLong(7, entity.getCreatedAt());
      }
    };
    this.__deleteAdapterOfBabyEntity = new EntityDeleteOrUpdateAdapter<BabyEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "DELETE FROM `baby` WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final BabyEntity entity) {
        statement.bindLong(1, entity.getId());
      }
    };
    this.__updateAdapterOfBabyEntity = new EntityDeleteOrUpdateAdapter<BabyEntity>() {
      @Override
      @NonNull
      protected String createQuery() {
        return "UPDATE OR ABORT `baby` SET `id` = ?,`name` = ?,`nickname` = ?,`gender` = ?,`birthDateTime` = ?,`avatarColorIndex` = ?,`createdAt` = ? WHERE `id` = ?";
      }

      @Override
      protected void bind(@NonNull final SQLiteStatement statement,
          @NonNull final BabyEntity entity) {
        statement.bindLong(1, entity.getId());
        if (entity.getName() == null) {
          statement.bindNull(2);
        } else {
          statement.bindText(2, entity.getName());
        }
        if (entity.getNickname() == null) {
          statement.bindNull(3);
        } else {
          statement.bindText(3, entity.getNickname());
        }
        if (entity.getGender() == null) {
          statement.bindNull(4);
        } else {
          statement.bindText(4, entity.getGender());
        }
        statement.bindLong(5, entity.getBirthDateTime());
        statement.bindLong(6, entity.getAvatarColorIndex());
        statement.bindLong(7, entity.getCreatedAt());
        statement.bindLong(8, entity.getId());
      }
    };
  }

  @Override
  public Object insert(final BabyEntity baby, final Continuation<? super Long> arg1) {
    if (baby == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      return __insertAdapterOfBabyEntity.insertAndReturnId(_connection, baby);
    }, arg1);
  }

  @Override
  public Object delete(final BabyEntity baby, final Continuation<? super Unit> arg1) {
    if (baby == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __deleteAdapterOfBabyEntity.handle(_connection, baby);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Object update(final BabyEntity baby, final Continuation<? super Unit> arg1) {
    if (baby == null) throw new NullPointerException();
    return DBUtil.performSuspending(__db, false, true, (_connection) -> {
      __updateAdapterOfBabyEntity.handle(_connection, baby);
      return Unit.INSTANCE;
    }, arg1);
  }

  @Override
  public Flow<List<BabyEntity>> observeAll() {
    final String _sql = "SELECT * FROM baby ORDER BY createdAt ASC";
    return FlowUtil.createFlow(__db, false, new String[] {"baby"}, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfNickname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nickname");
        final int _columnIndexOfGender = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gender");
        final int _columnIndexOfBirthDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "birthDateTime");
        final int _columnIndexOfAvatarColorIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avatarColorIndex");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<BabyEntity> _result = new ArrayList<BabyEntity>();
        while (_stmt.step()) {
          final BabyEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpNickname;
          if (_stmt.isNull(_columnIndexOfNickname)) {
            _tmpNickname = null;
          } else {
            _tmpNickname = _stmt.getText(_columnIndexOfNickname);
          }
          final String _tmpGender;
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null;
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender);
          }
          final long _tmpBirthDateTime;
          _tmpBirthDateTime = _stmt.getLong(_columnIndexOfBirthDateTime);
          final int _tmpAvatarColorIndex;
          _tmpAvatarColorIndex = (int) (_stmt.getLong(_columnIndexOfAvatarColorIndex));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new BabyEntity(_tmpId,_tmpName,_tmpNickname,_tmpGender,_tmpBirthDateTime,_tmpAvatarColorIndex,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    });
  }

  @Override
  public Object getAll(final Continuation<? super List<BabyEntity>> arg0) {
    final String _sql = "SELECT * FROM baby ORDER BY createdAt ASC";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfNickname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nickname");
        final int _columnIndexOfGender = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gender");
        final int _columnIndexOfBirthDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "birthDateTime");
        final int _columnIndexOfAvatarColorIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avatarColorIndex");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final List<BabyEntity> _result = new ArrayList<BabyEntity>();
        while (_stmt.step()) {
          final BabyEntity _item;
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpNickname;
          if (_stmt.isNull(_columnIndexOfNickname)) {
            _tmpNickname = null;
          } else {
            _tmpNickname = _stmt.getText(_columnIndexOfNickname);
          }
          final String _tmpGender;
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null;
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender);
          }
          final long _tmpBirthDateTime;
          _tmpBirthDateTime = _stmt.getLong(_columnIndexOfBirthDateTime);
          final int _tmpAvatarColorIndex;
          _tmpAvatarColorIndex = (int) (_stmt.getLong(_columnIndexOfAvatarColorIndex));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _item = new BabyEntity(_tmpId,_tmpName,_tmpNickname,_tmpGender,_tmpBirthDateTime,_tmpAvatarColorIndex,_tmpCreatedAt);
          _result.add(_item);
        }
        return _result;
      } finally {
        _stmt.close();
      }
    }, arg0);
  }

  @Override
  public Object getById(final long id, final Continuation<? super BabyEntity> arg1) {
    final String _sql = "SELECT * FROM baby WHERE id = ?";
    return DBUtil.performSuspending(__db, true, false, (_connection) -> {
      final SQLiteStatement _stmt = _connection.prepare(_sql);
      try {
        int _argIndex = 1;
        _stmt.bindLong(_argIndex, id);
        final int _columnIndexOfId = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "id");
        final int _columnIndexOfName = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "name");
        final int _columnIndexOfNickname = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "nickname");
        final int _columnIndexOfGender = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "gender");
        final int _columnIndexOfBirthDateTime = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "birthDateTime");
        final int _columnIndexOfAvatarColorIndex = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "avatarColorIndex");
        final int _columnIndexOfCreatedAt = SQLiteStatementUtil.getColumnIndexOrThrow(_stmt, "createdAt");
        final BabyEntity _result;
        if (_stmt.step()) {
          final long _tmpId;
          _tmpId = _stmt.getLong(_columnIndexOfId);
          final String _tmpName;
          if (_stmt.isNull(_columnIndexOfName)) {
            _tmpName = null;
          } else {
            _tmpName = _stmt.getText(_columnIndexOfName);
          }
          final String _tmpNickname;
          if (_stmt.isNull(_columnIndexOfNickname)) {
            _tmpNickname = null;
          } else {
            _tmpNickname = _stmt.getText(_columnIndexOfNickname);
          }
          final String _tmpGender;
          if (_stmt.isNull(_columnIndexOfGender)) {
            _tmpGender = null;
          } else {
            _tmpGender = _stmt.getText(_columnIndexOfGender);
          }
          final long _tmpBirthDateTime;
          _tmpBirthDateTime = _stmt.getLong(_columnIndexOfBirthDateTime);
          final int _tmpAvatarColorIndex;
          _tmpAvatarColorIndex = (int) (_stmt.getLong(_columnIndexOfAvatarColorIndex));
          final long _tmpCreatedAt;
          _tmpCreatedAt = _stmt.getLong(_columnIndexOfCreatedAt);
          _result = new BabyEntity(_tmpId,_tmpName,_tmpNickname,_tmpGender,_tmpBirthDateTime,_tmpAvatarColorIndex,_tmpCreatedAt);
        } else {
          _result = null;
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
