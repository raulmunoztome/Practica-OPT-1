package cat.insmarianao.optpractica

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Collections.emptyList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar

class ClientsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)

        val usuarioActivo = intent.getStringExtra("email")
        val nomIng = findViewById<EditText>(R.id.nombreIngrediente)
        val fechaCad = findViewById<EditText>(R.id.fechaCaducidad)
        val cantidad = findViewById<EditText>(R.id.ingredientesCantidad)
        val spinnerUnidades = findViewById<Spinner>(R.id.spinnerUnidades)
        val spinnerCategorias = findViewById<Spinner>(R.id.spinnerCategoria)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView6)
        val botonAddIngredient = findViewById<Button>(R.id.addIngredients)
        val botonLimpiar = findViewById<Button>(R.id.limpiarForm)

        //PASAMOS LOS DATOS DE FIRESTORE AL ADAPTER DEL RECICLER
        fun pintarRecicleView(lista: List<Map<String, Any>>) {
            Log.d("FIRESTORE", "Ingredientes: $lista")

            recyclerView.layoutManager = LinearLayoutManager(this)
            recyclerView.adapter = IngredientesAdapter(lista)
        }
        fun ingredienteCorrecto(): Boolean{

            return true
        }
        //AÑADIMOS INGREDIENTE A LA BD FIRESTORE
        fun addIngredienteBD(){
            if(ingredienteCorrecto()) {
                val formato = DateTimeFormatter.ofPattern("d/M/yyyy")
                val fechaC = LocalDate.parse(fechaCad.text.toString(), formato)
                val nomI = nomIng.text.toString()
                val cantidadIngrediente = cantidad.text.toString().toIntOrNull()
                val unidadCantidad = spinnerUnidades.selectedItem.toString()
                val unidadCategoria = spinnerCategorias.selectedItem.toString()
                Log.d("DEBUG", "El valor es: $fechaC")
            }

        }

        val db = FirebaseFirestore.getInstance()
        var listado: List<Map<String, Any>> = emptyList()

        db.collection("clients").document(usuarioActivo.toString())
            .get()
            .addOnSuccessListener { doc ->
                listado = doc.get("ingredients") as? List<Map<String, Any>> ?: emptyList()
                pintarRecicleView(listado)
            }
            .addOnFailureListener { e ->
                Log.e("FIRESTORE", "Error al leer", e)
            }



        val unidades = arrayOf(
            "Kilogramos",
            "Gramos",
            "Litros",
            "Mililitros",
            "Unidades",
            "Packs"
        )

        val categoria = arrayOf(
            "Fruit & Vegs",
            "Meat & Fish",
            "Dairy & Eggs",
            "Bakery & Bread",
            "Grains & Pasta",
            "Sauces & Condiments",
            "Drinks",
            "Leftovers",
            "Other"
        )

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

        //ACCIONES DE BOTONES
        botonLimpiar.setOnClickListener {
            nomIng.setText("")
            cantidad.setText("")
            fechaCad.setText("")

            spinnerUnidades.setSelection(0)
            spinnerCategorias.setSelection(0)
            nomIng.requestFocus()
        }

        botonAddIngredient.setOnClickListener {
            addIngredienteBD()
        }
        fechaCad.setOnClickListener {

            val calendario = Calendar.getInstance()

            DatePickerDialog(
                this,
                { _, year, month, day ->
                    fechaCad.setText("$day/${month + 1}/$year")
                },
                calendario.get(Calendar.YEAR),
                calendario.get(Calendar.MONTH),
                calendario.get(Calendar.DAY_OF_MONTH)
            ).show()
        }


    }
}