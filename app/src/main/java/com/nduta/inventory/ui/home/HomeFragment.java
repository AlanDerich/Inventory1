package com.nduta.inventory.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.nduta.inventory.R;
import com.nduta.inventory.ui.inventory.InventoryFragment;
import com.nduta.inventory.ui.sales.SalesFragment;

public class HomeFragment extends Fragment {

    private Button btnInventory;
    private Button btnSales;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_landingpage, container, false);
        btnInventory = root.findViewById(R.id.button2_inventoryy);
        btnSales = root.findViewById(R.id.button_saless);
        btnSales.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragmentStaff = new SalesFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                transactionStaff.commit();
            }
        });
        btnInventory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppCompatActivity activity = (AppCompatActivity) v.getContext();
                Fragment fragmentStaff = new InventoryFragment();
                FragmentTransaction transactionStaff = activity.getSupportFragmentManager().beginTransaction();
                transactionStaff.replace(R.id.nav_host_fragment,fragmentStaff);
                transactionStaff.addToBackStack(null);
                transactionStaff.commit();
            }
        });
        return root;
    }
}