package israelontanilla.es.instashop.Fragments;



import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import de.hdodenhof.circleimageview.CircleImageView;
import israelontanilla.es.instashop.LoginActivity;
import israelontanilla.es.instashop.R;
import israelontanilla.es.instashop.UpdateUserRegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //-------------------------------------------------------------------------------
    private ImageButton mBtnSingOut;
    private ImageButton mBtnDeleteProfile;
    private ImageButton mBtnUpdateProfile;
    private CircleImageView mBtnAddImage;
    private TextView mTextviewName;
    private TextView mTextviewNick;
    private TextView mTextviewMobile;
    private String imageUrl;
    private Uri mImageUri;
    private Bitmap bitmap = null;
    private ProgressDialog mProgressDialog;
    private View rootView;
    private static int LOAD_IMAGE_RESULT = 1;
    private UploadTask uploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    //---------------------------------------------------------------------------------

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mProgressDialog = new ProgressDialog(rootView.getContext());

        mTextviewName = rootView.findViewById(R.id.viewName);
        mTextviewNick = rootView.findViewById(R.id.viewNick);
        mTextviewMobile = rootView.findViewById(R.id.viewMobile);
        mBtnSingOut = rootView.findViewById(R.id.btnSingOut);
        mBtnDeleteProfile = rootView.findViewById(R.id.btnDeleteProfile);
        mBtnUpdateProfile = rootView.findViewById(R.id.btnUpdateProfile);
        mBtnAddImage = rootView.findViewById(R.id.btnAddImage);

        getUserInfo();

        mBtnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dBuild = new AlertDialog.Builder(rootView.getContext());
                dBuild.setMessage("Do you want to log out of your user account?");
                dBuild.setTitle("Sing off");
                dBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(rootView.getContext(), LoginActivity.class));

                    }
                });

                dBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = dBuild.create();
                dialog.show();
            }
        });

        mBtnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuild = new AlertDialog.Builder(rootView.getContext());
                mBuild.setMessage("Do you want to delete your user account?");
                mBuild.setTitle("Delete user account");
                mBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProductsOfUser();
                        deleteUser();
                        mAuth.signOut();
                        startActivity(new Intent(rootView.getContext(), LoginActivity.class));

                    }
                });

                mBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = mBuild.create();
                dialog.show();
            }
        });

        mBtnUpdateProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder nBuild = new AlertDialog.Builder(rootView.getContext());
                nBuild.setMessage("Do you want to update your user account?");
                nBuild.setTitle("Update user account");
                nBuild.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(rootView.getContext(), UpdateUserRegisterActivity.class));
                        dialog.cancel();
                    }
                });

                nBuild.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog mDialog = nBuild.create();
                mDialog.show();
            }
        });


        return rootView;
    }

    //--------------------------------------------------------------------------------
    private void getUserInfo(){

        String id = "";
        if(mAuth.getCurrentUser() != null)
            id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();
                    String nick = dataSnapshot.child("nick").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String mobile = dataSnapshot.child("mobile").getValue().toString();
                    String url = String.valueOf(dataSnapshot.child("image").getValue());
                    if (!url.trim().equals("")){
                        Glide.with(rootView)
                                .load(url)
                                .fitCenter()
                                .centerCrop()
                                .into(mBtnAddImage);
                    }
                    mTextviewName.setText("Name: " +name);
                    mTextviewNick.setText("Nick: " +nick);
                    mTextviewMobile.setText("Mobile: " +mobile);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void deleteProductsOfUser(){
        String idUser = "";
        if(mAuth.getCurrentUser() != null)
            idUser = mAuth.getCurrentUser().getUid();


        mDatabase.child("Users").child(idUser).child("nick").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshotUSer) {
                String nick = String.valueOf(dataSnapshotUSer.getValue());

                Query query = mDatabase.child("Products").orderByChild("seller").equalTo(nick);

                query.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot data : dataSnapshot.getChildren()){
                            if (dataSnapshot.exists()){
                                String idProduct = data.getKey();
                                mDatabase.child("Products").child(idProduct).removeValue();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    //--------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------
    private void deleteUser(){
           String id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(id).removeValue();

        FirebaseUser user = mAuth.getCurrentUser();
        user.delete();

    }
    //-------------------------------------------------------------------------------
}
