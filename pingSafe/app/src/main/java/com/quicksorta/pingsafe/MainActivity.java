package com.quicksorta.pingsafe;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Dialog;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.GenericTypeIndicator;
import com.firebase.client.ValueEventListener;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.LocationCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.lang.Math.*;


public class MainActivity extends Activity implements

    GooglePlayServicesClient.ConnectionCallbacks,
    GooglePlayServicesClient.OnConnectionFailedListener,
    LocationListener {

    // Global constants
    /*
     * Define a request code to send to Google Play services
     * This code is returned in Activity.onActivityResult
     */

    private final static int
            CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // Milliseconds per second
    private static final int MILLISECONDS_PER_SECOND = 1000;
    // Update frequency in seconds
    public static final int UPDATE_INTERVAL_IN_SECONDS = 1;
    // Update frequency in milliseconds
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    // The fastest update frequency, in seconds
    private static final int FASTEST_INTERVAL_IN_SECONDS = 1;
    // A fast frequency ceiling in milliseconds
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private static double pingerLongitude, pingerLatitude;

    LocationClient mLocationClient;
    boolean mUpdatesRequested;
    boolean sentToStrangers = false;
    TextView longitudeView;
    TextView latitudeView;
    TextView nameView;
    User selfUser;
    String[] friendList;

    Firebase myFirebaseRef;
    Firebase usersRef;
    Firebase pingList;
    Firebase pingStats;


    // Define an object that holds accuracy and frequency parameters
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the message from the intent
        Intent intent = getIntent();
        String name = intent.getStringExtra(LogIn.EXTRA_MESSAGE);
        // Get the message from the intent
        // Create the text view
        TextView textView = new TextView(this);
        textView.setTextSize(40);
        textView.setText(name);

        // Set the text view as the activity layout
        setContentView(textView);


        setContentView(R.layout.fragment_main);
        // Create the LocationRequest object
        mLocationRequest = LocationRequest.create();
        // Use high accuracy
        mLocationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        // Set the update interval to 5 seconds
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        // Set the fastest update interval to 1 second
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
         /*
         * Create a new location client, using the enclosing class to
         * handle callbacks.
         */
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
        longitudeView = (TextView) findViewById(R.id.longitude);
        latitudeView = (TextView) findViewById(R.id.latitude);

        //initializing Firebase Context and instantiating object by referring it to my database
        Firebase.setAndroidContext(this);
        myFirebaseRef = new Firebase("https://dazzling-fire-2743.firebaseio.com/");


        pingList = myFirebaseRef.child("pingList");
        pingStats = myFirebaseRef.child("pingStats");
        usersRef = myFirebaseRef.child("users");
        Firebase newUsersRef = usersRef.push();
        selfUser = new User(name);
        friendList = new String[5];

//        usersRef.setValue(selfUser);
        selfUser.setUserNameID(newUsersRef.getKey());
        //function initializes User class and pushes users to database. Also handles location updates

        pingList.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Context context = getApplicationContext();
                Map<String,Object> data = (Map<String,Object>) dataSnapshot.getValue();

//                Toast.makeText(context, "Longitude toast: " + data.get("longitude").toString(), Toast.LENGTH_SHORT).show();
                if(data!=null) {
                    Double longitude = (Double) data.get("longitude");
                    Double latitude = (Double) data.get("latitude");
                    String userID = (String) data.get("name");
                    boolean test = isInRange(longitude, latitude);
//                    System.out.println(userID);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
        //function checks for updates to specific user's location and prints to the command line.

    }

     public boolean isInRange(Double longitude, Double latitude){
//         geoFire.getLocation(dataSnapshot.toString(), new LocationCallback() {
//             @Override
//             public void onLocationResult(String key, GeoLocation location) {
//                 if (location != null) {
////                     System.out.println(String.format("The location for key %s is [%f,%f]", key, location.latitude, location.longitude));
//                     pingerLatitude = location.latitude;
//                     pingerLongitude = location.longitude;
//                 } else {
//                     System.out.println(String.format("There is no location for key %s in GeoFire", key));
//                 }
//             }
//
//             @Override
//             public void onCancelled(FirebaseError firebaseError) {
//                 System.err.println("There was an error getting the GeoFire location: " + firebaseError);
//             }

//        parse dataSnapshot, get other longitude and latitude
//        pingerLatitude =
//        pingerLongitude =
//        Map<String,String> value = (Map<String,String>)dataSnapshot.getValue();
//         GenericTypeIndicator<List<String>> t = new GenericTypeIndicator<List<String>>() {};
//         List<String> messages = dataSnapshot.getValue(t);
//         if( messages == null ) {
//             System.out.println("No messages");
//         }
//         else {
//             System.out.println("The first message is: " + messages.get(0) );
//         }

//

//         System.out.println("Longitude: " + data.get("longitude").toString());
//        //Toast.makeText(this, data.get("longitude"), Toast.LENGTH_LONG).show();
        double dist = Math.sqrt(Math.pow(latitude - selfUser.getLatitude(), 2) + Math.pow(longitude - selfUser.getLongitude(), 2));
        dist*=(1000000/9);
        Toast.makeText(this, Double.toString(dist), Toast.LENGTH_SHORT).show();
        return false;


     }
    //User class
    public class User {

        private String fullName;
        private double latitude;
        private double longitude;
        private String userNameID;
        private boolean ping;

        public User() {}

        public User(String fullName) {
            this.fullName = fullName;
            this.latitude = 0;
            this.longitude = 0;
            this.ping = false;
        }
        public void setPing(Boolean pingBool){
            this.ping = pingBool;
        }
        public boolean getPing(){ return ping; }
        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getUserNameID() { return userNameID; }
        public void setUserNameID(String ID) {
            userNameID = ID; }

        public void setLatitude(double updateLat) {
            this.latitude = updateLat;
        }

        public void setLongitude(double updateLong) {
            this.longitude = updateLong;
        }
        public String getFullName() {
            return fullName;
        }
    }

    public void initializeFriendList(){
        //From intent
        String[] friendListIntent = new String[5];

        for (int i = 0; i < 5; i++) {
            friendListIntent[i] = "";
            friendList[i] = friendListIntent[i];
        }
    }

    public void onLocationChanged(Location location){
//        String msg = "Updated Location: " +
//                Double.toString(location.getLatitude()) + "," +
//                Double.toString(location.getLongitude());
//        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        selfUser.setLatitude(location.getLatitude());
        selfUser.setLongitude(location.getLongitude());
        usersRef.child(selfUser.getUserNameID()+"/latitude").setValue(selfUser.getLatitude());
        usersRef.child(selfUser.getUserNameID()+"/longitude").setValue(selfUser.getLongitude());
//        geoFire.setLocation(selfUser.getUserNameID(), new GeoLocation(selfUser.getLatitude(), selfUser.getLongitude()));

        Toast.makeText(this, "Updated latitude: " + Double.toString(selfUser.getLatitude()), Toast.LENGTH_SHORT).show();
//        sentToStrangers();
    }

//    public void sentToStrangers() {
//            if (!sentToStrangers && (selfUser.getLatitude() != 0)) {
//                sentToStrangers = true;
//                //send to firebase
//                Toast.makeText(this, "Sent to strangers: " + Double.toString(selfUser.getLatitude()), Toast.LENGTH_SHORT).show();
//            }
//    }

    public void sendLocation(View view){
//        mCurrentLocation = mLocationClient.getLastLocation();
//        double longitude = mCurrentLocation.getLongitude();
//        double latitude = mCurrentLocation.getLatitude();
//        if(!mUpdatesRequested) {
//
////            mLocationClient.connect();
//            mUpdatesRequested = true;
//        }
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        longitudeView.setText(String.valueOf(selfUser.getLongitude()));
        latitudeView.setText(String.valueOf(selfUser.getLatitude()));
        Firebase newPingStats = pingStats.push();


        newPingStats.setValue(selfUser);
        Map<String, Object> coordinates = new HashMap<String,Object>();
        coordinates.put("longitude", selfUser.getLongitude());
        coordinates.put("latitude", selfUser.getLatitude());
        coordinates.put("name", selfUser.getUserNameID());
        pingList.setValue(coordinates);

        usersRef.child(selfUser.getUserNameID()+"/ping").setValue(selfUser.getPing());

//        sentToStrangers();
    }



    /*
     * Called when the Activity becomes visible.
     */
    protected void onStart() {
        super.onStart();
        // Connect the client.

    }

    /*
    * Called when the Activity is no longer visible.
    */
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    // Define a DialogFragment that displays the error dialog
    public static class ErrorDialogFragment extends DialogFragment {
        // Global field to contain the error dialog
        private Dialog mDialog;
        // Default constructor. Sets the dialog field to null
        public ErrorDialogFragment() {
            super();
            mDialog = null;
        }
        // Set the dialog to display
        public void setDialog(Dialog dialog) {
            mDialog = dialog;
        }
        // Return a Dialog to the DialogFragment.
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return mDialog;
        }
    }

    /*
     * Handle results returned to the FragmentActivity
     * by Google Play services
     */
    @Override
    protected void onActivityResult(
            int requestCode, int resultCode, Intent data) {
        // Decide what to do based on the original request code
        switch (requestCode) {

            case CONNECTION_FAILURE_RESOLUTION_REQUEST :
            /*
             * If the result code is Activity.RESULT_OK, try
             * to connect again
             */
                switch (resultCode) {
                    case Activity.RESULT_OK :
                    /*
                     * Try the request again
                     */

                        break;
                }
        }
    }

    private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode =
                GooglePlayServicesUtil.
                        isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
            // In debug mode, log the status
            Log.d("Location Updates",
                    "Google Play services is available.");
            // Continue
            return true;
            // Google Play services was not available for some reason.
            // resultCode holds the error code.
        } else {
            // Get the error dialog from Google Play services
            Dialog errorDialog = GooglePlayServicesUtil.getErrorDialog(
                    resultCode,
                    this,
                    CONNECTION_FAILURE_RESOLUTION_REQUEST);

            // If Google Play services can provide an error dialog
            if (errorDialog != null) {
                // Create a new DialogFragment for the error dialog
                ErrorDialogFragment errorFragment =
                        new ErrorDialogFragment();
                // Set the dialog in the DialogFragment
                errorFragment.setDialog(errorDialog);
                // Show the error dialog in the DialogFragment
//                errorFragment.show(getSupportFragmentManager(), "Location Updates");
            }
        }
        return false;
    }


    /*
     * Called by Location Services when the request to connect the
     * client finishes successfully. At this point, you can
     * request the current location or start periodic updates
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        // Display the connection status
        Toast.makeText(this, "Connected", Toast.LENGTH_SHORT).show();
        mLocationClient.requestLocationUpdates(mLocationRequest, this);

    }

    /*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onDisconnected() {
        // Display the connection status
        Toast.makeText(this, "Disconnected. Please re-connect.",
                Toast.LENGTH_SHORT).show();
    }

    public void showErrorDialog(int connectionResult) {
        // Display the connection status
        Toast.makeText(this, "Error. I did my best.",
                Toast.LENGTH_SHORT).show();
    }


    /*
     * Called by Location Services if the attempt to
     * Location Services fails.
     */
    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        /*
         * Google Play services can resolve some errors it detects.
         * If the error has a resolution, try sending an Intent to
         * start a Google Play services activity that can resolve
         * error.
         */
        if (connectionResult.hasResolution()) {
            try {
                // Start an Activity that tries to resolve the error
                connectionResult.startResolutionForResult(
                        this,
                        CONNECTION_FAILURE_RESOLUTION_REQUEST);
                /*
                 * Thrown if Google Play services canceled the original
                 * PendingIntent
                 */
            } catch (IntentSender.SendIntentException e) {
                // Log the error
                e.printStackTrace();
            }
        } else {
            /*
             * If no resolution is available, display a dialog to the
             * user with the error.
             */
            showErrorDialog(connectionResult.getErrorCode());



        }
    }
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main,
                    container, false);
            return rootView;
        }
    }
}
