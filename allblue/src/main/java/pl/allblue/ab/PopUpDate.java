package pl.allblue.ab;

import android.app.DatePickerDialog;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;
import java.util.TimeZone;

import pl.allblue.util.Date;

public class PopUpDate
{

    TextView date = null;
    Button clear = null;

    DatePickerDialog datePickerDialog = null;

    Long time = null;

    public PopUpDate(TextView date, Button clear)
    {
        this.date = date;
        this.clear = clear;

        date.setFocusable(false);

        final PopUpDate self = this;

        Calendar calendar = Calendar.getInstance();
        this.datePickerDialog = new DatePickerDialog(date.getContext(),
                new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year,
                    int month_of_year, int day_of_month) {
                long time = Date.GetSeconds(year, month_of_year, day_of_month);

                self.time = time;
                self.date.setText(Date.Format_Date(time));

                self.datePickerDialog.hide();
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));

        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar time_calendar;

                if (self.time == null)
                    time_calendar = Calendar.getInstance();
                else {
                    time_calendar = Date.GetCalendar();
                    time_calendar.setTimeInMillis(self.time * 1000);
                }

                self.datePickerDialog.updateDate(time_calendar.get(Calendar.YEAR),
                        time_calendar.get(Calendar.MONTH),
                        time_calendar.get(Calendar.DAY_OF_MONTH));
                self.datePickerDialog.show();
            }
        });

        if (clear != null) {
            clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    self.time = null;
                    self.date.setText("");
                }
            });
        }
    }

    public DatePickerDialog getDatePickerDialog()
    {
        return this.datePickerDialog;
    }

    public Long getTime()
    {
        return this.time;
    }

    public void setTime(Long time)
    {
        this.time = time;
        this.date.setText(Date.Format_Date(time));
    }

}
