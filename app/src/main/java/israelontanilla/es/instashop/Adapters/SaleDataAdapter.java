package israelontanilla.es.instashop.Adapters;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import Models.Product;
import israelontanilla.es.instashop.R;
import israelontanilla.es.instashop.UpdateUserRegisterActivity;

public class SaleDataAdapter extends RecyclerView.Adapter<SaleDataAdapter.ViewHolderSale> {

    private List<Product> productList;
    private AlertDialog dialogData;
    int mPosition = 0;
    View view;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    String key;

    public SaleDataAdapter(List<Product> list) {
        productList = list;
    }

    @NonNull
    @Override
    public ViewHolderSale onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sale,null,false);
        return new ViewHolderSale(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSale holder, int i) {

        final int position = i;

        Product p = productList.get(position);
        holder.setData(p);
        //---------------------------------------------------------

        //---------------------------------------------------------------------
        holder.linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(),"Mensaje desde onclick del holder", Toast.LENGTH_SHORT).show();
                mPosition = position;
                notifyDataSetChanged();
            }
        });
        //----------------------------------------------------------------------

        //----------------------------------------------------------------------
        if (mPosition == i){

            //--------------------------------------------------------------------------------
            final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
            mAuth = FirebaseAuth.getInstance();
            mDatabase = FirebaseDatabase.getInstance().getReference();

            final View viewDialog = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_dates_product,null, false);

            builder.setView(viewDialog);
            dialogData = builder.create();
            dialogData.show();

            final Product product = productList.get(i);
            //---------------------------------------------------------------------------------

                //-----------------------------------------------------------------------------
                holder.textViewNameProductDialog = viewDialog.findViewById(R.id.textViewNameSaleDialog);
                holder.textViewPriceProductDialog = viewDialog.findViewById(R.id.textViewPriceSaleDialog);
                holder.textViewLocationProductDialog = viewDialog.findViewById(R.id.textViewLocationSaleDialog);
                holder.buttonDeleteProductDialog = viewDialog.findViewById(R.id.btnDeleteProduct);
                holder.buttonUpdateProductDialog = viewDialog.findViewById(R.id.btnUpdateProduct);

                holder.textViewNameProductDialog.setText(product.getName()!=null ? product.getName() : "Default");
                holder.textViewPriceProductDialog.setText(product.getPrice()!=null ? product.getPrice() : "0");
                holder.textViewLocationProductDialog.setText(product.getLocation()!=null ? product.getLocation() : "Default");

                    //------------------------------------------
                    holder.buttonDeleteProductDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder nBuild = new AlertDialog.Builder(viewDialog.getContext());
                            nBuild.setMessage("Do you want to delete this product?");
                            nBuild.setTitle("Delete product");
                            nBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface mDialog, int which) {
                                    removeProduct(product);
                                    mDialog.dismiss();
                                    dialogData.cancel();
                                }
                            });

                            nBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface mDialog, int which) {
                                    mDialog.cancel();
                                }
                            });

                            AlertDialog mDialog = nBuild.create();
                            mDialog.show();
                        }
                    });
                    //-----------------------------------------

                    //--------------------------------------------------------------------
                    holder.buttonUpdateProductDialog.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final AlertDialog.Builder builderUpdate = new AlertDialog.Builder(viewDialog.getContext());
                            builderUpdate.setMessage("Do you want to update this product");
                            builderUpdate.setTitle("Update product");
                            builderUpdate.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    updateProduct(product, holder, dialogData);
                                    dialog.dismiss();
                                    dialog.cancel();
                                }
                            });

                            builderUpdate.setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.cancel();
                                }
                            });

                            AlertDialog dialogUpdate = builderUpdate.create();
                            dialogUpdate.show();
                        }
                    });
                    //---------------------------------------------------------------------------
            //------------------------------------------------------------------------------
        }
        //----------------------------------------------------------------------------------
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    void removeProduct(Product p){
        //----------------------------------------------------------------------------------------
        mDatabase.child("Products").orderByChild("name").equalTo(p.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotProduct) {

                        for (DataSnapshot data : dataSnapshotProduct.getChildren()){
                            String productKey = data.getKey();

                            mDatabase.child("Products").child(productKey).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(view.getContext(),"Delete successfull", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(view.getContext(),"Delete failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                //----------------------------------------------------------
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //-------------------------------------------------------------------------------------
    }

    void updateProduct(final Product p, final ViewHolderSale holder, final DialogInterface dialogData){
        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
        final View viewUpdateDialog = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_add_product,null,false);

        builder.setView(viewUpdateDialog);
        final AlertDialog dialogUpdate = builder.create();
        dialogUpdate.show();

        getDataCurrenProduct(p, holder, viewUpdateDialog);

        holder.buttonUpdateProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadUpdateProduct(p, holder);
                dialogUpdate.dismiss();
                dialogData.dismiss();
            }
        });

        holder.buttonCloseUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogUpdate.dismiss();
                dialogUpdate.cancel();
            }
        });

    }

    private void loadUpdateProduct(final Product product, final ViewHolderSale holderSale){

        mDatabase.child("Products").orderByChild("name").equalTo(product.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotProduct) {
                Product productUpdated = new Product(
                        holderSale.editTextNameUpdate.getText().toString(),
                        holderSale.editTextPriceUpdate.getText().toString(),
                        "image",
                        holderSale.editTextLocationUpdate.getText().toString(),
                        product.getSeller(),
                        "category",
                        product.getMobile()
                );

                for (DataSnapshot data : dataSnapshotProduct.getChildren()){
                    String productKey = data.getKey();

                    mDatabase.child("Products").child(productKey).setValue(productUpdated).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            dialogData.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    void getDataCurrenProduct(Product p, ViewHolderSale holder, View viewDialog){
        holder.editTextLocationUpdate = viewDialog.findViewById(R.id.edittextSaleLocation);
        holder.editTextNameUpdate = viewDialog.findViewById(R.id.edittextSaleName);
        holder.editTextPriceUpdate = viewDialog.findViewById(R.id.edittextSalePrice);
        holder.buttonUpdateProduct = viewDialog.findViewById(R.id.btnAddProduct);
        holder.buttonCloseUpdate =viewDialog.findViewById(R.id.btnCloseDialogAddProduc);

        holder.editTextNameUpdate.setText(p.getName());
        holder.editTextPriceUpdate.setText(p.getPrice());
        holder.editTextLocationUpdate.setText(p.getLocation());
    }


    public class ViewHolderSale extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPrice;
        LinearLayout linearLayout;
        private TextView textViewNameProductDialog;
        private TextView textViewPriceProductDialog;
        private TextView textViewLocationProductDialog;
        private Button buttonUpdateProductDialog;
        private Button buttonDeleteProductDialog;
        private EditText editTextNameUpdate;
        private EditText editTextPriceUpdate;
        private EditText editTextLocationUpdate;
        private Button buttonUpdateProduct;
        private Button buttonCloseUpdate;

        public ViewHolderSale(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewNameSale);
            textViewPrice = itemView.findViewById(R.id.textViewPriceSale);
            linearLayout = itemView.findViewById(R.id.item_list_sale);
        }

        public void setData(Product p) {
            textViewName.setText(p.getName());
            textViewPrice.setText(p.getPrice());
        }
    }

}
