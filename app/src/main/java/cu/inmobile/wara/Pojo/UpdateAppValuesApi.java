
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class UpdateAppValuesApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessDataUpdateValues successDataUpdateValues;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataUpdateValues getSuccessDataUpdateValues() {
        return successDataUpdateValues;
    }

    public void setSuccessDataUpdateValues(SuccessDataUpdateValues successDataUpdateValues) {
        this.successDataUpdateValues = successDataUpdateValues;
    }

}
