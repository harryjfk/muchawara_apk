package cu.inmobile.wara.RoomModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class CityViewModel extends AndroidViewModel {

    private CityRepo mRepository;

    private LiveData<List<City>> mAllCities;

    public CityViewModel (Application application) {
        super(application);
        mRepository = new CityRepo(application);
        mAllCities = mRepository.getAllCitiesLive();
    }

    public LiveData<List<City>> getAllCities() { return mAllCities; }

    public void insert(City city) { mRepository.insert(city); }
}
