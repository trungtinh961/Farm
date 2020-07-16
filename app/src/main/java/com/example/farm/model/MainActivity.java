package com.example.farm.model;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.farm.R;
import com.example.farm.ui.HistoryFragment;
import com.example.farm.ui.HomeFragment;
import com.example.farm.ui.LoginActivity;
import com.example.farm.ui.GraphFragment;
import com.example.farm.ui.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private ActionBar toolbar;
    FirebaseAuth mAuth;
    BottomNavigationView bottomNavigationView;
    private  int startSpeakerValue = 40;
    int temparature = 0;
    int humidity = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Set Title */
        toolbar = getSupportActionBar();
        toolbar.setTitle("Trang chủ");

        /* Bottom Navigation */
        bottomNavigationView = findViewById(R.id.bottom_navigation_view);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        bottomNavigationView.setSelectedItemId(R.id.navigation_home);

        /* Get user ID */
        mAuth   = FirebaseAuth.getInstance();

        /* Start MQTT */
        startMQTT();
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    HomeFragment fragmentHome = new HomeFragment();
    SettingFragment settingFragment = new SettingFragment();
    GraphFragment graphFragment = new GraphFragment();
    HistoryFragment historyFragment = new HistoryFragment();

    public void capnhatnhietdo(int x){
        settingFragment.capnhat(x);
    }

    /* Creat Navigation Menu */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean result = false;
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
                Bundle bundle = new Bundle();
                bundle.putString("value",String.valueOf(temparature));
                fragmentHome.setArguments(bundle);
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, fragmentHome).commit();
                toolbar.setTitle("Trang chủ");
                result = true;
                break;

            case R.id.navigation_setting:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, settingFragment).commit();
                toolbar.setTitle("Cài đặt");
                result = true;
                break;

            case R.id.navigation_notification:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, graphFragment).commit();
                toolbar.setTitle("Biểu đồ");
                result = true;
                break;

            case R.id.navigation_history:
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, historyFragment).commit();
                toolbar.setTitle("Lịch sử");
                result = true;
                break;
        }
        if (!result) {
            result = true;
        }
        return result;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.custom_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.menuLogOut:
                mAuth.signOut();
                finish();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /* MQTT Class */

    MQTTHelper mqttHelper;
    public void startMQTT(){
        mqttHelper = new MQTTHelper(getApplicationContext(),"Topic/TempHumi");
        mqttHelper.setCallback(new MqttCallbackExtended() {
            @Override
            public void connectComplete(boolean b, String s) {
            }

            @Override
            public void connectionLost(Throwable throwable) {
            }

            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if (topic.equals("Topic/TempHumi")) {
                    final String message = mqttMessage.toString().substring(1, mqttMessage.toString().length() - 1);
                    Log.w("Main", message);
                    JSONObject jsonObject = new JSONObject(message);
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    temparature = Integer.parseInt(valuesArray.getString(0));
                    humidity = Integer.parseInt(valuesArray.getString(1));
                    capnhatnhietdo(temparature);
                    /* Auto turn on Speaker and send notification */
                    if (temparature > startSpeakerValue) {
                        Calendar calendar = Calendar.getInstance();
                        @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
                        Alert alert = new Alert(temparature,format.format(calendar.getTime())+"\n");
                        sendDataToMQTT("Speaker","1","5000");
                        Toast.makeText(MainActivity.this, "Chú ý nhiệt độ bất thường! " + String.valueOf(temparature) + " oC", Toast.LENGTH_SHORT).show();

                        /* Send alert to firebase */
                        mRef = db.collection("alert").document(calendar.getTimeInMillis()+"");
                        mRef.set(alert);

                    }
                }
            }
            @Override
            public void deliveryComplete(IMqttDeliveryToken iMqttDeliveryToken) {

            }
        });
    }

    public void sendDataToMQTT(final String ID, final String value1, final String value2){
        MqttMessage msg = new MqttMessage();
        msg.setId(1234);
        msg.setQos(0);
        msg.setRetained(true);
        String data = "[{\"device_id\":\"Speaker\", \"values\":[\"" + value1 + "\",\"" + value2 + "\"]}]";
        byte[] b = data.getBytes(Charset.forName("UTF-8"));
        msg.setPayload(b);
        try {
            mqttHelper.mqttAndroidClient.publish("Topic/Speaker", msg);
            Log.e("publish","[{\"device_id\":\"" + ID + "\", \"values\":[\"" + value1 + "\",\"" + value2 + "\"]}]");
        }catch (MqttException e){
        }
    }

}