
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SuccessDataGetBullets {

    @Json(name = "bullets")
    private Integer bullets;
    @Json(name = "success_text")
    private String successText;

    public Integer getBullets() {
        return bullets;
    }

    public void setBullets(Integer bullets) {
        this.bullets = bullets;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

}
