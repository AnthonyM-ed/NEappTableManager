package com.example.neapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neapp.R;
import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.viewmodel.ClienteViewModel;
import com.example.neapp.viewmodel.ZonaViewModel;

import java.util.ArrayList;
import java.util.List;

public class MaestroPublicidadAdapter extends RecyclerView.Adapter<MaestroPublicidadAdapter.PublicidadViewHolder> {
    private final List<MaestroPublicidadEntity> publicaciones;
    private List<MaestroPublicidadEntity> filteredPublicaciones;
    private final Context context;
    private OnItemClickListener listener;
    private ClienteViewModel clienteViewModel;
    private ZonaViewModel zonaViewModel;
    private MaestroPublicidadEntity selectedPublicidad;

    public MaestroPublicidadAdapter(Context context, List<MaestroPublicidadEntity> publicaciones) {
        this.context = context;
        this.publicaciones = publicaciones;
        this.filteredPublicaciones = new ArrayList<>(publicaciones);
    }

    public void updatePublicaciones(List<MaestroPublicidadEntity> nuevasPublicaciones) {
        this.filteredPublicaciones.clear();
        this.filteredPublicaciones.addAll(nuevasPublicaciones);
        notifyDataSetChanged();
    }

    public interface OnItemClickListener {
        void onItemClick(MaestroPublicidadEntity publicidad);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedPublicidad(MaestroPublicidadEntity publicidad) {
        this.selectedPublicidad = publicidad; // Método para actualizar la publicidad seleccionada
    }

    @NonNull
    @Override
    public PublicidadViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_publicidad, parent, false);
        return new PublicidadViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PublicidadViewHolder holder, int position) {
        MaestroPublicidadEntity publicidad = filteredPublicaciones.get(position);

        holder.itemCodigo.setText("PUB" + publicidad.getPubCod());
        holder.itemNombrePub.setText(publicidad.getPubNom());
        holder.itemCalleUbicacion.setText(publicidad.getPubUbi());
        holder.itemEstado.setText(publicidad.getPubEstReg());

        // Usar el ViewModel para obtener el nombre del cliente
        clienteViewModel.getNombreClienteByCodigo(publicidad.getCliCod()).observeForever(nombreCliente -> {
            holder.itemNombreCli.setText(nombreCliente != null ? nombreCliente : "Cliente desconocido");
        });

        // Usar el ViewModel para obtener el nombre de la zona
        zonaViewModel.getNombreZonaByCodigo(publicidad.getZonCod()).observeForever(nombreZona -> {
            holder.itemNombreZon.setText(nombreZona != null ? nombreZona : "Zona desconocida");
        });

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(publicidad);
            }
        });
    }


    @Override
    public int getItemCount() {
        return filteredPublicaciones.size();
    }

    static class PublicidadViewHolder extends RecyclerView.ViewHolder {
        TextView itemCodigo;
        TextView itemNombrePub;
        TextView itemNombreCli;
        TextView itemNombreZon;
        TextView itemCalleUbicacion;
        TextView itemEstado;

        public PublicidadViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCodigo = itemView.findViewById(R.id.itemCodigo);
            itemNombrePub = itemView.findViewById(R.id.itemNombrePub);
            itemNombreCli = itemView.findViewById(R.id.itemNombreCli);
            itemNombreZon = itemView.findViewById(R.id.itemNombreZon);
            itemCalleUbicacion = itemView.findViewById(R.id.itemCalleUbicacion);
            itemEstado = itemView.findViewById(R.id.itemEstado);
        }
    }
}
