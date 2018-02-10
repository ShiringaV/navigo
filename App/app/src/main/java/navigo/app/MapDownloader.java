package navigo.app;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

import org.osmdroid.tileprovider.cachemanager.CacheManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.views.MapView;

import static navigo.app.MainActivity.LOG;
import static navigo.app.MainActivity.cityName;

/**
 * Created by ASUS 553 on 08.02.2018.
 */

public class MapDownloader {
    Context context;
    SharedPreferences.Editor writeData;
    public MapDownloader(Context context,SharedPreferences.Editor writeData){
        this.context = context;
        this.writeData = writeData;
    }

    public void start( MapView map){

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
        BoundingBox box = new BoundingBox(46.500699, 30.767735, 46.355437, 30.671887 );
        CacheManager manager = new CacheManager(map);
        CacheManager.CacheManagerCallback callback;

       try {
               manager.downloadAreaAsync(context, box, 13, 16, new CacheManager.CacheManagerCallback() {
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
}
