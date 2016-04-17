package com.udacity.myappportfolio.utility;

import android.databinding.BindingAdapter;
import android.support.v4.content.ContextCompat;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.udacity.myappportfolio.R;
import com.udacity.myappportfolio.network.Config;

/**
 * Created by Amardeep on 25/2/16.
 */
public class MyAppBindingUtils {
    @BindingAdapter({"app:imageUrl"})
    public static void loadImage(ImageView view, String url) {
        Glide.with(view.getContext()).load(Config.UrlConstants.IMAGE_BASE_URL + url).placeholder(ContextCompat.getDrawable(view.getContext(),
                R.drawable.placeholder))
                .error(R.drawable.image_error)
                .into(view);
    }
}
