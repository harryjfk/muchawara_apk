
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

import java.util.List;

public class Related {

    @Json(name = "id")
    private String id;
    @Json(name = "system_id")
    private String systemId;
    @Json(name = "chat_user")
    private String chatUser;
    @Json(name = "name")
    private String name;
    @Json(name = "username")
    private String username;
    @Json(name = "popularity")
    private String popularity;
    @Json(name = "fullcity")
    private String fullcity;
    @Json(name = "fullname")
    private String fullname;
    @Json(name = "profile")
    private String profile;
    @Json(name = "last_time")
    private String lastTime;
    @Json(name = "friends")
    private Object friends;
    @Json(name = "profile_picture")
    private String profilePicture;
    @Json(name = "last_msg")
    private LastMsg lastMsg;
    @Json(name = "messages")
    private List<Message> messages = null;
    @Json(name = "state")
    private String state;
    @Json(name = "total_unread_messages_count")
    private Integer totalUnreadMessagesCount;
    @Json(name = "contact_id")
    private String contactId;
    @Json(name = "aboutme")
    private String aboutme;
    @Json(name = "age")
    private Integer age;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getChatUser() {
        return chatUser;
    }

    public void setChatUser(String chatUser) {
        this.chatUser = chatUser;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPopularity() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }


    public String getFullcity() {
        return fullcity;
    }

    public void setFullcity(String fullcity) {
        this.fullcity = fullcity;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public Object getFriends() {
        return friends;
    }

    public void setFriends(Object friends) {
        this.friends = friends;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public LastMsg getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(LastMsg lastMsg) {
        this.lastMsg = lastMsg;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Integer getTotalUnreadMessagesCount() {
        return totalUnreadMessagesCount;
    }

    public void setTotalUnreadMessagesCount(Integer totalUnreadMessagesCount) {
        this.totalUnreadMessagesCount = totalUnreadMessagesCount;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public Integer getAge() { return age; }

    public void setAge(Integer age) {this.age = age; }

}
