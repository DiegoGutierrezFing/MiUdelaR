package diego.com.miudelar;

import com.google.gson.GsonBuilder;

import java.util.Date;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;
import android.content.Context;
import android.content.res.AssetManager;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

//import okhttp3.logging.HttpLoggingInterceptor;

public class ApiClient {

    //public static final String BASE_URL = "http://tsi-diego.eastus.cloudapp.azure.com:8080/miudelar-server/";
    private static Retrofit retrofit = null;

    // Implementar patron singleton
    public static Retrofit getClient(String BASE_URL) {
        if (retrofit==null) {
                retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create(new GsonBuilder()
                                .setLenient()
                                .registerTypeAdapter(Date.class,new ImprovedDateTypeAdapter())
                                .create()))
                        .addConverterFactory(ScalarsConverterFactory.create())
                        .build();
        }
        return retrofit;
    }

    /**
     * Codigo para leer el archivo de propiedades
     * Se toma como base el codigo creado por Nirmal Dhara el 12-07-2015.
     * http://javaant.com/how-to-use-properties-file-in-android/
     */
    public static String getProperty(String key, Context context) throws IOException {
        Properties properties = new Properties();
        AssetManager assetManager = context.getAssets();
        InputStream inputStream = assetManager.open("configuracion.properties");
        properties.load(inputStream);
        return properties.getProperty(key);
    }
}
