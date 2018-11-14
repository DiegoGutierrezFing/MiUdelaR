package diego.com.miudelar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import diego.com.miudelar.api.web.ApiClient;
import diego.com.miudelar.api.web.ApiInterface;
import diego.com.miudelar.data.api.model.DtCarrera;
import diego.com.miudelar.data.api.model.InscripcionCarreraBody;
import diego.com.miudelar.data.prefs.SessionPrefs;
import diego.com.miudelar.R;
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
    private String contentType;
    private String usuario;

    private ApiInterface apiService;
    private String url;

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

        getSupportActionBar().setHomeButtonEnabled(true);

        spinnerCarreras = (Spinner) findViewById(R.id.carreras);
        tituloListaCarreras = (TextView) findViewById(R.id.tituloListaCarreras);
        descripcion = (TextView) findViewById(R.id.descripcion);
        botonConfirmar = (Button) findViewById(R.id.botonConfirmar);
        mProgressView = findViewById(R.id.progressBar);

        try {
            url = ApiClient.getProperty("urlServidor",getApplicationContext());
        } catch (IOException e) {
            e.printStackTrace();
            url = "http://tsi-diego.eastus.cloudapp.azure.com:8080/miudelar-server/";
        }

        showProgress(true);

        apiService = ApiClient.getClient(url).create(ApiInterface.class);

        authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);
        usuario = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USERNAME, null);
        contentType = "application/json";

        //Realizar peticion al servidor de MiUdelaR y llenar el Spinner de Carreras con elementos
        Call<List<DtCarrera>> c = apiService.getAllCarreras(authorization);
        c.enqueue(new Callback<List<DtCarrera>>() {

            @Override
            public void onResponse(Call<List<DtCarrera>> call, Response<List<DtCarrera>> response) {

                if (response.isSuccessful()) {
                    //Toast.makeText(InscripcionACarrera.this, "la respuesta se recibio", Toast.LENGTH_SHORT).show();
                    if (response.body() != null) {
                        List<DtCarrera> carreras = new ArrayList<DtCarrera>();
                        carreras = response.body();

                        ArrayAdapter<DtCarrera> adapter = new MiAdaptador(InscripcionACarrera.this, R.layout.list_item_carrera, carreras);

                        adapter.setDropDownViewResource(R.layout.list_item_carrera);

                        spinnerCarreras.setAdapter(adapter);

                        showProgress(false);
                    }
                    else {
                        Toast.makeText(InscripcionACarrera.this, "Error desconocido: respuesta del servidor vacia", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(InscripcionACarrera.this, "Error: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<DtCarrera>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se contactaba al servidor", Toast.LENGTH_LONG).show();
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

        spinnerCarreras = (Spinner) findViewById(R.id.carreras);

        DtCarrera dtCarrera;

        if(spinnerCarreras != null && spinnerCarreras.getSelectedItem() !=null ) {

            dtCarrera = (DtCarrera) spinnerCarreras.getSelectedItem();

            //Toast.makeText(InscripcionACarrera.this, "Carrera seleccionada: " + dtCarrera.toString(), Toast.LENGTH_SHORT).show();
            Toast.makeText(InscripcionACarrera.this, "Realizando inscripción: Espere...", Toast.LENGTH_SHORT).show();
            //Snackbar.make(findViewById(R.id.nav_inscripcion_a_carrera_layout), "Realizando inscripción: Espere...", Snackbar.LENGTH_LONG).show();

            apiService = ApiClient.getClient(url).create(ApiInterface.class);

            authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);

            Call<String> c = apiService.inscripcionCarrera(authorization, contentType, new InscripcionCarreraBody(usuario, dtCarrera.getCodigo()));

            c.enqueue(new Callback<String>() {

                @Override
                public void onResponse(Call<String> call, Response<String> response) {

                    if (response.isSuccessful()){
                        if (response.body() != null){

                            //if (!(response.body().toString().contains("Error"))){
                            // Mostrar mensaje de que se tuvo exito en la inscripcion
                            //Log.i("response.body", response.body());
                            //Toast.makeText(InscripcionACarrera.this, response.body(), Toast.LENGTH_SHORT).show();
                            if (response.body().contains("OK")) {

                                Snackbar.make(findViewById(R.id.nav_inscripcion_a_carrera_layout), "Inscripción a carrera exitosa!", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getResources().getColor(R.color.snackbar_action))
                                        .setAction("Aceptar", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Log.i("Snackbar", "Pulsada acción snackbar!");
                                            }
                                        }).show();
                            }
                            else {
                                //Toast.makeText(InscripcionACarrera.this, response.body(), Toast.LENGTH_SHORT).show();
                                Log.i("Response: ", response.body());
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.nav_inscripcion_a_carrera_layout), response.body(), Snackbar.LENGTH_LONG);
                                View snackbarView = snackbar.getView();
                                TextView snackTextView = (TextView) snackbarView
                                        .findViewById(android.support.design.R.id.snackbar_text);
                                snackTextView.setMaxLines(2);
                                snackbar.show();

                                // Ir al menu principal (main activity)
                                //irAMenuPrincipal();
                            }

                        } else {
                            Log.i("response.body", response.body());
                            Toast.makeText(InscripcionACarrera.this, "Error: respuesta del servidor vacia", Toast.LENGTH_SHORT).show();
                            irAMenuPrincipal();
                        }
                    }
                    // Procesar errores
                    else {
                        Log.i("response.body", response.body());
                        Toast.makeText(InscripcionACarrera.this, "Error: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        irAMenuPrincipal();
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    Toast.makeText(InscripcionACarrera.this, "Error: No fue posible contactar con el servidor", Toast.LENGTH_SHORT).show();
                    irAMenuPrincipal();
                }
            });


        } else  {
            Toast.makeText(InscripcionACarrera.this, "Error: No se han cargado elementos en la lista de carreras o no se ha seleccionado ningun elemento", Toast.LENGTH_SHORT).show();
            irAMenuPrincipal();
        }
    }

    private void irAMenuPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private class MiAdaptador extends ArrayAdapter<DtCarrera> {

        List<DtCarrera> carreras = new ArrayList<DtCarrera>();

        public MiAdaptador(Context context, int resource, List<DtCarrera> objects) {
            super(context, resource, objects);
            carreras = objects;
        }

        public View getDropDownView(int position, View convertView,ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            return getCustomView(position, convertView, parent);
        }

        public View getCustomView(int position, View convertView, ViewGroup parent) {

            LayoutInflater inflater=getLayoutInflater();

            View row = inflater.inflate(R.layout.list_item_carrera, parent, false);

            TextView idCarrera = (TextView)row.findViewById(R.id.codigoCarrera);

            idCarrera.setText("Código de carrera: " + carreras.get(position).getCodigo().toString());

            TextView nombreCarrera = (TextView)row.findViewById(R.id.nombreCarrera);

            nombreCarrera.setText("Carrera: " + carreras.get(position).getNombre());

            return row;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}