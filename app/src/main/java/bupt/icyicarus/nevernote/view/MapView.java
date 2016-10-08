package bupt.icyicarus.nevernote.view;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import org.json.JSONException;
import org.json.JSONObject;

import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.http.HTTPTool;
import bupt.icyicarus.nevernote.http.HttpCallbackListener;

public class MapView extends FragmentActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMapClickListener, GoogleMap.OnMarkerDragListener {

    public static String EXTRA_LOCATION = "location";
    protected Marker oldMarker = null;
    protected GoogleMap mGoogleMap;
    protected GoogleApiClient mGoogleApiClient;
    protected Location mLastLocation;
    protected MaterialSearchBar addressSearchBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.aty_map);
        addressSearchBar = (MaterialSearchBar) findViewById(R.id.addressSearchBar);
        addressSearchBar.setText("111");
        addressSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean b) {

            }

            @Override
            public void onSearchConfirmed(CharSequence charSequence) {
                getCoordinate(charSequence.toString());
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                switch (buttonCode) {
                    case MaterialSearchBar.BUTTON_SPEECH:
                        Log.e("msb", "speech");
                        break;
                    default:
                        break;
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapView.this);
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(MapView.this).addConnectionCallbacks(MapView.this).addOnConnectionFailedListener(MapView.this).addApi(LocationServices.API).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        buildGoogleApiClient();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onBackPressed() {
        if (oldMarker != null) {
            Intent i = getIntent();
            i.putExtra("latitude", String.valueOf(oldMarker.getPosition().latitude));
            i.putExtra("longitude", String.valueOf(oldMarker.getPosition().longitude));
            setResult(RESULT_OK, i);
        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        AndPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults, new PermissionListener() {
            @Override
            public void onSucceed(int requestCode) {
                Toast.makeText(MapView.this, "Permission Granted", Toast.LENGTH_SHORT).show();
                onMapReady(mGoogleMap);
            }

            @Override
            public void onFailed(int requestCode) {
                Toast.makeText(MapView.this, "Permission Denied, Please Check", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }

    // OnMapReadyCallback
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mGoogleMap = googleMap;
        mGoogleMap.setOnMapClickListener(MapView.this);
        mGoogleMap.setOnMarkerDragListener(MapView.this);
        mGoogleMap.setPadding(0, 150, 0, 0);
        if (mGoogleMap != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                AndPermission.with(MapView.this).requestCode(1).permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).send();
            } else {
                mGoogleMap.setMyLocationEnabled(true);
            }
        }
    }

    // GoogleApiClient.ConnectionCallbacks
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            AndPermission.with(MapView.this).requestCode(1).permission(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION).send();
        } else {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            if (mLastLocation != null) {
                mGoogleMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude())));
            } else {
                Toast.makeText(MapView.this, "Unable to locate, please check location service status", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(MapView.this, "Connection suspended", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    // GoogleApiClient.OnConnectionFailedListener
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Toast.makeText(MapView.this, "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode(), Toast.LENGTH_SHORT).show();
    }

    // GoogleMap.OnMapClickListener
    @Override
    public void onMapClick(LatLng latLng) {
        if (oldMarker != null) {
            oldMarker.remove();
        }
        getAddress(latLng);
        oldMarker = mGoogleMap.addMarker(new MarkerOptions().position(latLng).draggable(true).flat(true));
    }

    // GoogleMap.OnMarkerDragListener
    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        getAddress(marker.getPosition());
    }

    public void getAddress(LatLng latLng) {
        HTTPTool.sendRequest("https://maps.google.com/maps/api/geocode/json?latlng=" + latLng.latitude + "," + latLng.longitude + "&key=AIzaSyACwJPuT9EH5OI-r_mUJillausCQ6txedE",
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            final String addressRecord = jsonObject.getJSONArray("results").getJSONObject(0).getString("formatted_address");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    addressSearchBar.setText(addressRecord);
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    public void getCoordinate(String address) {
        address = address.replace(" ", "+");
        HTTPTool.sendRequest("https://maps.google.com/maps/api/geocode/json?address=" + address + "&key=AIzaSyACwJPuT9EH5OI-r_mUJillausCQ6txedE",
                new HttpCallbackListener() {
                    @Override
                    public void onFinish(String response) {
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(response);
                            final double latitude = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lat");
                            final double longitude = jsonObject.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location").getDouble("lng");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (oldMarker != null) {
                                        oldMarker.remove();
                                    }
                                    oldMarker = mGoogleMap.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)).draggable(true).flat(false));
                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(Exception e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}
