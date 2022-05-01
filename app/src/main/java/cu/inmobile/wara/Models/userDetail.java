package cu.inmobile.wara.Models;

/**
 * Created by amal on 20/01/17.
 */
public class userDetail {
    private String id;
    private String name;
    private String picture;
    private String picture_perfil;
    private String age;
    private String aboutMe;
    private String distance;
    private String slug_name;
    private boolean should_show;


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSlugName() {
        return slug_name;
    }

    public void setSlugName(String slug_name) {
        this.slug_name = slug_name;
    }

    public String getPicturePerfil() {
        return picture_perfil;
    }

    public void setPicturePerfil(String picture_perfil) {
        this.picture_perfil = picture_perfil;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getAboutMe() {
        return aboutMe;
    }

    public void setAboutMe(String description) {
        this.aboutMe = description;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public boolean isShould_show() {
        return should_show;
    }

    public void setShould_show(boolean should_show) {
        this.should_show = should_show;
    }
}
