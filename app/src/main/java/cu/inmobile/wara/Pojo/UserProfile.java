
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class UserProfile {

    @Json(name = "latitude")
    private Double latitude;
    @Json(name = "longitude")
    private Double longitude;
    @Json(name = "city")
    private String city;
    @Json(name = "country")
    private String country;

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

}
