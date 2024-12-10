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
import com.example.neapp.model.database.AppDatabase;
import com.example.neapp.model.ent.UsuarioEntity;
import com.example.neapp.R;

import java.util.concurrent.Executors;

public class CreateAccountFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_create_account, container, false);

        EditText editTextName = view.findViewById(R.id.editTextRegName);
        EditText editTextSurname = view.findViewById(R.id.editTextRegSurname);
        EditText editTextEmail = view.findViewById(R.id.editTextRegEmail);
        EditText editTextPassword = view.findViewById(R.id.editTextRegPassword);
        EditText editTextPasswordConfirm = view.findViewById(R.id.editTextRegPasswordConfirm);
        Button buttonRegister = view.findViewById(R.id.buttonRegister);
        ImageView buttonCancel = view.findViewById(R.id.buttonCancel);

        buttonCancel.setOnClickListener(v -> requireActivity().finish());

        buttonRegister.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String surname = editTextSurname.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String password = editTextPassword.getText().toString().trim();
            String confirmPassword = editTextPasswordConfirm.getText().toString().trim();

            if (name.isEmpty() || surname.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(getContext(), "Todos los campos son obligatorios", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(getContext(), "Correo electrónico no válido", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(getContext(), "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show();
                return;
            }

            // Crear el nuevo usuario
            UsuarioEntity newUser = new UsuarioEntity();
            newUser.setUsuNombre(name);
            newUser.setUsuApellido(surname);
            newUser.setUsuCorreo(email);
            newUser.setUsuContrasena(password);
            newUser.setUsuEstReg("A");

            // Insertar en la base de datos usando un Executor
            Executors.newSingleThreadExecutor().execute(() -> {
                AppDatabase db = AppDatabase.getInstance(requireContext());
                db.usuarioDao().insertUsuario(newUser);

                // Volver al hilo principal para actualizar la UI
                requireActivity().runOnUiThread(() -> {
                    Toast.makeText(getContext(), "Usuario registrado exitosamente", Toast.LENGTH_SHORT).show();
                    requireActivity().finish(); // Volver al Activity anterior
                });
            });
        });

        return view;
    }
}
