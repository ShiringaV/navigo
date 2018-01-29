package navigo.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;

import android.widget.ListView;

import java.util.ArrayList;
import static navigo.app.MainActivity.LOG;
import static navigo.app.MainActivity.dbVersion;


import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;

public class MapsActivity extends AppCompatActivity  {

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
        Context ctx = getApplicationContext();
        //important! set your user agent to prevent getting banned from the osm servers
        Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));
        setContentView(R.layout.activity_maps);
         //Obtain the SupportMapFragment and get notified when the map is ready to be used.
        MapView map = (MapView) findViewById(R.id.map);
        map.setTileSource(TileSourceFactory.MAPNIK);
        map.setMultiTouchControls(true);

        IMapController mapController = map.getController();
        mapController.setZoom(13);
        GeoPoint startPoint = new GeoPoint(46.467360, 30.743843);
        mapController.setCenter(startPoint);

        createMarkers(map);

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
    public void onResume(){
        super.onResume();
        //this will refresh the osmdroid configuration on resuming.
        //if you make changes to the configuration, use
        //SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        //Configuration.getInstance().save(this, prefs);
        Configuration.getInstance().load(this, PreferenceManager.getDefaultSharedPreferences(this));
    }



    public void createMarkers(MapView map) {
        Log.d(LOG, "создание карты");
        Marker.ENABLE_TEXT_LABELS_WHEN_NO_IMAGE = true;
        Marker m = new Marker(map);
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

           // m.setTextLabelBackgroundColor(backgroundColor);
           // m.setTextLabelFontSize(fontSizeDp);
          //  m.setTextLabelForegroundColor(fontColor);
            m.setTitle(name);
            m.setIcon(null);
            m.setPosition(new GeoPoint(lat,lon));
            map.getOverlays().add(m);
        }
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
       getSupportActionBar().setTitle(mTitle);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }

    void setupToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(mToolbar);
    }

    void setupDrawerToggle() {
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.app_name, R.string.app_name);
        // Это необходимо для изменения иконки на основании текущего состояния
        mDrawerToggle.syncState();
    }

}
