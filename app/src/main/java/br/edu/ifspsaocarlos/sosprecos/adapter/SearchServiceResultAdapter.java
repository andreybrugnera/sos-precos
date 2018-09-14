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
import br.edu.ifspsaocarlos.sosprecos.dto.SearchServiceResultDto;
import br.edu.ifspsaocarlos.sosprecos.util.NumberUtils;

/**
 * Created by Andrey R. Brugnera on 03/09/2018.
 */
public class SearchServiceResultAdapter extends ArrayAdapter<SearchServiceResultDto> {
    protected List<SearchServiceResultDto> results;
    protected Context context;

    public SearchServiceResultAdapter(Context context, int resource, List<SearchServiceResultDto> objects) {
        super(context, resource, objects);
        this.results = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return results != null ? results.size() : 0;
    }

    @Override
    public SearchServiceResultDto getItem(int position) {
        return results != null ? results.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SearchServiceResultAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_service_result_layout, null);

            TextView placeName = convertView.findViewById(R.id.et_place_name);
            TextView serviceName = convertView.findViewById(R.id.et_service_name);
            TextView servicePrice = convertView.findViewById(R.id.et_service_price);
            TextView distance = convertView.findViewById(R.id.et_place_distance);
            RatingBar ratingBar = convertView.findViewById(R.id.rb_place_avg_score);


            viewHolder = new SearchServiceResultAdapter.ViewHolder(placeName, serviceName, servicePrice, distance, ratingBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (SearchServiceResultAdapter.ViewHolder) convertView.getTag();
        }

        SearchServiceResultDto searchResult = results.get(position);
        viewHolder.getPlaceName().setText(searchResult.getPlace().getName());
        viewHolder.getServiceName().setText(searchResult.getService().getName());
        viewHolder.getServicePrice().setText("$ " + NumberUtils.formatCurrency(searchResult.getService().getPrice(), context.getResources().getConfiguration().locale));
        viewHolder.getDistance().setText(NumberUtils.format(searchResult.getDistanceFromCurrentLocation()) + " Km");
        viewHolder.getRatingBar().setRating(searchResult.getPlaceAverageScore());

        return convertView;
    }

    private class ViewHolder {
        private TextView placeName;
        private TextView serviceName;
        private TextView servicePrice;
        private TextView distance;
        private RatingBar ratingBar;

        public ViewHolder(TextView placeName, TextView serviceName, TextView servicePrice, TextView distance, RatingBar ratingBar) {
            this.placeName = placeName;
            this.serviceName = serviceName;
            this.servicePrice = servicePrice;
            this.distance = distance;
            this.ratingBar = ratingBar;
        }

        public TextView getPlaceName() {
            return placeName;
        }

        public TextView getServiceName() {
            return serviceName;
        }

        public TextView getServicePrice() {
            return servicePrice;
        }

        public TextView getDistance() {
            return distance;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }
    }
}
