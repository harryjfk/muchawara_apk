
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SendLikeStatusApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessDataSendLike successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataSendLike getSuccessData() {
        return successData;
    }

    public void setSuccessData(SuccessDataSendLike successData) {
        this.successData = successData;
    }

}
