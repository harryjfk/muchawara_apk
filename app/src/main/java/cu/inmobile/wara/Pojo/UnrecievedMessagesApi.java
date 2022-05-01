
package cu.inmobile.wara.Pojo;

import java.util.List;
import com.squareup.moshi.Json;

public class UnrecievedMessagesApi {

    @Json(name = "status")
    private String status;
    @Json(name = "result")
    private List<List<Result>> result = null;
    @Json(name = "resultUsers")
    private List<ResultUser> resultUsers = null;

    @Json(name = "error")
    private String error;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<List<Result>> getResult() {
        return result;
    }

    public void setResult(List<List<Result>> result) {
        this.result = result;
    }

    public List<ResultUser> getResultUsers() {
        return resultUsers;
    }

    public void setResultUsers(List<ResultUser> resultUsers) {
        this.resultUsers = resultUsers;
    }

}
