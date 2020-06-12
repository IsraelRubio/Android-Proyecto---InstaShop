package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;
//----------------------------------
import java.util.Properties;
import javax.mail.Authenticator;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
//----------------------------------
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.security.MessageDigest;
import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.mail.MessagingException;
import javax.mail.PasswordAuthentication;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import Models.Product;
import Models.Update;
import israelontanilla.es.instashop.Adapters.SaleDataAdapter;

public class UpdateUserRegisterActivity extends AppCompatActivity {

    // vistas del registro
    private EditText mEditTextName;
    private EditText mEditTextNick;
    private EditText mEditTextPassword;
    private EditText mEditTextMobile;
    private EditText mEditTextPasswordRepeat;
    private ImageButton mImageButtonImageUpdate;
    String imageUrl;
    private List<String> listEmail;

    List<Product> lista;
    static boolean testEmail;

    // variables del registro
    private String upPass = "";
    private String upEmail = "";
    private String name = "";
    private String nick = "";
    private boolean productUpdated = false;
    private boolean pressButtonImage;
    private String password = "";
    private String email = "";
    private String mobile = "";
    private boolean boolImageUrl;
    private int access = 0;
    Session session = null;
    FirebaseAuth mAuth;
    DatabaseReference mDatabase; // Referencia a la base de datos
    FirebaseStorage storage;
    StorageReference storageReference;
    private Uri mImageUri;

