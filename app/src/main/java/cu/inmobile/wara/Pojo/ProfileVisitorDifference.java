
package cu.inmobile.wara.Pojo;

import com.squareup.moshi.Json;

public class ProfileVisitorDifference {

    @Json(name = "today_increased")
    private String todayIncreased;
    @Json(name = "this_week_increased")
    private String thisWeekIncreased;
    @Json(name = "this_month_increased")
    private String thisMonthIncreased;

    public String getTodayIncreased() {
        return todayIncreased;
    }

    public void setTodayIncreased(String todayIncreased) {
        this.todayIncreased = todayIncreased;
    }

    public String getThisWeekIncreased() {
        return thisWeekIncreased;
    }

    public void setThisWeekIncreased(String thisWeekIncreased) {
        this.thisWeekIncreased = thisWeekIncreased;
    }

    public String getThisMonthIncreased() {
        return thisMonthIncreased;
    }

    public void setThisMonthIncreased(String thisMonthIncreased) {
        this.thisMonthIncreased = thisMonthIncreased;
    }

}
