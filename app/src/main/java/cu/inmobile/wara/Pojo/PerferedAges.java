
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class PerferedAges {

    @Json(name = "min")
    private String min;
    @Json(name = "max")
    private String max;

    public String getMin() {
        return min;
    }

    public void setMin(String min) {
        this.min = min;
    }

    public String getMax() {
        return max;
    }

    public void setMax(String max) {
        this.max = max;
    }

}
