
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class GetFilterApi {

    @Json(name = "status")
    private Boolean status;
    @Json(name = "success_type")
    private String successType;
    @Json(name = "success_text")
    private String successText;
    @Json(name = "success_data")
    private SuccessDataGetFilter successData;

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }

    public String getSuccessType() {
        return successType;
    }

    public void setSuccessType(String successType) {
        this.successType = successType;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

    public SuccessDataGetFilter getSuccessDataGetFilter() {
        return successData;
    }

    public void setSuccessDataGetFilter(SuccessDataGetFilter successData) {
        this.successData = successData;
    }

}
