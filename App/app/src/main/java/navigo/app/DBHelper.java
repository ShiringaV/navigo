package navigo.app;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


/**
 * Created by Vlad Malakeev on 09.01.2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    final String LOG = "myLog";

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "navigo", null, MainActivity.dbVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // создаем таблицу при самом первом запуске
        createTable(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
      if(oldVersion < newVersion){
          Log.d(LOG, "старая версия - " + oldVersion + ", новая - " + newVersion);
          createTable(db);
      }
      else {
          Log.d(LOG, "база не поменялась");
      }
    }

    private void createTable(SQLiteDatabase db){
        Log.d(LOG, "создание таблицы " + MainActivity.cityName);
        db.execSQL("create table "+ MainActivity.cityName + " ("
                + "id integer primary key autoincrement,"
                + "name text,"
                + "type text,"
                + "lat text,"
                + "lon text,"
                + "descript text,"
                + "image text,"
                + "video text,"
                + "count_people integer,"
                + "summ_mark integer,"
                + "comment text,"
                + "version integer" + ");");
    }


    //удаление ненужной таблицы
    public void dropTable(SQLiteDatabase db){
        db.execSQL("drop table "+ MainActivity.cityName);
    }


}
