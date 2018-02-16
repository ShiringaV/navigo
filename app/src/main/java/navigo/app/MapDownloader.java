package navigo.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import static navigo.app.activity.MainActivity.cityName;
import static navigo.app.activity.MainActivity.sPref;
import static navigo.app.activity.MainActivity.writeData;

/**
 * Created by ASUS 553 on 08.02.2018.
 */

public class MapDownloader {
    Context context;
    final static String LOG = "myLog";
    MapView map;
    public MapDownloader(Context context,MapView map){
        this.context = context;
        this.map = map;
    }


    public void downloadTiles(Double n, Double e, Double s, Double w, int zoomMin, int zoomMax){

        Log.d(LOG, "create manager");
        /*
        String outputName = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "osmdroid" + File.separator + cityName;
        SqliteArchiveTileWriter writer= null;
        try {
            writer = new SqliteArchiveTileWriter(outputName);
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        BoundingBox box = new BoundingBox(n,e,s,w);
        CacheManager manager = new CacheManager(map);

       try {
               manager.downloadAreaAsync(context, box, zoomMin, zoomMax, new CacheManager.CacheManagerCallback() {
                @Override
                public void onTaskComplete() {
                    Log.d(LOG, "onTaskComplete: ");
                    writeData.putString(cityName + "cache", "true");
                    writeData.commit();
                    Toast.makeText(context, "Карта успешно установлена", Toast.LENGTH_LONG).show();
                }


                @Override
                public void updateProgress(int progress, int currentZoomLevel, int zoomMin, int zoomMax) {
                    Log.d(LOG, "updateProgress: " + progress);

                }

                @Override
                public void downloadStarted() {
                    Log.d(LOG, "downloadStarted: ");
                }

                @Override
                public void setPossibleTilesInArea(int total) {
                    Log.d(LOG, "setPossibleTilesInArea: " + total);
                }

                @Override
                public void onTaskFailed(int errors) {
                    Log.d(LOG, "onTaskFailed: " + errors);
                    writeData.putString(cityName + "cache", "false");
                    writeData.commit();
                    Toast.makeText(context, "Ошибка! Карта не установлена", Toast.LENGTH_LONG).show();
                }
            });

        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public void deleteTiles(Double n, Double e, Double s, Double w, int zoomMin, int zoomMax){

        BoundingBox box = new BoundingBox(n,e,s,w);
        CacheManager manager = new CacheManager(map);

        manager.cleanAreaAsync(context, box, zoomMin, zoomMax);
        writeData.putString(cityName + "cache", "false");
        writeData.commit();
    }
}
