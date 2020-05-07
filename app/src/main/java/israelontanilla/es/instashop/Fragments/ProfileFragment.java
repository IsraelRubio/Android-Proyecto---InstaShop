package israelontanilla.es.instashop.Fragments;


import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import java.io.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
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
    private ImageButton mBtnSingOut;
    private ImageButton mBtnDeleteProfile;
    private ImageButton mBtnUpdateProfile;
    private ImageButton mBtnAddImage;
    private TextView mTextviewName;
    private TextView mTextviewNick;
    private TextView mTextviewMobile;
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
        mStorage = FirebaseStorage.getInstance().getReference("Images");
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

        mBtnAddImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder iBuild = new AlertDialog.Builder(rootView.getContext());
                iBuild.setTitle("Add image");
                iBuild.setMessage("do you want update image in your user account?");
                iBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        //---------------------------------------------------------------------------
                        // Seleccionar archivo
                        Filechooser();
                        System.out.println("FILECHOOSER - HA SALIDO");
                        Fileuploader();
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

    private void Filechooser(){
        System.out.println("FILECHOOSER - HA ENTRADO");
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);

            startActivityForResult(intent, LOAD_IMAGE_RESULT);
    }

    private String getExtension(Uri uri){
        System.out.println("URI on GetExtension -> " +uri);

        ContentResolver contentResolver = rootView.getContext().getContentResolver();
        System.out.println("CONTENTRESOLVER -> " + contentResolver);

        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        System.out.println("MIMETYPEMAP -> " +mimeTypeMap);

        String mime = mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
        System.out.println("MIMESTRING -> " +mime);

        // Return file Extension
        return mime;
    }

    private void Fileuploader(){
        StorageReference Ref = mStorage.child(System.currentTimeMillis()+"."+getExtension(mImageUri));

        Ref.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        // Get a URL to the uploaded content
                        //Uri downloadUrl = taskSnapshot.getDownloadUrl();
                        Toast.makeText(rootView.getContext(), "Image uploaded successListener", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                        // ...
                    }
                });
    }


    //--------------------------------------------------------------------------------
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // si hay respuesta de la galeria de imagenes y el resultado es ok, tenemos datos y podemos conseguirlos
        //if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null){

            // obtengo la imagen y la seteo
            System.out.println("ActivityResult - Ha entrado");
            mImageUri = data.getData();
            System.out.println("URI on ActivityResult -> " +mImageUri);
            mBtnAddImage.setImageURI(mImageUri);

            /*// cargo los datos del ProgressDialo
            mProgressDialog.setTitle("...uploading image");
            mProgressDialog.setMessage("Loading an image to your profile");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(),mUri);
                mBtnAddImage.setImageBitmap(mBitmap);
                mProgressDialog.dismiss();
            } catch (IOException e) {
                e.printStackTrace();
            }*/

        }

    //}
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

    private void deleteProductsOfUser(){
        String idUser = mAuth.getCurrentUser().getUid();

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
