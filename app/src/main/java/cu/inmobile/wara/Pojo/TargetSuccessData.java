
package cu.inmobile.wara.Pojo;

import java.util.List;
import com.squareup.moshi.Json;

public class TargetSuccessData {

    @Json(name = "commom_interests")
    private List<Object> commomInterests = null;
    @Json(name = "photos")
    private List<Photo> photos = null;
    @Json(name = "field_sections")
    private List<Object> fieldSections = null;
    @Json(name = "user_popularity")
    private TargetUserPopularity userPopularity;
    @Json(name = "liked_me")
    private Integer likedMe;
    @Json(name = "i_liked")
    private Integer iLiked;
    @Json(name = "profile_complete_percentage")
    private Integer profileCompletePercentage;
    @Json(name = "user_score")
    private UserScore userScore;
    @Json(name = "user")
    private TargetUser user;
    @Json(name = "blocked")
    private String blocked;
    @Json(name = "blocked_me")
    private String blockedMe;
    @Json(name = "about_me")
    private String aboutMe;
    @Json(name = "distance")
    private TargetDistance distance;
    @Json(name = "user_interests")
    private List<Object> userInterests = null;
    @Json(name = "profile_visitor_count")
    private ProfileVisitorCount profileVisitorCount;
    @Json(name = "profile_visitor_difference")
    private ProfileVisitorDifference profileVisitorDifference;
    @Json(name = "max_file_upload_size")
    private MaxFileUploadSize maxFileUploadSize;
    @Json(name = "minimum_photos_required")
    private Integer minimumPhotosRequired;
    @Json(name = "user_can_see_all_photos")
    private Boolean userCanSeeAllPhotos;
    @Json(name = "city")
    private String city;
    @Json(name = "success_text")
    private String successText;

    public List<Object> getCommomInterests() {
        return commomInterests;
    }

    public void setCommomInterests(List<Object> commomInterests) {
        this.commomInterests = commomInterests;
    }

    public List<Photo> getPhotos() {
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }

    public List<Object> getFieldSections() {
        return fieldSections;
    }

    public void setFieldSections(List<Object> fieldSections) {
        this.fieldSections = fieldSections;
    }

    public TargetUserPopularity getUserPopularity() {
        return userPopularity;
    }

    public void setUserPopularity(TargetUserPopularity userPopularity) {
        this.userPopularity = userPopularity;
    }

    public Integer getLikedMe() {
        return likedMe;
    }

    public void setLikedMe(Integer likedMe) {
        this.likedMe = likedMe;
    }

    public Integer getILiked() {
        return iLiked;
    }

    public void setILiked(Integer iLiked) {
        this.iLiked = iLiked;
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

    public TargetUser getUser() {
        return user;
    }

    public void setUser(TargetUser user) {
        this.user = user;
    }

    public String getBlocked() {
        return blocked;
    }

    public void setBlocked(String blocked) {
        this.blocked = blocked;
    }

    public String getBlockedMe() {
        return blockedMe;
    }

    public void setBlockedMe(String blockedMe) {
        this.blockedMe = blockedMe;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String aboutMe) {
        this.aboutMe = aboutMe;
    }

    public TargetDistance getDistance() {
        return distance;
    }

    public void setDistance(TargetDistance distance) {
        this.distance = distance;
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

    public Integer getMinimumPhotosRequired() {
        return minimumPhotosRequired;
    }

    public void setMinimumPhotosRequired(Integer minimumPhotosRequired) {
        this.minimumPhotosRequired = minimumPhotosRequired;
    }

    public Boolean getUserCanSeeAllPhotos() {
        return userCanSeeAllPhotos;
    }

    public void setUserCanSeeAllPhotos(Boolean userCanSeeAllPhotos) {
        this.userCanSeeAllPhotos = userCanSeeAllPhotos;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

}
