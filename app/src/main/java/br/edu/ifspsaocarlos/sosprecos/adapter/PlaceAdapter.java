package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Place;

/**
 * Created by Andrey R. Brugnera on 16/05/2018.
 */
public class PlaceAdapter extends ArrayAdapter<Place> {
    private List<Place> places;
    private Context context;

    public PlaceAdapter(Context context, int resource, List<Place> objects) {
        super(context, resource, objects);
        this.places = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return places != null ? places.size() : 0;
    }

    @Override
    public Place getItem(int position) {
        return places != null ? places.get(position) : null;
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

        Place place = places.get(position);
        viewHolder.getName().setText(place.getName());

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
