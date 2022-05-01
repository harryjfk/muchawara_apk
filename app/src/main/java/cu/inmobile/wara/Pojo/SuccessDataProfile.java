
package cu.inmobile.wara.Pojo;

import java.util.List;
import com.squareup.moshi.Json;

public class SuccessDataProfile {

    @Json(name = "balas")
    private Integer balas;
    @Json(name = "user")
    private User user;
    @Json(name = "city")
    private String city;
    @Json(name = "field_sections")
    private List<Object> fieldSections = null;
    @Json(name = "user_popularity")
    private UserPopularity userPopularity;
    @Json(name = "about_me")
    private Object aboutMe;
    @Json(name = "profile_complete_percentage")
    private Integer profileCompletePercentage;
    @Json(name = "user_score")
    private UserScore userScore;
    @Json(name = "user_interests")
    private List<Object> userInterests = null;
    @Json(name = "profile_visitor_count")
    private ProfileVisitorCount profileVisitorCount;
    @Json(name = "profile_visitor_difference")
    private ProfileVisitorDifference profileVisitorDifference;
    @Json(name = "max_file_upload_size")
    private MaxFileUploadSize maxFileUploadSize;
    @Json(name = "locality")
    private List<String> locality = null;
    @Json(name = "photos")
    private List<Photo> photos = null;
    @Json(name = "success_text")
    private String successText;

    public Integer getBalas() {
        return balas;
    }

    public void setBalas(Integer balas) {
        this.balas = balas;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Object> getFieldSections() {
        return fieldSections;
    }

    public void setFieldSections(List<Object> fieldSections) {
        this.fieldSections = fieldSections;
    }

    public UserPopularity getUserPopularity() {
        return userPopularity;
    }

    public void setUserPopularity(UserPopularity userPopularity) {
        this.userPopularity = userPopularity;
    }

    public Object getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(Object aboutMe) {
        this.aboutMe = aboutMe;
    }

    public Integer getProfileCompletePercentage() {
        return profileCompletePercentage;
    }

    public void setProfileCompletePercentage(Integer profileCompletePercentage) {
        this.profileCompletePercentage = profileCompletePercentage;
    }

    public UserScore getUserScore() {
        return userScore;
    }

    public void setUserScore(UserScore userScore) {
        this.userScore = userScore;
    }

    public List<Object> getUserInterests() {
        return userInterests;
    }

    public void setUserInterests(List<Object> userInterests) {
        this.userInterests = userInterests;
    }

    public ProfileVisitorCount getProfileVisitorCount() {
        return profileVisitorCount;
    }

    public void setProfileVisitorCount(ProfileVisitorCount profileVisitorCount) {
        this.profileVisitorCount = profileVisitorCount;
    }

    public ProfileVisitorDifference getProfileVisitorDifference() {
        return profileVisitorDifference;
    }

    public void setProfileVisitorDifference(ProfileVisitorDifference profileVisitorDifference) {
        this.profileVisitorDifference = profileVisitorDifference;
    }

    public MaxFileUploadSize getMaxFileUploadSize() {
        return maxFileUploadSize;
    }

    public void setMaxFileUploadSize(MaxFileUploadSize maxFileUploadSize) {
        this.maxFileUploadSize = maxFileUploadSize;
    }

    public List<String> getLocality() {
        return locality;
    }

    public void setLocality(List<String> locality) {
        this.locality = locality;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

}
