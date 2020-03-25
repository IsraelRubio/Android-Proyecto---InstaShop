package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    private EditText mEditTextPassword;
    private EditText mEditTextEmail;
    private Button mbtnLogin;
    private Button mbtnCheckInToLogin;

    private String email;
    private String password;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mbtnLogin = (Button) findViewById(R.id.btnLogin);
        mbtnCheckInToLogin = (Button) findViewById(R.id.btnRegisterToLogin);

        mAuth = FirebaseAuth.getInstance();

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEditTextEmail.getText().toString();
                password = mEditTextPassword.getText().toString();

                if (!email.isEmpty() || !password.isEmpty()){
                    loginUser();
                }else{
                    Toast.makeText(LoginActivity.this,"Text empty!!",Toast.LENGTH_LONG).show();
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
    }

    private void loginUser(){
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isComplete()){
                    startActivity(new Intent(LoginActivity.this,ProfileActivity.class));
                    finish();
                }else{
                    Toast.makeText(LoginActivity.this,"Could not login to account, check the data", Toast.LENGTH_LONG).show();
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
}
