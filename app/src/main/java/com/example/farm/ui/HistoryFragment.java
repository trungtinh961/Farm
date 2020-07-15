package com.example.farm.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.farm.Alert;
import com.example.farm.R;
import com.example.farm.alertAdapter;

import java.util.ArrayList;

/**
 * Created by Trung Tinh on 7/15/2020.
 */
public class HistoryFragment extends Fragment {

    public View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_history, container, false);

        initView();

        return view;

    }

    private void initView() {
        final RecyclerView rvHistory = (RecyclerView) view.findViewById(R.id.recyclerviewHistory);
        rvHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvHistory.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),layoutManager.getOrientation());
        rvHistory.addItemDecoration(dividerItemDecoration);

        final ArrayList<Alert> alertArrayList = new ArrayList<>();
        alertArrayList.add(new Alert(20,"11/06/2020"));
        alertArrayList.add(new Alert(20,"11/06/2020"));
        alertArrayList.add(new Alert(20,"11/06/2020"));
        alertArrayList.add(new Alert(20,"11/06/2020"));
        alertAdapter alertAdapter  = new alertAdapter(getContext(),R.layout.item_alert,alertArrayList);
        rvHistory.setAdapter(alertAdapter);
    }
}
