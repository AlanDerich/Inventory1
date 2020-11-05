package com.nduta.inventory.ui.sales;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.nduta.inventory.R;
import com.nduta.inventory.ui.inventory.AllDrugsAdapter;
import com.nduta.inventory.ui.inventory.DrugDetails;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SalesFragment extends Fragment implements AllSalesAdapter.OnItemsClickListener{

    private RecyclerView rvSales;
    List<SalesDetails> mCategory;
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private LinearLayoutManager linearLayoutManager;
    private Spinner sp_add_sale;
    private EditText quantity_add_sale;
    private EditText customer_id_add_sale;
    private SalesDetails saleDetails;
    List<DrugDetails> mDrugs;
    List<String> cats = new ArrayList<>();
    private FloatingActionButton fabAdd;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        View root = inflater.inflate(R.layout.fragment_sales, container, false);
        rvSales = root.findViewById(R.id.rv_all_salwes);
        fabAdd = root.findViewById(R.id.floatingActionButtonAddSale);
        fabAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addSales();
            }
        });
        init();
        getAllSales();
        return root;
    }

    private void addSales() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
        alertDialog.setTitle("Add new Drug");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_layout = inflater.inflate(R.layout.add_sale_dialog,null);

        sp_add_sale = add_menu_layout.findViewById(R.id.sp_add_sale);
        populateSpinner();
        quantity_add_sale = add_menu_layout.findViewById(R.id.quantity_add_sale);
        customer_id_add_sale = add_menu_layout.findViewById(R.id.customer_id_add_sale);
        //event for button
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy",Locale.US);
        final String formattedDate = df.format(c);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm a", Locale.US);
        final String date = sdf.format(c);
        alertDialog.setView(add_menu_layout);
        alertDialog.setIcon(R.drawable.med);

        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();

                if(quantity_add_sale.getText().toString() !=  null && customer_id_add_sale.getText().toString() !=  null)
                {
                    FirebaseFirestore db = FirebaseFirestore.getInstance();
                    String price="",totalAmount="";
                    String targetName = sp_add_sale.getSelectedItem().toString();
                    DrugDetails result = null;
                    for (DrugDetails c : mDrugs) {
                        if (targetName.equals(c.getDrugName())) {
                            result = c;
                            price=c.getDrugPrice();
                            totalAmount=String.valueOf(Integer.parseInt(price)* Integer.parseInt(quantity_add_sale.getText().toString().trim()));
                            break;
                        }
                    }
                    saleDetails = new SalesDetails(sp_add_sale.getSelectedItem().toString(),quantity_add_sale.getText().toString(),date,customer_id_add_sale.getText().toString(),totalAmount);
                    db.collection("AllSales").document(encode(formattedDate)).collection("todayssales").document()
                            .set(saleDetails)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
//                                startActivity(new Intent(getContext(), MainActivityAdmin.class));
                                    Toast.makeText(getContext(),"Sale added successfully",Toast.LENGTH_LONG).show();
                                    getAllSales();
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

    public static String encode(String date){
        return date.replace("/",",");
    }

    private void populateSpinner() {
        db.collection("AllDrugs").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mDrugs = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mDrugs.add(snapshot.toObject(DrugDetails.class));
                            int size = mDrugs.size();
                            int position;
                            for (position=0;position<size;position++){
                                DrugDetails uDetails= mDrugs.get(position);
                                cats.add(uDetails.getDrugName());
                            }
                            ArrayAdapter<String> usersAdapter = new ArrayAdapter<>(
                                    getContext(), android.R.layout.simple_spinner_item, cats);
                            sp_add_sale.setAdapter(usersAdapter);
                        } else {
                            Toast.makeText(mContext, "No products found. Please contact admin to add a new product", Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(mContext, "Something went terribly wrong." + e, Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void init(){
        linearLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rvSales.setLayoutManager(linearLayoutManager);
    }
    private void getAllSales() {
        db.collectionGroup("todayssales").get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        mCategory = new ArrayList<>();
                        if (!queryDocumentSnapshots.isEmpty()) {
                            for (DocumentSnapshot snapshot : queryDocumentSnapshots)
                                mCategory.add(snapshot.toObject(SalesDetails.class));
                            populate();
                        } else {
                            Toast.makeText(mContext, "No sales found.", Toast.LENGTH_LONG).show();
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
        AllSalesAdapter salesAdapter = new AllSalesAdapter(mCategory,this);
        salesAdapter.setHasStableIds(true);
        salesAdapter.notifyDataSetChanged();
        rvSales.setAdapter(salesAdapter);

    }

    @Override
    public void onItemsClick(int position) {

    }
}