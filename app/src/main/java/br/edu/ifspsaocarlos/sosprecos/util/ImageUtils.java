package br.edu.ifspsaocarlos.sosprecos.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;

import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

/**
 * Created by Andrey R. Brugnera on 07/08/2018.
 */
public class ImageUtils {

    public static BitmapDescriptor createBitmapDescriptorFromVector(Context context, int vectorId, int resizeFactor) {
        resizeFactor = resizeFactor == 0 ? 1 : resizeFactor;
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth() * resizeFactor, vectorDrawable.getIntrinsicHeight() * resizeFactor);
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth() * resizeFactor, vectorDrawable.getIntrinsicHeight() * resizeFactor, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
}
