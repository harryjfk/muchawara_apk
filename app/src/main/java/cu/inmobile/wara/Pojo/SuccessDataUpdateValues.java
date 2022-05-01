
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SuccessDataUpdateValues {

    @Json(name = "likes")
    private Integer likes;
    @Json(name = "matches")
    private Integer matches;
    @Json(name = "bullets")
    private Integer bullets;
    @Json(name = "admin_mess")
    private String adminMess;
    @Json(name = "admin_url")
    private String adminUrl;
    @Json(name = "messages")
    private Integer messages;
    @Json(name = "usersCount")
    private Integer usersCount;

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getMatches() {
        return matches;
    }

    public void setMatches(Integer matches) {
        this.matches = matches;
    }

    public Integer getBullets() {
        return bullets;
    }

    public void setBullets(Integer bullets) {
        this.bullets = bullets;
    }

    public String getAdminMess() {
        return adminMess;
    }

    public void setAdminMess(String adminMess) {
        this.adminMess = adminMess;
    }

    public String getAdminUrl() {
        return adminUrl;
    }

    public void setAdminUrl(String adminUrl) {
        this.adminUrl = adminUrl;
    }

    public Integer getMessages() {
        return messages;
    }

    public void setMessages(Integer messages) {
        this.messages = messages;
    }

    public Integer getUsersCount() {
        return usersCount;
    }

    public void setUsersCount(Integer usersCount) {
        this.usersCount = usersCount;
    }
}
