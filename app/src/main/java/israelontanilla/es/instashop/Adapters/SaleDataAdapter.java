package israelontanilla.es.instashop.Adapters;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityManagerCompat;
import androidx.core.app.ActivityOptionsCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.Continuation;
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
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Properties;

import Models.Category;
import Models.Product;
import israelontanilla.es.instashop.Fragments.SaleFragment;
import israelontanilla.es.instashop.MainActivity;
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
    String imgUrl;
    Context mContex;

    public SaleDataAdapter(List<Product> list) {
        productList = list;
    }

    @NonNull
    @Override
    public ViewHolderSale onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_sale,null,false);
        mContex = parent.getContext();
        return new ViewHolderSale(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolderSale holder, final int i) {

        final int position = i;
        Product p = productList.get(position);
        holder.setData(p);

        //---------------------------------------------------------

        //---------------------------------------------------------------------
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    mPosition = position;
                    notifyDataSetChanged();

                //----------------------------------------------------------------------
                if (mPosition == i){
                    //--------------------------------------------------------------------------------
                    final AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    mAuth = FirebaseAuth.getInstance();
                    mDatabase = FirebaseDatabase.getInstance().getReference();

                    final View viewDialog = LayoutInflater.from(view.getContext()).inflate(R.layout.dialog_dates_product,null, false);

                    System.out.println("MPOSITION -> " + mPosition);
                    System.out.println("i -> " +i);

                    builder.setView(viewDialog);
                    dialogData = builder.create();
                    dialogData.show();

                    final Product product = productList.get(i);
                    //---------------------------------------------------------------------------------

                    //-----------------------------------------------------------------------------
                    holder.textViewNameProductDialog = viewDialog.findViewById(R.id.textViewNameSaleDialog);
                    holder.textViewPriceProductDialog = viewDialog.findViewById(R.id.textViewPriceSaleDialog);
                    holder.textViewLocationProductDialog = viewDialog.findViewById(R.id.textViewLocationSaleDialog);
                    holder.imageViewDialogDataSale = viewDialog.findViewById(R.id.logoDialogDateProducts);
                    holder.textViewCategory = viewDialog.findViewById(R.id.textViewCategorySaleDialog);
                    holder.buttonDeleteProductDialog = viewDialog.findViewById(R.id.btnDeleteProduct);
                    holder.buttonUpdateProductDialog = viewDialog.findViewById(R.id.btnUpdateProduct);
                    holder.textViewNameProductDialog.setText(product.getName()!=null ? product.getName() : "Default");

                    if (product.getImage().trim().equals("")){
                        holder.imageViewDialogDataSale.setImageResource(R.mipmap.sale_empty_icon);
                    }else{
                        Glide.with(viewDialog.getContext())
                                .load(product.getImage())
                                .fitCenter()
                                .centerCrop()
                                .into(holder.imageViewDialogDataSale);
                    }

                    holder.textViewPriceProductDialog.setText(String.valueOf(product.getPrice())!=null ?
                            String.valueOf(product.getPrice()) :
                            "Error");
                    holder.textViewLocationProductDialog.setText(product.getLocation()!=null ? product.getLocation() : "Default");
                    holder.textViewCategory.setText(product.getCategory() != null ? product.getCategory() : "Default");

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
        });
        //----------------------------------------------------------------------

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

        loadCategories(viewUpdateDialog,holder);
        holder.buttonImageProduct = viewUpdateDialog.findViewById(R.id.imageButtonAddImageSale);
        getDataCurrenProduct(p, holder, viewUpdateDialog);

        if (p.getImage().trim().equals("")){
            holder.buttonImageProduct.setImageResource(R.mipmap.sale_empty_icon);
        }else{
            Glide.with(viewUpdateDialog.getContext())
                    .load(p.getImage())
                    .fitCenter()
                    .centerCrop()
                    .into(holder.buttonImageProduct);
        }

        loadCategory(holder,p);

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

    private void loadCategories(final View viewDialog, final ViewHolderSale holder){
        final List<Category> categoryList = new ArrayList<>();

        mDatabase.child("Categories").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot data : dataSnapshot.getChildren()){
                        String id = data.getKey();
                        String category = String.valueOf(data.child("category").getValue());

                        categoryList.add(new Category(id, category));
                    }

                    ArrayAdapter<Category> adapterCategory = new ArrayAdapter<>(viewDialog.getContext(), R.layout.support_simple_spinner_dropdown_item, categoryList);
                    holder.spinnerCategories.setAdapter(adapterCategory);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadCategory(ViewHolderSale holderSale, Product product){

        System.out.println("CATEGORY: -> " +product.getCategory());
        if (product.getCategory().contains("animals"))
            holderSale.spinnerCategories.setSelection(1);

        if (product.getCategory().contains("home"))
            holderSale.spinnerCategories.setSelection(2);

        if (product.getCategory().contains("clothes"))
            holderSale.spinnerCategories.setSelection(3);

        if (product.getCategory().contains("videogames"))
            holderSale.spinnerCategories.setSelection(4);

        if (product.getCategory().contains("motor"))
            holderSale.spinnerCategories.setSelection(5);

        if (product.getCategory().contains("tech"))
            holderSale.spinnerCategories.setSelection(6);

        if (product.getCategory().contains("book"))
            holderSale.spinnerCategories.setSelection(7);

        if (product.getCategory().contains("rent"))
            holderSale.spinnerCategories.setSelection(8);

        if (product.getCategory().contains("other"))
            holderSale.spinnerCategories.setSelection(9);
    }

    private void loadUpdateProduct(final Product product, final ViewHolderSale holderSale){

        mDatabase.child("Products").orderByChild("name").equalTo(product.getName()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotProduct) {
                Category category;
                category = (Category) holderSale.spinnerCategories.getSelectedItem();
                Product productUpdated = new Product(
                        holderSale.editTextNameUpdate.getText().toString(),
                        Double.parseDouble(isDouble(holderSale.editTextPriceUpdate.getText().toString()) ?
                                holderSale.editTextPriceUpdate.getText().toString() :
                                "0"),
                        product.getImage(),
                        holderSale.editTextLocationUpdate.getText().toString(),
                        product.getSeller(),
                        category.getCategory(),
                        product.getEmail()
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

    private void getDataCurrenProduct(Product p, ViewHolderSale holder, View viewDialog){
        holder.editTextLocationUpdate = viewDialog.findViewById(R.id.edittextSaleLocation);
        holder.editTextNameUpdate = viewDialog.findViewById(R.id.edittextSaleName);
        holder.editTextPriceUpdate = viewDialog.findViewById(R.id.edittextSalePrice);
        holder.buttonUpdateProduct = viewDialog.findViewById(R.id.btnAddProduct);
        holder.spinnerCategories = viewDialog.findViewById(R.id.spinnerCategory);
        holder.buttonCloseUpdate = viewDialog.findViewById(R.id.btnCloseDialogAddProduc);
        holder.editTextNameUpdate.setText(p.getName());
        holder.editTextPriceUpdate.setText(String.valueOf(p.getPrice()));
        holder.editTextLocationUpdate.setText(p.getLocation());


    }

    private static boolean isDouble(String cadena) {

        boolean resultado;

        try {
            Double.parseDouble(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }

    class ViewHolderSale extends RecyclerView.ViewHolder {

        TextView textViewName;
        TextView textViewPrice;
        TextView textViewCategory;
        CardView cardView;
        ImageView imageViewSale;
        private Spinner spinnerCategories;
        private TextView textViewNameProductDialog;
        private TextView textViewPriceProductDialog;
        private ImageView imageViewDialogDataSale;
        private TextView textViewLocationProductDialog;
        private ImageButton buttonImageProduct;
        private ImageButton buttonUpdateProductDialog;
        private ImageButton buttonDeleteProductDialog;
        private EditText editTextNameUpdate;
        private EditText editTextPriceUpdate;
        private EditText editTextLocationUpdate;
        private Button buttonUpdateProduct;
        private Button buttonCloseUpdate;

        ViewHolderSale(@NonNull View itemView) {
            super(itemView);
            textViewName = itemView.findViewById(R.id.textViewNameSale);
            textViewPrice = itemView.findViewById(R.id.textViewPriceSale);
            cardView = itemView.findViewById(R.id.item_list_sale);
            imageViewSale = itemView.findViewById(R.id.logoItemListSale);
        }

        void setData(Product p) {
            textViewName.setText(p.getName());
            textViewPrice.setText(String.valueOf(p.getPrice()));
            if (p.getImage().trim().equals("")){
                imageViewSale.setImageResource(R.mipmap.recyclerviewitemsale);
            }else{
                Glide.with(mContex)
                        .load(p.getImage())
                        .fitCenter()
                        .centerCrop()
                        .into(imageViewSale);
            }

        }
    }

}
