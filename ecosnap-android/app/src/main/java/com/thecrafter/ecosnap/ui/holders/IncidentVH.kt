package com.thecrafter.ecosnap.ui.holders

import android.content.Context
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.thecrafter.ecosnap.R
import com.thecrafter.ecosnap.ViewHolder
import com.thecrafter.ecosnap.data.Incident
import java.text.SimpleDateFormat
import java.util.*

class IncidentVH(itemView: View, context: Context) : ViewHolder<Incident>(itemView, context) {
    lateinit var date: TextView
    lateinit var time: TextView
    lateinit var address: TextView
    lateinit var ecopoints: TextView
    lateinit var status: ImageView

    val dateFormatter = SimpleDateFormat("dd/MM/yyyy", Locale.UK)
    val timeFormatter = SimpleDateFormat("hh:mm:ss", Locale.UK)

    override fun bind(item: Incident) {
        val date = Date(item.timestamp)
        val dateStr = dateFormatter.format(date)
        val timeStr = timeFormatter.format(date)

        this.date.text = dateStr
        this.time.text = timeStr
        this.address.text = item.address
        this.ecopoints.text = "48" // TODO: Get eco points from user
        val res = when (item.status.toLowerCase()) {
            "pending"   -> R.drawable.progress_wrench
            "uploading" -> R.drawable.progress_upload
            "failed"    -> R.drawable.progress_close
            "approved"  -> R.drawable.progress_check
            else        -> R.drawable.progress_close
        }

        val color = when (item.status) {
            "failed"               -> R.color.failed
            "approved"             -> R.color.colorPrimary
            else                   -> R.color.colorAccent
        }
        // this.status.setBackgroundResource(res)
        // this.status.setBackgroundColor(color)
        this.status.setImageResource(res)
        this.status.setColorFilter(ContextCompat.getColor(context, color), android.graphics.PorterDuff.Mode.SRC_IN)
    }
}