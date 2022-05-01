
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class GetProfileInfoApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private TargetSuccessData successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TargetSuccessData getSuccessData() {
        return successData;
    }

    public void setSuccessData(TargetSuccessData successData) {
        this.successData = successData;
    }

}
