package israelontanilla.es.instashop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    private Button mBtnSingOut;
    private Button mBtnDeleteProfile;
    private TextView mTextviewName;
    private TextView mTextviewNick;
    private TextView mTextviewEmail;
    private TextView mTextviewPassword;
    private TextView mTextviewMobile;

    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mTextviewName = (TextView) findViewById(R.id.viewName);
        mTextviewNick = (TextView) findViewById(R.id.viewNick);
        mTextviewEmail = (TextView) findViewById(R.id.viewEmail);
        mTextviewPassword = (TextView) findViewById(R.id.viewPassword);
        mTextviewMobile = (TextView) findViewById(R.id.viewMobile);
        mBtnSingOut = (Button) findViewById(R.id.btnSingOut);
        mBtnDeleteProfile = (Button) findViewById(R.id.btnDeleteProfile);

        getUserInfo();

        mBtnSingOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });

        mBtnDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteUser();
                mAuth.signOut();
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

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
    }
}
