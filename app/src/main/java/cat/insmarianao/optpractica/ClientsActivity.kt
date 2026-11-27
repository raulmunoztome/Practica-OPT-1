
package cat.insmarianao.optpractica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import cat.insmarianao.optpractica.adapters.IngredientesAdapter
import cat.insmarianao.optpractica.models.Ingrediente
import cat.insmarianao.optpractica.repositories.AuthRepository
import cat.insmarianao.optpractica.repositories.IngredientesRepository
import cat.insmarianao.optpractica.utils.DatePickerHelper
import com.google.firebase.Timestamp
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class ClientsActivity : AppCompatActivity() {

    private lateinit var usuarioActivo: String
    private lateinit var repository: IngredientesRepository
    private lateinit var authRepository: AuthRepository
    private lateinit var recyclerView: RecyclerView
    private lateinit var ingredientesAdapter: IngredientesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_clients)
        //!! para indicar que no será nulo, ya que el login se encarga de esto
        usuarioActivo = intent.getStringExtra("email")!!

        // Configuramos la Toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val nomIng = findViewById<EditText>(R.id.nombreIngrediente)
        val fechaCad = findViewById<EditText>(R.id.fechaCaducidad)
        val cantidad = findViewById<EditText>(R.id.ingredientesCantidad)
        val spinnerUnidades = findViewById<Spinner>(R.id.spinnerUnidades)
        val spinnerCategorias = findViewById<Spinner>(R.id.spinnerCategoria)
        recyclerView = findViewById<RecyclerView>(R.id.recyclerView6)
        val botonAddIngredient = findViewById<Button>(R.id.addIngredients)
        val botonLimpiar = findViewById<Button>(R.id.limpiarForm)

        //val con la clase de la conex a Firestore
        repository = IngredientesRepository()
        authRepository = AuthRepository()

        recyclerView.layoutManager = LinearLayoutManager(this)
        ingredientesAdapter = IngredientesAdapter(emptyList()) { ingrediente ->
            repository.eliminarIngrediente(
                usuarioActivo,
                ingrediente,
                onSuccess = {
                    Log.d("FIRESTORE", "Ingrediente eliminado correctamente")
                    Toast.makeText(this, "Ingrediente eliminado correctamente", Toast.LENGTH_SHORT).show()
                    actualizarIngredientes()
                },
                onFailure = { error ->
                    Log.e("FIRESTORE", "Error al eliminar ingrediente", error)
                    Toast.makeText(this, "Error al eliminar ingrediente", Toast.LENGTH_SHORT).show()
                }
            )
        }
        recyclerView.adapter = ingredientesAdapter
        //pedimos la lista a firestore
        actualizarIngredientes()

        //validaciones formulario ingredientes
        fun ingredienteCorrecto(): Boolean {
            if(nomIng.text.trim().isEmpty()){
                nomIng.error = "Nombre obligatorio"
                return false
            }
            else if(cantidad.text.trim().isEmpty()){
                cantidad.error = "Cantidad obligatoria"
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
                    usuarioActivo,
                    ingrediente,
                    onSuccess = {
                        Log.d("FIRESTORE", "Ingrediente añadido correctamente")
                        Toast.makeText(this, "Ingrediente añadido correctamente", Toast.LENGTH_SHORT).show()
                        botonLimpiar.performClick()
                        //pintamos de nuevo la lista al añadir nuevo ingrediente
                        actualizarIngredientes()
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_clients, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_cerrar_sesion -> {
                authRepository.cerrarSesion()

                Toast.makeText(
                    this,
                    "Sesión cerrada",
                    Toast.LENGTH_SHORT
                ).show()

                val intent = Intent(this, MainActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                true
            }

            R.id.menu_recetas -> {
                // Funcionalidad de Gemini pendiente
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun pintarRecicleView(lista: List<Ingrediente>) {
        Log.d("FIRESTORE", "Ingredientes: $lista")
        ingredientesAdapter.actualizarLista(lista)
    }

    private fun actualizarIngredientes() {
        repository.obtenerIngredientes(
            usuarioActivo,
            onSuccess = { lista ->
                pintarRecicleView(lista)
            },
            onFailure = { error ->
                Log.e("FIRESTORE", "Error al actualizar la lista", error)
                Toast.makeText(
                    this,
                    "Error al actualizar la lista de ingredientes",
                    Toast.LENGTH_SHORT
                ).show()
            }
        )
    }
}
