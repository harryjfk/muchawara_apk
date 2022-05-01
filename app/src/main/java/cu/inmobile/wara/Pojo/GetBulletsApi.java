
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class GetBulletsApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessDataGetBullets successData;
    @Json(name = "error_data")
    private ErrorDataGetBullets errorData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessDataGetBullets getSuccessDataBullets() {
        return successData;
    }

    public void setSuccessDataBullets(SuccessDataGetBullets successData) {
        this.successData = successData;
    }

    public ErrorDataGetBullets getErrorDataBullets() {
        return errorData;
    }

    public void setErrorDataBullets(ErrorDataGetBullets errorData) {
        this.errorData = errorData;
    }

}
