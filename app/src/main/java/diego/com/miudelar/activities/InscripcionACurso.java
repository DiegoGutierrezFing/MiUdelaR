package diego.com.miudelar.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;

import diego.com.miudelar.api.web.ApiClient;
import diego.com.miudelar.api.web.ApiInterface;
import diego.com.miudelar.data.api.model.DtCurso;
import diego.com.miudelar.data.api.model.InscripcionCursoBody;
import diego.com.miudelar.data.prefs.SessionPrefs;
import diego.com.miudelar.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class InscripcionACurso extends AppCompatActivity {

    private View mProgressView;
    private TextView descripcion;
    private TextView tituloListaCursos;
    private Button botonConfirmar;

    private String authorization;
    private String contentType;
    private String usuario;

    private ApiInterface apiService;
    private String url;

    private EditText etSearch;
    private ListView lvCursos;

    private List<DtCurso> mDtCursoArrayList = new ArrayList<DtCurso>();
    private DtCurso dtCursoSeleccionado;

    private MiAdaptador adapter;

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

        // Ajustar teclado software (soft-keyboard) para que no mueva los elementos de la pantalla cuando aparezca
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        tituloListaCursos = (TextView) findViewById(R.id.tituloListaCursos);
        descripcion = (TextView) findViewById(R.id.descripcion);
        botonConfirmar = (Button) findViewById(R.id.botonConfirmar);
        mProgressView = findViewById(R.id.progressBar);

        etSearch = (EditText) findViewById(R.id.etSearch);

        // Establecer color de hint (texto de sugerencia) para el EditText
        etSearch.setHintTextColor(Color.LTGRAY);

        lvCursos = (ListView)findViewById(R.id.lvCursos);

        // Agregar Text Change Listener a EditText
        etSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // Call back the Adapter with current character to Filter
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

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

        //Realizar peticion al servidor de MiUdelaR y llenar el Spinner de Cursos con elementos
        Call<List<DtCurso>> c = apiService.getCursosByCedula(authorization, usuario);
        c.enqueue(new Callback<List<DtCurso>>() {

            @Override
            public void onResponse(Call<List<DtCurso>> call, Response<List<DtCurso>> response) {

                if (response.isSuccessful()) {
                    if (response.body() != null) {

                        mDtCursoArrayList = response.body();

                        adapter = new MiAdaptador(InscripcionACurso.this, mDtCursoArrayList);

                        lvCursos.setAdapter(adapter);

                        showProgress(false);

                    } else {
                        Toast.makeText(InscripcionACurso.this, "Error: respuesta del servidor vacia: Intente más tarde", Toast.LENGTH_LONG).show();
                        irAMenuPrincipal();
                    }
                } else {
                    Toast.makeText(InscripcionACurso.this, "Error: no se ha podido recibir respuesta del servidor.", Toast.LENGTH_SHORT).show();
                    irAMenuPrincipal();
                }
            }

            @Override
            public void onFailure(Call<List<DtCurso>> call, Throwable t) {

                Toast.makeText(getApplicationContext(), "Ha ocurrido un error mientras se realizaba la peticion", Toast.LENGTH_LONG).show();
                irAMenuPrincipal();
            }
        });
    }

    public void onClickConfirmar(View view){

        lvCursos = (ListView) findViewById(R.id.lvCursos);

        final DtCurso dtCurso;

        if(lvCursos != null && dtCursoSeleccionado !=null ) {

            dtCurso = dtCursoSeleccionado;

            Toast.makeText(InscripcionACurso.this, "Realizando inscripción: Espere...", Toast.LENGTH_SHORT).show();

            apiService = ApiClient.getClient(url).create(ApiInterface.class);

            authorization = "Bearer " + getApplicationContext().getSharedPreferences(SessionPrefs.PREFS_NAME, MODE_PRIVATE).getString(SessionPrefs.PREF_USER_TOKEN, null);

            Call<String> c = apiService.inscripcionCurso(authorization, contentType, new InscripcionCursoBody(usuario, dtCurso.getId()));
            c.enqueue(new Callback<String>() {
                @Override
                public void onResponse(Call<String> call, Response<String> response) {
                    if (response.isSuccessful()){
                        if (response.body() != null){

                            // Mostrar mensaje de que se tuvo exito en la inscripcion
                            if (response.body().contains("OK")) {

                                AlertDialog.Builder builder = new AlertDialog.Builder(InscripcionACurso.this, R.style.Dialog);

                                builder.setMessage("Se ha completado la inscripción con éxito. Al pulsar Ok, se agregará un evento en el calendario para recordarle el inicio del curso.")
                                        .setTitle("Información")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                dialog.cancel();

                                                //Crear evento en el calendario
                                                Intent calIntent = new Intent(Intent.ACTION_INSERT);
                                                calIntent.setType("vnd.android.cursor.item/event");
                                                calIntent.putExtra(CalendarContract.Events.TITLE, "Curso: " + dtCurso.getAsignatura_Carrera().getAsignatura().getNombre());
                                                calIntent.putExtra(CalendarContract.Events.DESCRIPTION, "Inicio de curso de la asignatura " + dtCurso.getAsignatura_Carrera().getAsignatura().getNombre());

                                                GregorianCalendar calDate = new GregorianCalendar(dtCurso.getFecha().getYear(), dtCurso.getFecha().getMonth(), dtCurso.getFecha().getDay());
                                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, true);
                                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME,
                                                        calDate.getTimeInMillis());
                                                calIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME,
                                                        calDate.getTimeInMillis());

                                                startActivity(calIntent);
                                            }
                                        });

                                builder.create().show();

                            }
                            else {

                                mostrarDialogo("El estudiante ya se encuentra inscripto o no cumple con los requisitos de inscripción para este curso.");
                            }

                        } else {

                            mostrarDialogo("Error desconocido: respuesta del servidor vacia");
                        }
                    }
                    // Procesar errores
                    else {

                        mostrarDialogo("Error desconocido: no se ha podido recibir respuesta del servidor.");
                        Log.i("Body error", response.errorBody().toString());
                    }
                }

                @Override
                public void onFailure(Call<String> call, Throwable t) {

                    mostrarDialogo("Error: No fue posible contactar con el servidor");
                }
            });

        } else  {

            mostrarDialogo("Error: No se han cargado elementos en la lista de cursos o no se ha seleccionado ningun elemento");
        }
    }

    private void showProgress(boolean show) {
        mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);

        int visibility = show ? View.GONE : View.VISIBLE;
        tituloListaCursos.setVisibility(visibility);
        botonConfirmar.setVisibility(visibility);
        descripcion.setVisibility(visibility);
        etSearch.setVisibility(visibility);
        lvCursos.setVisibility(visibility);
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

    private void mostrarDialogo(String texto) {
        AlertDialog.Builder builder = new AlertDialog.Builder(InscripcionACurso.this, R.style.Dialog);

        builder.setMessage(texto)
                .setTitle("Información")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        irAMenuPrincipal();
                    }
                });

        builder.create().show();
    }

    public class MiAdaptador extends BaseAdapter implements Filterable {

        private List<DtCurso> mOriginalValues;     // Valores originales
        private List<DtCurso> mDisplayedValues;    // Valores a mostrar despues de filtrar
        LayoutInflater inflater;

        public MiAdaptador(Context context, List<DtCurso> mProductArrayList) {
            this.mOriginalValues = mProductArrayList;
            this.mDisplayedValues = mProductArrayList;
            inflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return mDisplayedValues.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        private class ViewHolder {
            LinearLayout llContainer;
            TextView nombreCurso, codigoCurso, carreraCurso;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            ViewHolder holder = null;

            if (convertView == null) {

                holder = new ViewHolder();
                convertView = inflater.inflate(R.layout.curso_item, null);
                holder.llContainer = (LinearLayout)convertView.findViewById(R.id.cursos_layout);
                holder.nombreCurso = (TextView) convertView.findViewById(R.id.nombreCurso);
                holder.codigoCurso = (TextView) convertView.findViewById(R.id.codigoCurso);
                holder.carreraCurso = (TextView) convertView.findViewById(R.id.carreraCurso);
                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.nombreCurso.setText("Asignatura: " + mDisplayedValues.get(position).getAsignatura_Carrera().getAsignatura().getNombre());
            holder.codigoCurso.setText("Código de curso: " + mDisplayedValues.get(position).getId().toString());
            holder.carreraCurso.setText("Carrera: " + mDisplayedValues.get(position).getAsignatura_Carrera().getCarrera().getNombre());

            lvCursos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Toast.makeText(InscripcionACurso.this, "ID SELECCIONADO: " + mDisplayedValues.get(Integer.parseInt(parent.getItemAtPosition(position).toString())).getId(), Toast.LENGTH_SHORT).show();
                    Log.i("ListView", "Se ha presionado un elemento");
                    dtCursoSeleccionado = mDisplayedValues.get(Integer.parseInt(parent.getItemAtPosition(position).toString()));
                }
            });

            return convertView;
        }

        @Override
        public Filter getFilter() {
            Filter filter = new Filter() {

                @SuppressWarnings("unchecked")
                @Override
                protected void publishResults(CharSequence constraint,FilterResults results) {

                    mDisplayedValues = (ArrayList<DtCurso>) results.values; // tiene los valores filtrados
                    notifyDataSetChanged();                                 // notifica los datos con los nuevos valores filtrados
                }

                @Override
                protected FilterResults performFiltering(CharSequence constraint) {
                    FilterResults results = new FilterResults();                    // Almacena los resultados de la operacion de filtrado en values
                    ArrayList<DtCurso> FilteredArrList = new ArrayList<DtCurso>();

                    if (mOriginalValues == null) {
                        mOriginalValues = new ArrayList<DtCurso>(mDisplayedValues); // almacena los valores originales de los datos en mOriginalValues
                    }

                    /********
                     *
                     *  Si la restriccion (CharSequence que se recibe) es null retornar los valores de mOriginalValues(Original)
                     *  si no, realiza el filtrado y retorna FilteredArrList(Filtrado)
                     *
                     ********/
                    if (constraint == null || constraint.length() == 0) {

                        // establecer el resultado original para retornar
                        results.count = mOriginalValues.size();
                        results.values = mOriginalValues;
                    } else {
                        constraint = constraint.toString().toLowerCase();
                        for (int i = 0; i < mOriginalValues.size(); i++) {
                            String data = mOriginalValues.get(i).getAsignatura_Carrera().getAsignatura().getNombre();
                            if (data.toLowerCase().startsWith(constraint.toString())) {
                                FilteredArrList.add(
                                        new DtCurso(
                                                mOriginalValues.get(i).getId(),
                                                mOriginalValues.get(i).getFecha(),
                                                mOriginalValues.get(i).getAsignatura_Carrera(),
                                                mOriginalValues.get(i).getHorarios(),
                                                mOriginalValues.get(i).getCalificacionesCursos(),
                                                mOriginalValues.get(i).getInscriptos()
                                        )
                                );
                            }
                        }
                        // establecer el resultado filtrado para retornar
                        results.count = FilteredArrList.size();
                        results.values = FilteredArrList;
                    }
                    return results;
                }
            };
            return filter;
        }
    }
}
