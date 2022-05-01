
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class MaxFileUploadSize {

    @Json(name = "value")
    private String value;
    @Json(name = "unit")
    private String unit;

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
