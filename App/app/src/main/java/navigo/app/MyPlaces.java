package navigo.app;

/**
 * Created by ASUS 553 on 27.01.2018.
 */

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static navigo.app.MainActivity.LOG;


public class MyPlaces extends Fragment {

    public MyPlaces() {}
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(LOG, "places");
        View view = inflater.inflate(R.layout.fragment_places, container, false);
        return view;
    }
}