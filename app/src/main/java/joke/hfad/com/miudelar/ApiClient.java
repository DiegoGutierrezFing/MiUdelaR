package joke.hfad.com.miudelar;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

//import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {

    public static final String BASE_URL = "http://tsi-diego.eastus.cloudapp.azure.com:8080/miudelar-server/";
    private static Retrofit retrofit = null;

    public static Retrofit getClient() {
        if (retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                                .setLenient()
                                .create()))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
        }
        return retrofit;
    }
}
