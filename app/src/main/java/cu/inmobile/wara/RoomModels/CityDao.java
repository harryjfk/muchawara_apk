package cu.inmobile.wara.RoomModels;


import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface CityDao {

    @Insert
    void insert(City city);

    @Query("DELETE FROM city_table")
    void deleteAll();

    @Query("SELECT * from city_table ORDER BY id ASC")
    LiveData<List<City>> getAllCities();

    @Query("SELECT * from city_table ORDER BY id ASC")
    List<City> getAllCitiesList();

}
