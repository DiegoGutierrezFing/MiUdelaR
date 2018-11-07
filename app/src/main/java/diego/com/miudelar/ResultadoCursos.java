package diego.com.miudelar;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import diego.com.miudelar.data.api.model.DtCalificaciones;
import diego.com.miudelar.data.api.model.DtEstudiante_Curso;
import diego.com.miudelar.data.prefs.SessionPrefs;
import diego.com.miudelar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultadoCursos extends AppCompatActivity {

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

        setContentView(R.layout.activity_resultado_cursos);

        getSupportActionBar().setHomeButtonEnabled(true);

        listViewCalificaciones = (ListView) findViewById(R.id.listViewResultadosCursos);
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

                        List<DtEstudiante_Curso> calificaciones = (ArrayList<DtEstudiante_Curso>) dtCalificaciones.getEstudiante_curso();

                        ArrayAdapter<DtEstudiante_Curso> adapter = new MiAdaptador(ResultadoCursos.this, R.id.listViewResultadosCursos, calificaciones);

                        adapter.setDropDownViewResource(R.layout.resultado_curso_row);

                        listViewCalificaciones.setAdapter(adapter);

                        showProgress(false);
                    } else {
                        Toast.makeText(ResultadoCursos.this, "Error: respuesta del servidor vacia: Intente más tarde", Toast.LENGTH_LONG).show();
                        return;
                    }
                } else {

                    Toast.makeText(ResultadoCursos.this, "Error: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    Log.i("Body error", response.errorBody().toString());

                    return;
                }
            }

            @Override
            public void onFailure(Call<DtCalificaciones> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se realizaba la peticion", Toast.LENGTH_LONG).show();
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

    private class MiAdaptador extends ArrayAdapter<DtEstudiante_Curso> {

        List<DtEstudiante_Curso> lista = new ArrayList<>();

        public MiAdaptador(Context context, int resource, List<DtEstudiante_Curso> objects) {
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

            View row = inflater.inflate(R.layout.resultado_curso_row, parent, false);

            TextView idCurso = (TextView) row.findViewById(R.id.idCurso);
            idCurso.setText("Código de curso: " + lista.get(position).getCurso().getId().toString());

            TextView nombreAsignatura = (TextView)row.findViewById(R.id.nombreCurso);
            nombreAsignatura.setText("Asignatura: " + lista.get(position).getCurso().getAsignatura_Carrera().getAsignatura().getNombre());

            TextView nombreCarrera = (TextView)row.findViewById(R.id.nombreCarrera);
            nombreCarrera.setText("Carrera: " + lista.get(position).getCurso().getAsignatura_Carrera().getCarrera().getNombre());

            TextView fechaCurso = (TextView) row.findViewById(R.id.fechaCurso);
            fechaCurso.setText("Fecha del curso: " + lista.get(position).getCurso().getFecha().toString());

            TextView calificacion = (TextView) row.findViewById(R.id.calificacionCurso);
            calificacion.setText("Calificación: " + lista.get(position).getCalificacion().toString());

            return row;
        }
    }
}
