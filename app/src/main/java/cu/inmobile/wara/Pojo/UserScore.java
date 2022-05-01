
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class UserScore {

    @Json(name = "score")
    private float score;
    @Json(name = "likes")
    private Integer likes;
    @Json(name = "dislikes")
    private Integer dislikes;

    public float getScore() {
        return score;
    }

    public void setScore(float score) {
        this.score = score;
    }

    public Integer getLikes() {
        return likes;
    }

    public void setLikes(Integer likes) {
        this.likes = likes;
    }

    public Integer getDislikes() {
        return dislikes;
    }

    public void setDislikes(Integer dislikes) {
        this.dislikes = dislikes;
    }

}
