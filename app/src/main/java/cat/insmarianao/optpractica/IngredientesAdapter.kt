package cat.insmarianao.optpractica

import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class IngredientesAdapter(private val lista: List<Map<String, Any>>) :
    RecyclerView.Adapter<IngredientesAdapter.ViewHolder>() {

    class ViewHolder(vista: View) : RecyclerView.ViewHolder(vista) {
        val tvIngrediente: TextView = vista as TextView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val tv = TextView(parent.context)

        tv.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        return ViewHolder(tv)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvIngrediente.text = lista[position]["name"].toString()
    }

    override fun getItemCount(): Int = lista.size
}