package com.arvoice.utils;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

import com.arvoice.R;


public class ProgressUtils {
    public static Dialog customLoader;

    public static void showProgressDialog(Activity activity) {
        customLoader = new Dialog(activity);
        customLoader.setContentView(R.layout.dialog_progress);
        customLoader.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        customLoader.setCancelable(false);
        customLoader.show();
    }

    public static void hideProgressDialog() {
        if (customLoader != null) {
            customLoader.dismiss();
        }
    }
}

