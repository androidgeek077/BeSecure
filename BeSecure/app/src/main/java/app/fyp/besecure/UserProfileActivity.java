package app.fyp.besecure;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import app.fyp.besecure.PhoneModel.UserModel;

public class UserProfileActivity extends AppCompatActivity {
    private AVLoadingIndicatorView loader;


    EditText mEdtTxtASName, mEdtTxtASEmail, mEdtTxtASPassword, mEdtTxtASPasswordRep, mEdtTxtASPhone;
    String mStrASName, mStrASEmail, mStrASPassword, mStrASPasswordRep, mStrASPhone;

    LinearLayout linearLayout;
    View view;
    Button mBtnSignup;


    Button mSelectedImgBtn;
    ImageView profileImageView;
    String downloadUri;

    private StorageReference mProfilePicStorageReference;
    private static final int RC_PHOTO_PICKER = 1;
    private Uri selectedProfileImageUri;


    DatabaseReference databaseReference;
    FirebaseAuth auth;
    StorageReference profilePicRef;
    UserModel userModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        mProfilePicStorageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");
        databaseReference = FirebaseDatabase.getInstance().getReference("User");
        loader = findViewById(R.id.userLoader);
        linearLayout = findViewById(R.id.parentLayout);
        mEdtTxtASName = findViewById(R.id.edt_txt_as_name);
        mEdtTxtASEmail = findViewById(R.id.edt_txt_as_email);
        mEdtTxtASPassword = findViewById(R.id.edt_txt_as_password);
        mEdtTxtASPasswordRep = findViewById(R.id.edt_txt_as_rep_password);
        mEdtTxtASPhone = findViewById(R.id.edt_txt_as_phone);
        profileImageView = findViewById(R.id.selected_img_vw);
        mSelectedImgBtn = findViewById(R.id.select_iamge_btn);
        mSelectedImgBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfilePicture();
            }
        });

        //Registering TextView And Shifting Activity On Click TO Login Activity
        mBtnSignup = findViewById(R.id.btn_a_signup);
        mBtnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loader.show();
                mStrASName = mEdtTxtASName.getText().toString();
                mStrASEmail = mEdtTxtASEmail.getText().toString();
                mStrASPassword = mEdtTxtASPassword.getText().toString();
                mStrASPasswordRep = mEdtTxtASPasswordRep.getText().toString();
                mStrASPhone = mEdtTxtASPhone.getText().toString();

                if (mStrASName.isEmpty()) {
                    mEdtTxtASName.setError("Please Fill This Field");
                } else if (mStrASEmail.isEmpty()) {
                    mEdtTxtASEmail.setError("Please Fill This Field");
                } else if (mStrASPassword.isEmpty()) {
                    mEdtTxtASPassword.setError("Please Fill This Field");
                } else if (mStrASPasswordRep.isEmpty()) {
                    mEdtTxtASPasswordRep.setError("Please Fill This Field");
                } else if (mStrASPhone.isEmpty()) {
                    mEdtTxtASPhone.setError("Please Fill This Field");
                } else if (mStrASPassword.length() < 8) {
                    mEdtTxtASPassword.setError("Password Should Be Greater Than 8");
                } else if (mStrASPasswordRep.length() < 8) {
                    mEdtTxtASPasswordRep.setError("Password Should Be Greater Than 8");
                } else if (!(mStrASPassword.equalsIgnoreCase(mStrASPasswordRep))) {
                    mEdtTxtASPasswordRep.setError("Password Didn't Matched");
                    mEdtTxtASPassword.setError("Password Didn't Matched");
                } else if (!(Patterns.EMAIL_ADDRESS.matcher(mStrASEmail).matches())) {
                    mEdtTxtASEmail.setError("Email Not Valid");
                } else if (!(mEdtTxtASPhone.length() == 11)) {
                    mEdtTxtASPhone.setError("Phone Number Must Be 11 Digits Long");
                } else {

                    profilePicRef = mProfilePicStorageReference.child(selectedProfileImageUri.getLastPathSegment());
                    profilePicRef.putFile(selectedProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                            profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    downloadUri = uri.toString();
                                    uploadProduct(downloadUri);
                                    loader.setVisibility(View.GONE);
                                }
                            });
                        }
                    });

                }

            }
        });


    }


    public void getProfilePicture() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedProfileImageUri = selectedImageUri;
            profileImageView.setImageURI(selectedImageUri);
            profileImageView.setVisibility(View.VISIBLE);
        }

    }

    public void uploadProduct(String ImageUrl) {

        userModel = new UserModel(mStrASName, mStrASEmail, mStrASPassword, mStrASPhone, "user", ImageUrl);
        databaseReference.push().setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mEdtTxtASName.getText().clear();
                mEdtTxtASEmail.getText().clear();
                mEdtTxtASPassword.getText().clear();
                mEdtTxtASPasswordRep.getText().clear();
                mEdtTxtASPhone.getText().clear();
                profileImageView.setVisibility(View.GONE);
                final Snackbar snackbar = Snackbar.make(linearLayout, "User Added Successfully", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Ok", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onBackPressed() {

        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            finishAndRemoveTask();
                        }
                    }
                })
                .setNegativeButton("No", null)
                .show();
    }

}
