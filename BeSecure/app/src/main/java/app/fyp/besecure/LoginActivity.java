package app.fyp.besecure;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.victor.loading.rotate.RotateLoading;

public class LoginActivity extends AppCompatActivity {

    TextView mSignupTv;
    TextView mRegisterTV;
    FirebaseAuth mAuth;
    EditText mEmailET, mPasswordET;
    Button mLoginBtn;
    String emailStr, passwordStr;
    FirebaseAuth auth;
    RotateLoading loading;


    @Override
    protected void onStart() {
        super.onStart();
        auth=FirebaseAuth.getInstance();

        if (null != auth.getCurrentUser()) {
            startActivity(new Intent(LoginActivity.this, AuthorityNavDrawerActivity.class));
            finish();
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth=FirebaseAuth.getInstance();
        mEmailET=findViewById(R.id.EmailET);
        mPasswordET=findViewById(R.id.PasswordET);
        loading=findViewById(R.id.rotateloading);

        mSignupTv=findViewById(R.id.signupTV);
        mSignupTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, UserProfileActivity.class));
            }
        });
        mLoginBtn=findViewById(R.id.loginBtn);
        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                emailStr=mEmailET.getText().toString();
                passwordStr=mPasswordET.getText().toString();

                if (emailStr.isEmpty()) {
                    mEmailET.setError("Enter your email");
                } else if (passwordStr.isEmpty()) {
                    mPasswordET.setError("Enter your password");
                }
                else {
                    loading.setVisibility(View.VISIBLE);
                    loading.start();
                    mAuth.signInWithEmailAndPassword(emailStr, passwordStr).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                loading.setVisibility(View.GONE);
                                startActivity(new Intent(LoginActivity.this, AuthorityNavDrawerActivity.class));
                            }

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            loading.setVisibility(View.GONE);
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

    }
}
