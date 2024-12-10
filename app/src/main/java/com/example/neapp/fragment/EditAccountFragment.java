package com.example.neapp.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.neapp.R;
import com.example.neapp.model.ent.UsuarioEntity;
import com.example.neapp.viewmodel.UsuarioViewModel;

public class EditAccountFragment extends Fragment {

    private UsuarioViewModel usuarioViewModel;
    private int userId; // ID del usuario a editar
    private String estado;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_edit_account, container, false);

        if (getArguments() != null) {
            userId = getArguments().getInt("userId");
        }

        usuarioViewModel = new ViewModelProvider(this).get(UsuarioViewModel.class);
        EditText editTextName = view.findViewById(R.id.editTextRegName);
        EditText editTextSurname = view.findViewById(R.id.editTextRegSurname);
        EditText editTextEmail = view.findViewById(R.id.editTextRegEmail);
        EditText editTextPassword = view.findViewById(R.id.editTextRegPassword);
        Button buttonUpdate = view.findViewById(R.id.buttonUpdate);
        ImageView buttonCancel = view.findViewById(R.id.buttonCancel);

        // Cargar datos del usuario
        usuarioViewModel.getUsuarioById(userId).observe(getViewLifecycleOwner(), usuario -> {
            if (usuario != null) {
                editTextName.setText(usuario.getUsuNombre());
                editTextSurname.setText(usuario.getUsuApellido());
                editTextEmail.setText(usuario.getUsuCorreo());
                editTextPassword.setText(usuario.getUsuContrasena());
                estado = usuario.getUsuEstReg();
            }
        });

        buttonCancel.setOnClickListener(v -> requireActivity().finish());

        buttonUpdate.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String surname = editTextSurname.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty()) {
                Toast.makeText(getContext(), "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear la entidad actualizada y llamar al método de actualización en el ViewModel
            UsuarioEntity updatedUser = new UsuarioEntity();
            updatedUser.setUsuCod(userId);
            updatedUser.setUsuNombre(name);
            updatedUser.setUsuApellido(surname);
            updatedUser.setUsuCorreo(email);
            updatedUser.setUsuContrasena(password);
            updatedUser.setUsuEstReg(estado);

            usuarioViewModel.updateUsuario(updatedUser);

            Toast.makeText(getContext(), "Usuario actualizado exitosamente", Toast.LENGTH_SHORT).show();
            requireActivity().finish();
        });

        return view;
    }
}
