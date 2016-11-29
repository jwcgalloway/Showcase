package qut.wearable_remake.graphs;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.AxisValueFormatter;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

class DateFormatter implements AxisValueFormatter {
    private final DateFormat dateFormat;

    DateFormatter() {
        dateFormat = new SimpleDateFormat("hh a", Locale.getDefault());
    }

    /**
     * Called when a value from an axis is to be formatted
     * before being drawn. For performance reasons, avoid excessive calculations
     * and memory allocations inside this method.
     *
     * @param value the value to be formatted
     * @param axis  the axis the value belongs to
     * @return The formatted value.
     */
    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, (int) value);

        return dateFormat.format(calendar.getTime());
    } // end getFormattedValue()

    /**
     * Returns the number of decimal digits this formatter uses or -1, if unspecified.
     *
     * @return The number of decimal digits to use.
     */
    @Override
    public int getDecimalDigits() {
        return 0;
    } // end getDecimalDigits()
}
