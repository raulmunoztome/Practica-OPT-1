
package cat.insmarianao.optpractica.repositories

import com.google.firebase.auth.FirebaseAuth

class AuthRepository {

    private val auth = FirebaseAuth.getInstance()

    fun iniciarSesion(
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
                        task.exception ?: Exception("Error al iniciar sesión")
                    )
                }
            }
    }
    fun cerrarSesion() {
        auth.signOut()
    }
}
