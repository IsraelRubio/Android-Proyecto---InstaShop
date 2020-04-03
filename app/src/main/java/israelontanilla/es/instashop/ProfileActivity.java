package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class ProfileActivity extends AppCompatActivity {

    //-------------------------------------------------------------------------------
    private Button mBtnSingOut;
    private Button mBtnDeleteProfile;
    private Button mBtnUpdateProfile;
    private Button mBtnAddImage;
    private TextView mTextviewName;
    private TextView mTextviewNick;
    private TextView mTextviewEmail;
    private TextView mTextviewPassword;
    private TextView mTextviewMobile;

    private ImageView mImageViewProfile;
    private Bitmap bitmap = null;
    private ProgressDialog mProgressDialog;
    private Uri mUri;

    private UploadTask uploadTask;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;
    private StorageReference mStorage;

    //---------------------------------------------------------------------------------

    //---------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mStorage = FirebaseStorage.getInstance().getReference().child("imageProfile");
        mProgressDialog = new ProgressDialog(ProfileActivity.this);

        mImageViewProfile = findViewById(R.id.imageProfile);
        mTextviewName = findViewById(R.id.viewName);
        mTextviewNick = findViewById(R.id.viewNick);
        mTextviewEmail = findViewById(R.id.viewEmail);
        mTextviewPassword = findViewById(R.id.viewPassword);
        mTextviewMobile = findViewById(R.id.viewMobile);
        mBtnSingOut = findViewById(R.id.btnSingOut);
        mBtnDeleteProfile = findViewById(R.id.btnDeleteProfile);
        mBtnUpdateProfile = findViewById(R.id.btnUpdateProfile);
        mBtnAddImage = findViewById(R.id.btnAddImage);

        getUserInfo();

        mBtnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dBuild = new AlertDialog.Builder(ProfileActivity.this);
                dBuild.setMessage("Do you want to log out of your user account?");
                dBuild.setTitle("Sing off");
                dBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mAuth.signOut();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        finish();
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
                AlertDialog.Builder mBuild = new AlertDialog.Builder(ProfileActivity.this);
                mBuild.setMessage("Do you want to delete your user account?");
                mBuild.setTitle("Delete user account");
                mBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteUser();
                        mAuth.signOut();
                        startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                        finish();
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
                AlertDialog.Builder nBuild = new AlertDialog.Builder(ProfileActivity.this);
                nBuild.setMessage("Do you want to update your user account?");
                nBuild.setTitle("Update user account");
                nBuild.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        startActivity(new Intent(ProfileActivity.this, UpdateUserRegisterActivity.class));
                        finish();
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
                AlertDialog.Builder iBuild = new AlertDialog.Builder(ProfileActivity.this);
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
    }
    //--------------------------------------------------------------------------------

    //--------------------------------------------------------------------------------
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // si hay respuesta de la galeria de imagenes y el resultado es ok, tenemos datos y podemos conseguirlos
        if (requestCode == 1 && resultCode == RESULT_OK && data != null && data.getData() != null){

            // cargo los datos del ProgressDialo
            mProgressDialog.setTitle("...uploading image");
            mProgressDialog.setMessage("Loading an image to your profile");
            mProgressDialog.setCancelable(false);
            mProgressDialog.show();

            // obtengo la imagen
            mUri = data.getData();

            try {
                Bitmap mBitmap = MediaStore.Images.Media.getBitmap(getContentResolver(),mUri);
                mImageViewProfile.setImageBitmap(mBitmap);
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
                    mTextviewPassword.setText("Password: " +password);
                    mTextviewNick.setText("Nick: " +nick);
                    mTextviewEmail.setText("Email: " +email);
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
