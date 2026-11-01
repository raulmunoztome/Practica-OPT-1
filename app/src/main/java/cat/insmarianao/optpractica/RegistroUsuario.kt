package cat.insmarianao.optpractica

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class RegistroUsuario : AppCompatActivity() {
    private lateinit var botonCrearUsuario: Button
    private lateinit var passNewUsuarioConf: EditText
    private lateinit var passNewUsuario: EditText
    private lateinit var emailNewUsuario: EditText
    private lateinit var nameNewUsuario: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_usuario)

         nameNewUsuario = findViewById<EditText>(R.id.nomNuevoUsuario)
         emailNewUsuario = findViewById<EditText>(R.id.emailNuevoUsuario)
         passNewUsuario = findViewById<EditText>(R.id.passNuevoUsuario)
         passNewUsuarioConf = findViewById<EditText>(R.id.passNuevoUsuarioConfirmar)
         botonCrearUsuario = findViewById<Button>(R.id.confirmaCrearUsuario)

        botonCrearUsuario.setOnClickListener {

           crearUsuarioAction()
        }
    }

    fun crearUsuarioAction() {

        val nombre: String? = if (nameNewUsuario.text.toString().trim().isEmpty()) null else nameNewUsuario.text.toString().trim()
        val email = emailNewUsuario.text.toString().trim()

        //chequeamos el formulario
        if (checkForm()) {

            val client = hashMapOf(
                "name" to nombre,
                "email" to email,
            )

            addDataFirebase(client)



        }
    }

    private fun checkForm() : Boolean {
        if (emailNewUsuario.text.toString().isEmpty()) {
            emailNewUsuario.error = "El email es obligatorio"
            return false
        }

        if (passNewUsuario.text.toString().isEmpty()) {
            passNewUsuario.error = "La contraseña es obligatoria"
            return false
        } else if (passNewUsuario.text.toString().length < 6) {
            passNewUsuario.error = "Mínimo 6 caracteres"
            return false
        }

        if (passNewUsuario.text.toString().trim() != passNewUsuarioConf.text.toString().trim()) {
            passNewUsuarioConf.error = "Las contraseñas no coinciden"
            return false
        }
        return true
    }
    private fun addDataFirebase(client: HashMap<String, String?>){
        val db = Firebase.firestore
        db.collection("clients")
            .document("${client.getValue("email")}")
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    emailNewUsuario.error = "Este email ya está registrado"
                } else {
                    db.collection("clients")
                        .document("${client.getValue("email")}") // Fem servir l'email com a ID del document
                        .set(client)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document saved with ID: ${client.getValue("email")}")
                            finish()
                        }
                        .addOnFailureListener { e ->
                            Log.w("Firestore", "Error adding document", e)
                        }
                }
            }
            .addOnFailureListener { e ->
                Log.w("Firestore", "Error checking user", e)
            }
    }
}