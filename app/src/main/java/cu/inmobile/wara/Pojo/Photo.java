
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class Photo {

    @Json(name = "userid")
    private Integer userid;
    @Json(name = "photo_url")
    private PhotoUrl photoUrl;
    @Json(name = "updated_at")
    private String updatedAt;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "id")
    private Integer id;
    @Json(name = "photo_name")
    private String photoName;

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public PhotoUrl getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(PhotoUrl photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

}
