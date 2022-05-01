
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class CountriesCity {

    @Json(name = "id")
    private String id;
    @Json(name = "text")
    private String text;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}
