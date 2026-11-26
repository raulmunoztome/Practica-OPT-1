package cat.insmarianao.optpractica.utils

import android.app.DatePickerDialog
import android.content.Context
import android.app.DatePickerDialog.OnDateSetListener
import java.util.Calendar

class DatePickerHelper(
    private val context: Context,
    private val listener: OnDateSetListener
) {

    fun mostrarCalendario() {
        val calendario = Calendar.getInstance()

        DatePickerDialog(
            context,
            listener,
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        ).show()
    }
}