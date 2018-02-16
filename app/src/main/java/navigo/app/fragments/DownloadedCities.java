package navigo.app.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import navigo.app.MapDownloader;
import navigo.app.R;

import static navigo.app.activity.MainActivity.cityName;
import static navigo.app.activity.MainActivity.sPref;


/**
 * Created by ASUS 553 on 10.02.2018.
 */

public class DownloadedCities extends Fragment implements View.OnClickListener{
    final String LOG = "myLog";
    Context context;
    Button download, delete;
    TextView selectedCity;
    String parametr = null;
    MapDownloader cache;
    MapView map;
     ArrayList<HashMap<String, String>>  cities;
    HashMap<String, String>hacheMap;
    ListView listCities;

    public DownloadedCities(MapView map, Context context) {
        this.map = map;
        this.context = context;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.downloaded_cities, container, false);
        cities = new ArrayList<>();


        download = (Button) view.findViewById(R.id.btnDownload);
        delete = (Button) view.findViewById(R.id.btnDelete);
        selectedCity = (TextView) view.findViewById(R.id.selectedCity);
        download.setEnabled(false);
        delete.setEnabled(false);
        download.setOnClickListener(this);
        delete.setOnClickListener(this);
        listCities = (ListView) view.findViewById(R.id.citiesList);
        createList();

        // находим список



        return view;
    }

        public  void createList(){
        hacheMap = new HashMap<>();
        hacheMap.put("city", "Одесса");
        String mapStatus = sPref.getString(cityName + "cache", "");
        Log.d(LOG, "status "+cityName +" = "+ mapStatus);
        if(mapStatus.contains("true")) {
            hacheMap.put("status", "установленно");
        }
        else{
            hacheMap.put("status", "");
        }
        cities.add(hacheMap);

        hacheMap = new HashMap<>();
        hacheMap.put("city", "Киев");
        hacheMap.put("status", "");
        cities.add(hacheMap);


            // создаем адаптер
            SimpleAdapter adapter = new SimpleAdapter(context, cities, android.R.layout.simple_list_item_2,
                    new String[]{"city", "status"},
                    new int[]{android.R.id.text1, android.R.id.text2});

            // присваиваем адаптер списку
            listCities.setAdapter(adapter);

            listCities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View itemClicked, int position, long id) {
                    Log.d(LOG, "" + position);
                    String currentCity = cities.get(position).get("city");
                    parametr = currentCity;
                    selectedCity.setText(currentCity);
                    if(cities.get(position).get("status") == ""){
                        download.setEnabled(true);
                        delete.setEnabled(false);
                    }
                    else {
                        delete.setEnabled(true);
                        download.setEnabled(false);
                    }
                }
            });
    }

    @Override
    public  void onClick(View v){
        cache = new MapDownloader(context,map);
        switch (v.getId()) {
            case R.id.btnDownload:
                Log.d(LOG, "скачать " + parametr);
                cache.downloadTiles(46.500699, 30.767735, 46.355437, 30.671887, 12, 13);
                cities.clear();
                createList();
                break;
            case R.id.btnDelete:
                Log.d(LOG, "удалить " + parametr);
                cache.deleteTiles(46.500699, 30.767735, 46.355437, 30.671887, 12, 13);
                cities.clear();
                createList();
                break;
        }
    }
}
