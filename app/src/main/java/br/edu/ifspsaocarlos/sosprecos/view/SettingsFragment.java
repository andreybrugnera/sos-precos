package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import br.edu.ifspsaocarlos.sosprecos.R;
import br.edu.ifspsaocarlos.sosprecos.util.SystemConstants;
import br.edu.ifspsaocarlos.sosprecos.util.ViewUtils;

public class SettingsFragment extends Fragment {

    private static final String LOG_TAG = "SETTINGS";

    private SharedPreferences sharedPreferences;
    private Integer maximumDistanteInKilometers;

    private TextView tvTitle;
    private EditText etMaxDistanceInKilometersToSearch;
    private FrameLayout progressBarHolder;
    private EditText.OnEditorActionListener onEditorActionListener;

    public SettingsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.sharedPreferences = getActivity().getSharedPreferences(SystemConstants.SHARED_PREFERENCES_FILE, Context.MODE_PRIVATE);

        this.progressBarHolder = getActivity().findViewById(R.id.progress_bar_holder);
        this.tvTitle = getActivity().findViewById(R.id.tv_title);
        this.etMaxDistanceInKilometersToSearch = getActivity().findViewById(R.id.et_max_dist_km);

        this.onEditorActionListener = new EditText.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                saveSettings();
                return false;
            }
        };

        this.etMaxDistanceInKilometersToSearch.setOnEditorActionListener(this.onEditorActionListener);
        updateUI();
    }

    private void updateUI() {
        tvTitle.setText(getString(R.string.settings));

        maximumDistanteInKilometers = sharedPreferences.getInt(SystemConstants.MAX_DIST_KM, SystemConstants.MAX_DISTANCE_IN_KILOMETERS);
        etMaxDistanceInKilometersToSearch.setText(maximumDistanteInKilometers.toString());
    }

    private void saveSettings() {
        ViewUtils.showProgressBar(progressBarHolder);

        Log.i(LOG_TAG, getString(R.string.saving_application_settings));

        SharedPreferences.Editor sharedPreferencesEditor = sharedPreferences.edit();
        sharedPreferencesEditor.putInt(SystemConstants.MAX_DIST_KM, Integer.valueOf(etMaxDistanceInKilometersToSearch.getText().toString()));
        sharedPreferencesEditor.commit();

        ViewUtils.hideProgressBar(progressBarHolder);
    }
}
