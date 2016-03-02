package com.udacity.myappportfolio.utility;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by Amardeep on 23/2/16.
 */
public class DialogUtils {

//    private static ProgressDialog sProgressDialog;


    /**
     * Creates and displays Toast with given message.
     *
     * @param msg
     * @param ctx
     */
    public static void showToast(String msg, Context ctx) {
        //TODO Add flag to enable or disable toast
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates and displays Toast with given message.
     *
     * @param stringResourceId
     * @param ctx
     */
    public static void showToast(int stringResourceId, Context ctx) {
        //TODO Add flag to enable or disable toast
        Toast.makeText(ctx, stringResourceId, Toast.LENGTH_SHORT).show();
    }

    /*public static void displayProgressDialog(Context context) {
        displayProgressDialog(context, null);
    }

    *//**
     * Creates and shows progress dialog and sets your OnCancelListener
     *
     * @param context
     * @param listener
     *//*
    public static void displayProgressDialog(Context context,
                                             OnCancelListener listener) {
        displayProgressDialog(context, listener,
                context.getString(R.string.loading));
    }

    *//**
     * Creates and shows progress dialog with given message and sets your
     * OnCancelListener
     *
     * @param context
     * @param listener
     * @param msg
     *//*
    public static void displayProgressDialog(Context context,
                                             OnCancelListener listener, String msg) {
        if (sProgressDialog != null && sProgressDialog.isShowing())
            return;

        if (context != null) {
            sProgressDialog = new ProgressDialog(context,R.style.CustomAppCompatAlertDialogStyle);
            sProgressDialog.setMessage(msg);
            sProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            sProgressDialog.setCancelable(false);
            sProgressDialog.setOnCancelListener(listener);
            try {
                sProgressDialog.show();
            } catch (WindowManager.BadTokenException e) {
                e.printStackTrace();
            }
        }
    }

    *//**
     * Dismisses progress dialog.
     *//*
    public static void hideProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            try {
                sProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            sProgressDialog = null;
        }
    }*/
}
