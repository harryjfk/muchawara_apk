package cu.inmobile.wara.RoomModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;

import java.util.List;

public class UserViewModel extends AndroidViewModel {

    private UserRepo mRepository;

    private LiveData<List<User>> mAllUserLive;
    private LiveData<User> mUserLive;

    public UserViewModel(Application application) {
        super(application);
        mRepository = new UserRepo(application);
    }

    public LiveData<List<User>> getmAllUsersLive(String date) {

        mAllUserLive = mRepository.getmAllUsersLive( date);
        return mAllUserLive;
    }

    public LiveData<User> getmUserLive(String id){
        mUserLive = mRepository.getUserLiveById(id);
        return mUserLive;
    }

    public User getmUser(String id){
        User u = mRepository.getUserById(id);
        return u;
    }

    public void deleteTargetUserById(String target_id){
        mRepository.deleteUserById(target_id);
    }

    public void insert(User user) { mRepository.insert(user); }
}
