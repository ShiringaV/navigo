package navigo.app;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

import static navigo.app.MainActivity.LOG;
import static navigo.app.MainActivity.cityName;
import static navigo.app.MainActivity.dbVersion;


/**
 * Created by Vlad Malakeev on 09.01.2018.
 */

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context context) {
        // конструктор суперкласса
        super(context, "navigo", null, dbVersion);
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
        Log.d(LOG, "создание таблицы " + cityName);
        db.execSQL("create table "+ cityName + " ("
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
        db.execSQL("drop table "+ cityName);
    }

    //вывод бд
    public ArrayList<String[]> viewDataBase(SQLiteDatabase db, boolean log, boolean data) {
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

            if (log == true) {
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
                                    ", comment = " + c.getString(commentColIndex));
                    // переход на следующую строку
                    // а если следующей нет (текущая - последняя), то false - выходим из цикла
                } while (c.moveToNext());
            }

            if (data == true) {
                Log.d(LOG, "data == true");
                ArrayList<String[]> mapData = new ArrayList<String[]>();
               do {
                   String [] mapValues = {c.getString(latColIndex), c.getString(lonColIndex), c.getString(nameColIndex)};
                   mapData.add(mapValues);
                }
                while (c.moveToNext());
                Log.d(LOG, "возврат даных");
                return mapData;
            }
        } else {
             if (log == true) Log.d(LOG, "0 rows");
        }
        c.close();
    return null;
    }

    // просмотреть все таблицы бд
    public  void viewTables(SQLiteDatabase db){
        Cursor c = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table'", null);
        if (c.moveToFirst()) {
            while ( !c.isAfterLast() ) {
                Log.d(LOG, c.getString(0));
                c.moveToNext();
            }
        }
    }
}
