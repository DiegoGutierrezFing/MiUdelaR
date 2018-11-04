/*package joke.hfad.com.miudelar.data.api.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import joke.hfad.com.miudelar.R;

public class DtCarreraAdapter extends RecyclerView.Adapter<DtCarreraAdapter.DtCarreraViewHolder> {

    private List<DtCarrera> carreras;
    private int rowLayout;
    private Context context;


    public static class DtCarreraViewHolder extends RecyclerView.ViewHolder {
        LinearLayout carrerasLayout;
        TextView nombre;
        TextView codigo;


        public DtCarreraViewHolder(View v) {
            super(v);
            carrerasLayout = (LinearLayout) v.findViewById(R.id.carreras_layout);
            nombre = (TextView) v.findViewById(R.id.nombre);
            codigo = (TextView) v.findViewById(R.id.codigo);
        }
    }

    public DtCarreraAdapter(List<DtCarrera> carreras, int rowLayout, Context context) {
        this.carreras = carreras;
        this.rowLayout = rowLayout;
        this.context = context;
    }

    @Override
    public DtCarreraAdapter.DtCarreraViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(rowLayout, parent, false);
        return new DtCarreraViewHolder(view);
    }


    @Override
    public void onBindViewHolder(DtCarreraViewHolder holder, final int position) {
        holder.codigo.setText(carreras.get(position).getCodigo().toString());
        holder.nombre.setText(carreras.get(position).getNombre());
    }

    @Override
    public int getItemCount() {
        return carreras.size();
    }
}*/