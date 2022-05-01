
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class ProfileVisitorCount {

    @Json(name = "today")
    private Integer today;
    @Json(name = "this_week")
    private Integer thisWeek;
    @Json(name = "this_month")
    private Integer thisMonth;

    public Integer getToday() {
        return today;
    }

    public void setToday(Integer today) {
        this.today = today;
    }

    public Integer getThisWeek() {
        return thisWeek;
    }

    public void setThisWeek(Integer thisWeek) {
        this.thisWeek = thisWeek;
    }

    public Integer getThisMonth() {
        return thisMonth;
    }

    public void setThisMonth(Integer thisMonth) {
        this.thisMonth = thisMonth;
    }

}
