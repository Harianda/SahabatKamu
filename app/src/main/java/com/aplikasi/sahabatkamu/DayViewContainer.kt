package com.aplikasi.sahabatkamu

import android.graphics.Color
import android.graphics.Typeface
import android.util.TypedValue
import android.view.View
import android.view.View.INVISIBLE
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.kizitonwose.calendarview.model.CalendarDay
import com.kizitonwose.calendarview.model.DayOwner
import com.kizitonwose.calendarview.ui.DayBinder
import com.kizitonwose.calendarview.ui.ViewContainer
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.util.*

class DayViewContainer(view: View) : ViewContainer(view) {
    val textView = view.findViewById<TextView>(R.id.calendarDayText)
}

object DayBinder : DayBinder<DayViewContainer> {
    override fun create(view: View) = DayViewContainer(view)

    override fun bind(container: DayViewContainer, day: CalendarDay) {
        val context = container.view.context
        container.view.setBackgroundColor(Color.TRANSPARENT)
        container.textView.text = day.date.dayOfMonth.toString()
        container.textView.contentDescription =
            day.date.month.toString() + " " + day.date.dayOfMonth.toString()
        val today =
            Calendar.getInstance().time.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
        if (day.date == today) {
            container.textView.typeface = Typeface.DEFAULT_BOLD
        } else {
            container.textView.typeface = Typeface.DEFAULT
        }
        if (day.owner == DayOwner.THIS_MONTH) {
            val typedValue = TypedValue()
            context.theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true)
            container.textView.setTextColor(typedValue.data)
        } else {
            container.view.visibility = INVISIBLE
        }

        val prefsHelper = RealPreferencesHelper(context)
        val df = SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault())

        val successfulDaysSet = prefsHelper.getSuccessfulDays()
        val successToday = successfulDaysSet.find { success ->
            val date = df.parse(success)!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
            return@find date == day.date
        }

        if (successToday != null) {
            container.view.background =
                ContextCompat.getDrawable(context, R.drawable.circle_bg_green)
            container.textView.contentDescription =
                day.date.month.toString() + " " + day.date.dayOfMonth.toString() + " Success"
        } else {
            val failedDaysSet = prefsHelper.getFailedDays()
            failedDaysSet.find { failed ->
                val date =
                    df.parse(failed)!!.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
                return@find date == day.date
            }?.let {
                container.view.background =
                    ContextCompat.getDrawable(context, R.drawable.circle_bg_red)
                container.textView.contentDescription =
                    day.date.month.toString() + " " + day.date.dayOfMonth.toString() + " Failed"
            }
        }
    }
}