package cat.insmarianao.optpractica.models

import com.google.firebase.Timestamp

data class Ingrediente(
    val name: String = "",
    val cantidad: Long = 0,
    val unidad: String = "",
    val categoria: String = "",
    val expiration: Timestamp? = null
)
