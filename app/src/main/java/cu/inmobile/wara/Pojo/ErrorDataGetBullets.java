
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class ErrorDataGetBullets {

    @Json(name = "error_text")
    private String errorText;

    public String getErrorText() {
        return errorText;
    }

    public void setErrorText(String errorText) {
        this.errorText = errorText;
    }

}
