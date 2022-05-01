
package cu.inmobile.wara.Pojo;

import java.util.List;
import com.squareup.moshi.Json;

public class SuccessDataGetFilter {

    @Json(name = "perfered_ages")
    private PerferedAges perferedAges;
    @Json(name = "prefered_genders")
    private List<String> preferedGenders = null;
    @Json(name = "prefered_online_status")
    private String preferedOnlineStatus;
    @Json(name = "perfered_distance")
    private PerferedDistance perferedDistance;
    @Json(name = "locations")
    private Locations locations;
    @Json(name = "prefered_location")
    private String preferedLocation;
    @Json(name = "countriesCities")
    private List<CountriesCity> countriesCities = null;

    public PerferedAges getPerferedAges() {
        return perferedAges;
    }

    public void setPerferedAges(PerferedAges perferedAges) {
        this.perferedAges = perferedAges;
    }

    public List<String> getPreferedGenders() {
        return preferedGenders;
    }

    public void setPreferedGenders(List<String> preferedGenders) {
        this.preferedGenders = preferedGenders;
    }

    public String getPreferedOnlineStatus() {
        return preferedOnlineStatus;
    }

    public void setPreferedOnlineStatus(String preferedOnlineStatus) {
        this.preferedOnlineStatus = preferedOnlineStatus;
    }

    public PerferedDistance getPerferedDistance() {
        return perferedDistance;
    }

    public void setPerferedDistance(PerferedDistance perferedDistance) {
        this.perferedDistance = perferedDistance;
    }

    public Locations getLocations() {
        return locations;
    }

    public void setLocations(Locations locations) {
        this.locations = locations;
    }

    public String getPreferedLocation() {
        return preferedLocation;
    }

    public void setPreferedLocation(String preferedLocation) {
        this.preferedLocation = preferedLocation;
    }

    public List<CountriesCity> getCountriesCities() {
        return countriesCities;
    }

    public void setCountriesCities(List<CountriesCity> countriesCities) {
        this.countriesCities = countriesCities;
    }

}
