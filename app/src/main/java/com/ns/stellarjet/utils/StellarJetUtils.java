package com.ns.stellarjet.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import com.ns.stellarjet.BuildConfig;
import com.ns.stellarjet.R;
import okhttp3.ResponseBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class StellarJetUtils {


    /**
     * This method used to get the displayMetrics from the WindowManager
     * @param context to get the application context
     * @return DisplayMetrics - A structure describing general information about a display, such as its
     * size, density, and font scaling.
     */
    private static DisplayMetrics getDisplayMetrics(Context context){
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return displayMetrics;
    }

    /**
     * This method used to get the screen width
     * @param context to get the Application Context
     * @return int - width of the screen
     */
    public static int getScreenWidth(Context context){
        return getDisplayMetrics(context).widthPixels;
    }

    /**
     * This method used to get the screen height
     * @param context to get the Application Context
     * @return int - height of the screen
     */
    public static int getScreenHeight(Context context){
        return getDisplayMetrics(context).heightPixels;
    }

    /**
     * This method is used check the internet connection
     * @param context to get the Application Context
     * @return
     */
    public static boolean isConnectingToInternet(Context context){
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null)
        {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED)
                    {
                        return true;
                    }

        }
        return false;
    }

    /**
     * retuns the Desired date/time format
     */
    public static String getFormattedDate(long millis){
//        String pattern = "dd MMM , EEE - hh:mm aa";
        String pattern = "dd MMM , hh:mm aa";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }

    /**
     * returns the Desired date/time format
     */
    public static String getFormattedBookDate(long millis){
        String pattern = "dd MMM, EEE - hh:mm aa";
//        String pattern = "dd MMM , hh:mm aa";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }


    /**
     * returns the Desired date/time format
     */
    public static String getFormattedCalendarBookDate(long millis){
        String pattern = "EEEE dd MMMM, hh:mm a";
//        String pattern = "dd MMM , hh:mm aa";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }


    /**
     * returns the Desired date/time format
     */
    public static String getFormattedCalendarDate(long millis){
        String pattern = "dd MMM, EEE";
//        String pattern = "dd MMM , hh:mm aa";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }


    /**
     * retuns the Desired date/time format
     */
    public static String getFormattedCompeltedDate(long millis){
        String pattern = " EEE, dd -MM- yyyy";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }

    /**
     * retuns the bookings/boardingpass  date/time format
     */
    public static String getFormattedBookingsDate(long millis){
        String pattern = "EEEE, dd MMMM";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }


    /**
     * returns the hour format
     */
    public static String getFormattedhoursInAPM(long millis){
        String pattern = "hh:mm a";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }

    /**
     * returns the hour format
     */
    public static String getFormattedhours(long millis){
        String pattern = "HH:mm";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis);
        return simple.format(result);
    }

    /**
     * returns the reach by plane hour format
     */
    public static String getReachByPlaneHours(long millis){
        String pattern = "hh:mm a";
        // Creating date format
        DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis - 15*60*1000);

        return simple.format(result);
    }

    /**
     * returns the reach by plane hour format
     */
    public static String getPersonalizationHours(long millis){
        String pattern = "dd-MMM-yyyy, hh:mm aa";
        // Creating date format
        @SuppressLint("SimpleDateFormat") DateFormat simple = new SimpleDateFormat(pattern);

        // Creating date from milliseconds
        // using Date() constructor
        Date result = new Date(millis - 4*60*60*1000);

        return simple.format(result);
    }

    /**
     * retuns the Desired date/time format
     */
    public static String getDayOfTheWeek(long millis){
        GregorianCalendar cal = new GregorianCalendar();

        cal.setTime(new Date(millis));

        int dow = cal.get(Calendar.DAY_OF_WEEK);

        switch (dow) {
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
            case Calendar.SUNDAY:
                return "Sunday";
        }
        return "";
    }

    public static void showErrorMessage(
            Context context , JSONObject jsonObject
    ){
        try {
//            JSONObject mJsonObject  = new JSONObject(response.errorBody().string());
            String errorMessage = jsonObject.getString("message");
            Toast.makeText(context , errorMessage , Toast.LENGTH_LONG).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public static float dpFromPx(final Context context, final float px) {
        return px / context.getResources().getDisplayMetrics().density;
    }

    public static int pxFromDp(final Context context, final float dp) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }


    public static boolean writeResponseBodyToDisk(ResponseBody body, String fileName) {
        try {

            File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;


                inputStream = body.byteStream();
                outputStream = new FileOutputStream(outputFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean isFileExist(String fileName) {
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        return outputFile.exists();
    }

    public static void openPdfFile(String fileName, Context context) {
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
//        Uri photoURI = Uri.fromFile(outputFile);
        Uri photoURI = FileProvider.getUriForFile(context,
                BuildConfig.APPLICATION_ID + ".provider",
                outputFile);

        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(photoURI,"application/pdf");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        context.startActivity(intent);
    }

    public static void showToast(Context context, String message){
        Toast toast = Toast.makeText(
                context,
                message
                , Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public static void showSimpleDialog(Context context, String message){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(context);
        alertDialogBuilder.setMessage(message);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog simpleDialog = alertDialogBuilder.create();
        simpleDialog.setCanceledOnTouchOutside(false);
        simpleDialog.setCancelable(false);
        simpleDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                simpleDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(
                        ContextCompat.getColor(context, R.color.colorButtonNew));
            }
        });
        simpleDialog.show();
    }


}
