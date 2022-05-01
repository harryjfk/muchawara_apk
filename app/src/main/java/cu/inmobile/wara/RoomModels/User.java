package cu.inmobile.wara.RoomModels;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import cu.inmobile.wara.Applications.WaraApp;
import cu.inmobile.wara.Networking.Endpoints;


@Entity(tableName = "user_table")
public class User {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "id")
    private String mId;

    @NonNull
    @ColumnInfo(name = "chat_user")
    private String mChatUser;

    @ColumnInfo(name = "city")
    private String mCity;

    @ColumnInfo(name = "popularity")
    private String mPopularity;

    @NonNull
    @ColumnInfo(name = "username")
    private String mUsername;

    @NonNull
    @ColumnInfo(name = "name")
    private String mName;

    @ColumnInfo(name = "age")
    private Integer mAge;

    @ColumnInfo(name = "profile_picture")
    private String mProfilePicture;

    @ColumnInfo(name = "status")
    private String mState;

    @ColumnInfo(name = "aboutme")
    private String mAboutMe;

    @ColumnInfo(name = "updated")
    private Boolean mUpdated;

    public User (@NonNull String id, String chatUser, @NonNull String name, Integer age , String profilePicture, String state, String aboutMe, Boolean updated, String city, String popularity, String username) {
        this.mId = id;
        this.mChatUser = chatUser;
        this.mName = name;
        this.mAge = age;
        this.mProfilePicture = profilePicture;
        this.mState = state;
        this.mAboutMe = aboutMe;
        this.mUpdated = updated;
        this.mCity = city;
        this.mPopularity = popularity;
        this.mUsername = username;
    }

    public String getId(){return this.mId;}

    public String getChatUser(){return this.mChatUser;}

    public String getName(){return this.mName;}

    public Integer getAge(){return this.mAge;}

    public String getProfilePicture(){return this.mProfilePicture;}

    public String getState(){return this.mState;}

    public String getAboutMe(){return this.mAboutMe;}

    public Boolean getUpdated() {return this.mUpdated;}

    public String getCity() {
        return this.mCity;
    }

    public String getPopularity() {
        return this.mPopularity;
    }

    public String getUsername() {
        return this.mUsername;
    }

    public void setId(String id){this.mId=id;}

    public void setChatUser(String mChatUser){this.mChatUser = mChatUser;}

    public void setName(String mName){ this.mName = mName;}

    public void setAge(Integer mAge){ this.mAge=mAge;}

    public void setProfilePicture(String mProfilePicture){ this.mProfilePicture=mProfilePicture;}

    public void setState(String mState){ this.mState=mState;}

    public void setAboutMe(String mAboutMe){ this.mAboutMe=mAboutMe;}

    public void setCity(String mCity) {
         this.mCity = mCity;
    }

    public void setPopularity(String mPopularity) {
        this.mPopularity = mPopularity;
    }

    public void setUsername(String mUsername) { this.mUsername=mUsername;
    }

    public User mergeUser(User userApi){

        if (!mChatUser.equals(userApi.getChatUser())){
            this.setChatUser(userApi.getChatUser());
        }
        if (!mName.equals(userApi.getName())){
            this.setName(userApi.getName());
        }
        if (mAge != userApi.getAge()){
            this.setAge(userApi.getAge());
        }
        if (!mProfilePicture.equals(userApi.getProfilePicture())){
            String photo = userApi.getProfilePicture();//.split("/")[3];
            mProfilePicture = photo;
        }
//        if (!mState.equals(userApi.getState())){
//            mState = userApi.getState();
//        }
        if (!mAboutMe.equals(userApi.getAboutMe())){
            mAboutMe = userApi.getAboutMe();
        }
        if (!mCity.equals(userApi.getCity())){
            this.setCity(userApi.getCity());
        }
        if (!mPopularity.equals(userApi.getPopularity())){
            mPopularity = userApi.getPopularity();
        }
        if (!mUsername.equals(userApi.getUsername())){
            mUsername = userApi.getUsername();
        }

        User userToInsert = new User(
                mId,
                mChatUser,
                mName,
                mAge,
                mProfilePicture,
                mState,
                mAboutMe,
                true,
                mCity,
                mPopularity,
                mUsername

        );
        return userToInsert;
    }

}
