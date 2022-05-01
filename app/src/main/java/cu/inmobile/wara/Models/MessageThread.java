package cu.inmobile.wara.Models;

import org.joda.time.DateTime;

/**
 * Created by amal on 21/12/16.
 */
public class MessageThread {
    private String Id;
    private String userId;
    private String lastMessage;
    private DateTime lastSent;
    private String name;
    private String picture;
    private String contactId;
    private String date;
    private String userAboutMe;
    private int online;
    private boolean unread;

    public MessageThread(String userId, String lastMessage, DateTime lastSent, String name, String picture, String contactId, String date, String userAboutMe, int online, boolean unread) {
        this.userId = userId;
        this.lastMessage = lastMessage;
        this.lastSent = lastSent;
        this.name = name;
        this.picture = picture;
        this.contactId = contactId;
        this.date = date;
        this.userAboutMe = userAboutMe;
        this.online = online;
        this.unread = unread;
    }

    public String getUserAboutMe() {
        return userAboutMe;
    }

    public void setUserAboutMe(String userAboutMe) {
        this.userAboutMe = userAboutMe;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isUnread() {
        return unread;
    }

    public void setUnread(boolean unread) {
        this.unread = unread;
    }


    public void setOnline(int online) {
        this.online = online;
    }

    public int getOnline() {
        return online;
    }

    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }

    public DateTime getLastSent() {
        return lastSent;
    }

    public void setLastSent(DateTime lastSent) {
        this.lastSent = lastSent;
    }


    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getContactId() {
        return contactId;
    }

    public void setContactId(String contactId) {
        this.contactId = contactId;
    }
}
