package cu.inmobile.wara.RoomModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

// Repo a partir de la implementacion del patron Repository

public class CityRepo  {

    private CityDao mCityDao;
    private LiveData<List<City>> mAllCitiesLive;
    private List<City> mAllCitiesList;

    public CityRepo (Application application) {

        WaraRoomDatabase db = WaraRoomDatabase.getDatabase(application);
        mCityDao = db.cityDao();
        mAllCitiesLive = mCityDao.getAllCities();
        //mAllCitiesList = mCityDao.getAllCitiesList();

        Log.d("-- CityRepo", "contructor()" );

    }


    // Retornara el listado de las ciudades que esten en BD y creara una tarea que descargara nuevos resultados

    public LiveData<List<City>> getAllCitiesLive () {

        Log.d("-- CityRepo", "getAllCitiesLive()");

        //WSWorker.getCities(mAllCitiesList);
        new fetchAsyncTask(mCityDao).execute();

        return mAllCitiesLive;

    }

    public List<City> getAllCitiesList () {

        Log.d("-- CityRepo", "getAllCitiesList() = " + mCityDao.getAllCitiesList().size());

        return mAllCitiesList;

    }


    public void insert (City city) {
        new insertAsyncTask(mCityDao).execute(city);
    }

    public void deleteAll() {
        deleteAsyncTask task = new deleteAsyncTask(mCityDao);
        task.execute();
    }


    private static class insertAsyncTask extends AsyncTask<City, Void, Void> {

        private CityDao mAsyncTaskDao;

        insertAsyncTask(CityDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final City... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }


    }


    private class fetchAsyncTask extends AsyncTask< CityRepo, Void, Void> {

        private CityDao mAsyncTaskDao;

        fetchAsyncTask(CityDao dao) {
            mAsyncTaskDao = dao;
            Log.d("-- CityRepo" , "2");

        }

        @Override
        protected Void doInBackground(final CityRepo... params) {

            Log.d("-- CityRepo" , "3");

            mAllCitiesList = mAsyncTaskDao.getAllCitiesList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);


            //WSWorker.getCities(mAllCitiesList);

        }
    }


    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {

        private CityDao asyncTaskDao;

        deleteAsyncTask(CityDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.d("-- CityRepo", "deleteAsyncTask()");

            asyncTaskDao.deleteAll();
            return null;
        }

    }



}
