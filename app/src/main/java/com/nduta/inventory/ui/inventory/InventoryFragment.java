package com.nduta.inventory.ui.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nduta.inventory.R;

import java.util.ArrayList;
import java.util.List;

public class InventoryFragment extends Fragment implements AllDrugsAdapter.OnItemsClickListener{


    private RecyclerView rvDrugs;
    private FloatingActionButton fbAddDrugs;
    private EditText title;
    private EditText quantity;
    private EditText price;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DrugDetails drugDetails;
    List<DrugDetails> mCategory;
    private AllDrugsAdapter itemsAdapter;
    private LinearLayoutManager linearLayoutManager;
    private Context mContext;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_inventory, container, false);
        rvDrugs = root.findViewById(R.id.rvAllDrugs);
        mContext = getActivity();
        fbAddDrugs = root.findViewById(R.id.floatingActionButtonAddDrug);
        init();
        getAllDrugs();
        fbAddDrugs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAddDrugDialog();
            }
        });
        return root;
    }

    private void showAddDrugDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Add new Drug");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_drug_dialog,null);

        title = add_menu_layout.findViewById(R.id.title_add_drug);
        quantity = add_menu_layout.findViewById(R.id.quantity_add_drug);
        price = add_menu_layout.findViewById(R.id.price_add_drug);
        //event for button

        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.med);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                if(title.getText().toString() !=  null && quantity.getText().toString() !=  null && price.getText().toString() !=  null)
                {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    drugDetails=new DrugDetails(title.getText().toString(),quantity.getText().toString(),price.getText().toString());
                    db.collection("AllDrugs").document(title.getText().toString())
                            .set(drugDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Drug saved successfully",Toast.LENGTH_LONG).show();
                                    getAllDrugs();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(),"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else {
                    Toast.makeText(getContext(),"Please fill all the details.",Toast.LENGTH_LONG).show();
                }
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        alertDialog.show();

    }
    private void init(){

        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvDrugs.setLayoutManager(linearLayoutManager);
    }
    private void getAllDrugs() {
        db.collectionGroup("AllDrugs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mCategory = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mCategory.add(snapshot.toObject(DrugDetails.class));
                            populate();
                        } else {
                            Toast.makeText(mContext, "No Products found. Please add a new product", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                        Log.e(getActivity().toString(),"Error" +e);
                    }
                });
    }
    private void populate(){
        itemsAdapter = new AllDrugsAdapter(mCategory,this);
        itemsAdapter.setHasStableIds(true);
        itemsAdapter.notifyDataSetChanged();
        rvDrugs.setAdapter(itemsAdapter);

    }

    @Override
    public void onItemsClick(int position) {

    }
}