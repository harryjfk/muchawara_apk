
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SuccessDataUploadProfile {

    @Json(name = "name")
    private String name;
    @Json(name = "age")
    private Integer age;
    @Json(name = "gender")
    private String gender;
    @Json(name = "about_me")
    private Object aboutMe;
    @Json(name = "profile_picture_url")
    private uploadProfilePictureUrl profilePictureUrl;
    @Json(name = "city")
    private Object city;
    @Json(name = "success_text")
    private String successText;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public Object getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(Object aboutMe) {
        this.aboutMe = aboutMe;
    }

    public uploadProfilePictureUrl getProfilePictureUrl() {
        return profilePictureUrl;
    }

    public void setuploadProfilePictureUrl(uploadProfilePictureUrl profilePictureUrl) {
        this.profilePictureUrl = profilePictureUrl;
    }

    public Object getCity() {
        return city;
    }

    public void setCity(Object city) {
        this.city = city;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

}
