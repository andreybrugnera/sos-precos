package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Provider;

/**
 * Created by Andrey R. Brugnera on 16/05/2018.
 */
public class ProviderAdapter extends ArrayAdapter<Provider> {
    private List<Provider> providers;
    private Context context;

    public ProviderAdapter(Context context, int resource, List<Provider> objects) {
        super(context, resource, objects);
        this.providers = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return providers != null ? providers.size() : 0;
    }

    @Override
    public Provider getItem(int position) {
        return providers != null ? providers.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_layout, null);

            TextView etName = (TextView) convertView.findViewById(R.id.et_name);

            viewHolder = new ViewHolder(etName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Provider provider = providers.get(position);
        viewHolder.getName().setText(provider.getName());

        return convertView;
    }

    private class ViewHolder {
        private TextView name;

        public ViewHolder(TextView name) {
            this.name = name;
        }

        public TextView getName() {
            return name;
        }
    }
}
