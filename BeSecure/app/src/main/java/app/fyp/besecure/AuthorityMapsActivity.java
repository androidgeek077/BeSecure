

package app.fyp.besecure;

        import android.content.Context;
        import android.content.Intent;
        import android.content.pm.PackageManager;
        import android.graphics.Canvas;
        import android.graphics.Color;
        import android.graphics.Paint;
        import android.location.Location;
        import android.location.LocationListener;
        import android.location.LocationManager;
        import android.support.annotation.NonNull;
        import android.support.annotation.Nullable;
        import android.support.design.widget.Snackbar;
        import android.support.v4.app.ActivityCompat;
        import android.support.v4.app.FragmentActivity;
        import android.os.Bundle;
        import android.support.v4.content.ContextCompat;
        import android.support.v7.app.AppCompatActivity;
        import android.view.Menu;
        import android.view.MenuItem;
        import android.view.View;
        import android.widget.ArrayAdapter;
        import android.widget.TextView;
        import android.widget.Toast;

        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;

        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.BitmapDescriptorFactory;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.PolylineOptions;
        import com.google.android.gms.tasks.OnFailureListener;
        import com.google.android.gms.tasks.OnSuccessListener;
        import com.google.firebase.auth.FirebaseAuth;
        import com.google.firebase.database.DataSnapshot;
        import com.google.firebase.database.DatabaseError;
        import com.google.firebase.database.DatabaseReference;
        import com.google.firebase.database.FirebaseDatabase;
        import com.google.firebase.database.ValueEventListener;

        import java.util.ArrayList;

        import app.fyp.besecure.PhoneModel.getLocationModel;


public class AuthorityMapsActivity extends AppCompatActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener {

    private GoogleMap mMap;
    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    DatabaseReference AddLocation, mDatabase, mPostReference;

    ArrayList mLocationArr;
    private FirebaseAuth mAuth;


    Double latDouble, langDouble;




    TextView mLatitude, mLongitude;
    private String LatAsStr;
    private String LngAsStr;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("User's Location");
        mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_maps);
        mLatitude=findViewById(R.id.latitude);
        mLongitude=findViewById(R.id.longitude);


        AddLocation= FirebaseDatabase.getInstance().getReference("Locations");
        getDrivers();
//        Toast.makeText(context, LatAsStr, Toast.LENGTH_SHORT).show();
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Add a marker in Sydney and move the camera
        LatLng sargodha = new LatLng(32.078665, 72.68051999999999);
        LatLng rehman_plaza = new LatLng(32.076461, 72.678524);
        LatLng kirana_bar = new LatLng(32.076422, 72.675826);
        mMap.addMarker(new MarkerOptions().position(sargodha).title("Maaz").icon(BitmapDescriptorFactory.fromResource(R.drawable.boy)));
        mMap.addMarker(new MarkerOptions().position(kirana_bar).title("Mishaal").icon(BitmapDescriptorFactory.fromResource(R.drawable.woman)));
        mMap.addMarker(new MarkerOptions().position(rehman_plaza).title("Marker in Sydney"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(32.078665, 72.68051999999999), 15.0f));
    }


    @Override
    public void onLocationChanged(Location location) {

        LatLng currentLocation=new LatLng(location.getLatitude(), location.getLongitude());
        mMap.addMarker(new MarkerOptions().position(currentLocation).title("Driver position"));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latDouble, langDouble), 8.0f));
    }


    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {



    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }


    private ArrayList<String> getDrivers(){
        mLocationArr=new ArrayList<>();
        AddLocation.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                Toast.makeText(context, ""+dataSnapshot, Toast.LENGTH_SHORT).show();
//                latDouble = (Double) dataSnapshot.child("mLatitude").getValue();
//                langDouble = (Double) dataSnapshot.child("mLongitude").getValue();
//                LatAsStr=Double.toString(latDouble);
//                LngAsStr=Double.toString(langDouble);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return mLocationArr;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.authority_logout, menu);
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

            mAuth.signOut();

            startActivity(new Intent(AuthorityMapsActivity.this, LoginActivity.class));


        }

        return super.onOptionsItemSelected(item);
    }

}