    private static String ENCRYPT_KEY="C@-l-@a-@v@-e-@1-@5@-8-@|@#~€¬!·$%%&//(()==??";
    private String passEncrypt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_register);

        ImageButton mButtonUpdate;
        boolImageUrl = false;
        productUpdated = false;
        mEditTextName = findViewById(R.id.updateEditTextName);
        mEditTextMobile = findViewById(R.id.updateEditTextMobile);
        mEditTextNick = findViewById(R.id.updateEditTextNick);
        mEditTextPassword = findViewById(R.id.updateEditTextPassword);
        mEditTextPasswordRepeat = findViewById(R.id.repeatUpdateEditTextPassword);
        mImageButtonImageUpdate = findViewById(R.id.imageProfileEdit);
        mButtonUpdate = findViewById(R.id.btnUpdate);
        lista = new ArrayList<>();
        listEmail = new ArrayList<>();
        pressButtonImage = false;
        testEmail = true;
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();

        loadImage();
        loadData();

        mImageButtonImageUpdate.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                final AlertDialog.Builder nBuild = new AlertDialog.Builder(UpdateUserRegisterActivity.this);
                nBuild.setMessage("Do you want to update this image?");
                nBuild.setTitle("Update Image");
                nBuild.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface mDialog, int which) {
                        openFileChooser();
                        mDialog.dismiss();
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

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name =       mEditTextName.getText().toString();
                nick =       mEditTextNick.getText().toString();
                mobile =     mEditTextMobile.getText().toString();
                password =   mEditTextPassword.getText().toString();
                access =     password.contains("111111") ? 1 : 2;

                if (name.isEmpty() || nick.isEmpty() || mobile.isEmpty()){
                    Toast.makeText(UpdateUserRegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                }else if (name.length() > 50)
                {
                    mEditTextName.setError("The length of the name field must be less than 50");
                }
                else if (nick.length() > 10)
                {
                    mEditTextNick.setError("The length of the nick field must be less than 10");
                }else if(!mEditTextPassword.getText().toString().equals(mEditTextPasswordRepeat.getText().toString())){
                    mEditTextPassword.setError("Passwords are not equal");
                    mEditTextPasswordRepeat.setError("Passwords are not equal");
                }else{
                    if(mEditTextPassword.getText().length() > 0 && mEditTextPassword.getText().length() < 6){
                        mEditTextPassword.setError("The length of the name field must be greater than 6");
                    }else{
                        if (!password.equals(upPass)){ updatePassword(password, email, upPass);  }

                        if (mEditTextPassword.getText().toString().equals("")){
                            password = upPass;
                        }

                        if (pressButtonImage && !boolImageUrl) {
                            Toast.makeText(UpdateUserRegisterActivity.this, "Wait for the image upload!!", Toast.LENGTH_SHORT).show();
                        }else if(!pressButtonImage && !boolImageUrl){
                            updateProductsUser();
                        }else{
                            if (pressButtonImage && boolImageUrl){
                                updateProductsUser();
                            }
                        }

                    }
                }

            }
        });
    }

    private void loadImage(){

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setDataAndType(intent.getData(),"image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY,true);
        startActivityForResult(Intent.createChooser(intent, "Select a photo"),1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK
                && data != null && data.getData() != null){
            mImageUri = data.getData();
            if (mImageUri != null){
                Glide.with(this)
                        .load(mImageUri)
                        .fitCenter()
                        .centerCrop()
                        .into(mImageButtonImageUpdate);
                pressButtonImage = true;
            }
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
                        imageUrl = downloadUri.toString();
                        boolImageUrl = true;
                    }else{
                        Toast.makeText(UpdateUserRegisterActivity.this,"Upload failed", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    private void updateEmail(final String email, final String newEmail){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // Get auth credentials from the user for re-authentication
        AuthCredential credential = EmailAuthProvider
                .getCredential(newEmail, password); // Current Login Credentials \\
        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        System.out.println("reauthetication exitoso!!");
                        //Now change your email address \\
                        //----------------Code for Changing Email Address----------\\
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        user.updateEmail(email)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            System.out.println("actualización de email exitosa!!");
                                            //loadSendEmail_Email(email);
                                        }
                                    }
                                });
                        //----------------------------------------------------------\\
                    }
                });
    }

    private void updatePassword(final String newPass, final String email, final String pass){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        AuthCredential credential = EmailAuthProvider
                .getCredential(email, pass);

        // Prompt the user to re-provide their sign-in credentials
        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            user.updatePassword(newPass).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("TAG", "Password updated");
                                        loadSendEmail_Password(email,newPass);
                                        System.out.println("Existo de cambio de contraseña");
                                    } else {
                                        Log.d("TAG", "Error password not updated");
                                        System.out.println("Error de cambio de contraseña");
                                    }
                                }
                            });
                        } else {
                            Log.d("TAG", "Error auth failed");
                        }
                    }
                });
    }

    private boolean emailExist(final String email){

        mDatabase.child("Users").orderByChild("email").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listEmail.add(dataSnapshot.child("email").getValue().toString());

                System.out.println("LISTA DE EMAILS");
                for (String mEmail : listEmail){
                    System.out.println("LISTA " + mEmail);
                    if (email.equals(mEmail)) {
                        //mEditTextEmail.setError("This email already exist");
                        System.out.println("Repetido -> " + mEmail);
                        testEmail = false;
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

        System.out.println("testEmail -> " +testEmail);
        return testEmail;
    }

    private void loadSendEmail_Password(final String email, final String password){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //Construyo unas politicas
        StrictMode.setThreadPolicy(policy); // las agrego
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port", "465"); // socket para recibir respuesta de gmail
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // trabajar con socket con ssl
        properties.put("mail.smtp.auth", "true"); // indico que nos autenticamos

        try {
            // autentico los datos
            session = Session.getDefaultInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("speedshopcompany@gmail.com", "speedshopapp123456789");
                }
            });

            if (session != null){
                session.setDebug(true);
                String msn = "CONGRATULATIONS!! \nYour new password is " +password;
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("speedshopcompany@gmail.com")); // emisor
                message.setSubject("Updated your password"); // asunto
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // receptor
                message.setContent(msn,"text/html; charset=utf-8");
                Transport.send(message);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void loadSendEmail_Email(final String email){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build(); //Construyo unas politicas
        StrictMode.setThreadPolicy(policy); // las agrego
        Properties properties = new Properties();
        properties.put("mail.smtp.host", "smtp.googlemail.com");
        properties.put("mail.smtp.socketFactory.port", "465"); // socket para recibir respuesta de gmail
        properties.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory"); // trabajar con socket con ssl
        properties.put("mail.smtp.auth", "true"); // indico que nos autenticamos

        try {
            // autentico los datos
            session = Session.getDefaultInstance(properties, new Authenticator() {
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("speedshopcompany@gmail.com", "speedshopapp123456789");
                }
            });

            if (session != null){
                session.setDebug(true);
                String msn = "CONGRATULATIONS!! \nYour new email is " +email;
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress("speedshopcompany@gmail.com")); // emisor
                message.setSubject("Updated your email"); // asunto
                message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(email)); // receptor
                message.setContent(msn,"text/html; charset=utf-8");
                Transport.send(message);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private  void updateUser(){
        // creo un map
        Map<String, Object> map = new HashMap<>();

        // Añado los campos al map
        map.put("name", name);
        map.put("email", email);
        map.put("nick", nick);
        try {
            map.put("password", encrypt(password));
        } catch (Exception e) {
            e.printStackTrace();
        }
        map.put("mobile", mobile);
        map.put("access", access);

        map.put("image", imageUrl);


        // consigo el id del usuario que he creado
        String id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                        startActivity(new Intent(UpdateUserRegisterActivity.this, MainActivity.class));
                        finish();
                    }else {
                        Toast.makeText(UpdateUserRegisterActivity.this,"Wait for the image upload!!", Toast.LENGTH_SHORT).show();
                    }
            }
        });
    }

    private void updateProductsUser(){
        if (productUpdated){
            if (pressButtonImage && !boolImageUrl) {
                Toast.makeText(UpdateUserRegisterActivity.this, "Wait for the image upload!!", Toast.LENGTH_SHORT).show();
            }else {
                updateUser();
            }

            productUpdated = false;
        }else {
            //----------------------------------------------------------------------------------------
            mDatabase.child("Products").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull final DataSnapshot dataSnapshotProduct) {
                    // Saco el id del user actual y creo la query
                    //----------------------------------------------------------
                    String id = mAuth.getCurrentUser().getUid();

                    Query query = mDatabase.child("Users").child(id);
                    String nickQuery = mDatabase.child("Users").child(id).child("nick").toString();
                    System.out.println(nickQuery);
                    //-----------------------------------------------------------

                    // En la query para sacar el nombre del user creo el producto
                    //---------------------------------------------------------------
                    query.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshotUser) {

                            String nickQuery = String.valueOf(dataSnapshotUser.child("nick").getValue());

                            // Consulto todos los productos e ingreso a la lista solo los productos de este usuario
                            //-------------------------------------------------------------------
                            for (DataSnapshot data : dataSnapshotProduct.getChildren()) {
                                Product p = data.getValue(Product.class);

                                if (p != null) {
                                    if (p.getSeller().equals(nickQuery)) {
                                        if (data.getKey() != null) {
                                            String idProduct = data.getKey();
                                            mDatabase.child("Products").child(idProduct).removeValue();
                                            p.setSeller(nick);
                                            p.setEmail(email);
                                            lista.add(p);
                                        }
                                    }
                                }
                            }

                            for (Product product : lista) {
                                String productKey = mDatabase.child("Products").push().getKey();
                                //---------------------------------------------------------

                                // Añado el producto a la base de datos
                                //--------------------------------------------------------------------------
                                if (productKey != null)
                                    mDatabase.child("Products").child(productKey).setValue(product);
                            }

                            String id = mAuth.getCurrentUser().getUid();
                            String img = mDatabase.child("Users").child(id).child("nick").toString();

                            if (!pressButtonImage && img.trim().equals("")) {
                                imageUrl = "";
                                updateUser();
                            }

                            if (!pressButtonImage && !img.trim().equals("")){
                                updateUser();
                            }

                            if (pressButtonImage && !boolImageUrl) {
                                Toast.makeText(UpdateUserRegisterActivity.this, "Wait for the image upload!!", Toast.LENGTH_SHORT).show();
                                productUpdated = true;
                            }

                            if (pressButtonImage && boolImageUrl) {
                                boolImageUrl = false;
                                updateUser();
                            }


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

    private void loadData(){
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    passEncrypt = (String) dataSnapshot.child("password").getValue();
                    mEditTextName.setText((String)dataSnapshot.child("name").getValue());
                    email = (String)dataSnapshot.child("email").getValue();
                    mEditTextMobile.setText(dataSnapshot.child("mobile").getValue().toString());
                    mEditTextNick.setText((String)dataSnapshot.child("nick").getValue());
                    String url = String.valueOf(dataSnapshot.child("image").getValue());
                    if (!url.trim().equals("")){
                        Glide.with(UpdateUserRegisterActivity.this)
                                .load(url)
                                .fitCenter()
                                .centerCrop()
                                .into(mImageButtonImageUpdate);
                    }
                    try {
                        mEditTextPassword.setText("");
                        upPass = decrypt(String.valueOf(dataSnapshot.child("password").getValue()));
                        System.out.println("PASSWORD Load decrypt" +upPass);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private String decrypt(String pass) throws Exception{
        SecretKey secretKey = generateKey(ENCRYPT_KEY); // instacia de la llave secreta
        Cipher cipher = Cipher.getInstance("AES"); // instancio un objeto de cifrado de tipo AES
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte[] dataDecrypts = Base64.decode(pass,Base64.DEFAULT);
        byte[] dataDecryptsByte = cipher.doFinal(dataDecrypts);
        String dataDecryptsString = new String(dataDecryptsByte);

        return dataDecryptsString;
    }

    private String encrypt(String pass) throws Exception {
        SecretKey secretKey = generateKey(ENCRYPT_KEY); // instacia de la llave secreta
        Cipher cipher = Cipher.getInstance("AES"); // instancio un objeto de cifrado de tipo AES
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // inicializo el sistema de cifrado en modo encriptacion
        byte[] dataEncryptBytes = cipher.doFinal(pass.getBytes());
        String dataEncryptString = Base64.encodeToString(dataEncryptBytes, Base64.DEFAULT);

        return dataEncryptString;
    }

    private SecretKeySpec generateKey(String encryptPass) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = encryptPass.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES"); // convierto la secretkey en el algoritmo de encritacion AES

        return secretKey;
    }
}
