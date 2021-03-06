package app.fyp.besecure;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wang.avi.AVLoadingIndicatorView;

import app.fyp.besecure.Fragments.AuthorityFragment;
import app.fyp.besecure.PhoneModel.AuthorityModel;

public class MainActivity extends AppCompatActivity {


    MaterialEditText mNameET, mEmailET, mPhoneNoET;
    Button mAddBtn;
    private DatabaseReference databaseReference;
    private AVLoadingIndicatorView loader;

    String mNameStr, mEmailStr, mPhoneNoStr;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loader=findViewById(R.id.loader);

        mAddBtn = findViewById(R.id.addPhoneBtn);
        mNameET = findViewById(R.id.NameET);
        mEmailET = findViewById(R.id.EmailET);
        mPhoneNoET = findViewById(R.id.phoneNoET);




        databaseReference = FirebaseDatabase.getInstance().getReference("Authority");

        mAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                TextToString();
                if (mNameStr.isEmpty()) {
                    mNameET.setError("Please Fill This Field");
                } else if (mPhoneNoStr.isEmpty()) {
                    mPhoneNoET.setError("PLease enter phone no");
                } else if (mEmailStr.isEmpty()) {
                    mEmailET.setError("PLease enter Email");
                } else {
                    loader.show();
                    AuthorityModel authorityModel = new AuthorityModel(mPhoneNoStr, mEmailStr, mNameStr);
                    databaseReference.push().setValue(authorityModel).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            loader.setVisibility(View.GONE);
                            Snackbar snackbar = Snackbar.make(v, "Authority registered successfully", Snackbar.LENGTH_INDEFINITE);
                            snackbar.setAction("Proceed", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    startActivity(new Intent(MainActivity.this, NavDrawerActivity.class));
                                }
                            });
                            snackbar.show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar.make(v, e.getMessage(), Snackbar.LENGTH_LONG).show();

                        }
                    });
                }
            }
        });
    }

    void TextToString() {
        mNameStr=mNameET.getText().toString();
        mEmailStr=mEmailET.getText().toString();
        mPhoneNoStr=mPhoneNoET.getText().toString();
    }

    public void FragmentLoadinManagerNoBackStack(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }
}
