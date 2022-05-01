
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class ProfilePics {

    @Json(name = "thumbnail")
    private String thumbnail;
    @Json(name = "encounter")
    private String encounter;
    @Json(name = "other")
    private String other;
    @Json(name = "original")
    private String original;

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getEncounter() {
        return encounter;
    }

    public void setEncounter(String encounter) {
        this.encounter = encounter;
    }

    public String getOther() {
        return other;
    }

    public void setOther(String other) {
        this.other = other;
    }

    public String getOriginal() {
        return original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

}
