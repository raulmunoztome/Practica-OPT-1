package cat.insmarianao.optpractica

import android.os.Bundle
import android.util.Log
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.insmarianao.optpractica.adapters.IngredientesAdapter
import cat.insmarianao.optpractica.models.Ingrediente
import cat.insmarianao.optpractica.repositories.IngredientesRepository
import cat.insmarianao.optpractica.utils.DatePickerHelper
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

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

        val repository = IngredientesRepository()

        // PASAMOS LOS DATOS DE FIRESTORE AL ADAPTER DEL RECYCLER
        fun pintarRecicleView(lista: List<Ingrediente>) {
            Log.d("FIRESTORE", "Ingredientes: $lista")
            recyclerView.layoutManager = LinearLayoutManager(this)

            recyclerView.adapter = IngredientesAdapter(lista) { ingrediente ->
                repository.eliminarIngrediente(
                    usuarioActivo.toString(),
                    ingrediente,
                    onSuccess = {
                        Log.d("FIRESTORE", "Ingrediente eliminado correctamente")
                        Toast.makeText(this, "Ingrediente eliminado correctamente", Toast.LENGTH_SHORT).show()

                        repository.obtenerIngredientes(
                            usuarioActivo.toString(),
                            onSuccess = { listaActualizada ->
                                pintarRecicleView(listaActualizada)
                            },
                            onFailure = { error ->
                                Log.e("FIRESTORE", "Error al actualizar la lista", error)
                                Toast.makeText(this, "Error al actualizar la lista de ingredientes", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { error ->
                        Log.e("FIRESTORE", "Error al eliminar ingrediente", error)
                        Toast.makeText(this, "Error al eliminar ingrediente", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        repository.obtenerIngredientes(
            usuarioActivo.toString(),
            onSuccess = { lista ->
                pintarRecicleView(lista)
            },
            onFailure = { error ->
                Log.e("FIRESTORE", "Error al leer", error)
                Toast.makeText(this, "Error en obtener la lista de ingredientes", Toast.LENGTH_SHORT).show()
            }
        )

        fun ingredienteCorrecto(): Boolean {
            if(nomIng.text.trim().isEmpty()){
                nomIng.error = "Nombre obligatorio"
                return false
            }
            else if(cantidad.text.trim().isEmpty()){
                cantidad.error  = "Cantidad obligatoria"
                return false
            }
            return true
        }

        // AÑADIMOS INGREDIENTE A LA BD FIRESTORE
        fun addIngredienteBD() {
            if (ingredienteCorrecto()) {
                val formato = DateTimeFormatter.ofPattern("d/M/yyyy")

                val fechaCaducidad = if (fechaCad.text.isNotBlank()) {
                    val fechaC = LocalDate.parse(fechaCad.text.toString(), formato)

                    Timestamp(
                        fechaC.atStartOfDay(ZoneId.systemDefault()).toEpochSecond(),
                        0
                    )
                } else {
                    null
                }

                val ingrediente = Ingrediente(
                    name = nomIng.text.toString(),
                    cantidad = cantidad.text.toString().toLong(),
                    unidad = spinnerUnidades.selectedItem.toString(),
                    categoria = spinnerCategorias.selectedItem.toString(),
                    expiration = fechaCaducidad
                )

                repository.agregarIngrediente(
                    usuarioActivo.toString(),
                    ingrediente,
                    onSuccess = {
                        Log.d("FIRESTORE", "Ingrediente añadido correctamente")
                        Toast.makeText(this, "Ingrediente añadido correctamente", Toast.LENGTH_SHORT).show()
                        botonLimpiar.performClick()

                        repository.obtenerIngredientes(
                            usuarioActivo.toString(),
                            onSuccess = { lista ->
                                pintarRecicleView(lista)
                            },
                            onFailure = { error ->
                                Log.e("FIRESTORE", "Error al actualizar la lista", error)
                                Toast.makeText(this, "Error al actualizar la lista de ingredientes", Toast.LENGTH_SHORT).show()
                            }
                        )
                    },
                    onFailure = { error ->
                        Log.e("FIRESTORE", "Error al añadir ingrediente", error)
                        Toast.makeText(this, "Error al añadir ingrediente", Toast.LENGTH_SHORT).show()
                    }
                )
            }
        }

        // Unidades y categorías:
        val unidades = arrayOf(
            "Kilogramos", "Gramos", "Litros", "Mililitros", "Unidades", "Packs"
        )
        val categoria = arrayOf(
            "Fruit & Vegs", "Meat & Fish", "Dairy & Eggs", "Bakery & Bread",
            "Grains & Pasta", "Sauces & Condiments", "Drinks", "Leftovers", "Other"
        )

        // ADAPTER 1
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, unidades)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerUnidades.adapter = adapter

        // ADAPTER 2
        val adapter2 = ArrayAdapter(this, android.R.layout.simple_spinner_item, categoria)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCategorias.adapter = adapter2

        // ACCIONES DE BOTONES
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
            val datePicker = DatePickerHelper(this) { _, year, month, day ->
                val fecha = LocalDate.of(year, month + 1, day)
                fechaCad.setText(
                    fecha.format(DateTimeFormatter.ofPattern("d/M/yyyy"))
                )
            }
            datePicker.mostrarCalendario()
        }
    }
}