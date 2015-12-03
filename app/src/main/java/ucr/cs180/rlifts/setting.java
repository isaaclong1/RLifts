package ucr.cs180.rlifts;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * A login screen that offers login via email/password.
 */
public class setting extends AppCompatActivity   {

    private ListView lv;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        Bundle extras = getIntent().getExtras();


        lv = (ListView) findViewById(R.id.listView1);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String[] item = getResources().getStringArray(R.array.list);
                switch(position) {
                    case 0:

                        Intent intent = new Intent(setting.this, editprofile.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("name", getIntent().getExtras().getString("uname"));
                        bundle.putString("phonenumber", getIntent().getExtras().getString("phonenumber"));
                        bundle.putString("birthday", getIntent().getExtras().getString("birthday"));
                        bundle.putString("email", getIntent().getExtras().getString("email"));
                        bundle.putString("UID", getIntent().getExtras().getString("UID"));
                        bundle.putString("first_name", getIntent().getExtras().getString("first_name"));
                        bundle.putString("last_name", getIntent().getExtras().getString("last_name"));
                        intent.putExtras(bundle);
                        startActivity(intent);
                        break;


                   default:
                       Toast.makeText(getApplicationContext(),
                               "Wrong spot", Toast.LENGTH_SHORT).show();
                       break;


                }


            }
        });

    }


}




