
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class TargetUserScore {

    @Json(name = "score")
    private Integer score;
    @Json(name = "likes")
    private Integer likes;
    @Json(name = "dislikes")
    private Integer dislikes;

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
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
