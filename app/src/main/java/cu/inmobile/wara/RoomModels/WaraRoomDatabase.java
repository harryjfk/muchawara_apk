package cu.inmobile.wara.RoomModels;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {City.class, User.class, Message.class}, version = 7,exportSchema = false)
public abstract class WaraRoomDatabase extends RoomDatabase {

    public abstract CityDao cityDao();
    public abstract UserDao userDao();
    public abstract MessageDao messageDao();



    private static volatile WaraRoomDatabase INSTANCE;

    static WaraRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (WaraRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            WaraRoomDatabase.class, "wara_database")
                            .fallbackToDestructiveMigration()
                            .allowMainThreadQueries()
                            .build();
                }
            }
        }
        return INSTANCE;
    }


}
