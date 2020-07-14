package com.example.farm.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
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
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.akaita.android.circularseekbar.CircularSeekBar;
import com.example.farm.MQTTHelper;
import com.example.farm.MainActivity;
import com.example.farm.R;
import com.google.android.material.snackbar.Snackbar;
import com.zhouyou.view.seekbar.SignSeekBar;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.Locale;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class SettingFragment extends Fragment {
    private static final String TAG = "MyActivity";
    float AutoTemp=-1;
    float realtimeTemp=-1;
    SignSeekBar edtSpeakerSetting;
    TextView txtSpeakerValue;
    //    EditText edtTempSetting;
//    SeekBar sbSpeakerValue;
//    Button btnSetting;
    int speakerValue;
    public void capnhat(float x){
        realtimeTemp=x;
        if (AutoTemp!=-1&&x!=-1){
            if (x>=AutoTemp){
                edtSpeakerSetting.setProgress(5000);
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(5000));
            }
            else {
                edtSpeakerSetting.setProgress(0);
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(0));
            }
        }
    }
    @SuppressLint("ResourceAsColor")
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        final View view =  inflater.inflate(R.layout.fragment_setting, container, false);

//        txtSpeakerValue = view.findViewById(R.id.txtspeakerValue);
        edtSpeakerSetting = view.findViewById(R.id.seek_barcustom);
//        edtSpeakerSetting.setProgress(2000);
//        edtSpeakerSetting.getConfigBuilder().thumbColor(ContextCompat.getColor(getContext(), android.R.color.black));
//        edtSpeakerSetting.setBackgroundColor(android.R.color.black);

//        edtSpeakerSetting.getConfigBuilder().signTextSize(20);
        CircularSeekBar seekBar=view.findViewById(R.id.seekbar);
        seekBar.setProgressTextFormat(new DecimalFormat("###,###,##0.00"));
        seekBar.setRingColor(Color.BLACK);
        seekBar.setOnCenterClickedListener(new CircularSeekBar.OnCenterClickedListener() {
            @Override
            public void onCenterClicked(CircularSeekBar seekBar, float progress) {
//                seekBar.setProgressTextFormat(new StringFormat());
                Snackbar.make(seekBar, "Đã tắt tự động điều khiển nhiệt độ và bật điều khiển speaker thủ công!!!",Snackbar.LENGTH_SHORT*5).show();
                seekBar.setRingColor(Color.BLACK);
                seekBar.setProgress(0);

//                edtSpeakerSetting.getConfigBuilder().trackColor(R.color.colorAccent).build();
////                edtSpeakerSetting.getConfigBuilder().secondTrackColor(R.color.colorAccent).build();
//                edtSpeakerSetting.getConfigBuilder().thumbColor(R.color.colorAccent).build();
                edtSpeakerSetting.setEnabled(true);
//                seekBar.setProgress(seekBar.getProgress());
//                seekBar.setProgressText("OFF");
//                seekBar.setProgressTextFormat(new DecimalFormat("###,###,##0.00"));
//                seekBar.setProgress(seekBar.getProgress());
            }
        });

        seekBar.setOnCircularSeekBarChangeListener(new CircularSeekBar.OnCircularSeekBarChangeListener() {
            @Override
            public void onProgressChanged(CircularSeekBar seekBar, float progress, boolean fromUser) {
//                seekBar.setProgressTextFormat(new DecimalFormat("###,###,##0.00"));
//                seekBar.setProgressText(seekBar.getProgress());
                edtSpeakerSetting.setEnabled(false);
//                AutoTemp=progress;
//                edtSpeakerSetting.getConfigBuilder().trackColor(R.color.colorPrimaryDark).build();
//                edtSpeakerSetting.getConfigBuilder().secondTrackColor(R.color.colorPrimaryDark).build();
//                edtSpeakerSetting.getConfigBuilder().thumbColor(R.color.colorPrimaryDark).build();
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
                Snackbar.make(seekBar, "Đã tắt điều khiển speaker thủ công và đặt tự động điều khiển đến nhiệt độ: "+seekBar.getProgress()+"oC",Snackbar.LENGTH_SHORT*5).show();
                AutoTemp=seekBar.getProgress();
                capnhat(realtimeTemp);
            }
        });
//            edtSpeakerSetting.setEnabled(false);
//            edtSpeakerSetting.getConfigBuilder().trackColor(-7829368);
//            edtSpeakerSetting.getConfigBuilder().secondTrackColor(-7829368);
//        sbSpeakerValue = view.findViewById(R.id.sbSpeaker);
//        btnSetting = view.findViewById(R.id.btnSetting);
//
        startMQTT();
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

        edtSpeakerSetting.setOnProgressChangedListener(new SignSeekBar.OnProgressChangedListener() {
            //            @Override
//            public void onStopTrackingTouch(SignSeekBar seekBar) {
//                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(sbSpeakerValue.getProgress()));
//            }
            @Override
            public void onProgressChanged(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                //fromUser 表示是否是用户触发 是否是用户touch事件产生
                String s = String.format(Locale.CHINA, "onChanged int:%d, float:%.1f", progress, progressFloat);
//                Snackbar.make(view, s,Snackbar.LENGTH_SHORT).show();
//                progressText.setText(s);
            }

            @Override
            public void getProgressOnActionUp(SignSeekBar signSeekBar, int progress, float progressFloat) {
                String s = String.format(Locale.CHINA, "onActionUp int:%d, float:%.1f", progress, progressFloat);
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(progress));
                Snackbar.make(view, "Đã thiết lập speaker với công suất: " +progress,Snackbar.LENGTH_SHORT*3).show();
//                progressText.setText(s);
            }

            @Override
            public void getProgressOnFinally(SignSeekBar signSeekBar, int progress, float progressFloat,boolean fromUser) {
                Log.i(TAG, "abc: "+progress);
                String s = String.format(Locale.CHINA, "onFinally int:%d, float:%.1f", progress, progressFloat);
//                progressText.setText(s + getContext().getResources().getStringArray(R.array.labels)[progress]);
//                Snackbar.make(view, s,Snackbar.LENGTH_SHORT).show();
            }
        });
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
//                    txtSpeakerValue.setText(String.valueOf(speakerValue));
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
