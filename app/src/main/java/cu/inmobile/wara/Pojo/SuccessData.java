
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SuccessData {

    @Json(name = "user_id")
    private Integer userId;
    @Json(name = "name")
    private String name;
    @Json(name = "chat_token")
    private String chatToken;
    @Json(name = "chat_user")
    private String chatUser;
    @Json(name = "username")
    private String username;
    @Json(name = "credits")
    private Integer credits;
    @Json(name = "access_token")
    private String accessToken;
    @Json(name = "last_request_timestamp")
    private String lastRequestTimestamp;
    @Json(name = "profile_pictures")
    private ProfilePictures profilePictures;
    @Json(name = "success_text")
    private String successText;
    //nuevo
    @Json(name = "success_values")
    private String successValues;
    @Json(name = "success_os")
    private String successOs;
    @Json(name = "success_version")
    private String successVersion;

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getLastRequestTimestamp() {
        return lastRequestTimestamp;
    }

    public void setLastRequestTimestamp(String lastRequestTimestamp) {
        this.lastRequestTimestamp = lastRequestTimestamp;
    }

    public ProfilePictures getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(ProfilePictures profilePictures) {
        this.profilePictures = profilePictures;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }


}
