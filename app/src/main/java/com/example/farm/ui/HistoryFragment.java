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

import com.example.farm.model.Alert;
import com.example.farm.adapter.AlertAdapter;
import com.example.farm.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

/**
 * Created by Trung Tinh on 7/15/2020.
 */
public class HistoryFragment extends Fragment {

    public View view;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    CollectionReference mRef = db.collection("alert");

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view =  inflater.inflate(R.layout.fragment_history, container, false);
        initView();
        return view;
    }

    private void initView() {
        RecyclerView rvHistory = (RecyclerView) view.findViewById(R.id.recyclerviewHistory);
        rvHistory.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        rvHistory.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(),layoutManager.getOrientation());
        rvHistory.addItemDecoration(dividerItemDecoration);

        final ArrayList<Alert> alertArrayList = new ArrayList<>();

        mRef.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (QueryDocumentSnapshot documentSnapshot: queryDocumentSnapshots) {
                    Alert alert = documentSnapshot.toObject(Alert.class);
                    alertArrayList.add(0,alert);
                }
            }
        });

        AlertAdapter alertAdapter = new AlertAdapter(alertArrayList,getContext());
        rvHistory.setAdapter(alertAdapter);
    }
}
