
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class TargetDistance {

    @Json(name = "value")
    private Double value;
    @Json(name = "unit")
    private String unit;

    public Double getValue() {
        return value;
    }

    public void setValue(Double value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
