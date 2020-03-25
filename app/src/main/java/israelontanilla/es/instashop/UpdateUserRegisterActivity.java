package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

        String name;
        String password;
        String nick;
        String email;
        String mobile;

        mButtonUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateUser();
                startActivity(new Intent(UpdateUserRegisterActivity.this, ProfileActivity.class));
                finish();
            }
        });
    }

    private  void updateUser(){
        String id = mAuth.getCurrentUser().getUid();
        mDatabase.child("Users").child(id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dataSnapshot.child("name").getRef().setValue(name);
                dataSnapshot.child("name").getRef().setValue(password);
                dataSnapshot.child("name").getRef().setValue(nick);
                dataSnapshot.child("name").getRef().setValue(email);
                dataSnapshot.child("name").getRef().setValue(mobile);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
