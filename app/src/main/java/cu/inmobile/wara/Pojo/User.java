
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class User {

    @Json(name = "id")
    private Integer id;
    @Json(name = "username")
    private String username;
    @Json(name = "gender")
    private String gender;
    @Json(name = "name")
    private String name;
    @Json(name = "slug_name")
    private String slugName;
    @Json(name = "dob")
    private String dob;
    @Json(name = "city")
    private String city;
    @Json(name = "country")
    private String country;
    @Json(name = "hereto")
    private String hereto;
    @Json(name = "profile_pic_url")
    private String profilePicUrl;
    @Json(name = "status")
    private String status;
    @Json(name = "package_name")
    private Object packageName;
    @Json(name = "expired_at")
    private Object expiredAt;
    @Json(name = "activate_user")
    private String activateUser;
    @Json(name = "register_from")
    private String registerFrom;
    @Json(name = "verified")
    private String verified;
    @Json(name = "latitude")
    private Double latitude;
    @Json(name = "longitude")
    private Double longitude;
    @Json(name = "language")
    private String language;
    @Json(name = "last_request")
    private String lastRequest;
    @Json(name = "remember_token")
    private Object rememberToken;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;
    @Json(name = "deleted_at")
    private Object deletedAt;
    @Json(name = "township")
    private String township;
    @Json(name = "take_credits")
    private Integer takeCredits;
    @Json(name = "age")
    private Integer age;
    @Json(name = "aboutme")
    private String aboutme;
    @Json(name = "credits")
    private Credits credits;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getHereto() {
        return hereto;
    }

    public void setHereto(String hereto) {
        this.hereto = hereto;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Object getPackageName() {
        return packageName;
    }

    public void setPackageName(Object packageName) {
        this.packageName = packageName;
    }

    public Object getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Object expiredAt) {
        this.expiredAt = expiredAt;
    }

    public String getActivateUser() {
        return activateUser;
    }

    public void setActivateUser(String activateUser) {
        this.activateUser = activateUser;
    }

    public String getRegisterFrom() {
        return registerFrom;
    }

    public void setRegisterFrom(String registerFrom) {
        this.registerFrom = registerFrom;
    }

    public String getVerified() {
        return verified;
    }

    public void setVerified(String verified) {
        this.verified = verified;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getLastRequest() {
        return lastRequest;
    }

    public void setLastRequest(String lastRequest) {
        this.lastRequest = lastRequest;
    }

    public Object getRememberToken() {
        return rememberToken;
    }

    public void setRememberToken(Object rememberToken) {
        this.rememberToken = rememberToken;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public String getTownship() {
        return township;
    }

    public void setTownship(String township) {
        this.township = township;
    }

    public Integer getTakeCredits() {
        return takeCredits;
    }

    public void setTakeCredits(Integer takeCredits) {
        this.takeCredits = takeCredits;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.takeCredits = takeCredits;
    }

    public String getAboutme() {
        return aboutme;
    }

    public void setAboutme(String aboutme) {
        this.aboutme = aboutme;
    }

    public Credits getCredits() {
        return credits;
    }

    public void setCredits(Credits credits) {
        this.credits = credits;
    }

}
