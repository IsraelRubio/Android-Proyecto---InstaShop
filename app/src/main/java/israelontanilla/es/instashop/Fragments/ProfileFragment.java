package israelontanilla.es.instashop.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;

import israelontanilla.es.instashop.LoginActivity;
import israelontanilla.es.instashop.MainActivity;
import israelontanilla.es.instashop.R;
import israelontanilla.es.instashop.UpdateUserRegisterActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    //-------------------------------------------------------------------------------
    private Button mBtnSingOut;
    private Button mBtnDeleteProfile;
    private Button mBtnUpdateProfile;
    private ImageButton mBtnAddImage;
    private TextView mTextviewName;
    private TextView mTextviewNick;
    private TextView mTextviewMobile;

    private Bitmap bitmap = null;
    private ProgressDialog mProgressDialog;
    private Uri mUri;

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
        final View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference().child("imageProfile");
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

        mBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder iBuild = new AlertDialog.Builder(rootView.getContext());
                iBuild.setTitle("Add image");
                iBuild.setMessage("do you want add image in your user account?");
                iBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //---------------------------------------------------------------------------
                        Intent intent = new Intent();
                        intent.setType("image/*"); // cualquier tipo de imagen
                        intent.setAction(Intent.ACTION_GET_CONTENT); // Abrir galeria

                        // devuelve un resultado en formato uri que convertire a bitmap
                        // le asigno al Uri el dato/archivo seleccionado de en este caso la galeria
                        startActivityForResult(Intent.createChooser(intent,"Select an image"), 1);

                        if (mUri != null){
                            Log.i("TAG","Mi uri no es null");
                            final StorageReference imagenRef = mStorage.child(mUri.getLastPathSegment());

                            uploadTask = imagenRef.putFile(mUri);


                        }
                        //---------------------------------------------------------------------------

                    }
                });

                iBuild.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                AlertDialog dialog = iBuild.create();
                dialog.show();
            }
        });

        return rootView;
    }



    //--------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // si hay respuesta de la galeria de imagenes y el resultado es ok, tenemos datos y podemos conseguirlos
        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){

            // cargo los datos del ProgressDialo
            mProgressDialog.setTitle("...uploading image");
            mProgressDialog.setMessage("Loading an image to your profile");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            // obtengo la imagen
            mUri = data.getData();

            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),mUri);
                mBtnAddImage.setImageBitmap(mBitmap);
                mProgressDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }
    //--------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------
    private void getUserInfo(){
        String id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    String name = dataSnapshot.child("name").getValue().toString();
                    String password = dataSnapshot.child("password").getValue().toString();
                    String nick = dataSnapshot.child("nick").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String mobile = dataSnapshot.child("mobile").getValue().toString();

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

    private void deleteUser(){
        String id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(id).removeValue();
        mAuth.getCurrentUser().delete();
    }
    //-------------------------------------------------------------------------------
}
