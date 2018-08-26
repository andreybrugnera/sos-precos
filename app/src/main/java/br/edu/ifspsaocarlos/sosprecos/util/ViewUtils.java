package br.edu.ifspsaocarlos.sosprecos.util;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

/**
 * Created by Andrey R. Brugnera on 22/08/2018.
 */
public class ViewUtils {

    public static void showProgressBar(FrameLayout progressBarHolder) {
        ProgressBar progressBar = (ProgressBar) progressBarHolder.getChildAt(0);

        Drawable progressDrawable = progressBar.getIndeterminateDrawable().mutate();
        progressDrawable.setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.SRC_IN);
        progressBar.setProgressDrawable(progressDrawable);

        AlphaAnimation inAnimation = new AlphaAnimation(0f, 1f);
        inAnimation.setDuration(200);
        progressBarHolder.setAnimation(inAnimation);
        progressBarHolder.setVisibility(View.VISIBLE);
    }

    public static void hideProgressBar(FrameLayout progressBarHolder) {
        AlphaAnimation outAnimation = new AlphaAnimation(1f, 0f);
        outAnimation.setDuration(200);
        progressBarHolder.setAnimation(outAnimation);
        progressBarHolder.setVisibility(View.GONE);
    }
}
