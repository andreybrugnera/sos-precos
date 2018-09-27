package br.edu.ifspsaocarlos.sosprecos.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.List;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.model.Place;
import br.edu.ifspsaocarlos.sosprecos.util.NumberUtils;

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
            convertView = inflater.inflate(R.layout.place_list_item_layout, null);

            TextView etName = convertView.findViewById(R.id.et_place_name);
            RatingBar ratingBar = convertView.findViewById(R.id.rb_place_avg_score);
            TextView etDistance = convertView.findViewById(R.id.et_place_distance);
            viewHolder = new ViewHolder(etName, etDistance, ratingBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Place place = places.get(position);
        viewHolder.getName().setText(place.getName());
        viewHolder.getRatingBar().setRating(place.getAverageScore());
        if (place.getDistanceFromCurrentLocation() != null) {
            viewHolder.getDistance().setText(NumberUtils.format(place.getDistanceFromCurrentLocation()) + " Km");
        }
        return convertView;
    }

    private class ViewHolder {
        private TextView name;
        private TextView distance;
        private RatingBar ratingBar;

        public ViewHolder(TextView name, TextView distance, RatingBar ratingBar) {
            this.name = name;
            this.distance = distance;
            this.ratingBar = ratingBar;
        }

        public TextView getName() {
            return name;
        }

        public TextView getDistance() {
            return distance;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }
    }
}
