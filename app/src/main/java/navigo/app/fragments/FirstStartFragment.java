package navigo.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.osmdroid.views.MapView;

import navigo.app.MapDownloader;
import navigo.app.R;

import static navigo.app.activity.MainActivity.cityName;
import static navigo.app.activity.MainActivity.writeData;

/**
 * Created by ASUS 553 on 08.02.2018.
 */


public class FirstStartFragment extends Fragment implements View.OnClickListener{
    Button btnYes, btnNo;
    TextView textInfoMap;
    MapDownloader cache;
    FragmentManager fragmentManager;
    MapView map;
    Context context;

    public FirstStartFragment(FragmentManager fragmentManager, MapView map,  Context context) {
        super();
        this.fragmentManager =fragmentManager;
        this.map = map;
        this.context = context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.first_start_cache, container, false);
        btnYes = (Button) view.findViewById(R.id.buttonYes);
        btnYes.setText("Да, установить");
        btnNo = (Button) view.findViewById(R.id.buttonNo);
        btnNo.setText("Нет, спасибо");
        textInfoMap = (TextView) view.findViewById(R.id.textInfoMap);
        textInfoMap.setText("Установить offline карту города " + cityName + "?" );
        btnYes.setOnClickListener(this);
        btnNo.setOnClickListener(this);
        return view;
    }

    @Override
    public  void onClick(View v){
        switch (v.getId()) {
            case R.id.buttonYes:
                textInfoMap.setText("Идет установка карты");
                cache = new MapDownloader(context,map);
                cache.downloadTiles(46.500699, 30.767735, 46.355437, 30.671887, 12, 13);
                fragmentManager.beginTransaction().remove(this).commit();
                break;
            case R.id.buttonNo:
                fragmentManager.beginTransaction().remove(this).commit();
                writeData.putString(cityName + "cache", "false");
                writeData.commit();
                break;
        }
    }
}
