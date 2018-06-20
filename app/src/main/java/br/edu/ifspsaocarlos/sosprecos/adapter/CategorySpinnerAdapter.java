package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.model.Category;

/**
 * Created by Andrey R. Brugnera on 19/06/2018.
 */
public class CategorySpinnerAdapter extends CategoryAdapter {

    public CategorySpinnerAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
    }

    @Override
    public boolean isEnabled(int position) {
        if (position == 0) {
            // Disable the first item from Spinner, first item will be use for hint
            return false;
        } else {
            return true;
        }
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        if (position == 0) {
            // Set the hint text color gray
            tv.setTextColor(Color.GRAY);
        } else {
            tv.setTextColor(Color.BLACK);
        }
        return view;
    }
}
