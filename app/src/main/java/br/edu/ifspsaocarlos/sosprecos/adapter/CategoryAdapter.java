package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Category;

/**
 * Created by Andrey R. Brugnera on 10/05/2018.
 */
public class CategoryAdapter extends ArrayAdapter<Category> {
    private List<Category> categories;
    private Context context;

    public CategoryAdapter(Context context, int resource, List<Category> objects) {
        super(context, resource, objects);
        this.categories = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return categories != null ? categories.size() : 0;
    }

    @Override
    public Category getItem(int position) {
        return categories != null ? categories.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_layout, null);

            CheckBox cbSelected = (CheckBox) convertView.findViewById(R.id.cb_selected);
            TextView etName = (TextView) convertView.findViewById(R.id.et_name);

            viewHolder = new ViewHolder(etName, cbSelected);
            convertView.setTag(viewHolder);

            viewHolder.getSelectionCheckBox().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    CheckBox chkBox = (CheckBox) v;
                    Category category = (Category) chkBox.getTag();
                    category.setSelected(chkBox.isChecked());
                }
            });
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Category category = categories.get(position);
        viewHolder.getName().setText(category.getName());
        viewHolder.getSelectionCheckBox().setChecked(category.getSelected() != null ? category.getSelected() : false);
        viewHolder.getSelectionCheckBox().setTag(category);

        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private CheckBox selectionCheckBox;

        public ViewHolder(TextView name, CheckBox selectionCheckBox) {
            this.name = name;
            this.selectionCheckBox = selectionCheckBox;
        }

        public TextView getName() {
            return name;
        }

        public CheckBox getSelectionCheckBox() {
            return selectionCheckBox;
        }
    }
}
