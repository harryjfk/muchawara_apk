
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class Result {

    @Json(name = "messageID")
    private String messageID;
    @Json(name = "conversationID")
    private String conversationID;
    @Json(name = "fromJID")
    private String fromJID;
    @Json(name = "fromJIDResource")
    private String fromJIDResource;
    @Json(name = "toJID")
    private String toJID;
    @Json(name = "toJIDResource")
    private Object toJIDResource;
    @Json(name = "sentDate")
    private String sentDate;
    @Json(name = "stanza")
    private String stanza;
    @Json(name = "body")
    private String body;
    @Json(name = "readed")
    private String readed;
    @Json(name = "recieved")
    private String recieved;
    @Json(name = "message_token")
    private String messageToken;
    @Json(name = "sent_date")
    private String originSentdate;

    public String getMessageID() {
        return messageID;
    }

    public void setMessageID(String messageID) {
        this.messageID = messageID;
    }

    public String getConversationID() {
        return conversationID;
    }

    public void setConversationID(String conversationID) {
        this.conversationID = conversationID;
    }

    public String getFromJID() {
        return fromJID;
    }

    public void setFromJID(String fromJID) {
        this.fromJID = fromJID;
    }

    public String getFromJIDResource() {
        return fromJIDResource;
    }

    public void setFromJIDResource(String fromJIDResource) {
        this.fromJIDResource = fromJIDResource;
    }

    public String getToJID() {
        return toJID;
    }

    public void setToJID(String toJID) {
        this.toJID = toJID;
    }

    public Object getToJIDResource() {
        return toJIDResource;
    }

    public void setToJIDResource(Object toJIDResource) {
        this.toJIDResource = toJIDResource;
    }

    public String getSentDate() {
        return sentDate;
    }

    public void setSentDate(String sentDate) {
        this.sentDate = sentDate;
    }

    public String getStanza() {
        return stanza;
    }

    public void setStanza(String stanza) {
        this.stanza = stanza;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
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

    public String getMessageToken() {
        return messageToken;
    }

    public void setMessageToken(String messageToken) {
        this.messageToken = messageToken;
    }

    public String getOriginSentdate() {
        return originSentdate;
    }

    public void setOriginSentdate(String originSentdate) {
        this.originSentdate = originSentdate;
    }

}
