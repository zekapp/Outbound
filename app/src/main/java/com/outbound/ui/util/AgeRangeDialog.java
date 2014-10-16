package com.outbound.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.NumberPicker;

import com.outbound.R;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by zeki on 16/10/2014.
 */
public class AgeRangeDialog extends Dialog {

    private NumberPicker numberPickerFrom;
    private NumberPicker numberPickerTo;

    public interface AgeDialogListener {
        void onAgeSelected(Date dateOfBirthFrom, Date dateOfBirthTo, int ageFrom, int ageTo);
    }

    private final List<AgeDialogListener> listeners = new LinkedList<AgeDialogListener>();

    public AgeRangeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.age_range_dialog);

        setUpFromNumberPicker();
        setUpToNumberPicker();
        setUpSetLayout();
        setUpCancelLayout();
        setUpInitilaValueOfPickers();

    }

    private void setUpInitilaValueOfPickers() {
//        numberPickerFrom.setValue(23);
//        numberPickerTo.setValue(35);
    }

    private void setUpCancelLayout() {
        LinearLayout cancel = (LinearLayout)findViewById(R.id.set_cencel_layout);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    private void setUpSetLayout() {
        LinearLayout setLayout = (LinearLayout)findViewById(R.id.set_ageRange_layout);
        setLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                findTheDateOfBirth();
                selectedFromAge = numberPickerFrom.getValue();
                selectedToAge = numberPickerTo.getValue();
                for (AgeDialogListener listener : listeners){
                    if(listener != null){
                        listener.onAgeSelected(convertNumberToDate(selectedToAge), convertNumberToDate(selectedFromAge)
                                ,selectedFromAge,selectedToAge  );
                        dismiss();
                    }
                }
            }
        });
    }

    private Date convertNumberToDate(int selectedAge) {
        Calendar calNow = Calendar.getInstance();
        calNow.add(Calendar.YEAR , -selectedAge );
        return calNow.getTime();
    }

    private void setUpToNumberPicker() {
        numberPickerTo = (NumberPicker)findViewById(R.id.numberPickerTo);
        numberPickerTo.setMaxValue(100);
        numberPickerTo.setMinValue(1);

        numberPickerTo.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedToAge = numberPickerTo.getValue();
                selectedFromAge = numberPickerFrom.getValue();
            }
        });
    }

    private int selectedFromAge = 0;
    private int selectedToAge = 0;
    private void setUpFromNumberPicker() {
        numberPickerFrom = (NumberPicker)findViewById(R.id.numberPickerFrom);
        numberPickerFrom.setMaxValue(100);
        numberPickerFrom.setMinValue(1);

        numberPickerFrom.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                selectedFromAge = numberPickerFrom.getValue();
                numberPickerTo.setMinValue(selectedFromAge);
                selectedToAge = numberPickerTo.getMinValue();
            }
        });
    }

    public void addAgeDialogListener(AgeDialogListener listener) {
        listeners.add(listener);
    }
}
