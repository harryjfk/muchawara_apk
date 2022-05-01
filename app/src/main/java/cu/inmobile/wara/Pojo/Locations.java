
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class Locations {

    @Json(name = "user_profile")
    private UserProfile userProfile;
    @Json(name = "people_nearby")
    private PeopleNearby peopleNearby;

    public UserProfile getUserProfile() {
        return userProfile;
    }

    public void setUserProfile(UserProfile userProfile) {
        this.userProfile = userProfile;
    }

    public PeopleNearby getPeopleNearby() {
        return peopleNearby;
    }

    public void setPeopleNearby(PeopleNearby peopleNearby) {
        this.peopleNearby = peopleNearby;
    }

}
