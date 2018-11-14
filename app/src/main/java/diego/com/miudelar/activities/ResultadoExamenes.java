package diego.com.miudelar.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import diego.com.miudelar.api.web.ApiClient;
import diego.com.miudelar.api.web.ApiInterface;
import diego.com.miudelar.data.api.model.DtCalificaciones;
import diego.com.miudelar.data.api.model.DtEstudiante_Examen;
import diego.com.miudelar.data.prefs.SessionPrefs;
import diego.com.miudelar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultadoExamenes extends AppCompatActivity {

    private String authorization;
    private String contentType;
    private String usuario;

    private ListView listViewCalificaciones;
    private View mProgressView;
    private TextView descripcion;
    private TextView tituloListaCalificaciones;

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

        setContentView(R.layout.activity_resultado_examenes);

        getSupportActionBar().setHomeButtonEnabled(true);

        listViewCalificaciones = (ListView) findViewById(R.id.listViewResultadosExamenes);
        mProgressView = findViewById(R.id.progressBar);
        descripcion = (TextView) findViewById(R.id.descripcion);

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

        //Realizar peticion al servidor de MiUdelaR y llenar el ListView de calificaciones de cursos con elementos
        Call<DtCalificaciones> callDtCalificaciones = apiService.getCalificacionesSAsig(authorization, usuario);
        callDtCalificaciones.enqueue(new Callback<DtCalificaciones>() {

            @Override
            public void onResponse(Call<DtCalificaciones> call, Response<DtCalificaciones> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {
                        DtCalificaciones dtCalificaciones = response.body();

                        List<DtEstudiante_Examen> calificaciones = (ArrayList<DtEstudiante_Examen>) dtCalificaciones.getEstudiante_examen();

                        ArrayAdapter<DtEstudiante_Examen> adapter = new MiAdaptador(ResultadoExamenes.this, R.id.listViewResultadosExamenes, calificaciones);

                        adapter.setDropDownViewResource(R.layout.resultado_examen_row);

                        listViewCalificaciones.setAdapter(adapter);

                        showProgress(false);
                    } else {
                        Toast.makeText(ResultadoExamenes.this, "Error: respuesta del servidor vacia: Intente más tarde", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {

                    Toast.makeText(ResultadoExamenes.this, "Error: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    Log.i("Body error", response.errorBody().toString());

                    return;
                }
            }

            @Override
            public void onFailure(Call<DtCalificaciones> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se realizaba la petición", Toast.LENGTH_LONG).show();
                t.printStackTrace();

                return;
            }
        });
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        listViewCalificaciones.setVisibility(visibility);
        descripcion.setVisibility(visibility);
    }

    private void irAMenuPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private class MiAdaptador extends ArrayAdapter<DtEstudiante_Examen> {

        List<DtEstudiante_Examen> lista = new ArrayList<>();

        public MiAdaptador(Context context, int resource, List<DtEstudiante_Examen> objects) {
            super(context, resource, objects);
            lista = objects;
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

            View row = inflater.inflate(R.layout.resultado_examen_row, parent, false);

            TextView idExamen = (TextView) row.findViewById(R.id.idExamen);
            idExamen.setText("Código de examen: " + lista.get(position).getExamen().getId().toString());

            TextView nombreAsignatura = (TextView)row.findViewById(R.id.nombreExamen);
            nombreAsignatura.setText("Asignatura: " + lista.get(position).getExamen().getAsignatura_Carrera().getAsignatura().getNombre());

            TextView nombreCarrera = (TextView)row.findViewById(R.id.nombreCarrera);
            nombreCarrera.setText("Carrera: " + lista.get(position).getExamen().getAsignatura_Carrera().getCarrera().getNombre());

            TextView fechaCurso = (TextView) row.findViewById(R.id.fechaExamen);
            fechaCurso.setText("Fecha del examen: " + new SimpleDateFormat("dd/MM/yyyy").format(lista.get(position).getExamen().getFecha()));

            TextView calificacion = (TextView) row.findViewById(R.id.calificacionExamen);
            calificacion.setText("Calificación: " + lista.get(position).getCalificacion().toString());

            return row;
        }
    }
}
