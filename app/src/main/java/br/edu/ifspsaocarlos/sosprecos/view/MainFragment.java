package br.edu.ifspsaocarlos.sosprecos.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import br.edu.ifspsaocarlos.sosprecos.R;

/**
 * Created by Andrey R. Brugnera on 30/04/2018.
 */
public class MainFragment extends Fragment {

    private ImageView ivSearch;

    public MainFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        this.ivSearch = getActivity().findViewById(R.id.iv_search);
        this.ivSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSearchActivity();
            }
        });
    }

    private void openSearchActivity(){
        Intent searchIntent = new Intent(getActivity(), SearchActivity.class);
        startActivity(searchIntent);
    }
}
