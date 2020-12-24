package com.in.timelinenested.utils;

import android.view.View;
import android.widget.RelativeLayout;


/**
 * Created by ishratkhan on 26/02/16.
 */
public class ViewUtils {

    public static void handleVerticalLines(View v2) {

        RelativeLayout.LayoutParams pram = new RelativeLayout.LayoutParams(3, RelativeLayout.LayoutParams.MATCH_PARENT);
        pram.setMarginStart(ViewUtils.getLevelOneMargin());
        v2.setLayoutParams(pram);

        /*RelativeLayout.LayoutParams pram2 = new RelativeLayout.LayoutParams(3, RelativeLayout.LayoutParams.MATCH_PARENT);
        pram2.setMarginStart(getLevelTwoMargin());
        v3.setLayoutParams(pram2);*/
    }

    public static int getLevelOneMargin() {
        return 80;
    }

   //public static int getLevelTwoMargin() {
    //    return 150;
    //}
}
