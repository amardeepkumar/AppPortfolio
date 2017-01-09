package com.udacity.moviediary.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

/**
 * Created by Amardeep on 18/2/16.
 */
public class BaseFragment extends Fragment {

    protected Context mContext;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
    }
}
