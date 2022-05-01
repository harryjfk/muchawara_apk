
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class UserPopularity {
//todo se ha comentado el popularity por que en algunos usuarios viene un boolean y en otros no
   // @Json(name = "popularity")
  //  private boolean popularity;
    @Json(name = "popularity_type")
    private String popularityType;

    //todo kgfkjfkjgfkhgfjhfgjhfjhfgjhfg

   // public Boolean getPopularity() {
   //     return popularity;
   // }

  //  public void setPopularity(Boolean popularity) {
   //     this.popularity = popularity;
   // }

    public String getPopularityType() {
        return popularityType;
    }

    public void setPopularityType(String popularityType) {
        this.popularityType = popularityType;
    }

}
