package com.example.farm.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.farm.MainActivity;
import com.example.farm.R;

/**
 * Created by Trung Tinh on 6/20/2020.
 */
public class HomeFragment extends Fragment {

    public TextView txtTempValue, txtHumiValue, txtSpeakerValue;
    public SeekBar seekBarSpeaker;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view =  inflater.inflate(R.layout.fragment_home, container, false);

        txtTempValue = (TextView) view.findViewById(R.id.tempValue);
        txtHumiValue = (TextView) view.findViewById(R.id.humiValue);
        txtSpeakerValue = (TextView) view.findViewById(R.id.speakerValue);
        seekBarSpeaker = (SeekBar) view.findViewById(R.id.seekBarSpeaker);
        seekBarSpeaker.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                txtSpeakerValue.setText(String.valueOf(seekBarSpeaker.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((MainActivity)getActivity()).sendDataToMQTT("Speaker","1", String.valueOf(seekBarSpeaker.getProgress()));
            }
        });

        return view;
    }
}
