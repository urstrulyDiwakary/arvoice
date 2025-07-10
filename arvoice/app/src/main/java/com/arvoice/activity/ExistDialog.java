package com.arvoice.activity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;

import androidx.appcompat.widget.AppCompatButton;

import com.arvoice.R;


public class ExistDialog extends Dialog implements OnClickListener {
    private final Context context;
    private final LayoutInflater factory;
    ExistPopUp kycPopUp;
    private AppCompatButton btnValidate, btnCancel;

    public interface ExistPopUp {
        void onYesClicked();

    }

    public ExistDialog(Context context2, ExistPopUp kycPopUp) {
        super(context2, R.style.MyDialog);
        this.factory = LayoutInflater.from(context2);
        this.context = context2;
        this.kycPopUp = kycPopUp;
    }

    /* access modifiers changed from: protected */
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setCanceledOnTouchOutside(false);
        initWindow();
        View inflate = factory.inflate(R.layout.exist_dialog, null);
        setContentView(inflate);
        btnValidate = inflate.findViewById(R.id.btnContinue);
        btnCancel = inflate.findViewById(R.id.btnCancel);

        this.btnValidate.setOnClickListener(this);
        this.btnCancel.setOnClickListener(this);

    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnContinue) {
            kycPopUp.onYesClicked();
            dismiss();
        }
        if (id == R.id.btnCancel) {
            dismiss();
        }
    }

    private void initWindow() {
        Window window = getWindow();
        window.getDecorView().setPadding(0, 0, 0, 0);
        LayoutParams attributes = window.getAttributes();
        attributes.width = getScreenWidth(getContext());
        attributes.height = -2;
        attributes.gravity = Gravity.CENTER;
        window.setAttributes(attributes);
    }

    @SuppressLint("WrongConstant")
    private int getScreenWidth(Context context2) {
        return ((WindowManager) context2.getSystemService("window")).getDefaultDisplay().getWidth();
    }

    @SuppressLint("WrongConstant")
    private int getScreenHeight(Context context2) {
        return ((WindowManager) context2.getSystemService("window")).getDefaultDisplay().getHeight();
    }

    private int dp2px(Context context2, float f) {
        return (int) ((f * context2.getResources().getDisplayMetrics().density) + 0.5f);
    }

}
