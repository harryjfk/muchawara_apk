
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class MessageUserApi {

    @Json(name = "status")
    private String status;
    @Json(name = "users")
    private Users users;
    @Json(name = "connection")
    private Connection connection;

    @Json(name = "error")
    private String error;


    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Users getUsers() {
        return users;
    }

    public void setUsers(Users users) {
        this.users = users;
    }

    public Connection getConnection() {
        return connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

}
