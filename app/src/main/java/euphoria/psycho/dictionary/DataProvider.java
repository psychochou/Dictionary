package euphoria.psycho.dictionary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;

public class DataProvider extends SQLiteOpenHelper {
    private static DataProvider sDataProvider;

    private static final int DATABASE_VERSION = 1;

    public static DataProvider getInstance() {
        if (sDataProvider == null) {

            File databaseDirectory = new File(Environment.getExternalStorageDirectory(), ".readings");
            databaseDirectory.mkdirs();
            sDataProvider = new DataProvider(Utils.getContext(), new File(databaseDirectory, "psycho.db").getAbsolutePath());
        }
        return sDataProvider;
    }

    public String queryWord(String word) {
        Cursor cursor = getReadableDatabase().rawQuery("SELECT word from dic where key=?", new String[]{word});
        String value = "";
        if (cursor.moveToNext()) {

            value = cursor.getString(0);
        }
        cursor.close();
        return value;
    }

    public void insert(String key, String word) {

        ContentValues values = new ContentValues();
        values.put("key", key);
        values.put("word", word);

        getWritableDatabase().insert("dic", null, values);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("CREATE TABLE \"dic\" ( \"key\" varchar , \"word\" varchar )");
        sqLiteDatabase.execSQL("CREATE UNIQUE INDEX \"idx_key\" on \"dic\"(\"key\")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

    }

    public DataProvider(Context context, String name) {
        super(context, name, null, DATABASE_VERSION);
    }
}
