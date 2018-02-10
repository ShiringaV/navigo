package navigo.app;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.views.MapView;


public class MainActivity extends AppCompatActivity {

    final static String LOG = "myLog";   //константа для логов
    public static String cityName;      //название города - опредиляется координатами
    public static int dbVersion = 1;    //версия бд, default = 1
    String url;                         //наш домен
    TextView textInfo;
    ImageView errorImage;
    SharedPreferences sPref;
    boolean online;                     //проверка сети
    boolean first = false;              //первый запуск приложения

    DBHelper dbHelper;
    SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInfo = (TextView) findViewById(R.id.textInfo);
        errorImage = (ImageView) findViewById(R.id.error);
        errorImage.setVisibility(View.INVISIBLE);

        cityName = "odessa";
        url = "http://navigo.zzz.com.ua/index.php?city=" + cityName;
        online = isOnline(this);

        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor writeData = sPref.edit();

        Log.d(LOG, "проверка на первый запуск");
        String getVersion = sPref.getString("dataBase", "");

        if (getVersion == "") {
            Log.d(LOG, "Первая установка. Текущая версия = " + dbVersion);
            if (online == false) {
                errorImage.setVisibility(View.VISIBLE);
                textInfo.setText("Первый запуск, нет интернета!!!");
                Log.d(LOG, "Первый запуск, нет интернета!!!");
                first = true;
            } else{
                writeData.putString("dataBase", "1");
                writeData.commit();
            }

        } else {
            dbVersion = Integer.parseInt(getVersion);
        }

        if (online == true) {
            Log.d(LOG, "Есть интернет");
            String currentCity = sPref.getString(cityName, "");

            if (currentCity != "") {
                Log.d(LOG, "удаляем таблицу");
                dbVersion = Integer.parseInt(sPref.getString("dataBase", ""));
                dbHelper = new DBHelper(this);
                db = dbHelper.getWritableDatabase();
                dbHelper.dropTable(db);
                serverConnect();
            }
            else {
                serverConnect();
            }
        } else {
            Log.d(LOG, "нет сети, данные есть");
            if (first == false) {
                nextActivity();
            }
        }

    }



    private void serverConnect(){
        sPref = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor writeData = sPref.edit();

        Log.d(LOG, "файл пуст, идет создание...");
        writeData.putString(cityName, "данные по городу " + cityName + " существуют");
        dbVersion = Integer.parseInt(sPref.getString("dataBase", "")) + 1;
        writeData.putString("dataBase", String.valueOf(dbVersion));
        Log.d(LOG, "Текущая версия = " + dbVersion);

        dbHelper = new DBHelper(this);

        Log.d(LOG, "Пытаемся подключиться к серверу");
        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                String[] array_result = response.split("end_json_string");
                String result = array_result[0];
                Log.d(LOG, "Ответ сервера " + result.toString());

                try {
                    Log.d(LOG, "идет создание бд");
                    ContentValues dbValue = new ContentValues();
                    db = dbHelper.getWritableDatabase();

                    Log.d(LOG, "создание JSON объекта");
                    jsonObject = new JSONObject(result);
                    JSONArray city = jsonObject.getJSONArray(cityName);

                    for (int i = 0; i < city.length(); i++) {
                        //Следущие дынные будут писаться в базу SQLite
                        String name = city.getJSONObject(i).getString("name");               //название достопримечательности
                        String type = city.getJSONObject(i).getString("type");               //тип достопримечательности
                        String lat = city.getJSONObject(i).getString("lat");                 //широта
                        String lon = city.getJSONObject(i).getString("lon");                 //долгота
                        String descript = city.getJSONObject(i).getString("descript");       //описание
                        String image = city.getJSONObject(i).getString("image");             //путь к картинке
                        String video = city.getJSONObject(i).getString("video");             //путь к видео
                        int count_people = city.getJSONObject(i).getInt("count_people");     //количество проголосовавших людей
                        int summ_mark = city.getJSONObject(i).getInt("summ_mark");           //общая сумма голосов
                        String comment = city.getJSONObject(i).getString("comment");         //комментарии

                        Log.d(LOG, "Данные с сервера: " +
                                " Название: " + name +
                                ", Тип: " + type +
                                ", Широта: " + lat +
                                ", Долгота:" + lon +
                                ", Описание: " + descript + "...");

                        dbValue.put("name", name);
                        dbValue.put("type", type);
                        dbValue.put("lat", lat);
                        dbValue.put("lon", lon);
                        dbValue.put("descript", descript);
                        dbValue.put("image", image);
                        dbValue.put("video", video);
                        dbValue.put("count_people", count_people);
                        dbValue.put("summ_mark", summ_mark);
                        dbValue.put("comment", comment);
                        dbValue.put("version", "null");

                        long rowID = db.insert(cityName, null, dbValue);
                        Log.d(LOG, "строка добавлена, id = " + rowID);
                    }

                    dbHelper.viewDataBase(db, true, false);
                    dbHelper.viewTables(db);

                } catch (JSONException e) {
                    textInfo.setText("Ошибка");
                    Log.d(LOG, e.toString());
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                textInfo.setText("Ошибка соединения");
                Log.d(LOG, "Ошибка" + error.toString());
                nextActivity();
            }
        });
        queue.add(stringRequest);
        writeData.commit();
        SystemClock.sleep(500);
        Log.d(LOG, "nextActivity");
        nextActivity();
    }

    //переход к карте
    public void nextActivity() {

        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
        finish();
    }

    //проверка интернета
    public static boolean isOnline(final Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected()) {
            return true;
        }
        return false;
    }


}
