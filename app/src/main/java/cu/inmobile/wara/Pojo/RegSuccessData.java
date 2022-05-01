
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class RegSuccessData {

    @Json(name = "success_text")
    private String successText;
    @Json(name = "user_id")
    private Integer userId;
    @Json(name = "chat_user")
    private String chatUser;
    @Json(name = "access_token")
    private String accessToken;
    @Json(name = "chat_token")
    private String chatToken;
    @Json(name = "email_verify_required")
    private Integer emailVerifyRequired;
    @Json(name = "profile_pictures")
    private ProfilePictures profilePictures;

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getChatToken() {
        return chatToken;
    }

    public void setChatToken(String chatToken) {
        this.chatToken = chatToken;
    }

    public Integer getEmailVerifyRequired() {
        return emailVerifyRequired;
    }

    public void setEmailVerifyRequired(Integer emailVerifyRequired) {
        this.emailVerifyRequired = emailVerifyRequired;
    }

    public ProfilePictures getProfilePictures() {
        return profilePictures;
    }

    public void setProfilePictures(ProfilePictures profilePictures) {
        this.profilePictures = profilePictures;
    }

}
