package navigo.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import static navigo.app.MainActivity.LOG;
import static navigo.app.MainActivity.dbVersion;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    DBHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(LOG, "создание карты");
        mMap = googleMap;
        Log.d(LOG, "версия " + dbVersion );
        dbHelper = new DBHelper(this);
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ArrayList<String[]> mapData = new ArrayList<String[]>();
        Log.d(LOG, "запрос данных");
            mapData = dbHelper.viewDataBase(db, false, true);
            String[] arrayData = new String[mapData.size()];
        Log.d(LOG, "добавляем маркер");
        for(int i = 0; i < mapData.size();i++) {
            arrayData = mapData.get(i);
                Double lat = Double.parseDouble(arrayData[0]);
                Double lon = Double.parseDouble(arrayData[1]);
                String name = arrayData[2];
                Log.d(LOG, "данные с массива " + lat + "  " + lon + "  " + name);
                LatLng marker = new LatLng(lat, lon);
                mMap.addMarker(new MarkerOptions().position(marker).title(name));
        }

        LatLng startPoint = new LatLng(46.467360, 30.743843);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(startPoint));
        mMap.moveCamera(CameraUpdateFactory.zoomTo(13));
    }
}
