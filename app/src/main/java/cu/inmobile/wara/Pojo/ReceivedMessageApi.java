
package cu.inmobile.wara.Pojo;


import com.squareup.moshi.Json;

import java.util.List;


public class ReceivedMessageApi {

    @Json(name = "return")
    private Boolean received;

    public Boolean getReceived() {
        return received;
    }

    public void setReceived(Boolean received) {
        this.received = received;
    }



}


