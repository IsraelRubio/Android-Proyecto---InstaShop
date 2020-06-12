package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import Models.User;

public class RegistroActivity extends AppCompatActivity {

    // vistas del registro
    private EditText mEditTextName;
    private EditText mEditTextNick;
    private EditText mEditTextPassword;
    private EditText mEditTextPasswordRepeat;
    private EditText mEditTextEmail;
    private EditText mEditTextMobile;
    private ImageButton mButtonRegister;

    // variables del registro
    private String name = "";
    private String nick = "";
    private String password = "";
    private String email = "";
    private int mobile = 0;
    private int access = 0;
    private String image = "";

    FirebaseAuth mAuth;
    DatabaseReference mDatabase; // Referencia a la base de datos
    DatabaseReference nDatabase;

    private static String ENCRYPT_KEY="C@-l-@a-@v@-e-@1-@5@-8-@|@#~€¬!·$%%&//(()==??";
    private static final  String AES = "AES";

    List<String> listNick = new ArrayList<>();
    List<String> listEmail = new ArrayList<>();
    private static boolean testData = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // cargo las vistas del modelo
        mEditTextName = findViewById(R.id.editTextName);
        mEditTextEmail = findViewById(R.id.editTextEmail);
        mEditTextNick = findViewById(R.id.editTextNick);
        mEditTextPassword = findViewById(R.id.editTextPassword);
        mEditTextMobile = findViewById(R.id.editTextMobile);
        mButtonRegister = findViewById(R.id.btnRegister);
        mEditTextPasswordRepeat = findViewById(R.id.repeatRegisterEditTextPassword);

        mAuth = FirebaseAuth.getInstance(); // Conseguimos la autenticacion de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Conseguimos la referencia a la base de datos
        nDatabase = FirebaseDatabase.getInstance().getReference();

        // Registro de un nuevo usuario
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // consigo los datos de los edittext
                name = mEditTextName.getText().toString();
                nick = mEditTextNick.getText().toString();
                password = mEditTextPassword.getText().toString();
                email = mEditTextEmail.getText().toString();
                mobile = isNumeric(mEditTextMobile.getText().toString()) ? Integer.parseInt(mEditTextMobile.getText().toString()) : 0;


                access = password.contains("11111") ? 1 : 2;

                if (validateData()){
                    RegiterUser();
                }else {
                    Toast.makeText(RegistroActivity.this,"The register failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void RegiterUser()
    {
        // Con la autenticacion cargo el user y la password
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                // Si la autenticacion de los datos es correcta
                if (task.isComplete()){

                    // creo un map
                    final Map<String, Object> map = new HashMap<>();

                    // Añado los campos al map
                    map.put("name", name);
                    map.put("email", email);
                    map.put("nick", nick);
                    try {
                        map.put("password", encrypt(password));
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    map.put("mobile", mobile);
                    map.put("access", access);
                    map.put("image", "");

                    // consigo el id del usuario que he creado
                    String id = mAuth.getCurrentUser().getUid();

                    // Ingreso a la tabla users el map
                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            // Si envio los datos correctamente a la base de datos
                            if (task2.isComplete()){
                                // Envio al usuario un correo de confirmacion de registro
                                FirebaseUser user = mAuth.getCurrentUser();
                                user.sendEmailVerification();

                                // Abro el actvity hacia el que quiero ir
                                startActivity(new Intent(RegistroActivity.this,MainActivity.class));
                                finish(); // cierro este activity para que el usuario que se ha registrado, no vuelva
                            }else {
                                Toast.makeText(RegistroActivity.this,"The data has not been saved correctly", Toast.LENGTH_LONG);
                            }
                        }
                    });

                }else {
                    Toast.makeText(RegistroActivity.this, "Could not register user.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private String encrypt(String pass) throws Exception {
        SecretKey secretKey = generateKey(ENCRYPT_KEY); // instacia de la llave secreta
        Cipher cipher = Cipher.getInstance(AES); // instancio un objeto de cifrado de tipo AES
        cipher.init(Cipher.ENCRYPT_MODE, secretKey); // inicializo el sistema de cifrado en modo encriptacion
        byte[] dataEncryptBytes = cipher.doFinal(pass.getBytes());
        String dataEncryptString = Base64.encodeToString(dataEncryptBytes, Base64.DEFAULT);

        return dataEncryptString;
    }

    private SecretKeySpec generateKey(String encryptPass) throws Exception{
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = encryptPass.getBytes("UTF-8");
        key = sha.digest(key);
        SecretKeySpec secretKey = new SecretKeySpec(key, AES); // convierto la secretkey en el algoritmo de encritacion AES

        return secretKey;
    }

    private boolean validateData(){
        nDatabase.child("Users").orderByChild("nick").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                listNick.add(String.valueOf(dataSnapshot.child("nick").getValue()));
                listEmail.add(String.valueOf(dataSnapshot.child("email").getValue()));

                for (String mNick : listNick){
                    if (nick.equals(mNick)) {
                        Toast.makeText(RegistroActivity.this,"This nick already exist", Toast.LENGTH_SHORT).show();
                        testData = false;
                    }else if (name.trim().equals("") || nick.trim().equals("") || password.trim().equals("") || email.trim().equals("") || mobile < 111111111 || mobile > 999999999)
                    {
                        testData = false;
                        Toast.makeText(RegistroActivity.this,"All fields are required", Toast.LENGTH_SHORT).show();
                    }
                    else if (mobile < 600000000 || mobile > 799999999){
                        mEditTextMobile.setError("This mobile not is valid");
                        testData = false;
                    }else if(!mEditTextPassword.getText().toString().equals(mEditTextPasswordRepeat.getText().toString())){
                        mEditTextPassword.setError("Passwords are not equal");
                        mEditTextPasswordRepeat.setError("Passwords are not equal");
                        testData = false;
                    }else{
                        testData = true;
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

        return testData;
    }

    public static boolean isNumeric(String cadena) {

        boolean resultado;

        try {
            Integer.parseInt(cadena);
            resultado = true;
        } catch (NumberFormatException excepcion) {
            resultado = false;
        }

        return resultado;
    }
}
