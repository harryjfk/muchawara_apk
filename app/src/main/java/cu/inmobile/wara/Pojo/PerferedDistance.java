
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class PerferedDistance {

    @Json(name = "value")
    private Integer value;
    @Json(name = "unit")
    private String unit;

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }

}
