
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class Connection {

    @Json(name = "service")
    private String service;
    @Json(name = "user")
    private String user;
    @Json(name = "serverDomain")
    private String serverDomain;
    @Json(name = "password")
    private String password;

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getServerDomain() {
        return serverDomain;
    }

    public void setServerDomain(String serverDomain) {
        this.serverDomain = serverDomain;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

}
