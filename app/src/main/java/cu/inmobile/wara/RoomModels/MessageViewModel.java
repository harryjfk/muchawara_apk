package cu.inmobile.wara.RoomModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class MessageViewModel extends AndroidViewModel {

    private MessageRepo mRepository;

    private LiveData<List<Message>> mAllMessageLive;

    public MessageViewModel(Application application) {
        super(application);
        mRepository = new MessageRepo(application);
        mAllMessageLive = mRepository.getmAllMessageLive();
    }

    public LiveData<List<Message>> getmAllUsersLive() { return mAllMessageLive; }

    public LiveData<List<Message>> getMessageByUserLive(String userId) { return mRepository.getMessageByUserLive(userId); }

    public List<Message> getLastMessageByUser(String userId) { return mRepository.getLastMessageByUser(userId); }

    public int getUnreadMessageByUser_count(String userId) { return mRepository.getUnreadMessageByUserCount(userId); }

    public int getAllMessage_count() {

           return mRepository.getAllMessageList();

    }

    public void setUnreadMessageByUser(String userId) { mRepository.setUnreadMessageByUser(userId); }

    public void insert(Message message) { mRepository.insert(message, null); }

    public void sendMessage (Message message) { mRepository.sendMessage(message); }

    public void sendunsendedMessage (String id) { mRepository.sendUnsendedMessage( id ); }

    public void deleteTargetMessageById(String target_id){
        mRepository.deleteUserById(target_id);
    }

}
