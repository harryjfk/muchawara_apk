package cu.inmobile.wara.RoomModels;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface MessageDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Message message);

    @Query("DELETE FROM message_table")
    void deleteAll();

    @Query("SELECT * from message_table ORDER BY id ASC")
    List<Message> getAllMessageList();

    @Query("SELECT * from message_table WHERE token =:token LIMIT 1")
    Message getMessage(String token);

    @Query("SELECT * from message_table WHERE id =:messId LIMIT 1")
    List<Message> getMessagesByID(String messId);

    @Query("SELECT * from message_table WHERE user_to = :toId AND readed = 0")
    List<Message> getUnreadMessageByUser(String toId);

    @Query("UPDATE message_table SET readed=1 WHERE user_from = :toId")
    void setUnreadMessageByUser(String toId);

    @Query("DELETE from message_table WHERE id = :id")
    void deleteMessageById(String id);

    @Query("SELECT * from message_table ORDER BY time ASC")
    LiveData<List<Message>> getAllMessageLive();

    @Query("SELECT * from message_table WHERE user_from = :userId OR user_to=:userId ORDER BY time ASC")
    LiveData<List<Message>> getMessageByUserLive(String userId);

    @Query("SELECT * from message_table WHERE user_from =:userId OR user_to=:userId ORDER BY time DESC")
    List<Message> getLastMessageByUser(String userId);

    @Query("SELECT * from message_table WHERE user_to =:toId AND sended=:sended ORDER BY time ASC")
    List<Message> getUnsendedMessageByUser(String toId, int sended);

    @Query("SELECT * from message_table WHERE sended = 0 ORDER BY time ASC")
    List<Message> getUnsendedMessages( );

    @Query("SELECT * from message_table WHERE received = 0 ORDER BY time ASC")
    List<Message> getUnrecievedMessages( );

}
