package com.udacity.moviediary.utility;

import android.content.Context;
import android.widget.Toast;


/**
 * Created by Amardeep on 23/2/16.
 */
public class DialogUtils {
    /**
     * Creates and displays Toast with given message.
     *
     * @param msg
     * @param ctx
     */
    public static void showToast(String msg, Context ctx) {
        Toast.makeText(ctx, msg, Toast.LENGTH_SHORT).show();
    }

    /**
     * Creates and displays Toast with given message.
     *
     * @param stringResourceId
     * @param ctx
     */
    public static void showToast(int stringResourceId, Context ctx) {
        Toast.makeText(ctx, stringResourceId, Toast.LENGTH_SHORT).show();
    }
}
