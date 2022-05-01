
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SuccessDataEncountersSeen {

    @Json(name = "success_text")
    private String successText;

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

}
