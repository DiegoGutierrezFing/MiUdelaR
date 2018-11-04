package joke.hfad.com.miudelar;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import joke.hfad.com.miudelar.data.api.model.DtAsignatura;
import joke.hfad.com.miudelar.data.api.model.DtCalificaciones;
import joke.hfad.com.miudelar.data.api.model.DtCurso;
import joke.hfad.com.miudelar.data.api.model.InscripcionCursoBody;
import joke.hfad.com.miudelar.data.prefs.SessionPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ConsultarCalificacionesCurso extends AppCompatActivity {

    private View mProgressView;
    private Spinner spinnerCursos;
    private TextView descripcion;
    private TextView tituloListaCursos;
    private Button botonConfirmar;

    private String authorization;
    private String contentType;
    private String usuario;

    private ApiInterface apiService;

    private List<DtAsignatura> asignaturas;
    private List<DtCurso> cursos;

    private DtCurso dtCurso;
    private DtCalificaciones dtCalificaciones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Redirección al Login
        if (!SessionPrefs.get(this).isLoggedIn()) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        setContentView(R.layout.activity_inscripcion_acurso);

        getSupportActionBar().setHomeButtonEnabled(true);

        tituloListaCursos = (TextView) findViewById(R.id.tituloListaCursos);
        descripcion = (TextView) findViewById(R.id.descripcion);
        botonConfirmar = (Button) findViewById(R.id.botonConfirmar);
        mProgressView = findViewById(R.id.progressBar);

        showProgress(true);

        apiService = ApiClient.getClient().create(ApiInterface.class);

        authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);
        usuario = getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USERNAME, null);
        contentType = "application/json";

        asignaturas = new ArrayList<DtAsignatura>();
        cursos = new ArrayList<DtCurso>();

        //Realizar peticion al servidor de MiUdelaR y llenar el Spinner de Cursos con elementos
        Call<List<DtCurso>> callDtCurso = apiService.getCursosByCedula(authorization, usuario);
        callDtCurso.enqueue(new Callback<List<DtCurso>>() {

            @Override
            public void onResponse(Call<List<DtCurso>> call, Response<List<DtCurso>> response) {

                cursos = response.body();

                ArrayAdapter<DtCurso> adapter = new MiAdaptador(ConsultarCalificacionesCurso.this, R.layout.curso_item, cursos);

                adapter.setDropDownViewResource(R.layout.curso_item);

                spinnerCursos.setAdapter(adapter);

                showProgress(false);
            }

            @Override
            public void onFailure(Call<List<DtCurso>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se realizaba la peticion", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void onClickConfirmar(View view){

        spinnerCursos = (Spinner) findViewById(R.id.cursos);

        //DtCurso dtCurso;

        if(spinnerCursos != null && spinnerCursos.getSelectedItem() !=null ) {

            dtCurso= (DtCurso) spinnerCursos.getSelectedItem();

            Toast.makeText(ConsultarCalificacionesCurso.this, "Curso seleccionado: " + dtCurso.getId(), Toast.LENGTH_SHORT).show();
            Toast.makeText(ConsultarCalificacionesCurso.this, "Realizando consulta: Espere...", Toast.LENGTH_SHORT).show();

            apiService = ApiClient.getClient().create(ApiInterface.class);

            authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);

            Call<DtCalificaciones> c = apiService.getCalificaciones(authorization, usuario, dtCurso.getAsignatura_Carrera().getId());
            c.enqueue(new Callback<DtCalificaciones>() {
                @Override
                public void onResponse(Call<DtCalificaciones> call, Response<DtCalificaciones> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){

                            //if (!(response.body().toString().contains("Error"))){
                            // Mostrar mensaje de que se tuvo exito en la inscripcion
                            //Toast.makeText(ConsultarCalificacionesCurso.this, response.body().toString(), Toast.LENGTH_SHORT).show();

                            dtCalificaciones = response.body();

                            // Ir a la actividad de resultados (resultadoCursos activity)
                            irAResultadosCursos();

                        }else {
                            Toast.makeText(ConsultarCalificacionesCurso.this, "Error desconocido: respuesta del servidor vacia", Toast.LENGTH_SHORT).show();
                            return;
                        }
                    }

                    // Procesar errores
                    if (!response.isSuccessful()) {

                        Toast.makeText(ConsultarCalificacionesCurso.this, "Error desconocido: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                        return;
                    }
                }

                @Override
                public void onFailure(Call<DtCalificaciones> call, Throwable t) {
                    Toast.makeText(ConsultarCalificacionesCurso.this, "Error: No fue posible contactar con el servidor", Toast.LENGTH_SHORT).show();
                }
            });

        } else  {
            Toast.makeText(ConsultarCalificacionesCurso.this, "Error: No se han cargado elementos en la lista de carreras o no se ha seleccionado ningun elemento", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        tituloListaCursos.setVisibility(visibility);
        botonConfirmar.setVisibility(visibility);
        descripcion.setVisibility(visibility);
    }

    private void irAMenuPrincipal() {
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private void irAResultadosCursos() {

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("RESULTADOS_CURSOS", dtCalificaciones);
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    private class MiAdaptador extends ArrayAdapter<DtCurso> {

        List<DtCurso> cursos = new ArrayList<>();

        public MiAdaptador(Context context, int resource, List<DtCurso> objects) {
            super(context, resource, objects);
            cursos = objects;
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

            View row = inflater.inflate(R.layout.curso_item, parent, false);

            TextView idCurso = (TextView)row.findViewById(R.id.codigoCurso);

            idCurso.setText(cursos.get(position).getId().toString());

            TextView nombreAsignatura = (TextView)row.findViewById(R.id.nombreCurso);

            nombreAsignatura.setText(cursos.get(position).getAsignatura_Carrera().getAsignatura().getNombre());

            return row;
        }
    }
}
