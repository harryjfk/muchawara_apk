
package cu.inmobile.wara.Pojo;

import java.util.List;
import com.squareup.moshi.Json;

public class User2 {

    @Json(name = "system_id")
    private String systemId;
    @Json(name = "name")
    private String name;
    @Json(name = "fullname")
    private String fullname;
    @Json(name = "profile")
    private String profile;
    @Json(name = "last_time")
    private Object lastTime;
    @Json(name = "friends")
    private List<String> friends = null;
    @Json(name = "profile_picture")
    private String profilePicture;
    @Json(name = "last_msg")
    private Object lastMsg;
    @Json(name = "messages")
    private List<Object> messages = null;
    @Json(name = "state")
    private String state;
    @Json(name = "total_unread_messages_count")
    private Integer totalUnreadMessagesCount;
    @Json(name = "contact_id")
    private String contactId;
    @Json(name = "aboutme")
    private String aboutme;

    public String getSystemId() {
        return systemId;
    }

    public void setSystemId(String systemId) {
        this.systemId = systemId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Object getLastTime() {
        return lastTime;
    }

    public void setLastTime(Object lastTime) {
        this.lastTime = lastTime;
    }

    public List<String> getFriends() {
        return friends;
    }

    public void setFriends(List<String> friends) {
        this.friends = friends;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }

    public Object getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(Object lastMsg) {
        this.lastMsg = lastMsg;
    }

    public List<Object> getMessages() {
        return messages;
    }

    public void setMessages(List<Object> messages) {
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

}
