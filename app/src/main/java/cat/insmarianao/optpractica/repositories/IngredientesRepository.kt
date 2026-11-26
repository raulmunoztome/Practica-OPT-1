
package cat.insmarianao.optpractica.repositories

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import cat.insmarianao.optpractica.models.Ingrediente

class IngredientesRepository {

    private val db = FirebaseFirestore.getInstance()

    fun obtenerIngredientes(
        email: String,
        onSuccess: (List<Ingrediente>) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("clients").document(email)
            .get()
            .addOnSuccessListener { doc ->

                val datosFirestore =
                    doc.get("ingredients") as? List<Map<String, Any>> ?: emptyList()

                val listado = datosFirestore.map { datos ->
                    Ingrediente(
                        name = datos["name"] as? String ?: "",
                        cantidad = (datos["Cantidad"] as? Number)?.toLong() ?: 0L,
                        unidad = datos["Unidad"] as? String ?: "",
                        categoria = datos["categoria"] as? String ?: "",
                        expiration = datos["expiration"] as? Timestamp
                    )
                }

                onSuccess(listado)
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
    fun agregarIngrediente(
        email: String,
        ingrediente: Ingrediente,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("clients").document(email)
            .update("ingredients", com.google.firebase.firestore.FieldValue.arrayUnion(
                mapOf(
                    "name" to ingrediente.name,
                    "Cantidad" to ingrediente.cantidad,
                    "Unidad" to ingrediente.unidad,
                    "categoria" to ingrediente.categoria,
                    "expiration" to ingrediente.expiration
                )
            ))
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
    fun eliminarIngrediente(
        email: String,
        ingrediente: Ingrediente,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("clients").document(email)
            .update(
                "ingredients",
                com.google.firebase.firestore.FieldValue.arrayRemove(
                    mapOf(
                        "name" to ingrediente.name,
                        "Cantidad" to ingrediente.cantidad,
                        "Unidad" to ingrediente.unidad,
                        "categoria" to ingrediente.categoria,
                        "expiration" to ingrediente.expiration
                    )
                )
            )
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener { error ->
                onFailure(error)
            }
    }
}
