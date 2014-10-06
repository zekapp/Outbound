package com.outbound.ui.util;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Window;

import com.outbound.R;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zeki on 6/10/2014.
 */
public class TravellerTypeDialog extends Dialog{

    public interface TravellerTypeDialogListener {
        void onSelectedTypes(String[] selectedTypes);
    }

    private final List<TravellerTypeDialogListener> listeners = new LinkedList<TravellerTypeDialogListener>();
    public TravellerTypeDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.traveller_type_dialog);
    }
}
