package navigo.app;


import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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


public class MainActivity extends AppCompatActivity {


    final String LOG = "myLog";   //константа для логов
    TextView textInfo;
    ProgressBar bar;
    public static String cityName = "odessa";   //название города - опредиляется координатами
    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textInfo = (TextView) findViewById(R.id.textInfo);
        bar = (ProgressBar) findViewById(R.id.progressBar);
        dbHelper = new DBHelper(this);

        //TODO определяем координаты с LocationSingleton

        //TODO Определяем город (получаем String sity), это делается с помощью  GeoCoder
        //ресурс = https://developer.android.com/reference/android/location/Geocoder.html

        //TODO Проверка - если уже существует бд с таким городом, то сразу переходим к карте

        //TODO отправить запрос на сервер например http://www.navigo.ga/data/odessa
        //В результате мы получим JSON строку

        RequestQueue queue = Volley.newRequestQueue(this);

        //адрес куда отправляем запрос + название опредилившегося города
        String url = "http://navigo.zzz.com.ua/index.php?city=" + cityName;
        textInfo.setText("Пытамеся подключиться к серверу");
        bar.setProgress(0);
        Log.d(LOG, "Пытаемся подключиться к серверу");
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                String[] array_result = response.split("end_json_string");
                String result = array_result[0];

                Log.d(LOG, "Ответ сервера " + result.toString());
                textInfo.setText("Данные от сервера получены");
                bar.setProgress(10);
                //извлекаем двнные из json строки
               try{
                   Log.d(LOG, "идет создание бд");
                   textInfo.setText("Создаем таблицу" + cityName);
                   //TODO Создать SQL таблицы (если это первый запуск)
                   //только после того как получим название города что назвать таблицу (data_odessa)
                   ContentValues dbValue = new ContentValues();
                   SQLiteDatabase db = dbHelper.getWritableDatabase();
                   bar.setProgress(50);
                   //TODO распарсить JSON код в массив
                   Log.d(LOG, "создание JSON объекта");
                   jsonObject = new JSONObject(result);
                   JSONArray city = jsonObject.getJSONArray(cityName);

                   for(int i = 0; i < city.length();i++ ){
                       //Следущие дынные будут писаться в базу SQLite
                      // int id = city.getJSONObject(i).getInt("id");
                       String name = city.getJSONObject(i).getString("name");               //название достопримечательности
                       String type = city.getJSONObject(i).getString("type");               //тип достопримечательности
                       String lat = city.getJSONObject(i).getString("lat");                 //широта
                       String lon = city.getJSONObject(i).getString("lon");                 //долгота
                       String descript = city.getJSONObject(i).getString("descript");       //описание
                       String image = city.getJSONObject(i).getString("image");             //путь к картинке
                       String video = city.getJSONObject(i).getString("video");             //путь к видео
                       int count_people = city.getJSONObject(i).getInt("count_people"); //количество проголосовавших людей
                       int summ_mark = city.getJSONObject(i).getInt("summ_mark");     //общая сумма голосов
                       String comment = city.getJSONObject(i).getString("comment");     //комментарии
                      // int version = city.getJSONObject(i).getInt("version");           //версия

                       Log.d(LOG, "Данные с сервера: "  +
                                  " Название: " + name +
                                  ", Тип: " + type +
                                  ", Широта: " + lat +
                                  ", Долгота:" + lon +
                                  ", Описание: " + descript + "...");
                       //TODO заполняем таблицу данными из полученного массива
                       textInfo.setText("Заполняем таблицу данными");
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
                       //dbValue.put("version", version);
                       long rowID = db.insert(cityName, null, dbValue);
                       Log.d(LOG, "строка добавлена, id = " +rowID);
                       bar.setProgress(100);
                   }
                   //выводим дынные в консоль
                   viewDataBase(db);
               }
               catch (JSONException e){
                   textInfo.setText("Ошибка");
                   Log.d(LOG, e.toString());
               }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                textInfo.setText("Ошибка соединения");
                Log.d(LOG, "Ошибка" + error.toString());
            }
        });
        queue.add(stringRequest);

        //TODO стественно делаем на всё проверки и если всё ок переходим intend-ом в следующие активити с картой


    /*
    Некоторые разъяснения:

    Пользователь будет видеть только логотип и статус бар который будет показывать состояние загрузки данных
    При последующем запуске загрузки уже не будет (приложение запустится быстрее)

    Чтобы наш главный экран не логал весь слодный код будем осуществлять в отдельном потоке

     */
    }
    //метод который выводит всю бд в консоль
    public void viewDataBase(SQLiteDatabase db){
        Cursor c = db.query(cityName, null, null, null, null, null, null);

        // ставим позицию курсора на первую строку выборки
        // если в выборке нет строк, вернется false
        if (c.moveToFirst()) {

            // определяем номера столбцов по имени в выборке
            int idColIndex = c.getColumnIndex("id");
            int nameColIndex = c.getColumnIndex("name");
            int typeColIndex = c.getColumnIndex("type");
            int latColIndex = c.getColumnIndex("lat");
            int lonColIndex = c.getColumnIndex("lon");
            int descriptColIndex = c.getColumnIndex("descript");
            int imageColIndex = c.getColumnIndex("image");
            int videoColIndex = c.getColumnIndex("video");
            int count_peopleColIndex = c.getColumnIndex("count_people");
            int summ_markColIndex = c.getColumnIndex("summ_mark");
            int commentColIndex = c.getColumnIndex("comment");
            int versionColIndex = c.getColumnIndex("version");

            do {
                // получаем значения по номерам столбцов и пишем все в лог
                Log.d(LOG,
                        "ID = " + c.getInt(idColIndex) +
                                ", name = " + c.getString(nameColIndex) +
                                ", type = " + c.getString(typeColIndex) +
                                ", lat = " + c.getString(latColIndex) +
                                ", lon = " + c.getString(lonColIndex) +
                                ", descript = " + c.getString(descriptColIndex) +
                                ", image = " + c.getString(imageColIndex) +
                                ", video = " + c.getString(videoColIndex) +
                                ", count_people = " + c.getString(count_peopleColIndex) +
                                ", summ_mark = " + c.getString(summ_markColIndex) +
                                ", comment = " + c.getString(commentColIndex) +
                                ", version = " + c.getString(versionColIndex));
                // переход на следующую строку
                // а если следующей нет (текущая - последняя), то false - выходим из цикла
            } while (c.moveToNext());
        } else
            Log.d(LOG, "0 rows");
        c.close();
    }

}
