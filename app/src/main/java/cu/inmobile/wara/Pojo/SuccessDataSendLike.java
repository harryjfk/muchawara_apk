
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class SuccessDataSendLike {

    @Json(name = "match_found")
    private Boolean matchFound;
    @Json(name = "contact_id")
    private Integer contactId;
    @Json(name = "user")
    private UserSendLike user;
    @Json(name = "success_text")
    private String successText;
    @Json(name = "credits")
    private Integer credits;

    public Boolean getMatchFound() {
        return matchFound;
    }

    public void setMatchFound(Boolean matchFound) {
        this.matchFound = matchFound;
    }

    public Integer getContactId() {
        return contactId;
    }

    public void setContactId(Integer contactId) {
        this.contactId = contactId;
    }

    public UserSendLike getUser() {
        return user;
    }

    public void setUser(UserSendLike user) {
        this.user = user;
    }

    public String getSuccessText() {
        return successText;
    }

    public void setSuccessText(String successText) {
        this.successText = successText;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

}
