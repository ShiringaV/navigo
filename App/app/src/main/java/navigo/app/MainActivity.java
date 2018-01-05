package navigo.app;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    //TODO определяем координаты с LocationSingleton

    //TODO Определяем город (получаем String sity), это делается с помощью  GeoCoder
    //ресурс = https://developer.android.com/reference/android/location/Geocoder.html

    //TODO Проверка - если уже существует бд с таким городом, то сразу переходим к карте

    //TODO отправить запрос на сервер например http://www.navigo.ga/data/odessa
    //В результате мы получим JSON строку

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

  //тест для gitHub

}
