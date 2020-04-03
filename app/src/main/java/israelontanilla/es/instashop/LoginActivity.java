package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextPassword;
    private EditText mEditTextEmail;
    private Button mbtnLogin;
    private Button mbtnCheckInToLogin;
    private Button mbtnForgotPass;

    private String email;
    private String password;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    private static String ENCRYPT_KEY="C@-l-@a-@v@-e-@1-@5@-8-@|@#~€¬!·$%%&//(()==??";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mbtnLogin = (Button) findViewById(R.id.btnLogin);
        mbtnCheckInToLogin = (Button) findViewById(R.id.btnRegisterToLogin);
        mbtnForgotPass = (Button) findViewById(R.id.btnForgotPass);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEditTextEmail.getText().toString();
                password = mEditTextPassword.getText().toString();

                if (!email.isEmpty() || !password.isEmpty()){
                    loginUser();
                }else{
                    mEditTextEmail.setError("Text empty");
                    mEditTextPassword.setError("Text Empty");
                }
            }
        });

        mbtnCheckInToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
                //finish();
            }
        });

        mbtnForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ResetPasswordActivity.class));
            }
        });
    }

    private void loginUser(){
        try {
            password = decrypt(password);
        } catch (Exception e) {
            e.printStackTrace();
        }
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = mAuth.getCurrentUser();
                if (!user.isEmailVerified()){
                    mEditTextEmail.setError("This email not is verified");
                }else{
                    if (task.isComplete()){
                        startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
                        finish();
                    }else{
                        Toast.makeText(LoginActivity.this,"Could not login to account", Toast.LENGTH_LONG).show();
                        mEditTextEmail.setError("Check the data");
                        mEditTextPassword.setError("check the data");
                    }
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        if (mAuth.getCurrentUser() != null){
            startActivity(new Intent(LoginActivity.this, ProfileActivity.class));
            finish();
        }
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

    private SecretKeySpec generateKey(String encryptPass) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = encryptPass.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, "AES"); // convierto la secretkey en el algoritmo de encritacion AES

        return secretKey;
    }
}
