package diego.com.miudelar.services;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import diego.com.miudelar.api.web.ApiClient;
import diego.com.miudelar.api.web.ApiInterface;
import diego.com.miudelar.data.api.model.TokenFirebaseBody;
import diego.com.miudelar.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class FirebaseIDService extends FirebaseInstanceIdService {

    private static final String TAG = "FirebaseIDService";

    private SharedPreferences mPrefs;

    public FirebaseIDService(){

    }

    @Override
    public void onTokenRefresh() {
        // Get updated InstanceID token.
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();

        mPrefs = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = mPrefs.edit();

        editor.putString(SessionPrefs.PREF_TOKEN_FIREBASE, refreshedToken);
        editor.apply();

        Log.d(TAG, "Refreshed token: " + refreshedToken);

        // TODO: Implement this method to send any registration to your app's servers.
        sendRegistrationToServer(refreshedToken);
    }

    /**
     * Persist token to third-party servers.
     *
     * Modify this method to associate the user's FCM InstanceID token with any server-side account
     * maintained by your application.
     *
     * @param token The new token.
     */
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.
        Log.e(TAG, "sendRegistrationToServer: " + token);

        String cedula = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USERNAME, null);

        if ((cedula != null)&&(token != null)) {

            Log.e("Enviando: ", "sendRegistrationToServer: " + token);
            String authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);
            enviarDatosTokenFirebaseAServidor(authorization, cedula, token, getApplicationContext());
        }
    }

    public static void enviarDatosTokenFirebaseAServidor(String authorization, String cedula, String token, Context context){

        ApiInterface restApi;

        String url;

        try {
            url = ApiClient.getProperty("urlServidor", context);
        } catch (IOException e) {
            e.printStackTrace();
            url = "http://tsi-diego.eastus.cloudapp.azure.com:8080/miudelar-server/";
        }

        restApi = ApiClient.getClient(url).create(ApiInterface.class);

        Call<String> enviarTokenFirebaseAServidor = restApi.enviarTokenFirebase(authorization, "application/json", new TokenFirebaseBody(cedula, token));
        enviarTokenFirebaseAServidor.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response){

            }

            @Override
            public void onFailure(Call<String> call, Throwable t){
                Log.d("Respuesta token: ", "Ha ocurrido un error al enviar el token de firebase al servidor");
                t.printStackTrace();
            }
        });
    }
}
