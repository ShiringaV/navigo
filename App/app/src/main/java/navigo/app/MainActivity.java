package navigo.app;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
    TextView logo;
    String cityName = "odessa";                  //название города

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        logo = (TextView) findViewById(R.id.logo);

        //TODO определяем координаты с LocationSingleton

        //TODO Определяем город (получаем String sity), это делается с помощью  GeoCoder
        //ресурс = https://developer.android.com/reference/android/location/Geocoder.html

        //TODO Проверка - если уже существует бд с таким городом, то сразу переходим к карте

        //TODO отправить запрос на сервер например http://www.navigo.ga/data/odessa
        //В результате мы получим JSON строку

        RequestQueue queue = Volley.newRequestQueue(this);

        //адрес куда отправляем запрос + название опредилившегося города
        String url = "http://navigo.zzz.com.ua/index.php?city=" + cityName;
      //  String url ="https://www.foodapp.gq/android/data.php";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                String[] array_result = response.split("end_json_string");
                String result = array_result[0];
                Log.d(LOG, result.toString());
                findViewById(R.id.progressBar).setVisibility(View.GONE);
                //извлекаем двнные из json строки
               try{
                   //TODO распарсить JSON код в массив
                   jsonObject = new JSONObject(result);
                   JSONArray city = jsonObject.getJSONArray(cityName);
                   for(int i = 0; i < city.length();i++ ){

                       //Следущие дынные будут писаться в базу SQLite
                       String name = city.getJSONObject(i).getString("name");               //название достопримечательности
                       String type = city.getJSONObject(i).getString("type");               //тип достопримечательности
                       String lat = city.getJSONObject(i).getString("lat");                 //широта
                       String lon = city.getJSONObject(i).getString("lon");                 //долгота
                       String descript = city.getJSONObject(i).getString("descript");       //описание
                       String image = city.getJSONObject(i).getString("image");             //путь к картинке
                       String video = city.getJSONObject(i).getString("video");             //путь к видео
                       String sount_people = city.getJSONObject(i).getString("count_people"); //количество проголосовавших людей
                       String summ_mark = city.getJSONObject(i).getString("summ_mark");     //общая сумма голосов
                       String comment = city.getJSONObject(i).getString("comment");         //комментарии

                       Log.d(LOG, "Название: " + name +
                                  "Тип: " + type +
                                  "Широта: " + lat +
                                  "Долгота:" + lon +
                                  "Описание: " + descript);

                       //TODO заполняем таблицу данными из полученного массива
                   }
               }
               catch (JSONException e){
                   logo.setText("Ошибка");
                   Log.d(LOG, e.toString());
               }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                logo.setText("Ошибка соединения");
                Log.d(LOG, error.toString());
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }

        });
        queue.add(stringRequest);





        //TODO Создать SQL таблицы (если это первый запуск)
        //только после того как получим название города что назвать таблицу (data_odessa)



        //TODO стественно делаем на всё проверки и если всё ок переходим intend-ом в следующие активити с картой


    /*
    Некоторые разъяснения:

    Пользователь будет видеть только логотип и статус бар который будет показывать состояние загрузки данных
    При последующем запуске загрузки уже не будет (приложение запустится быстрее)

    Чтобы наш главный экран не логал весь слодный код будем осуществлять в отдельном потоке

     */



    }
}
