package com.example.neapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neapp.R;
import com.example.neapp.model.ent.ZonaEntity;

import java.util.ArrayList;
import java.util.List;

public class ZonaAdapter extends RecyclerView.Adapter<ZonaAdapter.ZonaViewHolder> {
    private final List<ZonaEntity> zonas;
    private List<ZonaEntity> filteredZonas;
    private final Context context;
    private OnItemClickListener listener;
    private ZonaEntity selectedZona;

    public ZonaAdapter(Context context, List<ZonaEntity> zonas) {
        this.context = context;
        this.zonas = zonas;
        this.filteredZonas = new ArrayList<>(zonas);
    }

    public void updateZonas(List<ZonaEntity> nuevasZonas) {
        this.filteredZonas.clear(); // Limpiar la lista filtrada
        this.filteredZonas.addAll(nuevasZonas); // Agregar las nuevas zonas filtradas
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    public interface OnItemClickListener {
        void onItemClick(ZonaEntity zona);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedZona(ZonaEntity zona) {
        this.selectedZona = zona; // Método para actualizar la zona seleccionada
    }

    @NonNull
    @Override
    public ZonaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_zona, parent, false);
        return new ZonaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZonaViewHolder holder, int position) {
        ZonaEntity zona = filteredZonas.get(position);
        holder.itemCodigo.setText("ZON" + zona.getZonCod());
        holder.itemNombre.setText(zona.getZonNom());
        holder.itemEstado.setText(zona.getZonEstReg());

        // Cambiar el color de fondo si la zona está seleccionada
        if (zona.equals(selectedZona)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected)); // Color de selección
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white)); // Color por defecto
        }

        // Configurar el clic en la fila
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(zona);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredZonas.size();
    }

    static class ZonaViewHolder extends RecyclerView.ViewHolder {
        TextView itemCodigo;
        TextView itemNombre;
        TextView itemEstado;

        public ZonaViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCodigo = itemView.findViewById(R.id.itemCodigo);
            itemNombre = itemView.findViewById(R.id.itemNombre);
            itemEstado = itemView.findViewById(R.id.itemEstado);
        }
    }
}