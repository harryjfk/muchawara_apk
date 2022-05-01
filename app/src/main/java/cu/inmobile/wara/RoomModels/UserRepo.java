package cu.inmobile.wara.RoomModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.WSWorker;

// Repo a partir de la implementacion del patron Repository

public class UserRepo {

    private UserDao mUserDao;
    private LiveData<List<User>> mAllUsersLive;
    private List<User> mAllUsersList;

    public UserRepo(Application application) {

        WaraRoomDatabase db = WaraRoomDatabase.getDatabase(application);
        mUserDao = db.userDao();
        mAllUsersLive = mUserDao.getAllUsersLive();
        //mAllUsersList = mUserDao.getAllCitiesList();

        Log.d("-- UserRepo", "contructor()" );

    }


    // Retornara el listado de las ciudades que esten en BD y creara una tarea que descargara nuevos resultados

    public LiveData<List<User>> getmAllUsersLive (String date) {

        Log.d("-- UserRepo", "getAllCitiesLive()");

        WSWorker.getMessages(WaraApp.lastUpdate);
        //new fetchAsyncTask(mUserDao).execute();

        //new deleteAsyncTask(mUserDao).execute();

        return mAllUsersLive;

    }

    public void setUpdatedById(String id, Boolean updated) {
        Log.d("-- UserRepo", "setUpdatedById() ... id: " + id + ", updated: " + updated);
        mUserDao.setUpdatedById(id, updated);
    }

    public void setStatusByChatUSer(String chatUser, String status) {
        Log.d("-- UserRepo", "setStatusById() ... id: " + chatUser + ", status: " + status);
        mUserDao.setStatusByChatUser(chatUser, status);
    }

    public LiveData<User> getUserLiveById(String id){
        Log.d("-- UserRepo", "getUserLiveById() ... id: " + id);
        return mUserDao.getUserLiveById(id);
    }

    public User getUserById(String id){
        Log.d("-- UserRepo", "getUserById() ... id: " + id);
        User user = mUserDao.getUserById(id);
        return user;
    }

    public List<User> getAllUsersList () {

        Log.d("-- UserRepo", "getAllCitiesList() = " + mUserDao.getAllUsersList().size());

        return mUserDao.getAllUsersList();

    }


    public void insert (User user) {
        new insertAsyncTask(mUserDao).execute(user);
    }

    public void deleteAll() {
        deleteAsyncTask task = new deleteAsyncTask(mUserDao);
        task.execute();
    }

    public void deleteUserById(String target_id) {
        mUserDao.deleteUserById(target_id);
    }


    private static class insertAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao mAsyncTaskDao;

        insertAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final User... params) {
            mAsyncTaskDao.insert(params[0]);
            return null;
        }


    }


    private class fetchAsyncTask extends AsyncTask<UserDao, Void, Void> {

        private UserDao mAsyncTaskDao;

        fetchAsyncTask(UserDao dao) {
            mAsyncTaskDao = dao;
            Log.d("-- UserRepo" , "2");

        }

        @Override
        protected Void doInBackground(final UserDao... params) {

            Log.d("-- UserRepo" , "3");

            mAllUsersList = mAsyncTaskDao.getAllUsersList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //WSWorker.getCities(mAllUsersList);

        }
    }


    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {

        private UserDao asyncTaskDao;

        deleteAsyncTask(UserDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.d("-- UserRepo", "deleteAsyncTask()");

            asyncTaskDao.deleteAll();
            return null;
        }

    }



}
