package com.nduta.inventory.ui.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.nduta.inventory.ItemClickListener;
import com.nduta.inventory.R;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.File;
import java.io.InputStream;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import static android.content.ContentValues.TAG;

public class AllDrugsAdapter extends RecyclerView.Adapter<AllDrugsAdapter.ViewHolder>{
    Context mContext;
    DrugDetails drugDetails;
    private EditText title;
    private EditText quantity;
    private EditText price;
    List<DrugDetails> mItemInfo;
    private File localFile;
    private Bitmap bmp;
    private ViewHolder holder1;
    FirebaseStorage storage;
    StorageReference storageReference;
    Uri saveUri;
    MaterialEditText edtName,edtDescription,edtPrice,edtDiscount;
    Button btnUpload, btnSelect;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private AllDrugsAdapter.OnItemsClickListener onItemsClickListener;
    private int pos;
    private ItemClickListener mItemClickListener;
    private String picUri;

    public AllDrugsAdapter(List<DrugDetails> mItemInfo, AllDrugsAdapter.OnItemsClickListener onItemsClickListener){
        this.mItemInfo = mItemInfo;
        this.onItemsClickListener = onItemsClickListener;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.list_view_drugs,parent,false);
        mContext = parent.getContext();
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        return new AllDrugsAdapter.ViewHolder(view,onItemsClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, final int position) {
        holder1 = holder;
//        try {
//          //  getImage(mItemInfo.get(position).getImage());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
        Locale locale = new Locale("en","KE");
        NumberFormat fmt = NumberFormat.getCurrencyInstance(locale);
        int price = (Integer.parseInt(mItemInfo.get(position).getDrugPrice()));
        holder.tvPrice.setText(fmt.format(price));
        holder.tvDescription.setText("Current amount: "+mItemInfo.get(position).getDrugAmount());
        holder.tvName.setText(mItemInfo.get(position).getDrugName());
        holder.mainLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pos = position;
                view.showContextMenu();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mItemInfo.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener,
            View.OnCreateContextMenuListener{
        private TextView tvName,tvDescription,tvPrice;
        private CardView mainLayout;
        AllDrugsAdapter.OnItemsClickListener onItemsClickListener;
        public ViewHolder(@NonNull View itemView,AllDrugsAdapter.OnItemsClickListener onItemsClickListener) {
            super(itemView);
            this.onItemsClickListener=onItemsClickListener;
            tvName=itemView.findViewById(R.id.titleMain);
            tvDescription=itemView.findViewById(R.id.descriptionMain);
            tvPrice=itemView.findViewById(R.id.priceMain);
            mainLayout=itemView.findViewById(R.id.card_view);
            itemView.setOnClickListener(this);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onClick(View view) {
            onItemsClickListener.onItemsClick(getAdapterPosition());
        }
        public void setItemClickListener(ItemClickListener itemClickListener){
            mItemClickListener = itemClickListener;
        }
        @Override
        public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
            contextMenu.setHeaderTitle("Select an action");
            MenuItem Update = contextMenu.add(Menu.NONE, 1, 1, "Update");
            MenuItem Delete = contextMenu.add(Menu.NONE, 2, 2, "Delete");
            Update.setOnMenuItemClickListener(onEditMenu);
            Delete.setOnMenuItemClickListener(onEditMenu);

        }

    }
    private final MenuItem.OnMenuItemClickListener onEditMenu = new MenuItem.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case 0:
                    //Do stuff
                    Toast.makeText(mContext, "View", Toast.LENGTH_LONG).show();
                    break;

                case 1:
                    //Do stuff
                    showUpdateDialog();
                    Toast.makeText(mContext, "Update", Toast.LENGTH_LONG).show();
                    break;
                case 2:
                    //Do stuff
                    deleteItem(mItemInfo.get(pos));
                    break;
            }
            return true;
        }
    };

    private void showUpdateDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(mContext);
        alertDialog.setTitle("Add new Drug");
        alertDialog.setMessage("Fill all the details.");

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                                    Toast.makeText(mContext,"Drug saved successfully",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(mContext,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });
                }
                else {
                    Toast.makeText(mContext,"Please fill all the details.",Toast.LENGTH_LONG).show();
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
    private void deleteItem(DrugDetails productDetails) {
        db.collection("AllDrugs").document(mItemInfo.get(pos).getDrugName())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "DocumentSnapshot successfully deleted!");
                        Toast.makeText(mContext, "successfully deleted!", Toast.LENGTH_LONG).show();
                        mItemInfo.remove(pos);
                        notifyDataSetChanged();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error deleting document", e);
                    }
                });
    }
    public interface OnItemsClickListener{
        void onItemsClick(int position);
    }
}