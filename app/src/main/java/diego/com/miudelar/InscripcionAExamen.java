package diego.com.miudelar;

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

import diego.com.miudelar.data.api.model.DtExamen;
import diego.com.miudelar.data.api.model.InscripcionExamenBody;
import diego.com.miudelar.data.prefs.SessionPrefs;
import diego.com.miudelar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InscripcionAExamen extends AppCompatActivity {

    private View mProgressView;
    private Spinner spinnerExamenes;
    private TextView descripcion;
    private TextView tituloListaExamenes;
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

        setContentView(R.layout.activity_inscripcion_aexamen);

        getSupportActionBar().setHomeButtonEnabled(true);

        spinnerExamenes = (Spinner) findViewById(R.id.examenes);
        tituloListaExamenes = (TextView) findViewById(R.id.tituloListaExamenes);
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

        //Realizar peticion al servidor de MiUdelaR y llenar el Spinner de Examenes con elementos
        Call<List<DtExamen>> callDtExamen = apiService.getExamenesByCedula(authorization, usuario);
        callDtExamen.enqueue(new Callback<List<DtExamen>>() {

            @Override
            public void onResponse(Call<List<DtExamen>> call, Response<List<DtExamen>> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        List<DtExamen> examenes = response.body();

                        ArrayAdapter<DtExamen> adapter = new MiAdaptador(InscripcionAExamen.this, R.layout.examen_item, examenes);

                        adapter.setDropDownViewResource(R.layout.examen_item);

                        spinnerExamenes.setAdapter(adapter);

                        showProgress(false);
                    }
                }
                else {
                    Toast.makeText(InscripcionAExamen.this, "Error: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    return;
                }
            }

            @Override
            public void onFailure(Call<List<DtExamen>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se realizaba la peticion", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClickConfirmar(View view){

        spinnerExamenes = (Spinner) findViewById(R.id.examenes);

        DtExamen dtExamen;

        if(spinnerExamenes != null && spinnerExamenes.getSelectedItem() !=null ) {

            dtExamen= (DtExamen) spinnerExamenes.getSelectedItem();

            //Toast.makeText(InscripcionAExamen.this, "Examen seleccionado: " + dtExamen.getId(), Toast.LENGTH_SHORT).show();
            Toast.makeText(InscripcionAExamen.this, "Realizando inscripción: Espere...", Toast.LENGTH_SHORT).show();

            apiService = ApiClient.getClient(url).create(ApiInterface.class);

            authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);

            Call<String> c = apiService.inscripcionExamen(authorization, contentType, new InscripcionExamenBody(usuario, dtExamen.getId()));
            c.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){

                            // Mostrar mensaje de que se tuvo exito en la inscripcion
                            //Toast.makeText(InscripcionAExamen.this, response.body().toString(), Toast.LENGTH_SHORT).show();
                            if (response.body().contains("OK")) {
                                Snackbar.make(findViewById(R.id.nav_inscripcion_a_carrera_layout), "Inscripción a exámen exitosa!", Snackbar.LENGTH_LONG)
                                        .setActionTextColor(getResources().getColor(R.color.snackbar_action))
                                        .setAction("Aceptar", new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Log.i("Snackbar", "Pulsada acción snackbar!");
                                            }
                                        }).show();
                                return;
                            }
                            else {
                                Snackbar.make(findViewById(R.id.nav_inscripcion_a_examen_layout), response.body(), Snackbar.LENGTH_LONG).show();
                                return;
                                // Ir al menu principal (main activity)
                                //irAMenuPrincipal();
                            }

                        } else {
                            Toast.makeText(InscripcionAExamen.this, "Error desconocido: respuesta del servidor vacia", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }
                    // Procesar errores
                    else {
                        Toast.makeText(InscripcionAExamen.this, "Error desconocido: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        Log.i("Body error", response.errorBody().toString());
                        return;
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {
                    Toast.makeText(InscripcionAExamen.this, "Error: No fue posible contactar con el servidor", Toast.LENGTH_SHORT).show();
                    t.printStackTrace();
                    return;
                }
            });

        } else  {
            Toast.makeText(InscripcionAExamen.this, "Error: No se han cargado elementos en la lista de carreras o no se ha seleccionado ningun elemento", Toast.LENGTH_SHORT).show();
            return;
        }
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        tituloListaExamenes.setVisibility(visibility);
        botonConfirmar.setVisibility(visibility);
        descripcion.setVisibility(visibility);
        spinnerExamenes.setVisibility(visibility);
    }

    private void irAMenuPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private class MiAdaptador extends ArrayAdapter<DtExamen> {

        List<DtExamen> examenes = new ArrayList<>();

        public MiAdaptador(Context context, int resource, List<DtExamen> objects) {
            super(context, resource, objects);
            examenes = objects;
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

            View row = inflater.inflate(R.layout.examen_item, parent, false);

            TextView idCurso = (TextView)row.findViewById(R.id.codigoExamen);

            idCurso.setText("Código de exámen: " + examenes.get(position).getId().toString());

            TextView nombreAsignatura = (TextView)row.findViewById(R.id.nombreCurso);

            nombreAsignatura.setText("Asignatura: " + examenes.get(position).getAsignatura_Carrera().getAsignatura().getNombre());

            TextView nombreCarrera = (TextView)row.findViewById(R.id.carreraExamen);

            nombreCarrera.setText("Carrera: " + examenes.get(position).getAsignatura_Carrera().getCarrera().getNombre());

            return row;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

}
