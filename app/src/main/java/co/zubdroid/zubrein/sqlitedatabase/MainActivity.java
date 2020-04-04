package co.zubdroid.zubrein.sqlitedatabase;

import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //SharedPreferences creates database to keep track of the new data table
    SharedPreferences sharePreference;
    //Editor edits in the SharedPreference
    SharedPreferences.Editor editor;

    //Declaring all the elements on the xml that we are working with
    EditText etname, etemail, id;
    Button insert, delete, update, show;
    TextView show_result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Creates database named 'db_exists'
        sharePreference = getSharedPreferences("db_exists", MODE_PRIVATE);
        editor = sharePreference.edit();

        etname = findViewById(R.id.name);
        etemail = findViewById(R.id.email);
        id = findViewById(R.id.id);
        insert = findViewById(R.id.insert);
        delete = findViewById(R.id.delete);
        insert = findViewById(R.id.insert);
        update = findViewById(R.id.update);
        show = findViewById(R.id.show);
        show_result = findViewById(R.id.show_result);


        //Creating database with the method
        create_db();

        insert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insert();
            }
        });
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                delete();
            }
        });
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                update();
            }
        });
        show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showResult();
            }
        });


    }

    public void create_db() {
        /*SharedPreference has 2 columns. 1st column contains a 'key' and 2nd column contains a 'value' associated with the key
         * Whenever we create a new data table we change the 'value' in the SharedPreference table
         * So we can know that we have created a new table
         * Here the initial value is 0 and after we have created a new value we change the value to 1
         * So isExists becomes 1
         */
        int isExists = sharePreference.getInt("exists", 0);
        //Initial state when the value is 0, only then we can create a new data table
        if (isExists == 0) {
            //Creating new data table named 'MyDB'
            SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);

            /*Creating table if there is no table present previously
             * And passing the column names with their data types specified
             * Here the UNIQUE keyword defines the 'id' column as the Primary Key and
             * UNIQUE can not be anything else other than INTEGER
             */
            String createTableQuery = "CREATE TABLE IF NOT EXISTS user (id INT UNIQUE, name VARCHAR, email VARCHAR);";
            //Triggering the table with 'execSQL' method
            db.execSQL(createTableQuery);
            db.close();

            Toast.makeText(this, "Table user Created", Toast.LENGTH_LONG).show();

            /*editor puts/change the value of the 'value' column in the SharedPreference table
             * As we have created a data table above we are changing the value from 0 to 1
             */
            editor.putInt("exists", 1);
            editor.commit();
        }

    }

    public void insert() {
        String inp_name = etname.getText().toString();
        String inp_email = etemail.getText().toString();
        String idd = id.getText().toString();

        //Inserting into the table when the EditText fields are not empty
        if (!inp_name.equals("") && !inp_email.equals("") && !idd.equals("")) {
            SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
            String query = "INSERT OR REPLACE INTO user VALUES ('" + idd + "','" + inp_name + "','" + inp_email + "');";

            db.execSQL(query);
            db.close();

            //After each entry we are setting the EditText fields as empty
            etname.setText("");
            etemail.setText("");
            id.setText("");
        } else {
            //If there is nothing on the EditText fields then showing the message
            Toast.makeText(this, "Field must not empty", Toast.LENGTH_SHORT).show();
        }
    }

    public void delete() {
        //Deleting any entry with the 'id' entity as we have defined it as a Primary Key
        String idd = id.getText().toString();
        if (!idd.equals("")) {
            int numb = Integer.parseInt(idd);
            SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);
            String query = "DELETE FROM user WHERE id = " + numb + ";";

            db.execSQL(query);
            db.close();
        } else {
            Toast.makeText(this, "Please insert id number", Toast.LENGTH_SHORT).show();
        }


    }

    public void update() {
        /*For updating we need the 'id' of any existing entry and then we need the updated values
         * And then put it in the data table
         * We can not change the 'id'        */
        String inp_name = etname.getText().toString();
        String inp_email = etemail.getText().toString();
        String idd = id.getText().toString();


        if (!inp_name.equals("") && !inp_email.equals("") && !idd.equals("")) {

            SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);

//            String query = "UPDATE user SET name = " + inp_name + ",email = " + inp_email + " WHERE id = " + numb + ";";
            String query = "UPDATE user SET name = '" + inp_name + "',email = '" + inp_email + "' WHERE id = " + idd + ";";

            db.execSQL(query);
            db.close();
        }


    }


    public void showResult() {

        SQLiteDatabase db = openOrCreateDatabase("MyDB", MODE_PRIVATE, null);

        //Fetching the whole table into a cursor type variable
        Cursor cursor = db.rawQuery("SELECT * FROM user ;", null);

        //Counting the rows
        int rowCount = cursor.getCount();

        String result = "";

        //If row count is or less than 0 that means there is no data in the table
        if (rowCount <= 0) {
            Toast.makeText(MainActivity.this, "No data available", Toast.LENGTH_SHORT).show();
        } else {
            //If there is data then start iterating from the first data
            cursor.moveToFirst();

            do {
                //Fetching data from each column using the Cursor and getColumnIndex
                String idd = cursor.getString(cursor.getColumnIndex("id"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String email = cursor.getString(cursor.getColumnIndex("email"));


                result = result + idd + " " + name + " " + email + "\n";


            } while (cursor.moveToNext());
        }
        show_result.setText(result);

    }


}
