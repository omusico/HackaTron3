package testingfirebase.app;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //initializing Firebase Context and instantiating object by referring it to my database
        Firebase.setAndroidContext(this);
        Firebase myFirebaseRef = new Firebase("https://dazzling-fire-2743.firebaseio.com/");

        myFirebaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
        //function initializes User class and pushes users to database. Also handles location updates
        createUsers(myFirebaseRef);
        //function checks for updates to specific user's location and prints to the command line.
       // checkForUpdates(myFirebaseRef);
    }

    //User class
    public class User {

        private String fullName;
        private double latitude;
        private double longitude;

        public User() {}

        public User(String fullName) {
            this.fullName = fullName;
            this.latitude = 0;
            this.longitude = 0;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Initializes five users and pushes to database
    public void createUsers(Firebase myFirebaseRef){
        User nader_helmy = new User("Nader Helmy");
        User simon_bloch = new User("Simon Bloch");
        User michael_piazza = new User("Michael Piazza");
        User miguel_gutierrez = new User("Miguel Guitierrez");
        User dylan_jeffers = new User("Dylan Jeffers");

        Firebase usersRef = myFirebaseRef.child("users");

        Map<String, User> users = new HashMap<String, User>();
        users.put("User1", nader_helmy);
        users.put("User2", simon_bloch);
        users.put("User3", michael_piazza);
        users.put("User4", miguel_gutierrez);
        users.put("User5", dylan_jeffers);

        usersRef.setValue(users, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });

        simon_bloch.setLatitude(46.00029238434);
        simon_bloch.setLongitude(24.23904857539);
        usersRef.child("User2/latitude").setValue(simon_bloch.getLatitude(),  new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
        usersRef.child("User2/longitude").setValue(simon_bloch.getLongitude(),  new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    System.out.println("Data could not be saved. " + firebaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }

    //checks for updates to User2's location
    public void checkForUpdates(Firebase myFirebaseRef){
        myFirebaseRef.child("User2").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
            }
            @Override public void onCancelled(FirebaseError error) { }
        });
    }
}
