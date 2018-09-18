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
import br.edu.ifspsaocarlos.sosprecos.dto.RatingInformationDto;
import br.edu.ifspsaocarlos.sosprecos.util.DateTimeUtils;

/**
 * Created by Andrey R. Brugnera on 15/09/2018.
 */
public class RatingInformationAdapter extends ArrayAdapter<RatingInformationDto> {
    protected List<RatingInformationDto> results;
    protected Context context;

    public RatingInformationAdapter(Context context, int resource, List<RatingInformationDto> objects) {
        super(context, resource, objects);
        this.results = objects;
        this.context = context;
    }

    @Override
    public int getCount() {
        return results != null ? results.size() : 0;
    }

    @Override
    public RatingInformationDto getItem(int position) {
        return results != null ? results.get(position) : null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RatingInformationAdapter.ViewHolder viewHolder;

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.rating_info_layout, null);

            TextView userName = convertView.findViewById(R.id.et_name);
            RatingBar ratingBar = convertView.findViewById(R.id.rating_bar);
            TextView registrationDate = convertView.findViewById(R.id.tv_rating_registration_date);
            TextView description = convertView.findViewById(R.id.et_rating_description);

            viewHolder = new RatingInformationAdapter.ViewHolder(userName, description, registrationDate, ratingBar);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (RatingInformationAdapter.ViewHolder) convertView.getTag();
        }

        RatingInformationDto ratingInformation = results.get(position);
        viewHolder.getUserName().setText(ratingInformation.getUserName());
        viewHolder.getDescription().setText(ratingInformation.getDescription());
        viewHolder.getRatingBar().setRating(ratingInformation.getScore());
        viewHolder.getRegistrationDate().setText(DateTimeUtils.formatDate(ratingInformation.getRegistrationDate()));

        return convertView;
    }

    private class ViewHolder {
        private TextView userName;
        private TextView description;
        private TextView registrationDate;
        private RatingBar ratingBar;

        public ViewHolder(TextView userName, TextView description, TextView registrationDate, RatingBar ratingBar) {
            this.userName = userName;
            this.description = description;
            this.registrationDate = registrationDate;
            this.ratingBar = ratingBar;
        }

        public TextView getUserName() {
            return userName;
        }

        public TextView getDescription() {
            return description;
        }

        public TextView getRegistrationDate() {
            return registrationDate;
        }

        public RatingBar getRatingBar() {
            return ratingBar;
        }
    }
}
