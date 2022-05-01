package cu.inmobile.wara.RoomModels;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.WSWorker;
import cu.inmobile.wara.Utils.Callback;

// Repo a partir de la implementacion del patron Repository

public class MessageRepo {

    private MessageDao mMessageDao;
    private LiveData<List<Message>> mAllMessageLive;
    private List<Message> mAllMessageList;
    private static Callback insertCallback = null;

    public MessageRepo(Application application) {

        WaraRoomDatabase db = WaraRoomDatabase.getDatabase(application);
        mMessageDao = db.messageDao();
        mAllMessageLive = mMessageDao.getAllMessageLive();
        //mAllUsersList = mMessageDao.getAllCitiesList();

        Log.d("-- MessageRepo", "contructor()");

    }

// --------------------------------------------------------------- CREATED METHODS

    public void insert(Message message, Callback callback) {
        insertCallback = callback;
        new insertAsyncTask(mMessageDao).execute(message);
    }

    public void deleteAll() {
        deleteAsyncTask task = new deleteAsyncTask(mMessageDao);
        task.execute();
    }

    public LiveData<List<Message>> getmAllMessageLive() {

        Log.d("-- MessageRepo", "getmAllMessageLive()");

        WSWorker.getMessages(WaraApp.lastUpdate);
        //new fetchAsyncTask(mMessageDao).execute();

        //new deleteAsyncTask(mMessageDao).execute();

        return mAllMessageLive;

    }

    public int getAllMessageList() {

        Log.d("-- MessageRepo", "getAllCitiesList() = " + mMessageDao.getAllMessageList().size());

        return mMessageDao.getAllMessageList().size();

    }

    public LiveData<List<Message>> getMessageByUserLive(String userId) {

        Log.d("-- MessageRepo", "getMessageByUserLive()");

        //WSWorker.getMessages();
        //new fetchAsyncTask(mMessageDao).execute();

        //new deleteAsyncTask(mMessageDao).execute();

        return mMessageDao.getMessageByUserLive(userId);
    }

    public List<Message> getLastMessageByUser(String userId) {

        Log.d("-- MessageRepo", "getLastMessageByUser(): " + userId);

        //new deleteAsyncTask(mMessageDao).execute();

        return mMessageDao.getLastMessageByUser(userId);
    }

    public List<Message> getUnsendedMessage(String userId) {

        Log.d("-- MessageRepo", "getLastMessageByUser()");

        //new deleteAsyncTask(mMessageDao).execute();

        return mMessageDao.getUnsendedMessages();
    }


    public int getUnreadMessageByUserCount(String userId) {

        Log.d("-- MessageRepo", "getLastMessageByUser()");
        if (mMessageDao.getUnreadMessageByUser(userId).size() == 0) {
            return 0;
        } else {
            return mMessageDao.getUnreadMessageByUser(userId).size();
        }
    }

    public void setUnreadMessageByUser(String userId) {
        Log.d("-- MessageRepo", "setUnreadMessageByUserCount()");
        mMessageDao.setUnreadMessageByUser(userId);
    }

    public boolean getMesIdFromID(String messId) {
        List<Message> messages = mMessageDao.getMessagesByID(messId);
        if (messages.size()==0)
            return false;
        else
            return true;

    }


    public List<Message> getUnrecievedMessages(String userId) {

        Log.d("-- MessageRepo", "getUnrecievedMessage()");

        //new deleteAsyncTask(mMessageDao).execute();

        return mMessageDao.getUnrecievedMessages();
    }

    public void sendMessage(Message message) {

        new insertAsyncTask(mMessageDao).execute(message);

        WSWorker.sendMessage(message);
        // WSWorker.getAppValues();
    }

    public void sendUnsendedMessage(String id) {

        new sendUnsendedAsyncTask(mMessageDao, id).execute();
    }


    // Para value = 0 : El estado se envio por el chat, solo actualizar recieved
    // Para value = 1 : El estado no se envio por el chat, actualizar recieved y dejar por enviar para actualizar

    public static final int MES_RECIEVED = 0;
    public static final int MES_NOT_RECIEVED = 1;


