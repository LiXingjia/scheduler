package com.in.timelinenested.sphelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Base64;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class SPManager {
    private static final String MY_PREFERENCE = "set";

    public static boolean putDrawable(Context context, String key, Drawable d) {
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        //paraCheck(sp, key);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ((BitmapDrawable) d).getBitmap()
                .compress(Bitmap.CompressFormat.JPEG, 50, baos);
        String imageBase64 = new String(Base64.encode(baos.toByteArray(),
                Base64.DEFAULT));
        SharedPreferences.Editor e = sp.edit();
        e.putString(key, imageBase64);
        return e.commit();
    }

    public static Drawable getDrawable(Context context, String key,
                                       Drawable defaultValue) {
        SharedPreferences sp = context.getSharedPreferences(MY_PREFERENCE, Context.MODE_PRIVATE);
        //paraCheck(sp, key);
        String imageBase64 = sp.getString(key, "");
        if (TextUtils.isEmpty(imageBase64)) {
            return defaultValue;
        }
        byte[] base64Bytes = Base64.decode(imageBase64.getBytes(),
                Base64.DEFAULT);
        ByteArrayInputStream bais = new ByteArrayInputStream(base64Bytes);
        Drawable ret = Drawable.createFromStream(bais, "");
        if (ret != null) {
            return ret;
        } else {
            return defaultValue;
        }
    }

}
