package com.example.farm.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.akaita.android.circularseekbar.CircularSeekBar;
import com.example.farm.model.Alert;
import com.example.farm.model.MQTTHelper;
import com.example.farm.model.MainActivity;
import com.example.farm.R;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.zhouyou.view.seekbar.SignSeekBar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "MyActivity";
    float AutoTemp=-1;
    float realtimeTemp=-1;
    SignSeekBar edtSpeakerSetting;
    int speakerValue;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private DocumentReference mRef;

    public void capnhat(float x){
        realtimeTemp = x;
        if (AutoTemp != -1 && x != -1){
            if (x >= AutoTemp){
                edtSpeakerSetting.setProgress(5000);
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(5000));
                Toast.makeText(getContext(), "Chú ý nhiệt độ bất thường! " + Math.round(x) + " oC", Toast.LENGTH_SHORT).show();
                Calendar calendar = Calendar.getInstance();
                @SuppressLint("SimpleDateFormat") SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss - dd/MM/yyyy");
                Alert alert = new Alert(Math.round(x),format.format(calendar.getTime())+"\n");

                /* Send alert to firebase */
                mRef = db.collection("alert").document(calendar.getTimeInMillis()+"");
                mRef.set(alert);
            }
            else {
                edtSpeakerSetting.setProgress(0);
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(0));
//                Toast.makeText(getContext(), "Đã tắt quạt!", Toast.LENGTH_SHORT).show();
            }
        }
    }
    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_setting, container, false);

        edtSpeakerSetting = view.findViewById(R.id.seek_barcustom);

        CircularSeekBar seekBar=view.findViewById(R.id.seekbar);
        seekBar.setProgressTextFormat(new DecimalFormat("###"));
        seekBar.setRingColor(Color.BLACK);
        seekBar.setOnCenterClickedListener(new CircularSeekBar.OnCenterClickedListener() {
            @Override
            public void onCenterClicked(CircularSeekBar seekBar, float progress) {
                Snackbar.make(seekBar, "Đã tắt tự động điều khiển nhiệt độ và bật điều khiển quạt thủ công!!!",Snackbar.LENGTH_SHORT*5).show();
                seekBar.setRingColor(Color.BLACK);
                seekBar.setProgress(0);
                edtSpeakerSetting.setEnabled(true);
            }
        });

        seekBar.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, float progress, boolean fromUser) {
                edtSpeakerSetting.setEnabled(false);
                if (progress==0) {
                    edtSpeakerSetting.setEnabled(true);
                    seekBar.setRingColor(Color.BLACK);
                }
                else if (progress<33)
                    seekBar.setRingColor(Color.GREEN);
                else if(progress<66)
                    seekBar.setRingColor(Color.YELLOW);
                else
                    seekBar.setRingColor(Color.RED);
            }

            @Override
            public void onStartTrackingTouch(CircularSeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(CircularSeekBar seekBar) {
                Snackbar.make(seekBar, "Đã tắt điều khiển quạt thủ công và đặt tự động điều khiển đến nhiệt độ: " + Math.round(seekBar.getProgress())+"oC",Snackbar.LENGTH_SHORT*5).show();
                AutoTemp=seekBar.getProgress();
                capnhat(realtimeTemp);
            }
        });

        startMQTT();

        edtSpeakerSetting.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {

            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                String s = String.format(Locale.CHINA, "onChanged int:%d, float:%.1f", progress, progressFloat);
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                String s = String.format(Locale.CHINA, "onActionUp int:%d, float:%.1f", progress, progressFloat);
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(progress));
                Snackbar.make(view, "Đã thiết lập quạt với công suất: " +progress,Snackbar.LENGTH_SHORT*3).show();
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                Log.i(TAG, "abc: "+progress);
                String s = String.format(Locale.CHINA, "onFinally int:%d, float:%.1f", progress, progressFloat);
            }
        });
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
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    speakerValue = Integer.parseInt(valuesArray.getString(1));
                    edtSpeakerSetting.setProgress(speakerValue);
                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
        });
    }
}
