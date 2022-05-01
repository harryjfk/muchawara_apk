
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class UserSendLike {

    @Json(name = "id")
    private Integer id;
    @Json(name = "name")
    private String name;
    @Json(name = "fullcity")
    private String fullcity;
    @Json(name = "popularity")
    private String popularity;
    @Json(name = "age")
    private Integer age;
    @Json(name = "about_me")
    private String aboutme;
    @Json(name = "slug_name")
    private String slugName;
    @Json(name = "profile_pics")
    private ProfilePics profilePics;
    @Json(name = "online")
    private Boolean online;
    @Json(name = "chat_username")
    private String chatUsername;
    @Json(name = "profile_pic_url")
    private String profilePicUrl;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlugName() {
        return slugName;
    }

    public void setSlugName(String slugName) {
        this.slugName = slugName;
    }

    public String getFullcity() {
        return fullcity;
    }

    public void setFullcity(String fullcity) {
        this.fullcity = fullcity;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public ProfilePics getProfilePics() {
        return profilePics;
    }

    public void setProfilePics(ProfilePics profilePics) {
        this.profilePics = profilePics;
    }

    public Boolean getOnline() {
        return online;
    }

    public void setOnline(Boolean online) {
        this.online = online;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public String getPopularitye() {
        return popularity;
    }

    public void setPopularity(String popularity) {
        this.popularity = popularity;
    }

    public String getChatUsername() {
        return chatUsername;
    }

    public void setChatUsername(String chatUsername) {
        this.chatUsername = chatUsername;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

}
