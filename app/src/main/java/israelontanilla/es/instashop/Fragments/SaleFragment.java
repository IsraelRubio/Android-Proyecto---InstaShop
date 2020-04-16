package israelontanilla.es.instashop.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Product;
import israelontanilla.es.instashop.Adapters.SaleDataAdapter;
import israelontanilla.es.instashop.R;

public class SaleFragment extends Fragment implements View.OnClickListener {

    RecyclerView recyclerView;
    ImageButton btnDialogProduct;
    Button btnAddProduct;
    Button btnCloseDialogAddProduct;
    EditText editTextName;
    EditText editTextPrice;
    EditText editTextLocation;
    ImageButton imageButtonAddImage;

    AlertDialog dialog;
    List<Product> productList;
    List<String> nameList;
    boolean testDataSale = true;

    private String id;
    private String name;
    private double price;
    private String image;
    private String location;
    private String user_seller;
    private String category;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;
    View rootView;
    SaleDataAdapter dataAdapter;

    public SaleFragment() {
        // Required empty public constructor

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sale, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        btnDialogProduct = rootView.findViewById(R.id.btnLoadDialogProduct);
        recyclerView = rootView.findViewById(R.id.recyclerViewSales);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(),2));

        productList = new ArrayList<>();

        loadDataSale();

        dataAdapter = new SaleDataAdapter(productList);
        recyclerView.setAdapter(dataAdapter);

        btnDialogProduct.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    private void loadDataSale() {
        String idProduct = mDatabase.child("Products").getKey();
        String idUser = mAuth.getCurrentUser().getUid();
        mDatabase.child("Products").child(idUser).orderByChild("name").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String mName = dataSnapshot.child("name").getValue().toString();
                String mPrice = dataSnapshot.child("price").getValue().toString();
                String mImage = "image";
                String mCategory = "mCategory";
                String mUser_seller = "seller";
                String mLocation = dataSnapshot.child("location").getValue().toString();

                System.out.println("*********************************************************************************************************");
                System.out.println("Name " +mName);
                System.out.println("Price " +mPrice);
                System.out.println("Imagen " +mImage);
                System.out.println("Category " +mCategory);
                System.out.println("User " +mUser_seller);
                System.out.println("Location " +mLocation);
                System.out.println("************************************************************************************************************");

                productList.add(new Product(
                        mName,
                        Double.valueOf(mPrice),
                        mImage,
                        mLocation,
                        mUser_seller,
                        mCategory
                ));
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    boolean testNameSale(){

        mDatabase.child("Users").child(mAuth.getCurrentUser().getUid()).child("Products").orderByChild("name").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                nameList.add(dataSnapshot.child("name").getValue().toString());

                System.out.println("LISTA DE NICKS");
                for (String n : nameList){
                    System.out.println("LISTA" + name);
                    if (name.equals(n)){
                        //mEditTextNick.setError("This nick already exist");
                        System.out.println("Repetido -> " +n);
                        testDataSale = false;
                    }else{
                        testDataSale = true;
                    }
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return testDataSale;
    }

    void createProduct(){
        String idUser = mAuth.getCurrentUser().getUid();


        name = editTextName.getText().toString();
        price = Double.valueOf(editTextPrice.getText().toString());
        image = "image";
        location = editTextLocation.getText().toString();
        user_seller = "user seller";
        category = "mi category";

        final Map<String, Object> map = new HashMap<>();

        map.put("name", name);
        map.put("price", price);
        map.put("image", image);
        map.put("location", location);
        map.put("user_seller", user_seller);
        map.put("category",category);

        mDatabase.child("Products").child(idUser).child(name).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    dialog.dismiss();
                }else{
                    Toast.makeText(rootView.getContext(), "La subida del producto ha sido un fracaso", Toast.LENGTH_SHORT);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
            switch (v.getId()) {
                case R.id.btnLoadDialogProduct:
                    loadDialogAddProduct();
                    break;

            }
    }

    void loadDialogAddProduct() {
        AlertDialog.Builder builder = new AlertDialog.Builder(rootView.getContext(),R.style.Theme_Dialog_Translucent);

        LayoutInflater inflater = getLayoutInflater();

        View viewDialog = inflater.inflate(R.layout.dialog_add_product, null);

        builder.setView(viewDialog);

        dialog = builder.create();

        dialog.show();

        editTextName = viewDialog.findViewById(R.id.edittextSaleName);
        editTextLocation = viewDialog.findViewById(R.id.edittextSaleLocation);
        editTextPrice = viewDialog.findViewById(R.id.edittextSalePrice);
        imageButtonAddImage = viewDialog.findViewById(R.id.imageButtonAddImageSale);

        btnAddProduct = viewDialog.findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProduct();
            }
        });

        btnCloseDialogAddProduct = viewDialog.findViewById(R.id.btnCloseDialogAddProduc);
        btnCloseDialogAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
    }
}
