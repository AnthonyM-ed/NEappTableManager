package com.example.neapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neapp.R;
import com.example.neapp.model.ent.MaestroPublicidadEntity;
import com.example.neapp.viewmodel.ClienteViewModel;
import com.example.neapp.viewmodel.ZonaViewModel;
import com.example.neapp.viewmodel.MaestroPublicidadViewModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MaestroPublicidadAdapter extends RecyclerView.Adapter<MaestroPublicidadAdapter.PublicidadViewHolder> {
    private final List<MaestroPublicidadEntity> publicaciones;
    private List<MaestroPublicidadEntity> filteredPublicaciones;
    private final Context context;
    private OnItemClickListener listener;
    private ClienteViewModel clienteViewModel;
    private ZonaViewModel zonaViewModel;
    private MaestroPublicidadEntity selectedPublicidad;
    private MaestroPublicidadViewModel publicidadViewModel;
    private Map<Integer, String> clienteMap;
    private Map<Integer, String> zonaMap;

    public MaestroPublicidadAdapter(Context context, List<MaestroPublicidadEntity> publicaciones, ClienteViewModel clienteViewModel, ZonaViewModel zonaViewModel, MaestroPublicidadViewModel publicidadViewModel) {
        this.context = context;
        this.publicaciones = publicaciones;
        this.filteredPublicaciones = new ArrayList<>(publicaciones);
        this.clienteViewModel = clienteViewModel;  // Inicializa el clienteViewModel
        this.zonaViewModel = zonaViewModel;        // Inicializa el zonaViewModel
        this.publicidadViewModel = publicidadViewModel;
        this.clienteMap = new HashMap<>();
        this.zonaMap = new HashMap<>();
    }


    public void updatePublicaciones(List<MaestroPublicidadEntity> nuevasPublicaciones) {
        filteredPublicaciones.clear();
        if (nuevasPublicaciones != null) {
            filteredPublicaciones.addAll(nuevasPublicaciones);
        }
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

    // Método para actualizar el mapa de clientes
    public void setClienteMap(Map<Integer, String> clienteMap) {
        this.clienteMap.clear(); // Limpia el mapa existente
        this.clienteMap.putAll(clienteMap); // Agrega el nuevo mapa
        notifyDataSetChanged(); // Notifica que los datos han cambiado
    }

    // Método para actualizar el mapa de clientes
    public void setZonaMap(Map<Integer, String> zonaMap) {
        this.zonaMap.clear(); // Limpia el mapa existente
        this.zonaMap.putAll(zonaMap); // Agrega el nuevo mapa
        notifyDataSetChanged(); // Notifica que los datos han cambiado
    }

    public void updateMaps(Map<Integer, String> clienteMap, Map<Integer, String> zonaMap) {
        this.clienteMap = clienteMap;
        this.zonaMap = zonaMap;
        notifyDataSetChanged(); // Notifica que los datos han cambiado
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

        // Usar LiveData para observar el estado del cliente y la zona
        clienteViewModel.getEstadoClienteByCodigo(publicidad.getCliCod()).observe((LifecycleOwner) context, estadoCliente -> {
            if ("I".equals(estadoCliente) || "*".equals(estadoCliente)) {
                holder.itemNombreCli.setText("CLI" + publicidad.getCliCod() + " Inactivo/Eliminado");
                // Inactivar la publicidad si es necesario
                if (publicidad.getPubEstReg().equals("A")) {
                    publicidad.setPubEstReg("I"); // Cambia el estado a Inactivo
                    publicidadViewModel.updatePublicidad(publicidad); // Guarda el cambio en la base de datos
                }
            } else {
                // Usar los nombres almacenados en los mapas
                String nombreCliente = clienteMap.get(publicidad.getCliCod());
                holder.itemNombreCli.setText(nombreCliente != null ? nombreCliente : "Cliente desconocido");
            }
        });

        zonaViewModel.getEstadoZonaByCodigo(publicidad.getZonCod()).observe((LifecycleOwner) context, estadoZona -> {
            if ("I".equals(estadoZona) || "*".equals(estadoZona)) {
                holder.itemNombreZon.setText("ZON" + publicidad.getZonCod() + " Inactivo/Eliminado");
                // Inactivar la publicidad si es necesario
                if (publicidad.getPubEstReg().equals("A")) {
                    publicidad.setPubEstReg("I"); // Cambia el estado a Inactivo
                    publicidadViewModel.updatePublicidad(publicidad); // Guarda el cambio en la base de datos
                }
            } else {
                // Usar los nombres almacenados en los mapas
                String nombreZona = zonaMap.get(publicidad.getZonCod());
                holder.itemNombreZon.setText(nombreZona != null ? nombreZona : "Zona desconocida");
            }
        });

        // Cambiar el color de fondo si la zona está seleccionada
        if (publicidad.equals(selectedPublicidad)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected)); // Color de selección
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white)); // Color por defecto
        }

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
