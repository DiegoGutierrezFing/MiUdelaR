package joke.hfad.com.miudelar;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import joke.hfad.com.miudelar.data.api.model.DtCarrera;
import joke.hfad.com.miudelar.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InscripcionACarrera extends AppCompatActivity {

    private View mProgressView;
    private Spinner spinnerCarreras;
    private TextView descripcion;
    private TextView tituloListaCarreras;
    private Button botonConfirmar;
    private String authorization;
    private ApiInterface apiService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_inscripcion_acarrera);

        spinnerCarreras = (Spinner) findViewById(R.id.carreras);
        tituloListaCarreras = (TextView) findViewById(R.id.tituloListaCarreras);
        descripcion = (TextView) findViewById(R.id.descripcion);
        botonConfirmar = (Button) findViewById(R.id.botonConfirmar);
        mProgressView = findViewById(R.id.progressBar);

        showProgress(true);

        //recyclerView.setLayoutManager(new LinearLayoutManager(this));

        apiService = ApiClient.getClient().create(ApiInterface.class);

        authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);

        //Toast.makeText(this, authorization, Toast.LENGTH_SHORT).show();

        Call<List<DtCarrera>> c = apiService.getAllCarreras(authorization);
        c.enqueue(new Callback<List<DtCarrera>>() {
            @Override
            public void onResponse(Call<List<DtCarrera>> call, Response<List<DtCarrera>> response) {

                showProgress(false);

                List<DtCarrera> carreras = new ArrayList<DtCarrera>();
                carreras = response.body();
                //recyclerView.setAdapter(new AlbumsAdapter(albums, R.layout.list_item_album, getApplicationContext()));

                List<String> listaCarreras = new ArrayList<String>();

                /*
                for (DtCarrera c : carreras){
                    listaCarreras.add(c.getCodigo() + " - " + c.getNombre());
                }
                */

                ArrayAdapter<DtCarrera> adapter = new ArrayAdapter<DtCarrera>(InscripcionACarrera.this, android.R.layout.simple_spinner_item, carreras);

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerCarreras.setAdapter(adapter);
            }

            @Override
            public void onFailure(Call<List<DtCarrera>> call, Throwable t) {
                // Log error here since request failed
                //Log.e(TAG, t.toString());
                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se realizaba la peticion", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        tituloListaCarreras.setVisibility(visibility);
        botonConfirmar.setVisibility(visibility);
        descripcion.setVisibility(visibility);
    }

    public void onClickConfirmar(View view){
        DtCarrera dtCarrera = (DtCarrera) spinnerCarreras.getSelectedItem();
        Toast.makeText(InscripcionACarrera.this, dtCarrera.toString(), Toast.LENGTH_SHORT);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);

        String json = "{ \"cedula\" : " + "\"41975046\"" + ", \"codigo\" : \"" + Long.toString(dtCarrera.getCodigo()) + "\" }";

        Call<String> c = apiService.inscripcionCarrera(authorization, json);
        c.enqueue(new Callback<String>() {
            @Override
            public void onResponse(Call<String> call, Response<String> response) {
                if (response.isSuccessful()){
                    if (response.body() != null){

                        //if (!(response.body().toString().contains("Error"))){
                            // Mostrar mensaje de que se tuvo exito en la inscripcion
                            Toast.makeText(InscripcionACarrera.this, response.body().toString(), Toast.LENGTH_SHORT);

                            // Ir al menu principal (main activity)
                            irAMenuPrincipal();
                        //}

                    }else {
                        Toast.makeText(InscripcionACarrera.this, "Error desconocido", Toast.LENGTH_SHORT);
                        return;
                    }
                }

                // Procesar errores
                if (!response.isSuccessful()) {
                    //String error = response.body().toString();
                        /*if (response.errorBody()
                                .contentType()
                                .subtype()
                                .equals("json")) {
                            ApiError apiError = ApiError.fromResponseBody(response.errorBody());

                            error = apiError.getMessage();
                            Log.d("LoginActivity", apiError.getDeveloperMessage());
                        } else {
                            try {
                                // Reportar causas de error no relacionado con la API
                                Log.d("LoginActivity", response.errorBody().string());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }*/
                    Toast.makeText(InscripcionACarrera.this, "Error desconocido", Toast.LENGTH_SHORT);
                    return;
                }
            }

            @Override
            public void onFailure(Call<String> call, Throwable t) {
                Toast.makeText(InscripcionACarrera.this, "No fue posible recibir respuesta del servidor", Toast.LENGTH_SHORT);
            }
        });
    }

    /*
    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        //mLogoView.setVisibility(visibility);
        mLoginFormView.setVisibility(visibility);
    }
    */

    private void irAMenuPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

}