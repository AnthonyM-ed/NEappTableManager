package com.example.neapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.neapp.R;
import com.example.neapp.model.ent.ClienteEntity;

import java.util.ArrayList;
import java.util.List;

public class ClienteAdapter extends RecyclerView.Adapter<ClienteAdapter.ClienteViewHolder> {
    private final List<ClienteEntity> clientes;
    private List<ClienteEntity> filteredClientes;
    private final Context context;
    private OnItemClickListener listener;
    private ClienteEntity selectedCliente;

    public ClienteAdapter(Context context, List<ClienteEntity> clientes) {
        this.context = context;
        this.clientes = clientes;
        this.filteredClientes = new ArrayList<>(clientes);
    }

    public void updateClientes(List<ClienteEntity> nuevosClientes) {
        this.filteredClientes.clear(); // Limpiar la lista filtrada
        this.filteredClientes.addAll(nuevosClientes); // Agregar los nuevos clientes filtrados
        notifyDataSetChanged(); // Notificar al adaptador que los datos han cambiado
    }

    public interface OnItemClickListener {
        void onItemClick(ClienteEntity cliente);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setSelectedCliente(ClienteEntity cliente) {
        this.selectedCliente = cliente; // Método para actualizar el cliente seleccionado
    }

    @NonNull
    @Override
    public ClienteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cliente, parent, false);
        return new ClienteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ClienteViewHolder holder, int position) {
        ClienteEntity cliente = filteredClientes.get(position);
        holder.itemCodigo.setText("CLI" + cliente.getCliCod());
        holder.itemNombre.setText(cliente.getCliNom());
        holder.itemEstado.setText(cliente.getCliEstReg());

        // Cambiar el color de fondo si el cliente está seleccionado
        if (cliente.equals(selectedCliente)) {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.selected)); // Color de selección
        } else {
            holder.itemView.setBackgroundColor(context.getResources().getColor(R.color.white)); // Color por defecto
        }

        // Configurar el clic en la fila
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(cliente);
            }
        });
    }

    @Override
    public int getItemCount() {
        return filteredClientes.size();
    }

    static class ClienteViewHolder extends RecyclerView.ViewHolder {
        TextView itemCodigo;
        TextView itemNombre;
        TextView itemEstado;

        public ClienteViewHolder(@NonNull View itemView) {
            super(itemView);
            itemCodigo = itemView.findViewById(R.id.itemCodigo);
            itemNombre = itemView.findViewById(R.id.itemNombre);
            itemEstado = itemView.findViewById(R.id.itemEstado);
        }
    }
}
