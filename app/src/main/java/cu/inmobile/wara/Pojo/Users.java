
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

import java.util.List;

public class Users {

    @Json(name = "user")
    private User2 user;
    @Json(name = "related")
    private List<Related> related = null;

    public User2 getUser() {
        return user;
    }

    public void setUser(User2 user) {
        this.user = user;
    }

    public List<Related> getRelated() {
        return related;
    }

    public void setRelated(List<Related> related) {
        this.related = related;
    }

}
