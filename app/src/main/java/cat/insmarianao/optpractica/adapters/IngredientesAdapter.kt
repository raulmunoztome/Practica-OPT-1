package cat.insmarianao.optpractica.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import cat.insmarianao.optpractica.R
import cat.insmarianao.optpractica.models.Ingrediente

class IngredientesAdapter(
    private var lista: List<Ingrediente>,
    //unit equivale a void
    private val onEliminarClick: (Ingrediente) -> Unit
) : RecyclerView.Adapter<IngredientesAdapter.ViewHolder>() {

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvNom: TextView = vista.findViewById(R.id.tvNom)
        val tvCantidad: TextView = vista.findViewById(R.id.tvCantidad)
        val tvCategoria: TextView = vista.findViewById(R.id.tvCategoria)
        val tvFecha: TextView = vista.findViewById(R.id.tvFecha)
        val botonEliminar: ImageButton = vista.findViewById(R.id.imageButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context)
            .inflate(R.layout.card_ingrediente, parent, false)

        return ViewHolder(vista)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val ingrediente = lista[position]

        holder.tvNom.text = ingrediente.name
        holder.tvCantidad.text = "${ingrediente.cantidad} ${ingrediente.unidad}"
        holder.tvCategoria.text = ingrediente.categoria
        //fecha null es posible, mostramos mensaje en vez de "null"
        holder.tvFecha.text = ingrediente.expiration?.toDate()?.let {
            java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())
                .format(it)
        } ?: holder.itemView.context.getString(R.string.texto_sin_fecha)//forma de referenciar el texto de strings

        holder.botonEliminar.setOnClickListener {
            onEliminarClick(ingrediente)
        }
    }

    override fun getItemCount(): Int = lista.size

    fun actualizarLista(nuevaLista: List<Ingrediente>) {
        lista = nuevaLista
        notifyDataSetChanged()
    }
}