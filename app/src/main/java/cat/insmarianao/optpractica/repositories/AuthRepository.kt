
package cat.insmarianao.optpractica.repositories

import android.content.ContentValues.TAG
import android.content.Context
import android.util.Log
import android.widget.Toast
import cat.insmarianao.optpractica.R
import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()
    private val ingRepo = IngredientesRepository()

    fun iniciarSesion(
        context: Context,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    onSuccess()
                } else {
                    onFailure(
                        task.exception ?: Exception(context.getString(R.string.texto_error_iniciar_sesion))
                    )
                }
            }
    }
    fun cerrarSesion() {
        auth.signOut()
    }

    fun crearUsuario(context: Context, nombre: String?, password: String, email: String){
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "createUserWithEmail:success")
                    Toast.makeText(context,context.getString(R.string.texto_usuario_creado), Toast.LENGTH_SHORT).show()
                    //si se ha creado bien, le creados los datos en la BD.
                    ingRepo.crearPrimerDocumento(nombre, email)


                } else {
                    Log.e(TAG, context.getString(R.string.texto_error_usuario_creado), task.exception)
                    Toast.makeText(context,context.getString(R.string.texto_error_usuario_creado),
                        Toast.LENGTH_SHORT).show()
                }
            }
    }
}
