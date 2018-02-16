package navigo.app.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import navigo.app.R;

/**
 * Created by ASUS 553 on 27.01.2018.
 */

public class CreateRoute extends Fragment {

    public CreateRoute() {}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_route, container, false);
        return view;
    }
}