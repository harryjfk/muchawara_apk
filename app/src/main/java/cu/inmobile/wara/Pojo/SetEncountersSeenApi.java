
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SetEncountersSeenApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessDataEncountersSeen successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataEncountersSeen getSuccessData() {
        return successData;
    }

    public void setSuccessData(SuccessDataEncountersSeen successData) {
        this.successData = successData;
    }

}
