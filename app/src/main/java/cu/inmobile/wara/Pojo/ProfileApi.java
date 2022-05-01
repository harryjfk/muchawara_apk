
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class ProfileApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessDataProfile successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataProfile getSuccessDataProfile() {
        return successData;
    }

    public void setSuccessDataProfile(SuccessDataProfile successData) {
        this.successData = successData;
    }

}
