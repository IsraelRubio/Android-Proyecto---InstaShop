package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class UpdateUserRegisterActivity extends AppCompatActivity {

    // vistas del registro
    private EditText mEditTextName;
    private EditText mEditTextNick;
    private EditText mEditTextPassword;
    private EditText mEditTextEmail;
    private EditText mEditTextMobile;
    private Button mButtonUpdate;

    // variables del registro
    private String name = "";
    private String nick = "";
    private String password = "";
    private String email = "";
    private String mobile = "";
    private int access = 0;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase; // Referencia a la base de datos

    private static String ENCRYPT_KEY="C@-l-@a-@v@-e-@1-@5@-8-@|@#~€¬!·$%%&//(()==??";
    private String passEncrypt = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_user_register);

        mEditTextName = (EditText) findViewById(R.id.updateEditTextName);
        mEditTextEmail = (EditText) findViewById(R.id.updateEditTextEmail);
        mEditTextMobile = (EditText) findViewById(R.id.updateEditTextMobile);
        mEditTextNick = (EditText) findViewById(R.id.updateEditTextNick);
        mEditTextPassword = (EditText) findViewById(R.id.updateEditTextPassword);
        mButtonUpdate = (Button) findViewById(R.id.btnUpdate);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        loadData();

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                name = mEditTextName.getText().toString();
                email = mEditTextEmail.getText().toString();
                nick = mEditTextNick.getText().toString();
                mobile = mEditTextMobile.getText().toString();
                password = mEditTextPassword.getText().toString();
                access = password.contains("11111") ? 1 : 2;

                if (name.isEmpty() | email.isEmpty() | nick.isEmpty() | mobile.isEmpty() | password.isEmpty()){
                    Toast.makeText(UpdateUserRegisterActivity.this, "All fields are required", Toast.LENGTH_LONG).show();
                }else if(password.length() < 6){
                    Toast.makeText(UpdateUserRegisterActivity.this, "The length of the name field must be greater than 6", Toast.LENGTH_LONG).show();
                }else if (name.length() > 50)
                {
                    Toast.makeText(UpdateUserRegisterActivity.this, "The length of the name field must be less than 50", Toast.LENGTH_LONG).show();
                }
                else if (nick.length() > 10)
                {
                    Toast.makeText(UpdateUserRegisterActivity.this, "The length of the nick field must be less than 10", Toast.LENGTH_LONG).show();
                }else{
                    updateUser();
                }
            }
        });
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

        // consigo el id del usuario que he creado
        String id = mAuth.getCurrentUser().getUid();

        mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isComplete()){
                    startActivity(new Intent(UpdateUserRegisterActivity.this, MainActivity.class));
                    finish();
                }else {
                    Toast.makeText(UpdateUserRegisterActivity.this, "Could not update fields correctly", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void loadData(){
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    passEncrypt = dataSnapshot.child("password").getValue().toString();
                    mEditTextName.setText(dataSnapshot.child("name").getValue().toString());
                    mEditTextEmail.setText(dataSnapshot.child("email").getValue().toString());
                    mEditTextMobile.setText(dataSnapshot.child("mobile").getValue().toString());
                    mEditTextNick.setText(dataSnapshot.child("nick").getValue().toString());
                    try {
                        mEditTextPassword.setText(decrypt(dataSnapshot.child("password").getValue().toString()));
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
