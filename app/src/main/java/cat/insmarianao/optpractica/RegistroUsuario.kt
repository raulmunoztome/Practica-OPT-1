package cat.insmarianao.optpractica

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import cat.insmarianao.optpractica.repositories.AuthRepository


class RegistroUsuario : AppCompatActivity() {
    private lateinit var botonCrearUsuario: Button
    private lateinit var passNewUsuarioConf: EditText
    private lateinit var passNewUsuario: EditText
    private lateinit var emailNewUsuario: EditText
    private lateinit var nameNewUsuario: EditText
    private val auth = AuthRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registro_usuario)

        nameNewUsuario = findViewById<EditText>(R.id.nomNuevoUsuario)
        emailNewUsuario = findViewById<EditText>(R.id.emailNuevoUsuario)
        passNewUsuario = findViewById<EditText>(R.id.passNuevoUsuario)
        passNewUsuarioConf = findViewById<EditText>(R.id.passNuevoUsuarioConfirmar)
        botonCrearUsuario = findViewById<Button>(R.id.confirmaCrearUsuario)
        val botonVolver = findViewById<Button>(R.id.volverInicio)

        botonCrearUsuario.setOnClickListener {
            crearUsuarioAction()
        }
        botonVolver.setOnClickListener {
            finish()
        }


    }

    fun crearUsuarioAction() {

        val nombre: String? = if (nameNewUsuario.text.toString().trim()
                .isEmpty()
        ) null else nameNewUsuario.text.toString().trim()
        val email = emailNewUsuario.text.toString().trim()
        val password = passNewUsuario.text.toString().trim()

        //chequeamos el formulario
        if (checkForm()) {
            Toast.makeText(
                this,
                getString(R.string.texto_procesando_datos),
                Toast.LENGTH_SHORT
            ).show()
            comprobarUsuarios()
        }
    }

    private fun checkForm(): Boolean {
        if (emailNewUsuario.text.toString().isEmpty()) {
            emailNewUsuario.error = getString(R.string.texto_email_obligatorio)
            return false
        }

        if (passNewUsuario.text.toString().isEmpty()) {
            passNewUsuario.error = getString(R.string.texto_pass_obligatoria)
            return false
        } else if (passNewUsuario.text.toString().length < 6) {
            passNewUsuario.error = getString(R.string.texto_pass_tamaño_minimo)
            return false
        }

        if (passNewUsuario.text.toString().trim() != passNewUsuarioConf.text.toString().trim()) {
            passNewUsuarioConf.error = getString(R.string.texto_pass_no_coinciden)
            return false
        }
        return true
    }

    private fun comprobarUsuarios() {
        val email = emailNewUsuario.text.toString().trim()
        val password = passNewUsuario.text.toString().trim()
        val nombre: String? = if (nameNewUsuario.text.toString().trim()
                .isEmpty()
        ) null else nameNewUsuario.text.toString().trim()

        auth.crearUsuario(this,nombre,password,email)
    }


}