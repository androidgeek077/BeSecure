package app.fyp.besecure;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;

import app.fyp.besecure.Fragments.AuthorityFragment;
import app.fyp.besecure.Fragments.ProfileFragment;
import app.fyp.besecure.Fragments.UserMapFragment;
import app.fyp.besecure.PhoneModel.AuthorityModel;
import app.fyp.besecure.PhoneModel.ImageUploadModel;
import app.fyp.besecure.PhoneModel.UserModel;
import de.hdodenhof.circleimageview.CircleImageView;

public class NavDrawerActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView mNameTV,mPhoneTV, mEmailTV;
    CircleImageView mProfilePic;
    private String Username, UserPhone, UserEmail, UserImgUrl;
    ArrayList mLocationList, mLongList;
    Button mEditProfile;

    DatabaseReference UserRef;
    DatabaseReference ProfileReference;

    int count = 0;
    private ArrayList<String> mAdmins = new ArrayList<>();
    private ArrayList<String> mAdminsIds = new ArrayList<>();

    private static final int CAMERA_REQUEST = 1888;
    private ImageView imageView;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS = 0;
    private StorageReference mProfilePicStorageReference;
    private static final int RC_PHOTO_PICKER = 1;
    private static final int REQUEST_TAKE_GALLERY_VIDEO = 1;
    private Uri selectedProfileImageUri;
    String UserId;
    DatabaseReference databaseReference, MobileNoReference;
    FirebaseAuth auth;
    StorageReference profilePicRef;
    UserModel userModel;
    ImageUploadModel imageUploadModel;
    private ImageView mImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_drawer);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        auth = FirebaseAuth.getInstance();
        UserId = FirebaseAuth.getInstance().getUid();
        mImageView=findViewById(R.id.NavHeaderimageView);
        mNameTV=findViewById(R.id.navHeaderNameTV);
        mPhoneTV=findViewById(R.id.NavHeaderPhonetextView);
        mProfilePicStorageReference = FirebaseStorage.getInstance().getReference().child("Pictures");
        databaseReference = FirebaseDatabase.getInstance().getReference().child("ImagesUrl");
        MobileNoReference = FirebaseDatabase.getInstance().getReference().child("Authority");
        UserRef= FirebaseDatabase.getInstance().getReference().child("User");

        FragmentLoadinManagerNoBackStack(new UserMapFragment());


        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getProfilePicture();
                SendSmsToAll();

            }
        });
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.logout_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.logout_item) {
            auth.signOut();
            startActivity(new Intent(NavDrawerActivity.this, LoginActivity.class));
        } else if (id == R.id.action_vdo) {
            getVideo();

        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Handle the camera action
            FragmentLoadinManagerNoBackStack(new UserMapFragment());
        } else if (id == R.id.nav_profile) {
            getStudentInfo();

            FragmentLoadinManagerNoBackStack(new ProfileFragment());

        } else if (id == R.id.nav_add_authority) {
            FragmentLoadinManagerNoBackStack(new AuthorityFragment());

//            startActivity(new Intent(NavDrawerActivity.this, MainActivity.class));
        } else if (id == R.id.nav_about) {

            Toast.makeText(this, "We cared for our generation", Toast.LENGTH_SHORT).show();

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void FragmentLoadinManagerNoBackStack(Fragment fragment) {

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();


    }

    protected void sendSMSMessage(String mPhoneNo) {


        PendingIntent pi = PendingIntent.getActivity(this, 0,
                new Intent(this, MapsActivity.class), 0);
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(mPhoneNo, null, "Alert! Hey, I'm in trouble situation please respond quickly and track my location on BeSecure to help me.", null, null);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    SmsManager smsManager = SmsManager.getDefault();
                    smsManager.sendTextMessage("+923004626618", null, "Alert! Hey, I'm in trouble situation please respond quickly and track my location on BeSecure to help me.", null, null);
                    Toast.makeText(getApplicationContext(), "SMS sent.",
                            Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }
    }

    public void getVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Video"), REQUEST_TAKE_GALLERY_VIDEO);

    }

    public void getProfilePicture() {
//        Intent intent = new Intent();
//        intent.setType("video/*");
//        intent.setAction(Intent.ACTION_GET_CONTENT);
//        startActivityForResult(Intent.createChooser(intent,"Select Video"),REQUEST_TAKE_GALLERY_VIDEO);


        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
        startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
//
//        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//        startActivityForResult(cameraIntent, CAMERA_REQUEST);

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            Uri selectedImageUri = data.getData();
            selectedProfileImageUri = selectedImageUri;

            profilePicRef = mProfilePicStorageReference.child(selectedProfileImageUri.getLastPathSegment());
            profilePicRef.putFile(selectedProfileImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(NavDrawerActivity.this, "File uploaded successfully", Toast.LENGTH_SHORT).show();
                    profilePicRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            String downloadUri = uri.toString();
                            uploadProduct(downloadUri);

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(NavDrawerActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }

    }

    public void uploadProduct(String ImageUrl) {

        imageUploadModel = new ImageUploadModel(ImageUrl, UserId);
        databaseReference.child(FirebaseAuth.getInstance().getUid()).push().setValue(imageUploadModel).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {

                Toast.makeText(NavDrawerActivity.this, "File Url Saved Successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
            }
        });

    }

    private void SendSmsToAll() {
        mAdmins = new ArrayList<>();
        MobileNoReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> adminsList = dataSnapshot.getChildren();
                for (DataSnapshot admins : adminsList) {
                    mAdminsIds.add(admins.getKey());
                    AuthorityModel model = admins.getValue(AuthorityModel.class);
                    mAdmins.add(model.getPhone());

                    sendSMSMessage(model.getPhone());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                Toast.makeText(this, "Menu key pressed", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_SEARCH:
                Toast.makeText(this, "Search key pressed", Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_BACK:
                onBackPressed();
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                event.startTracking();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                count = ++count;
                if (count % 3 == 0) {
                    SendSmsToAll();
                }
//                Toast.makeText(this,""+count, Toast.LENGTH_SHORT).show();
                return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void getStudentInfo() {
//        mLocationList = new ArrayList<>();
//        mLongList = new ArrayList<>();
        UserRef.child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                Username = dataSnapshot.child("name").getValue().toString();
                UserPhone = dataSnapshot.child("phone").getValue().toString();
                UserEmail = dataSnapshot.child("email").getValue().toString();
                UserImgUrl = dataSnapshot.child("imageUrl").getValue().toString();
//                Toast.makeText(NavDrawerActivity.this, Username, Toast.LENGTH_SHORT).show();
//                mNameTV.setText(Username);
//                mPhoneTV.setText(UserPhone);
//                mEmailTV.setText(UserEmail);
//                Glide.with(NavDrawerActivity.this)
//                        .load(UserImgUrl)
//                        .into(mImageView);

//
//
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


    }
}

