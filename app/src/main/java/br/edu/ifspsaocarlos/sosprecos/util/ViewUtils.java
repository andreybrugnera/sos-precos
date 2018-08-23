package br.edu.ifspsaocarlos.sosprecos.util;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.FrameLayout;

/**
 * Created by Andrey R. Brugnera on 22/08/2018.
 */
public class ViewUtils {

    public static void showProgressBar(FrameLayout progressBarHolder) {
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
