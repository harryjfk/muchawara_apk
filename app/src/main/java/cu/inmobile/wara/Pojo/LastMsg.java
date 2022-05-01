
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class LastMsg {

    @Json(name = "fromUser")
    private String fromUser;
    @Json(name = "toUser")
    private String toUser;
    @Json(name = "id")
    private String id;
    @Json(name = "token")
    private String token;
    @Json(name = "text")
    private String text;
    @Json(name = "sended")
    private String sended;
    @Json(name = "date")
    private String date;
    @Json(name = "readed")
    private String readed;
    @Json(name = "recieved")
    private String recieved;

    public String getFromUser() {
        return fromUser;
    }

    public void setFromUser(String fromUser) {
        this.fromUser = fromUser;
    }

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getSended() {
        return sended;
    }

    public void setSended(String sended) {
        this.sended = sended;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getReaded() {
        return readed;
    }

    public void setReaded(String readed) {
        this.readed = readed;
    }

    public String getRecieved() {
        return recieved;
    }

    public void setRecieved(String recieved) {
        this.recieved = recieved;
    }

}
