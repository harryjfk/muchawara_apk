
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class RegisterApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private RegSuccessData successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public RegSuccessData getSuccessData() {
        return successData;
    }

    public void setSuccessData(RegSuccessData successData) {
        this.successData = successData;
    }

}
