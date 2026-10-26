package cat.insmarianao.optpractica

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import kotlin.jvm.java

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val emailEditText = findViewById<EditText>(R.id.emailEditText)
        val passwordEditText = findViewById<EditText>(R.id.passwordEditText)
        val loginButton = findViewById<Button>(R.id.loginButton)

        loginButton.setOnClickListener {
            loginForm(emailEditText, passwordEditText)
        }
    }

    private fun loginForm(emailEditText: EditText, passwordEditText: EditText) {
        val email = emailEditText.text.toString().trim()
        val password = passwordEditText.text.toString().trim()

        login(email, password)
    }

    private fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(
                this, "Email and password required", Toast.LENGTH_SHORT
            ).show()
            return
        }

        // * Firebase login * //
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(
                        this,
                        "Welcome ${FirebaseAuth.getInstance().currentUser?.email}",
                        Toast.LENGTH_SHORT
                    ).show()
                    val clientsActivity = Intent(this, ClientsActivity::class.java)
                    clientsActivity.putExtra(
                        "email",
                        findViewById<EditText>(R.id.emailEditText).text.toString()
                    )
                    startActivity(clientsActivity)
                } else {
                    Toast.makeText(
                        this, "Error: ${task.exception?.message}", Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }

}
