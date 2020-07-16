package com.example.farm.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farm.model.Alert;
import com.example.farm.model.MQTTHelper;
import com.example.farm.R;
import com.example.farm.model.MainActivity;
import com.github.lzyzsd.circleprogress.ArcProgress;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class HomeFragment extends Fragment {

    public ArcProgress tTempValue, tHumiValue;
////    public Button btnTest;
    int temparature = 0;
    int humidity = 0;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mRef;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        tTempValue = (ArcProgress) view.findViewById(R.id.arc_progress1);
        tTempValue.setSuffixText("oC");
        tHumiValue = (ArcProgress) view.findViewById(R.id.arc_progress2);
        startMQTT();

        return view;
    }

    MQTTHelper mqttHelper;
    public void startMQTT(){
        mqttHelper = new MQTTHelper(getContext(),"Topic/TempHumi");
        mqttHelper.setCallback(new MqttCallbackExtended(){

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if (topic.equals("Topic/TempHumi")) {
                    final String message = mqttMessage.toString().substring(1, mqttMessage.toString().length() - 1);
                    Log.w("Home", message);
                    JSONObject jsonObject = new JSONObject(message);
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    temparature = Integer.parseInt(valuesArray.getString(0));
                    humidity = Integer.parseInt(valuesArray.getString(1));
                    tTempValue.setProgress(temparature);
                    tHumiValue.setProgress(humidity);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
        });
    }
}
