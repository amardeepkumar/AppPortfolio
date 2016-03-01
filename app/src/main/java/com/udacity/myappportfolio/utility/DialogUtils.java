package com.udacity.myappportfolio.utility;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface.OnCancelListener;
import android.view.WindowManager;
import android.widget.Toast;

import com.udacity.myappportfolio.R;


/**
 * Created by Amardeep on 23/2/16.
 */
public class DialogUtils {

    private static ProgressDialog sProgressDialog;


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

    public static void displayProgressDialog(Context context) {
        displayProgressDialog(context, null);
    }

    /**
     * Creates and shows progress dialog and sets your OnCancelListener
     *
     * @param context
     * @param listener
     */
    public static void displayProgressDialog(Context context,
                                             OnCancelListener listener) {
        displayProgressDialog(context, listener,
                context.getString(R.string.loading));
    }

    /**
     * Creates and shows progress dialog with given message and sets your
     * OnCancelListener
     *
     * @param context
     * @param listener
     * @param msg
     */
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

    /**
     * Dismisses progress dialog.
     */
    public static void hideProgressDialog() {
        if (sProgressDialog != null && sProgressDialog.isShowing()) {
            try {
                sProgressDialog.dismiss();
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            }
            sProgressDialog = null;
        }
    }

    /*public static void getInfoDialog(Context context) {
        final Dialog dialog = new Dialog(context);
        // Include dialog.xml file
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.donation_dialog);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        // Set dialog title
        // set values for custom dialog components - text, image and button
        dialog.show();

    }

    public static void showNetworkAlertDialog(Context mContext, final OkClickListener listener){
        final Dialog alertDialog = new Dialog(mContext,
                android.R.style.Theme_Translucent);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View layout = mInflater.inflate(R.layout.dialog_layout, null);
        alertDialog.setContentView(layout);

        TextViewBoldFont mTitle = (TextViewBoldFont) layout.findViewById(R.id.title);
        mTitle.setText(mContext.getResources().getString(R.string.no_network_title));

        TextViewRegularFont mMessage = (TextViewRegularFont) layout.findViewById(R.id.message);

        mMessage.setText(mContext.getResources().getString(R.string.no_network_message));

        alertDialog.setCancelable(false);

        alertDialog.show();

        ButtonBoldFont mOkButton = (ButtonBoldFont) layout.findViewById(R.id.button_ok);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if (listener != null)
                    listener.onOkClicked();

            }
        });
        // aiImage.post(new Starter(activityIndicator));

    }


    public static void showCustomAlertDialog(Context mContext,String title, String message,final OkClickListener listener){
        final Dialog alertDialog = new Dialog(mContext,
                android.R.style.Theme_Translucent);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View layout = mInflater.inflate(R.layout.dialog_layout, null);
        alertDialog.setContentView(layout);

        TextViewBoldFont mTitle = (TextViewBoldFont) layout.findViewById(R.id.title);
        mTitle.setText(title);

        TextViewRegularFont mMessage = (TextViewRegularFont) layout.findViewById(R.id.message);

        mMessage.setText(message);

        alertDialog.setCancelable(false);

        alertDialog.show();

        ButtonBoldFont mOkButton = (ButtonBoldFont) layout.findViewById(R.id.button_ok);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                if(listener!=null)
                listener.onOkClicked();

            }
        });
        // aiImage.post(new Starter(activityIndicator));

    }

    public static void showCustomAlertDialog(Context mContext, String title, String message) {
        showCustomAlertDialog(mContext,title,message,null);

    }


    public static void showCustomAlertDialogWithTwoButtons(Context mContext, String title, String message, final OkClickListener okClickListener) {

        final Dialog alertDialog = new Dialog(mContext,
                android.R.style.Theme_Translucent);
        alertDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater mInflater = LayoutInflater.from(mContext);
        View layout = mInflater.inflate(R.layout.dialog_layout_two_buttons, null);
        alertDialog.setContentView(layout);

        TextViewBoldFont mTitle = (TextViewBoldFont) layout.findViewById(R.id.title);
        mTitle.setText(title);

        TextViewRegularFont mMessage = (TextViewRegularFont) layout.findViewById(R.id.message);
        mMessage.setText(message);

        alertDialog.setCancelable(false);

        alertDialog.show();

        ButtonBoldFont mOkButton = (ButtonBoldFont) layout.findViewById(R.id.button_ok);
        ButtonBoldFont mCancelButton = (ButtonBoldFont) layout.findViewById(R.id.button_cancel);

        mOkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                okClickListener.onOkClicked();
            }
        });
        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
                okClickListener.onCancelClicked();
            }
        });
        // aiImage.post(new Starter(activityIndicator));


    }*/
}
