
package cat.insmarianao.optpractica

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import cat.insmarianao.optpractica.repositories.AuthRepository

class MainActivity : AppCompatActivity() {

    private val authRepository = AuthRepository()

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
            val intent = Intent(this, RegistroUsuario::class.java)
            startActivity(intent)
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
        authRepository.iniciarSesion(
            email,
            password,
            onSuccess = {
                Toast.makeText(
                    this,
                    "Welcome $email",
                    Toast.LENGTH_SHORT
                ).show()

                val clientsActivity = Intent(this, ClientsActivity::class.java)
                clientsActivity.putExtra("email", email)
                startActivity(clientsActivity)
            },
            onFailure = { error ->
                Log.e("FIREBASE_LOGIN", "Error al iniciar sesión", error)

                Toast.makeText(
                    this,
                    "Error: ${error.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        )
    }
}
