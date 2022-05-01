package cu.inmobile.wara.Networking;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.moshi.MoshiConverterFactory;

public class APIClient {

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
       // OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();

        OkHttpClient.Builder client2 = new OkHttpClient.Builder();
        client2.connectTimeout(15,TimeUnit.SECONDS);
        client2.readTimeout(30,TimeUnit.SECONDS);


        retrofit = new Retrofit.Builder()
                .baseUrl(Endpoints.baseUrl)
          //      .baseUrl("https://www.muchawara.com/api/")
                .addConverterFactory(MoshiConverterFactory.create())
                .client(client2.addInterceptor(interceptor).build())
                .build();

        return retrofit;
    }

}
