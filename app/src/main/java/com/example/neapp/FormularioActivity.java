package com.example.neapp;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.neapp.fragment.CrearClienteFragment;
import com.example.neapp.fragment.EditarClienteFragment;
import com.example.neapp.fragment.CrearZonaFragment;
import com.example.neapp.fragment.EditarZonaFragment;
import com.example.neapp.fragment.CrearPublicidadFragment;
import com.example.neapp.fragment.EditarPublicidadFragment;

public class FormularioActivity extends AppCompatActivity {

    public static final String EXTRA_FRAGMENT_TYPE = "fragment_type";
    public static final String EXTRA_DATA = "extra_data"; // Datos opcionales para los fragmentos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_formulario);

        // Recibir el tipo de fragmento y los datos opcionales
        String fragmentType = getIntent().getStringExtra(EXTRA_FRAGMENT_TYPE);
        Bundle data = getIntent().getBundleExtra(EXTRA_DATA);

        // Determinar el fragmento a cargar
        Fragment fragment = getFragmentByType(fragmentType, data);

        if (fragment == null) {
            throw new IllegalStateException("Tipo de fragmento desconocido: " + fragmentType);
        }

        // Cargar el fragmento en el contenedor
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_form_container, fragment)
                .commit();
    }

    private Fragment getFragmentByType(String fragmentType, Bundle data) {
        switch (fragmentType) {
            case "crear_cliente":
                return CrearClienteFragment.newInstance(); // Sin datos
            case "editar_cliente":
                return EditarClienteFragment.newInstance(data); // Con datos
            case "crear_zona":
                return CrearZonaFragment.newInstance(); // Sin datos
            case "editar_zona":
                return EditarZonaFragment.newInstance(data); // Con datos
            case "crear_publicidad":
                return CrearPublicidadFragment.newInstance(); // Sin datos
            case "editar_publicidad":
                return EditarPublicidadFragment.newInstance(data); // Con datos
            default:
                return null;
        }
    }
}
