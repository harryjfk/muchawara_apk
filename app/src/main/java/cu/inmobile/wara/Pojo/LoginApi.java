
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class LoginApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessData successData;

    @Json(name = "error_data")
    private ErrorData errorData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
    public SuccessData getSuccessData() {
        return successData;
    }

    public void setSuccessData(SuccessData successData) {
        this.successData = successData;
    }

    public ErrorData getErrorData() {
        return errorData;
    }

    public void setErrorData(ErrorData errorData) {
        this.errorData = errorData;
    }
}
