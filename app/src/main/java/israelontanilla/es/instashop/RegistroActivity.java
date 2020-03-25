package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistroActivity extends AppCompatActivity {

    // vistas del registro
    private EditText mEditTextName;
    private EditText mEditTextNick;
    private EditText mEditTextPassword;
    private EditText mEditTextEmail;
    private EditText mEditTextMobile;
    private Button mButtonRegister;

    // variables del registro
    private String name = "";
    private String nick = "";
    private String password = "";
    private String email = "";
    private String mobile = "";
    private int access = 0;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase; // Referencia a la base de datos

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        // cargo las vistas del modelo
        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextNick = (EditText) findViewById(R.id.editTextNick);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mEditTextMobile = (EditText) findViewById(R.id.editTextMobile);
        mButtonRegister = (Button) findViewById(R.id.btnRegister);

        mAuth = FirebaseAuth.getInstance(); // Conseguimos la autenticacion de firebase
        mDatabase = FirebaseDatabase.getInstance().getReference(); // Conseguimos la referencia a la base de

        // Registro de un nuevo usuario
        mButtonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // consigo los datos de los edittext
                name = mEditTextName.getText().toString();
                nick = mEditTextNick.getText().toString();
                password = mEditTextPassword.getText().toString();
                email = mEditTextEmail.getText().toString();
                mobile = mEditTextMobile.getText().toString();

                access = password.contains("11111") ? 1 : 2;

                // compruebo que los datos estan bien
                if (!name.isEmpty() || !nick.isEmpty() || !password.isEmpty() || !email.isEmpty() || !mobile.isEmpty())
                {
                    if (name.length() > 50)
                    {
                        Toast.makeText(RegistroActivity.this, "The length of the name field must be less than 50", Toast.LENGTH_LONG).show();
                    }
                    else if (nick.length() > 10)
                    {
                        Toast.makeText(RegistroActivity.this, "The length of the nick field must be less than 10", Toast.LENGTH_LONG).show();
                    }
                    else if (password.length() < 6)
                    {
                        Toast.makeText(RegistroActivity.this, "The length of the name field must be greater than 6", Toast.LENGTH_LONG).show();
                    }
                    else
                    {
                        RegiterUser();
                    }
                }
                else
                {
                    Toast.makeText(RegistroActivity.this, "All fields are required.", Toast.LENGTH_LONG).show();
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

                    // creo un map de string y object
                    Map<String, Object> map = new HashMap<>();

                    // Añado los campos al map
                    map.put("name", name);
                    map.put("email", email);
                    map.put("nick", nick);
                    map.put("password", password);
                    map.put("mobile", mobile);
                    map.put("access", access);

                    // consigo el id del usuario
                    String id = mAuth.getCurrentUser().getUid();

                    // Ingreso a la tabla users el map
                    mDatabase.child("Users").child(id).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task2) {
                            // Si envio los datos correctamente a la base de datos
                            if (task2.isComplete()){
                                // Abro el actvity hacia el que quiero ir
                                startActivity(new Intent(RegistroActivity.this,ProfileActivity.class));
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
}
