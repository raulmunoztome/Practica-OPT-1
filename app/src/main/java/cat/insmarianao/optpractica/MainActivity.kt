package cat.insmarianao.optpractica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)
        val nuevoButton = findViewById<Button>(R.id.botonNuevoUsuario)

        loginButton.setOnClickListener {
            loginForm(emailEditText, passwordEditText)
        }
        nuevoButton.setOnClickListener {

        }
    }

    private fun loginForm(emailEditText: EditText, passwordEditText: EditText) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        var valid = true

        if (email.isEmpty()) {
            emailEditText.error = "El email es obligatorio"
            valid = false
        }

        if (password.isEmpty()) {
            passwordEditText.error = "La contraseña es obligatoria"
            valid = false
        }

        if (!valid) return

        login(email, password)
    }

    private fun login(email: String, password: String) {
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Welcome ${FirebaseAuth.getInstance().currentUser?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val clientsActivity = Intent(this, ClientsActivity::class.java)
                    clientsActivity.putExtra("email", email)
                    startActivity(clientsActivity)
                } else {
                    Toast.makeText(
                        this,
                        "Error: ${task.exception?.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}