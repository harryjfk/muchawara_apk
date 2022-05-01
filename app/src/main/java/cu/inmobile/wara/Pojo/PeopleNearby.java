
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class PeopleNearby {

    @Json(name = "latitude")
    private Double latitude;
    @Json(name = "longitude")
    private Double longitude;
    @Json(name = "location_name")
    private Object locationName;

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

    public Object getLocationName() {
        return locationName;
    }

    public void setLocationName(Object locationName) {
        this.locationName = locationName;
    }

}