    public void setReceived(String token, int value) {

        Log.d("-- MessageRepo", "setReceived() token: " + token);

        Message messageToUpdate = mMessageDao.getMessage(token);


        if (value == MES_RECIEVED) {
            messageToUpdate.setSended(1);
            messageToUpdate.setmeceived(1);
            WSWorker.setReceivedMessage(token);
        } else {
            messageToUpdate.setmeceived(1);
            messageToUpdate.setSended(0);
        }

        insert(messageToUpdate, null);
    }

    public String getMesIdFromToken(String token) {
        Message message = mMessageDao.getMessage(token);
        return message.getId();

    }


// --------------------------------------------------------------- CREATED ASYNC CLASSES


    private static class insertAsyncTask extends AsyncTask<Message, Void, String> {

        private MessageDao mAsyncTaskDao;

        insertAsyncTask(MessageDao dao) {
            mAsyncTaskDao = dao;
        }


        @Override
        protected String doInBackground(final Message... params) {

            Log.d("-- MessageRepo", "insertAsyncTask().doInBackground: " + params[0].getToken());


            Message upcomingMes = params[0];

            Log.d("-- MessageRepo", "insertAsyncTask().doInBackground -> from: " + params[0].getFrom() + " to: " + params[0].getTo());

            Message daoMes = mAsyncTaskDao.getMessage(upcomingMes.getToken());

            if (daoMes != null)
                if (upcomingMes.getReceived() == 1 || daoMes.getReceived() == 1)
                    upcomingMes.setmeceived(1);

            mAsyncTaskDao.insert(upcomingMes);


            return upcomingMes.getFrom();
        }

        @Override
        protected void onPostExecute(final String fromChatUser) {
            super.onPostExecute(fromChatUser);
            if(insertCallback != null){
                insertCallback.execute();
                insertCallback = null;
            }
            WaraApp.userRepo.setUpdatedById(fromChatUser, true);
        }


    }

    private class fetchAsyncTask extends AsyncTask<MessageDao, Void, Void> {

        private MessageDao mAsyncTaskDao;

        fetchAsyncTask(MessageDao dao) {
            mAsyncTaskDao = dao;
            Log.d("-- MessageRepo", "2");
        }

        @Override
        protected Void doInBackground(final MessageDao... params) {

            Log.d("-- MessageRepo", "3");

            mAllMessageList = mAsyncTaskDao.getAllMessageList();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

        }
    }

    private class sendUnsendedAsyncTask extends AsyncTask<MessageDao, Void, Void> {

        private MessageDao mAsyncTaskDao;
        private String toId;
        private List<Message> messagesUnsended;

        sendUnsendedAsyncTask(MessageDao dao, String toId) {
            mAsyncTaskDao = dao;
            this.toId = toId;
            messagesUnsended = new ArrayList<>();

            Log.d("-- MessageRepo", "sendUnsendedAsyncTask()");
        }

        @Override
        protected Void doInBackground(final MessageDao... params) {

            Log.d("-- MessageRepo", "sendUnsendedAsyncTask().doInBackground");

            if (toId.equals("0")) {
                messagesUnsended = mAsyncTaskDao.getUnsendedMessages();
            } else
                messagesUnsended = mAsyncTaskDao.getUnsendedMessageByUser(toId, 0);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for (Message mes : messagesUnsended) {
                Log.d("-- MessageRepo", "sendUnsendedAsyncTask().onPostExecute() - mes: " + mes.getBody());

                WSWorker.sendMessage(mes);
            }

        }
    }

    private static class deleteAsyncTask extends AsyncTask<String, Void, Void> {

        private MessageDao asyncTaskDao;

        deleteAsyncTask(MessageDao dao) {
            asyncTaskDao = dao;
        }

        @Override
        protected Void doInBackground(final String... params) {
            Log.d("-- MessageRepo", "deleteAsyncTask()");

            asyncTaskDao.deleteAll();
            return null;
        }
    }


    public void deleteUserById(String target_id) {
        mMessageDao.deleteMessageById(target_id);
    }
}
