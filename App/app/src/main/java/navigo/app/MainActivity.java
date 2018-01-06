package navigo.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {

    final String LOG = "myLog";   //константа для логов
    TextView logo;
    String city;                  //название города

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
        String url = "http://navigo.ga/data.php?city=" + city;

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                logo.setText("Response => " + response.toString());
                Log.d(LOG, response.toString());
                findViewById(R.id.progressBar).setVisibility(View.GONE);

                /* извлекаем двнные из json строки
               try{
                   JSONArray name = response.getJSONArray("name");
               }
               catch (JSONException e){
                   logo.setText("Ошибка");
                   Log.d(LOG, e.toString());
               }
               */


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                logo.setText("Ошибка соединения");
                Log.d(LOG, error.toString());
                findViewById(R.id.progressBar).setVisibility(View.INVISIBLE);
            }

        });
        queue.add(jsObjRequest);



        //TODO распарсить JSON код в массив

        //TODO Создать SQL таблицы (если это первый запуск)
        //только после того как получим название города что назвать таблицу (data_odessa)

        //TODO заполняем таблицу данными из полученного массива

        //TODO стественно делаем на всё проверки и если всё ок переходим intend-ом в следующие активити с картой


    /*
    Некоторые разъяснения:

    Пользователь будет видеть только логотип и статус бар который будет показывать состояние загрузки данных
    При последующем запуске загрузки уже не будет (приложение запустится быстрее)

    Чтобы наш главный экран не логал весь слодный код будем осуществлять в отдельном потоке

     */

<<<<<<< HEAD
  
=======
>>>>>>> VladMalakeev

    }
}
