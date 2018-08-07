package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Service;

/**
 * Created by Andrey R. Brugnera on 30/07/2018.
 */
public class ServiceAdapter extends ArrayAdapter<Service> {
    private List<Service> services;
    private Context context;

    public ServiceAdapter(Context context, int resource, List<Service> objects) {
        super(context, resource, objects);
        this.services = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return services != null ? services.size() : 0;
    }

    @Override
    public Service getItem(int position) {
        return services != null ? services.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ServiceAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.category_layout, null);

            TextView etName = (TextView) convertView.findViewById(R.id.et_name);

            viewHolder = new ServiceAdapter.ViewHolder(etName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ServiceAdapter.ViewHolder) convertView.getTag();
        }

        Service service = services.get(position);
        viewHolder.getName().setText(service.getName());

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
