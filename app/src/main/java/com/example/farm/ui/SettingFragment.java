package com.example.farm.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farm.MQTTHelper;
import com.example.farm.MainActivity;
import com.example.farm.R;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class SettingFragment extends Fragment {

    TextView txtSpeakerValue;
    EditText edtTempSetting;
    SeekBar sbSpeakerValue;
    Button btnSetting;
    int speakerValue;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_setting, container, false);

//        txtSpeakerValue = view.findViewById(R.id.txtspeakerValue);
//        edtTempSetting = view.findViewById(R.id.edtTempSetting);
//        sbSpeakerValue = view.findViewById(R.id.sbSpeaker);
//        btnSetting = view.findViewById(R.id.btnSetting);
//
//        //startMQTT();
//
//        sbSpeakerValue.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
//            @Override
//            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
//                txtSpeakerValue.setText(String.valueOf(sbSpeakerValue.getProgress()));
//            }
//
//            @Override
//            public void onStartTrackingTouch(SeekBar seekBar) {
//            }
//
//            @Override
//            public void onStopTrackingTouch(SeekBar seekBar) {
//                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(sbSpeakerValue.getProgress()));
//            }
//        });
//
//        btnSetting.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (!edtTempSetting.getText().toString().isEmpty()) {
//                    ((MainActivity)getActivity()).setSpeakerSetting(Integer.parseInt(edtTempSetting.getText().toString()));
//                    Toast.makeText(getActivity(), "Thiết lập thành công.", Toast.LENGTH_SHORT).show();
//                    edtTempSetting.setText("");
//                }
//                else Toast.makeText(getActivity(), "Vui lòng nhập nhiệt độ cảnh báo.", Toast.LENGTH_SHORT).show();
//            }
//        });
//
        return view;
    }

    MQTTHelper mqttHelper;
    public void startMQTT(){
        mqttHelper = new MQTTHelper(getContext(),"Topic/Speaker");
        mqttHelper.setCallback(new MqttCallbackExtended(){

            @Override
            public void connectionLost(Throwable cause) {}

            @Override
            public void messageArrived(String topic, MqttMessage mqttMessage) throws Exception {
                if (topic.equals("Topic/Speaker")) {
                    final String message = mqttMessage.toString().substring(1, mqttMessage.toString().length() - 1);
                    Log.w("Debug", message);
                    JSONObject jsonObject = new JSONObject(message);
                    String device_id = jsonObject.getString("device_id");
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    speakerValue = Integer.parseInt(valuesArray.getString(1));
                    txtSpeakerValue.setText(String.valueOf(speakerValue));
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
        });

    }
}
