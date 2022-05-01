
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SaveFilterApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_type")
    private String successType;
    @Json(name = "success_data")
    private SuccessData successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getSuccessType() {
        return successType;
    }

    public void setSuccessType(String successType) {
        this.successType = successType;
    }

    public SuccessData getSuccessData() {
        return successData;
    }

    public void setSuccessData(SuccessData successData) {
        this.successData = successData;
    }

}
