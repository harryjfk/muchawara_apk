
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SetMatchesSeenApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessDataMatchesSeen successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataMatchesSeen getSuccessData() {
        return successData;
    }

    public void setSuccessData(SuccessDataMatchesSeen successData) {
        this.successData = successData;
    }

}
