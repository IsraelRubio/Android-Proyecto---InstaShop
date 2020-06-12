package israelontanilla.es.instashop.Fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.Preference;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bumptech.glide.Glide;
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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Models.Category;
import Models.Product;
import israelontanilla.es.instashop.Adapters.SaleDataAdapter;
import israelontanilla.es.instashop.R;
import israelontanilla.es.instashop.UpdateUserRegisterActivity;

public class SaleFragment extends Fragment implements View.OnClickListener {

    private SharedPreferences preferences;
    private RecyclerView recyclerView;
    private ImageButton btnDialogProduct;
    private Button btnAddProduct;
    private Button btnCloseDialogAddProduct;
    private EditText editTextName;
    private static boolean testData = true;
    private EditText editTextPrice;
    private EditText editTextLocation;
    private ImageButton imageButtonAddImage;
    private AlertDialog dialog;
    private String name;
    private Double price;
    private String location;
    private boolean imageLoaded;
    private Uri mImageUri;
    FirebaseStorage storage;
    StorageReference storageReference;
    private String user_seller;
    private Category category;
    private Spinner spinnerCategories;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private View rootView;
    private boolean chooseImage;
    private SaleDataAdapter dataAdapter;
    private Activity activity;
    private String imgUrl;
    private final int CODE_IMG_GALLERY = 1;
    private final String SAMPLE_CROPPED_IMG_NAME = "SampleCropImg";

