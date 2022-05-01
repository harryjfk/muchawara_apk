package cu.inmobile.wara.RoomModels;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(User user);

    @Query("DELETE FROM user_table")
    void deleteAll();

    @Query("SELECT * from user_table ORDER BY id ASC")
    List<User> getAllUsersList();

    @Query("SELECT * from user_table ORDER BY id ASC")
    LiveData<List<User>> getAllUsersLive();

    @Query("UPDATE user_table SET updated = :updated WHERE id = :id")
    void setUpdatedById(String id, Boolean updated);

    @Query("DELETE from user_table WHERE id = :id")
    void deleteUserById(String id);

    @Query("UPDATE user_table SET status = :status WHERE chat_user = :chatUser")
    void setStatusByChatUser(String chatUser, String status);

    @Query("SELECT * from user_table WHERE id = :id")
    LiveData<User> getUserLiveById(String id);

    @Query("SELECT * from user_table WHERE id = :id")
    User getUserById(String id);
}
