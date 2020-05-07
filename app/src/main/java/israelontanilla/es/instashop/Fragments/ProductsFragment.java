package israelontanilla.es.instashop.Fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import Models.Product;
import israelontanilla.es.instashop.Adapters.ProductsDataAdapter;
import israelontanilla.es.instashop.Adapters.SaleDataAdapter;
import israelontanilla.es.instashop.R;

public class ProductsFragment extends Fragment {

    private RecyclerView recyclerView;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private EditText editTextSearch;

    public ProductsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView;
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_products, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        recyclerView = rootView.findViewById(R.id.recyclerViewProducts);
        editTextSearch = rootView.findViewById(R.id.editTextSearch);

        recyclerView.setLayoutManager(new GridLayoutManager(rootView.getContext(),3));

        loadDataRecyclerView("");

            editTextSearch.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    loadDataRecyclerView(s.toString());
                }
            });

        return rootView;
    }

    private void loadDataRecyclerView(final String s) {
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

                            if (!s.trim().equals("")){
                                if (p.getName().toLowerCase().contains(s.toLowerCase()) ||
                                        p.getSeller().toLowerCase().contains(s.toLowerCase()) ||
                                        p.getCategory().toLowerCase().contains(s.toLowerCase()) ||
                                        p.getLocation().toLowerCase().contains(s.toLowerCase()) ||
                                        p.getPrice().toLowerCase().contains(s.toLowerCase())){

                                    if (!p.getSeller().equals(nick))
                                        productList.add(p);

                                }
                            }else{
                                if (!p.getSeller().equals(nick))
                                    productList.add(p);
                            }

                        }

                        // Instancio el adaptador y se lo añado al recyclerview
                        ProductsDataAdapter dataAdapter = new ProductsDataAdapter(productList);
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

}
