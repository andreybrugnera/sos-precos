package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Place;

/**
 * Created by Andrey R. Brugnera on 16/05/2018.
 */
public class PlaceAdapter extends ArrayAdapter<Place> {
    private List<Place> places;
    private Context context;
    private DecimalFormat decimalFormat;


    public PlaceAdapter(Context context, int resource, List<Place> objects) {
        super(context, resource, objects);
        this.places = objects;
        this.context = context;
        this.decimalFormat = new DecimalFormat();
        this.decimalFormat.setMaximumFractionDigits(2);
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
            convertView = inflater.inflate(R.layout.place_layout, null);

            TextView etName = convertView.findViewById(R.id.et_name);
            TextView etDistance = convertView.findViewById(R.id.et_distance);
            viewHolder = new ViewHolder(etName, etDistance);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Place place = places.get(position);
        viewHolder.getName().setText(place.getName());
        if (place.getDistanceFromCurrentLocation() != null) {
            viewHolder.getDistance().setText(decimalFormat.format(place.getDistanceFromCurrentLocation()) + " Km");
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView distance;

        public ViewHolder(TextView name, TextView distance) {
            this.name = name;
            this.distance = distance;
        }

        public TextView getName() {
            return name;
        }

        public TextView getDistance() {
            return distance;
        }

        public void setDistance(TextView distance) {
            this.distance = distance;
        }
    }
}
