package cat.insmarianao.optpractica

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore

class RegistroUsuario : AppCompatActivity() {
    private lateinit var botonCrearUsuario: Button
    private lateinit var passNewUsuarioConf: EditText
    private lateinit var passNewUsuario: EditText
    private lateinit var emailNewUsuario: EditText
    private lateinit var nameNewUsuario: EditText
    private lateinit var auth: FirebaseAuth

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
        val password = passNewUsuario.toString().trim()

        //chequeamos el formulario
        if (checkForm()) {
            comprobarUsuarios()
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
    private fun comprobarUsuarios(){
        val email = emailNewUsuario.text.toString().trim()
        val password = passNewUsuario.toString().trim()
        val nombre: String? = if (nameNewUsuario.text.toString().trim().isEmpty()) null else nameNewUsuario.text.toString().trim()

        auth = Firebase.auth

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    val user = auth.currentUser
                    //si se ha creado bien, le creados los datos en la BD.
                    addDataFirebase(nombre, email)

                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "createUserWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun addDataFirebase(nom: String?, correo: String) {
        val db = Firebase.firestore
        val client = hashMapOf(
            "name" to nom,
            "email" to correo,
        )
        db.collection("clients")
            .document("$correo")
            .get()
            .addOnSuccessListener { documento ->
                if (documento.exists()) {
                    emailNewUsuario.error = "Este email ya está registrado"
                } else {
                    db.collection("clients")
                        .document("$correo") // Fem servir l'email com a ID del document
                        .set(client)
                        .addOnSuccessListener {
                            Log.d("Firestore", "Document saved with ID: $correo")
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