    public SaleFragment(Activity activity) {
        // Required empty public constructor
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_sale, container, false);
        chooseImage = false;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        imageLoaded = false;
        btnDialogProduct = rootView.findViewById(R.id.btnLoadDialogProduct);
        recyclerView = rootView.findViewById(R.id.recyclerViewSales);
        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(),3));
        storage = FirebaseStorage.getInstance();
        //recyclerView.setLayoutManager(new GridLayoutManager(getContext(),2));
        loadDataSale();

        btnDialogProduct.setOnClickListener(this);

        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onClick(View v) {
        if (v != null){
            if (v.getId() == R.id.btnLoadDialogProduct) {
                    loadDialogAddProduct();
            }
        }
    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setDataAndType(intent.getData(),"image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);

        System.out.println("Estas en el selector");

        startActivityForResult(Intent.createChooser(intent,"Select your product image"),1);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);



        if (requestCode == 1 && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null){

            mImageUri = data.getData();
            if (mImageUri != null){
                Glide.with(rootView.getContext())
                        .load(mImageUri)
                        .fitCenter()
                        .centerCrop()
                        .into(imageButtonAddImage);
                chooseImage = true;
            }
            //---
                // sube el producto con la anterior url de descarga, y ahora lo pide
            //---
            storageReference = storage.getReference("imagenes");
            final StorageReference photoReference = storageReference.child(mImageUri.getLastPathSegment());
            photoReference.putFile(mImageUri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return photoReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if (task.isSuccessful()){
                        Uri downloadUri = task.getResult();
                        imgUrl = downloadUri.toString();
                        imageLoaded = true;
                    }else{
                        Toast.makeText(rootView.getContext(),"Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
    }


    private void loadDataSale() {

        //----------------------------------------------------------------------------------------
        mDatabase.child("Products").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot dataSnapshotProduct) {

                // Saco el id del user actual y creo la query
                //----------------------------------------------------------
                String id = "";
                if(mAuth.getCurrentUser() != null)
                    id = mAuth.getCurrentUser().getUid();

                Query query = mDatabase.child("Users").child(id);
                //-----------------------------------------------------------

                // En la query para sacar el nombre del user creo el producto
                //---------------------------------------------------------------
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {

                        // Consulto todos los productos e ingreso a la lista solo los productos de este usuario
                        //-------------------------------------------------------------------
                        List<Product> productList = new ArrayList<>();
                        for (DataSnapshot data : dataSnapshotProduct.getChildren()){
                            Product p = data.getValue(Product.class);

                            String nick = dataSnapshotUser.child("nick").getValue().toString();

                            if (p.getSeller().equals(nick))
                                productList.add(p);
                        }

                        // Instancio el adaptador y se lo añado al recyclerview
                        dataAdapter = new SaleDataAdapter(productList);
                        recyclerView.setAdapter(dataAdapter);
                        //---------------------------------------------------------------------
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //----------------------------------------------------------
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        //-------------------------------------------------------------------------------------
    }

    private void createProduct(){
        System.out.println("imagen(createProduct) -> " +imgUrl);
        // Saco el id del user actual y creo la query
        //----------------------------------------------------------
        String id = "";
        if(mAuth.getCurrentUser() != null)
            id = mAuth.getCurrentUser().getUid();

        final Query query = mDatabase.child("Users").child(id);
        //-----------------------------------------------------------
            if (CheckData()) {
                // En la query para sacar el nombre del user, creo el producto
                //---------------------------------------------------------------
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull final DataSnapshot dataSnapshotSeller) {

                        //*********************************************************************************
                        Query q = mDatabase.child("Products").orderByChild("name");

                        q.addListenerForSingleValueEvent(new ValueEventListener() {
                            @SuppressLint("CommitPrefEdits")
                            @Override
                            public void onDataChange(@NonNull DataSnapshot dataSnapshotName) {
                                boolean isTrue = false;
                                if (dataSnapshotName.exists()){
                                    String nameCheck = editTextName.getText().toString();
                                    isTrue = true;
                                    for (DataSnapshot data : dataSnapshotName.getChildren()){
                                        String name = String.valueOf(data.child("name").getValue());
                                        if (nameCheck.toLowerCase().equals(name.toLowerCase()))
                                            isTrue = false;
                                    }
                                }else {
                                    isTrue = true;
                                }

                                if (isTrue) {
                                    //-------------------------------------------------------------------
                                    // Saco los datos del formulario e instancio el producto
                                    //--------------------------------------------------------
                                    name = editTextName.getText().toString();
                                    price = isDouble(editTextPrice.getText().toString()) ?
                                        Double.valueOf(editTextPrice.getText().toString()) :
                                        0;
                                    location = editTextLocation.getText().toString();
                                    user_seller = String.valueOf(dataSnapshotSeller.child("nick").getValue());
                                    category = (Category) spinnerCategories.getSelectedItem();

                                    Query queryMobile = mDatabase.child("Users").child(mAuth.getCurrentUser().getUid());
                                    queryMobile.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                            String email = String.valueOf(dataSnapshot.child("email").getValue());
                                            if (!chooseImage){
                                                imgUrl = "";
                                            }

                                            final Product product = new Product(name, price, imgUrl, location, user_seller, category.getCategory(), email);
                                            String productKey = mDatabase.child("Products").push().getKey();
                                            //---------------------------------------------------------

                                            // Añado el producto a la base de datos
                                            //--------------------------------------------------------------------------
                                            mDatabase.child("Products").child(productKey).setValue(product).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    if (task.isSuccessful()) {
                                                        dialog.dismiss();
                                                    } else {
                                                        Toast.makeText(rootView.getContext(), "La subida del producto ha sido un fracaso", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError databaseError) {

                                        }
                                    });



                                }else {
                                    editTextName.setError("This name is already exist!!");
                                }
                                //---------------------------------------------------------------------------
                                //--------------------------------------------------------------------
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {

                            }
                        });
                        //*********************************************************************************

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                //----------------------------------------------------------
            }else{
                Toast.makeText(rootView.getContext(), "There is a problem with the data entered", Toast.LENGTH_LONG).show();
            }
    }

    private boolean CheckData(){

        if (editTextName.getText().toString().trim().isEmpty() ||
                editTextLocation.getText().toString().trim().isEmpty() ||
                    editTextPrice.getText().toString().trim().isEmpty()){

            editTextName.setError("Fill in all the fields");
            editTextPrice.setError("Fill in all the fields");
            editTextLocation.setError("Fill in all the fields");
            testData = false;
        }else{
            if (isDouble(editTextPrice.getText().toString()))
                testData = true;
            else {
                editTextPrice.setError("This value not is numeric");
                testData = false;
            }
        }


        return testData;
    }

    static boolean isDouble(String cadena) {

        boolean resultado;

        try {
            Double.parseDouble(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }

    private void loadCategories(final View viewDialog){
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
                    spinnerCategories.setAdapter(adapterCategory);
                    spinnerCategories.setSelection(8);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void loadDialogAddProduct() {

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
        spinnerCategories = viewDialog.findViewById(R.id.spinnerCategory);

        loadCategories(viewDialog);

        imageButtonAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        btnAddProduct = viewDialog.findViewById(R.id.btnAddProduct);
        btnAddProduct.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!chooseImage){
                    createProduct();
                }

                if(chooseImage && !imageLoaded){
                    Toast.makeText(rootView.getContext(),"Wait for the image upload!!", Toast.LENGTH_SHORT).show();
                }else if (imageLoaded && chooseImage) {
                    createProduct();
                    imageLoaded = false;
                }

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
