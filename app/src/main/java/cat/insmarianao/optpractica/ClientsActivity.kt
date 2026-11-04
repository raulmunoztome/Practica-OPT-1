package cat.insmarianao.optpractica

import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class ClientsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)

        val spinnerUnidades = findViewById<Spinner>(R.id.spinnerUnidades)
        val spinnerCategorias = findViewById<Spinner>(R.id.spinnerCategoria)

        val unidades = arrayOf(
            "Kilogramos",
            "Gramos",
            "Litros",
            "Mililitros",
            "Unidades",
            "Packs")

        val categoria = arrayOf("Fruit & Vegs",
            "Meat & Fish",
            "Dairy & Eggs",
            "Bakery & Bread",
            "Grains & Pasta",
            "Sauces & Condiments",
            "Drinks",
            "Leftovers",
            "Other")

        //ADAPTER 1
        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            unidades
        )

        adapter.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerUnidades.adapter = adapter

        //ADAPTER 2
        val adapter2 = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            categoria
        )
        adapter2.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )
        adapter2.setDropDownViewResource(
            android.R.layout.simple_spinner_dropdown_item
        )

        spinnerCategorias.adapter = adapter2
    }
}