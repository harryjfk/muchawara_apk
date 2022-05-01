
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class ErrorData {

    @Json(name = "error_text")
    private String error;

    public String getError() {
        return error;
    }
    public void setError(String error) {
        this.error = error;
    }

}
