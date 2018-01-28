package navigo.app;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import static navigo.app.MainActivity.LOG;
import static navigo.app.MainActivity.dbVersion;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

public class MapsActivity extends FragmentActivity implements  OnMapReadyCallback  {

    private GoogleMap mMap;
   DBHelper dbHelper;

    private String[] mItemTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerListView;
    private Toolbar mToolbar;
    private CharSequence mTitle;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
         //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
              .findFragmentById(R.id.map);
       mapFragment.getMapAsync(this);


        mTitle = getTitle();
        mItemTitles = getResources().getStringArray(R.array.drawer_items);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerListView = (ListView) findViewById(R.id.left_drawer);

        setupToolbar();

        ItemModel[] dItems = fillDataModel();

        DrawerAdapter adapter = new DrawerAdapter(this, R.layout.item_row, dItems);
        mDrawerListView.setAdapter(adapter);
        mDrawerListView.setOnItemClickListener(new ItemClickListener());
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mDrawerLayout.addDrawerListener(mDrawerToggle);
        setupDrawerToggle();
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


    // формируем массив с данными для адаптера
    private ItemModel[] fillDataModel() {
        return new ItemModel[]{
                new ItemModel(android.R.drawable.ic_dialog_email, "Карта"),
                new ItemModel(android.R.drawable.ic_dialog_info, "Проложить маршрут"),
                new ItemModel(android.R.drawable.ic_dialog_map, "Настройки")
        };
    }




    // по клику на элемент списка устанавливаем нужный фрагмент в контейнер
    private class ItemClickListener implements ListView.OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            Fragment fragment = null;

            // на основании выбранного элемента меню
            // вызываем соответственный ему фрагмент
            switch (position) {
                case 0:
                    fragment = new MyPlaces();
                    break;
                case 1:
                    fragment = new CreateRoute();
                    break;
                case 2:
                    fragment = new Settings();
                    break;

                default:
                    break;
            }
            if (fragment != null) {
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.map, fragment).commit();

                mDrawerListView.setItemChecked(position, true);
                mDrawerListView.setSelection(position);
                setTitle(mItemTitles[position]);
                mDrawerLayout.closeDrawer(mDrawerListView);
            }
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return mDrawerToggle.onOptionsItemSelected(item) || super.onOptionsItemSelected(item);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
      // getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
      //  setSupportActionBar(mToolbar);
    }

    void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        // Это необходимо для изменения иконки на основании текущего состояния
        mDrawerToggle.syncState();
    }

}
