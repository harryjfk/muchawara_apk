
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class TargetPhoto {

    @Json(name = "id")
    private Integer id;
    @Json(name = "userid")
    private Integer userid;
    @Json(name = "source_photo_id")
    private Object sourcePhotoId;
    @Json(name = "photo_source")
    private Object photoSource;
    @Json(name = "photo_url")
    private PhotoUrl photoUrl;
    @Json(name = "created_at")
    private String createdAt;
    @Json(name = "updated_at")
    private String updatedAt;
    @Json(name = "deleted_at")
    private Object deletedAt;
    @Json(name = "nudity")
    private Integer nudity;
    @Json(name = "is_checked")
    private Integer isChecked;
    @Json(name = "photo_name")
    private String photoName;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Object getSourcePhotoId() {
        return sourcePhotoId;
    }

    public void setSourcePhotoId(Object sourcePhotoId) {
        this.sourcePhotoId = sourcePhotoId;
    }

    public Object getPhotoSource() {
        return photoSource;
    }

    public void setPhotoSource(Object photoSource) {
        this.photoSource = photoSource;
    }

    public PhotoUrl getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(PhotoUrl photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Object getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Object deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Integer getNudity() {
        return nudity;
    }

    public void setNudity(Integer nudity) {
        this.nudity = nudity;
    }

    public Integer getIsChecked() {
        return isChecked;
    }

    public void setIsChecked(Integer isChecked) {
        this.isChecked = isChecked;
    }

    public String getPhotoName() {
        return photoName;
    }

    public void setPhotoName(String photoName) {
        this.photoName = photoName;
    }

}
