
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class TargetUserPopularity {

    @Json(name = "popularity")
    private Boolean popularity;
    @Json(name = "popularity_type")
    private String popularityType;

    public Boolean getPopularity() {
        return popularity;
    }

    public void setPopularity(Boolean popularity) {
        this.popularity = popularity;
    }

    public String getPopularityType() {
        return popularityType;
    }

    public void setPopularityType(String popularityType) {
        this.popularityType = popularityType;
    }

}
