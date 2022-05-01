
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class UploadProfileApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")

    private SuccessDataUploadProfile successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataUploadProfile getSuccessDataUploadProfile() {
        return successData;
    }

    public void SuccessDataUploadProfile(SuccessDataUploadProfile successData) {
        this.successData = successData;
    }

}
