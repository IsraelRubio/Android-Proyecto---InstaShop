package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ResetPasswordActivity extends AppCompatActivity {

    private ImageButton mbtnResetPassword;
    private EditText mEditTextEmail;

    String email;

    FirebaseAuth mAuth;
    ProgressDialog mDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        mbtnResetPassword = findViewById(R.id.btnResetPassword);
        mEditTextEmail = findViewById(R.id.editTextEmailReset);

        mAuth = FirebaseAuth.getInstance();
        mDialog = new ProgressDialog(ResetPasswordActivity.this);

        mbtnResetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = mEditTextEmail.getText().toString();

                if (mEditTextEmail.getText().toString().isEmpty()){
                    mEditTextEmail.setError("Add your email");
                }else{
                    mDialog.setMessage("Wait a moment please");
                    mDialog.setCanceledOnTouchOutside(false);
                    mDialog.show();
                    resetPassword();
                }
            }
        });
    }

    private void resetPassword(){
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(ResetPasswordActivity.this,"An email has been sent to reset the password", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(ResetPasswordActivity.this, LoginActivity.class));
                    finish();
                }else{
                    mEditTextEmail.setError("Email not found");
                }
                mDialog.dismiss();
            }
        });
    }
}
