package joke.hfad.com.miudelar;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;

import joke.hfad.com.miudelar.data.api.model.LoginBody;
import joke.hfad.com.miudelar.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    //private Retrofit mRestAdapter;
    private ApiInterface restApi;
    private String url;

    // UI references.
    private ImageView mLogoView;
    private EditText mUserIdView;
    private EditText mPasswordView;
    private TextInputLayout mFloatLabelUserId;
    private TextInputLayout mFloatLabelPassword;
    private View mProgressView;
    private View mLoginFormView;
    private Button mSignInButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Crear adaptador Retrofit
        //mRestAdapter = ApiClient.getClient();

        // Crear conexión a la API
        try {
            url = ApiClient.getProperty("urlServidor",getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
            url = "http://tsi-diego.eastus.cloudapp.azure.com:8080/miudelar-server/";
        }

        restApi = ApiClient.getClient(url).create(ApiInterface.class);

        //mLogoView = (ImageView) findViewById(R.id.logo);
        mUserIdView = (EditText) findViewById(R.id.user_id);
        mPasswordView = (EditText) findViewById(R.id.password);
        mFloatLabelUserId = (TextInputLayout) findViewById(R.id.float_label_user_id);
        mFloatLabelPassword = (TextInputLayout) findViewById(R.id.float_label_password);
        mSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mLoginFormView = findViewById(R.id.layout_login);
        mProgressView = findViewById(R.id.login_progress);

        // Setup
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    if (!isOnline()) {
                        showLoginError(getString(R.string.error_network));
                        return false;
                    }
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });

        mSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isOnline()) {
                    showLoginError(getString(R.string.error_network));
                    return;
                }
                attemptLogin();

            }
        });
    }

    private void attemptLogin() {

        // Reset errors.
        mFloatLabelUserId.setError(null);
        mFloatLabelPassword.setError(null);

        // Store values at the time of the login attempt.
        final String userId = mUserIdView.getText().toString();
        final String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Verificar si el password es valido si el usuario ha ingresado alguno.
        if (TextUtils.isEmpty(password)) {
            mFloatLabelPassword.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelPassword;
            cancel = true;
        } /*else if (!isPasswordValid(password)) {
            mFloatLabelPassword.setError(getString(R.string.error_invalid_password));
            focusView = mFloatLabelPassword;
            cancel = true;
        }*/

        // Verificar si el ID tiene contenido.
        if (TextUtils.isEmpty(userId)) {
            mFloatLabelUserId.setError(getString(R.string.error_field_required));
            focusView = mFloatLabelUserId;
            cancel = true;
        } /*else if (!isUserIdValid(userId)) {
            mFloatLabelUserId.setError(getString(R.string.error_invalid_user_id));
            focusView = mFloatLabelUserId;
            cancel = true;
        }*/

        if (cancel) {

            // Hubo un error; no intentar realizar login y enfocar el primer campo de formulario con un error.
            focusView.requestFocus();

        } else {

            // Mostrar el indicador de carga y luego iniciar la petición asíncrona.
            showProgress(true);

            Call<String> loginCall = restApi.login(new LoginBody(userId, password));
            loginCall.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    // Ocultar la barra de progreso
                    showProgress(false);

                    if (response.isSuccessful()){
                        if (response.body() != null){

                            if (!(response.body().contains("Error"))){

                                // Guardar token y userID en preferencias
                                SessionPrefs.get(LoginActivity.this).guardarToken(response.body().toString(), userId);

                                // Ir al menu principal (main activity)
                                irAMenuPrincipal();
                            }
                            else {
                                String error = response.body().toString();
                                showLoginError(error);
                                return;
                            }

                        }else{

                            String error = "Error: Usuario o contraseña incorrecta";
                            showLoginError(error);
                            return;
                        }
                    }
                    // Procesar errores
                    else {

                        String error = response.body().toString();

                        showLoginError(error);
                        return;
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    showProgress(false);
                    showLoginError(t.getMessage());
                }
            });
        }
    }

    private boolean isUserIdValid(String userId) {
        return true/*userId.length() < 10*/;
    }

    private boolean isPasswordValid(String password) {
        return true/*password.length() > 4*/;
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        //mLogoView.setVisibility(visibility);
        mLoginFormView.setVisibility(visibility);
    }

    private void irAMenuPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void showLoginError(String error) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show();
    }

    // Esta operacion verifica si hay conectividad
    private boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnected();
    }

}

