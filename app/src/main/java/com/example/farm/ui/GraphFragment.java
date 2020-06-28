package com.example.farm.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farm.MQTTHelper;
import com.example.farm.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallbackExtended;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class GraphFragment extends Fragment {

    GraphView graphTemperature, graphHumidity;
    private LineGraphSeries<DataPoint> series;
    private int lastX = 0;
    int temparature = 0;
    int humidity = 0;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_graph, container, false);

        startMQTT();

        graphTemperature = (GraphView) view.findViewById(R.id.graphTemperature);
        graphHumidity = (GraphView) view.findViewById(R.id.graphHumidity);

        graphTemperature.getViewport().setMinY(0);
        graphTemperature.getViewport().setMaxY(100);
        graphTemperature.getViewport().setYAxisBoundsManual(true);
        graphTemperature.getViewport().setScrollable(true);

        graphHumidity.getViewport().setMinY(0);
        graphHumidity.getViewport().setMaxY(100);
        graphHumidity.getViewport().setYAxisBoundsManual(true);
        graphHumidity.getViewport().setScrollable(true);

        series = new LineGraphSeries<DataPoint>();
        graphTemperature.addSeries(series);

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
                    Log.w("Noti", message);
                    JSONObject jsonObject = new JSONObject(message);
                    JSONArray valuesArray = jsonObject.getJSONArray("values");
                    temparature = Integer.parseInt(valuesArray.getString(0));
                    humidity = Integer.parseInt(valuesArray.getString(1));

                    series.appendData(new DataPoint(lastX++,temparature), true, 10 );

                }
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {}

            @Override
            public void connectComplete(boolean reconnect, String serverURI) {}
        });

    }

}
