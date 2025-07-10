package com.arvoice.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.AssetManager;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.Patterns;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;


import com.arvoice.R;
import com.arvoice.base.AppController;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Utils {
    private static final int BUILD_VERSION = Build.VERSION.SDK_INT;
    private static final int VERSION_LOLLIPOP = Build.VERSION_CODES.LOLLIPOP;
    private static final int VERSION_JELLYBEAN = Build.VERSION_CODES.JELLY_BEAN;
    private static final String IMAGE_NAME = "SavedImage.png";
    private static final Context mContext = AppController.getContext();
    private static String formatedDate;
    private static String erg = "";
    private static TelephonyManager telephonyManager;


    /**
     * The typeface cache.
     */
    public static Map<String, Typeface> typefaceCache = new HashMap<>();

    public static boolean isJellyBean() {
        return BUILD_VERSION >= VERSION_JELLYBEAN;
    }

    public static boolean isLollipop() {
        return BUILD_VERSION >= VERSION_LOLLIPOP;
    }

    public static boolean validateString(String value) {
        return value != null && !value.isEmpty();
    }
    public static boolean isValidPanNumber(String panNumber) {
        // Regular expression for PAN card validation
        String panPattern = "[A-Z]{5}[0-9]{4}[A-Z]{1}";

        // Create a Pattern object
        Pattern pattern = Pattern.compile(panPattern);

        // Create a Matcher object
        Matcher matcher = pattern.matcher(panNumber);

        // Check if the PAN number matches the pattern
        return matcher.matches();
    }
    public static String convertDateFormat(String inputDate) {
        // Input format of the timestamp
        String inputFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'";

        // Desired output format
        String outputFormat = "dd-MMM-yyyy";

        // Parse the input string to Date
        SimpleDateFormat inputFormatter = new SimpleDateFormat(inputFormat);
        inputFormatter.setTimeZone(TimeZone.getTimeZone("UTC")); // Assuming input is in UTC
        Date date;
        try {
            date = inputFormatter.parse(inputDate);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

        // Format the Date to the desired output format
        SimpleDateFormat outputFormatter = new SimpleDateFormat(outputFormat);
        return outputFormatter.format(date);
    }
    public static boolean validateEmail(String email) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS;
        return emailPattern.matcher(email).matches();
    }

    public static boolean validateURL(String URL) {
        Pattern urlPattern = Patterns.WEB_URL;
// Pattern urlPattern = Pattern.compile("(@)?(href=')?(HREF=')?(HREF=\")?(href=\")?(http://)?[a-zA-Z_0-9\\-]+(\\.\\w[a-zA-Z_0-9\\-]+)+(/[#&\\n\\-=?\\+\\%/\\.\\w]+)?");
        return urlPattern.matcher(URL).matches();
    }


    public static Point getDisplayInfo(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            display.getRealSize(point);
        } else {
            display.getSize(point);
        }
        return point;
    }

    public static void showToast(int msgId) {
        Toast.makeText(mContext, msgId, Toast.LENGTH_SHORT).show();
    }

    public static void showToast(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();
    }

    public static int getIntRes(int resId) {
        return mContext.getResources().getInteger(resId);
    }

    public static String getStringRes(int resId) {
        return mContext.getResources().getString(resId);
    }

    public static String[] getStringArray(int resId) {
        return mContext.getResources().getStringArray(resId);
    }

    public static int getColorRes(int resId) {
        return mContext.getResources().getColor(resId);
    }

    public static float getDimenRes(int resId) {
        return mContext.getResources().getDimension(resId);
    }

    public static Drawable getDrawableRes(int id) {
        return mContext.getResources().getDrawable(id);
    }

    public static Drawable getDrawableImage(String name) {
        int drawable = mContext.getResources().getIdentifier(name, "drawable", mContext.getPackageName());
        return ContextCompat.getDrawable(mContext, drawable);
    }

    public static File getSavedImage(boolean cache) {
        String path;
        if (cache) {
            path = mContext.getCacheDir() + "/" + System.currentTimeMillis() + ".png";
        } else {
            path = mContext.getCacheDir() + IMAGE_NAME;
        }
        return new File(path);
    }

    public static String encode(String t) {
        // Log.e("Str Encode: ",t);
        return Base64.encodeToString(t.getBytes(StandardCharsets.UTF_8), Base64.DEFAULT).trim();
    }

    public static String decode(String t) {
        //  Log.i("Str Decode: ",t);
        return new String(Base64.decode(t, Base64.DEFAULT), StandardCharsets.UTF_8);
    }

    public static void rateNow(Context context) {
        String str = "android.intent.action.VIEW";
        try {
            Intent intent = new Intent(str);
            String sb = "market://details?id=" +
                    context.getPackageName();
            intent.setData(Uri.parse(sb));
            intent.setPackage("com.android.vending");
            if (intent.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent);
                return;
            }
            Intent intent2 = new Intent(str);
            String sb2 = "https://play.google.com/store/apps/details?id=" +
                    context.getPackageName();
            intent2.setData(Uri.parse(sb2));
            if (intent2.resolveActivity(context.getPackageManager()) != null) {
                context.startActivity(intent2);
            }
        } catch (ActivityNotFoundException unused) {
            Log.e("update", "GoogleMarket Intent not found");
        }
    }

    public static String parseCommonDateToddMMyyyy(String time) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        String str = null;

        try {
            Date Fdate = originalFormat.parse(time);
            assert Fdate != null;
            str = targetFormat.format(Fdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseCommonTimeToddMMyyyy(String time) {
        DateFormat originalFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss a", Locale.US);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy hh:mm:ss a", Locale.US);

        String str = null;

        try {
            Date Fdate = originalFormat.parse(time);
            assert Fdate != null;
            str = targetFormat.format(Fdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseOCRDateToddMMyyyy(String time) {
        String inputPattern = "dd/mm/yyyy";
        String outputPattern = "yyyy-mm-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            assert date != null;
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseToServer(String time) {
        String inputPattern = "dd-MMM-yyyy";
        String outputPattern = "yyyy-MM-dd";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            assert date != null;
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static int getSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            if (width > height) {
                inSampleSize = Math.round((float) height / (float) reqHeight);
            } else {
                inSampleSize = Math.round((float) width / (float) reqWidth);
            }
        }
        return inSampleSize;
    }

    public static String parseDateToddMMyyyy_1(String time) {
        DateFormat originalFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        String str = null;

        try {
            Date Fdate = originalFormat.parse(time);
            assert Fdate != null;
            str = targetFormat.format(Fdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseDateToddMMyyyy(String time) {
        DateFormat originalFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
        DateFormat targetFormat = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);

        String str = null;

        try {
            Date Fdate = originalFormat.parse(time);
            assert Fdate != null;
            str = targetFormat.format(Fdate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }

    public static String parseTime(String time) {
        String inputPattern = "HH:mm:ss";
        String outputPattern = "hh:mm a";
        SimpleDateFormat inputFormat = new SimpleDateFormat(inputPattern);
        SimpleDateFormat outputFormat = new SimpleDateFormat(outputPattern);

        Date date = null;
        String str = null;

        try {
            date = inputFormat.parse(time);
            assert date != null;
            str = outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    @SuppressLint("NewApi")
    public static void setDrawable(View view, int value) {
        if (mContext != null && view != null) {
            Drawable drawable = getDrawableRes(value);
            if (isJellyBean()) {
                view.setBackground(drawable);
            } else {
                view.setBackgroundDrawable(drawable);
            }
        }
    }


    public static void hideKeyboard(EditText editText) {
        hideKeyboard(editText, false);
    }

    public static void hideKeyboard(EditText editText, boolean clear) {
        InputMethodManager inputManager = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert inputManager != null;
        inputManager.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        if (clear) {
            editText.setText("");
        }
    }

    public static void hideSoftKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void showKeyboard(EditText editText) {
        editText.requestFocus();
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
    }

    public static boolean doesPackageExist(String targetPackage) {
        PackageManager pm = mContext.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(targetPackage, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
        return true;
    }

    public static PackageInfo getPackageInfo() {
        PackageInfo packageInfo = null;
        PackageManager manager = mContext.getPackageManager();
        try {
            packageInfo = manager.getPackageInfo(mContext.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return packageInfo;
    }


    public static String getCurrentDay() {
        int day = Calendar.getInstance().get(Calendar.DAY_OF_WEEK);

        switch (day) {
            case Calendar.SUNDAY:
                return "Sunday";
            case Calendar.MONDAY:
                return "Monday";
            case Calendar.TUESDAY:
                return "Tuesday";
            case Calendar.WEDNESDAY:
                return "Wednesday";
            case Calendar.THURSDAY:
                return "Thursday";
            case Calendar.FRIDAY:
                return "Friday";
            case Calendar.SATURDAY:
                return "Saturday";
        }
        return "Sunday";
    }

    public static String getCurrentTime() {
        Calendar calander = Calendar.getInstance();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");

        return simpleDateFormat.format(calander.getTime());
    }

    public static String getCurrentDate() {
        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd MMM yyyy");
        formatedDate = df.format(c.getTime());
        return formatedDate;
    }

    public static String dateDialogfrom() {
        final Calendar c = Calendar.getInstance();

        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH);
        int d = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dp = new DatePickerDialog(mContext,
                (view, year, monthOfYear, dayOfMonth) -> {

                    erg = String.valueOf(dayOfMonth);
                    erg += "-" + (monthOfYear + 1);
                    erg += "-" + year;

                }, y, m, d);
        dp.setTitle("Calender");
        dp.show();

        return erg;
    }


    public static String replaceSpaceToDashes(String value) {
        return value.replaceAll("\\s", "-");
    }


//    private static String getTodaysDate() {
//        DateTime date = new DateTime();
//        date = date.minusHours(7);
//
//        DateTimeFormatter dtf = DateTimeFormat.forPattern(AppConstants.utc_format);
//        return dtf.print(date);
//    }


    public static String capitalize(final String line) {
        return Character.toUpperCase(line.charAt(0)) + line.substring(1);
    }


    public static double round(double value, int places) {
        try {
            if (places > 0) {
                long factor = (long) Math.pow(10, places);
                value = value * factor;
                long tmp = Math.round(value);
                return (double) tmp / factor;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static boolean isValidEmaillId(String email) {

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();
    }


    public static int dpToPx(Context context, int dp) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        return Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
    }

    public static void setLocale(Context context, String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = context.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);

    }


    public static String getFormattedDates(String strCurrentDate, String dateFormat1, SimpleDateFormat format3) {
        try {
            SimpleDateFormat format = new SimpleDateFormat(dateFormat1);
            Date newDate = format.parse(strCurrentDate);
            assert newDate != null;
            return format3.format(newDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    public static void animateView(View v) {
        if (v != null) {
            Animation fadeIn = new AlphaAnimation(0.5f, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(50);
            fadeIn.setStartOffset(100);
            Animation fadeOut = new AlphaAnimation(1, 0.5f);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            fadeOut.setDuration(50);
            AnimationSet animation = new AnimationSet(false); //change to false
            animation.addAnimation(fadeOut);
            animation.addAnimation(fadeIn);
            v.startAnimation(animation);
        }
    }


    public static String getAssetJsonResponse(Context context, String filename) {
        AssetManager assetManager = context.getAssets();
        InputStream input;
        String text = "";

        try {
            input = assetManager.open(filename);

            int size = input.available();
            byte[] buffer = new byte[size];
            input.read(buffer);
            input.close();

            // byte buffer into a string
            text = new String(buffer);

        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return text;
    }

    public static boolean isExternalStorageReadOnly() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED_READ_ONLY.equals(extStorageState);
    }

    public static boolean isExternalStorageAvailable() {
        String extStorageState = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(extStorageState);
    }

    public static void setHighLightedText(Context context, TextView tv, String textToHighlight) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight);
        Spannable wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                // set color here
                wordToSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), ofe, ofe + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }

    public static void setHighLightedBlackText(Context context, TextView tv, String textToHighlight) {
        String tvt = tv.getText().toString();
        int ofe = tvt.indexOf(textToHighlight);
        Spannable wordToSpan = new SpannableString(tv.getText());
        for (int ofs = 0; ofs < tvt.length() && ofe != -1; ofs = ofe + 1) {
            ofe = tvt.indexOf(textToHighlight, ofs);
            if (ofe == -1)
                break;
            else {
                // set color here
                wordToSpan.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary)), ofe, ofe + textToHighlight.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                tv.setText(wordToSpan, TextView.BufferType.SPANNABLE);
            }
        }
    }

}
