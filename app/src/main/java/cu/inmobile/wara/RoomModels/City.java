package cu.inmobile.wara.RoomModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;


@Entity(tableName = "city_table")
public class City {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    private Long mId;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;



    public City(@NonNull Long id , @NonNull String name ) {
        this.mId = id;
        this.mName = name;
    }

    public Long getId(){return this.mId;}

    public String getName(){return this.mName;}

}
