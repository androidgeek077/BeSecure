package app.fyp.besecure.Fragments;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import app.fyp.besecure.PhoneModel.UserModel;
import app.fyp.besecure.R;
import app.fyp.besecure.UserProfileActivity;

/**
 * A simple {@link Fragment} subclass.
 */
public class EditProfileFragment extends Fragment {

    EditText mEdtTxtASName, mEdtTxtASEmail, mEdtTxtASPhone;

    String mStrASName, mStrASEmail, mStrASPhone;

    DatabaseReference databaseReference;
    FirebaseAuth auth;
    StorageReference profilePicRef;
    UserModel userModel;
    Button updateBtn, mSelectBtn;


    LinearLayout linearLayout;
    View view;
    Button mBtnSignup;


    Button mSelectedImgBtn;
    ImageView profileImageView;
    String downloadUri;

    private StorageReference mProfilePicStorageReference;
    private static final int RC_PHOTO_PICKER = 1;
    private Uri selectedProfileImageUri;




    public EditProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_profile, container, false);


        auth = FirebaseAuth.getInstance();
        mProfilePicStorageReference = FirebaseStorage.getInstance().getReference().child("ProfilePictures");
        databaseReference = FirebaseDatabase.getInstance().getReference("User");

        mEdtTxtASName = view.findViewById(R.id.edt_txt_as_name);
        mEdtTxtASEmail = view.findViewById(R.id.edt_txt_as_email);
        mEdtTxtASPhone = view.findViewById(R.id.edt_txt_as_phone);
        profileImageView = view.findViewById(R.id.selected_img_vw);

        mSelectBtn = view.findViewById(R.id.select_iamge_btn);
        mSelectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProfilePicture();
            }
        });
        updateBtn = view.findViewById(R.id.btn_a_signup);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                mStrASName = mEdtTxtASName.getText().toString();
                mStrASEmail = mEdtTxtASEmail.getText().toString();
                mStrASPhone = mEdtTxtASPhone.getText().toString();

                profilePicRef = mProfilePicStorageReference.child(selectedProfileImageUri.getLastPathSegment());
                profilePicRef.putFile(selectedProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                downloadUri = uri.toString();
                                uploadProduct(downloadUri);
                            }
                        });
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

    return view;
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

        userModel = new UserModel(mStrASName, mStrASEmail, mStrASPhone, ImageUrl);
        databaseReference.child(FirebaseAuth.getInstance().getUid()).setValue(userModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                mEdtTxtASName.getText().clear();
                mEdtTxtASEmail.getText().clear();
                mEdtTxtASPhone.getText().clear();
                profileImageView.setVisibility(View.GONE);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Snackbar.make(view, e.getMessage(), Snackbar.LENGTH_LONG).show();
            }
        });
    }
}
