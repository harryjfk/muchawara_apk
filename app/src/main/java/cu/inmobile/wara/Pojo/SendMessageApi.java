
package cu.inmobile.wara.Pojo;

import java.util.List;
import com.squareup.moshi.Json;

public class SendMessageApi {

    @Json(name = "0")
    private List<MessageConfirmed> mesList = null;
    @Json(name = "result")
    private Boolean result;
    @Json(name = "user")
    private String user;

    public List<MessageConfirmed> get0() {
        return mesList;
    }

    public void set0(List<MessageConfirmed> _0) {
        this.mesList = _0;
    }

    public Boolean getResult() {
        return result;
    }

    public void setResult(Boolean result) {
        this.result = result;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

}
