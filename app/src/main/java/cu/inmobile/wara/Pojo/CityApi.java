
package cu.inmobile.wara.Pojo;


import com.squareup.moshi.Json;

import java.util.List;



public class CityApi {

    @Json(name = "status")
    private String status;
    @Json(name = "success_data")
    private SuccessData successData;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public SuccessData getSuccessData() {
        return successData;
    }

    public void setSuccessData(SuccessData successData) {
        this.successData = successData;
    }


    public static class City {

        @Json(name = "name")
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

    }

    public static class SuccessData {

        @Json(name = "city")
        private List<City> city = null;

        public List<City> getCity() {
            return city;
        }

        public void setCity(List<City> city) {
            this.city = city;
        }

    }

}


