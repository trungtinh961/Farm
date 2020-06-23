package com.example.farm;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.farm.ui.HomeFragment;
import com.example.farm.ui.LoginActivity;
import com.example.farm.ui.NotificationFragment;
import com.example.farm.ui.SettingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.Charset;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private Handler mHandler = new Handler();
    private ActionBar toolbar;
    FirebaseAuth mAuth;
    private  String userID;
    BottomNavigationView bottomNavigationView;

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
//        FragmentManager fragmentManager = getSupportFragmentManager();
//        final HomeFragment homeFragment = (HomeFragment) fragmentManager.findFragmentById(R.id.fragment_home);
//        mHandler.postDelayed(new Runnable() {
//            public void run() {
//                homeFragment.txtTempValue.setText(String.valueOf(30));
//            }
//        }, 5000);

        
    }



    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    HomeFragment fragmentHome = new HomeFragment();
    SettingFragment settingFragment = new SettingFragment();
    NotificationFragment notificationFragment = new NotificationFragment();

    /* Creat Navigation Menu */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        boolean result = false;
        switch (menuItem.getItemId()) {
            case R.id.navigation_home:
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
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.fade_in, R.anim.fade_out).replace(R.id.container, notificationFragment).commit();
                toolbar.setTitle("Thông báo");
                result = true;
                break;
        }
        if (!result) {
            result = true;
        }
        return result;
    }

    /* Create menu reload */
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

        mqttHelper = new MQTTHelper(getApplicationContext());
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
                final String message = mqttMessage.toString().substring(1, mqttMessage.toString().length() - 1);
                Log.w("Debug", message);
                JSONObject jsonObject = new JSONObject(message);
                String device_id = jsonObject.getString("device_id");
                JSONArray valuesArray = jsonObject.getJSONArray("values");
                int temparature = Integer.parseInt(valuesArray.getString(0));
                int humidity = Integer.parseInt(valuesArray.getString(1));
                Log.w("Showdata",device_id + ": temp = " + temparature + ", humi = " + humidity);
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