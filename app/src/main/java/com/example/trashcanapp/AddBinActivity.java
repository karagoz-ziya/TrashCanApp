package com.example.trashcanapp;

import static com.example.trashcanapp.constants.CONSTANTS.MY_PREFS_NAME;
import static com.example.trashcanapp.constants.CONSTANTS.binTypeArray;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.trashcanapp.constants.CONSTANTS;
import com.example.trashcanapp.dbmodel.RecycleBin;
import com.example.trashcanapp.dbmodel.User;
import com.github.clans.fab.FloatingActionButton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class AddBinActivity extends AppCompatActivity {


    private final String USER_ID_PREF = "USER_ID_PREF";
    private static  final String TAG = "ADD_BIN_TEST";
    private StringBuilder stringBuilder;

    private FirebaseFirestore db;

    private Bitmap photo;
    private Boolean switchFlag=false;
    String binRefID;
    String userIDD;
    User tempUser;
    ArrayList<String> tempBinList;

    private boolean isOnCamera = false;
    private boolean isCameraPermissionGranted = false;
    private static final int pic_id = 1;

    boolean[] selectedBinType;
    ArrayList<Integer> binList = new ArrayList<>();

    Double lat;
    Double longt;

    TextView textView;
    Button submitButton;
    EditText description;
    Button camera_open_id;
    ImageView click_image_id;
    GeoPoint locat;
    Switch themeSwitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bin);


        lat = getIntent().getDoubleExtra("lat", 0);
        longt = getIntent().getDoubleExtra("longt", 0);
        locat = new GeoPoint(lat, longt);

        SharedPreferences prefs = getSharedPreferences(USER_ID_PREF, MODE_PRIVATE);
         userIDD = prefs.getString("userID", "-1");

        // access to the elements of popup window
        submitButton = findViewById(R.id.submitBin);
        textView = findViewById(R.id.tv_bin);
        description = findViewById(R.id.description);
        themeSwitch = findViewById(R.id.theme_switch);

        camera_open_id = findViewById(R.id.camera_button);
        click_image_id = findViewById(R.id.click_image);


        selectedBinType = new boolean[binTypeArray.length];

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(AddBinActivity.this);

                // set title
                builder.setTitle("Select Bin Type");

                // set dialog non cancelable
                builder.setCancelable(false);

                builder.setMultiChoiceItems(binTypeArray, selectedBinType, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i, boolean b) {
                        // check condition
                        if (b) {
                            // when checkbox selected
                            // Add position  in lang list
                            binList.add(i);
                            // Sort array list
                            Collections.sort(binList);
                        } else {
                            // when checkbox unselected
                            // Remove position from binList
                            binList.remove(Integer.valueOf(i));
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Initialize string builder
                        stringBuilder = new StringBuilder();
                        // use for loop
                        for (int j = 0; j < binList.size(); j++) {
                            // concat array value
                            stringBuilder.append(binTypeArray[binList.get(j)]);
                            // check condition
                            if (j != binList.size() - 1) {
                                // When j value  not equal
                                // to lang list size - 1
                                // add comma
                                stringBuilder.append(", ");
                            }
                        }
                        // set text on textView
                        textView.setText(stringBuilder.toString());
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("binString", stringBuilder.toString());
                        editor.putBoolean("isOnCamera", true);
                        editor.apply();
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // dismiss dialog
                        dialogInterface.dismiss();
                        Log.i(TAG, binList.toString());
                    }
                });
                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // use for loop
                        for (int j = 0; j < selectedBinType.length; j++) {
                            // remove all selection
                            selectedBinType[j] = false;
                            // clear language list
                            binList.clear();
                            // clear text view value
                            textView.setText("");
                        }

                    }
                });
                // show dialog
                builder.show();

            }
        });

        camera_open_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                isOnCamera = true;
                if(isCameraPermissionGranted){
                    allowCamera();
                }
                if(photo != null){

                    click_image_id.setImageBitmap(photo);
                }
            }
        });

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(stringBuilder != null && photo != null) {
                    AddRecycleBinToDB(binList, description.getText().toString());
                    startActivity(new Intent(AddBinActivity.this, MapsActivity.class));
                }
                else {
                    Toast.makeText(AddBinActivity.this, "Must Provide Bin Type and Photo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        themeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                }
                else{
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                }
            }
        });
    }

    private void ChangeUsersBinList(){
        db = FirebaseFirestore.getInstance();
        CollectionReference dbRef = db.collection("User");

        dbRef.document(userIDD).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                DocumentSnapshot document = task.getResult();
                if (document != null){
                    tempUser = document.toObject(User.class);
                    if (tempUser.getRecycleBinList() == null){
                        tempBinList = new ArrayList<>();
                        tempBinList.add(binRefID);
                    }
                    else {
                        tempBinList = tempUser.getRecycleBinList();
                       tempBinList.add(binRefID);
                    }
                    dbRef.document(userIDD).update("recycleBinList", tempBinList).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "DocumentSnapshot successfully updated!");
                        }
                    });
                }
            }
        });
    }

    // This method will help to retrieve the image
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // Match the request 'pic id with requestCode
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == pic_id && resultCode == RESULT_OK) {

            // BitMap is data structure of image file
            // which store the image in memory

                photo = (Bitmap) data.getExtras().get("data");

                // Set the image in imageview for display
                click_image_id.setImageBitmap(photo);

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
        SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        Boolean flag = prefs.getBoolean("isOnCamera", false);
        if(flag){
            editor.clear();
            editor.apply();
        }
        else{
            editor.putBoolean("isOnCamera", false);
        }

        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.CAMERA};

        if(ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[0])== PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[1])==PackageManager.PERMISSION_GRANTED
                && ContextCompat.checkSelfPermission(this.getApplicationContext(),permissions[2])==PackageManager.PERMISSION_GRANTED){
            isCameraPermissionGranted = true;
            allowCamera();
        }else{
            ActivityCompat.requestPermissions(AddBinActivity.this, permissions, pic_id);
        }

    }

    private void AddRecycleBinToDB(ArrayList<Integer> binlist, String description) {
        db = FirebaseFirestore.getInstance();

        // Setting the object which will send to db with informations given by he user
        RecycleBin recycleBin = new RecycleBin();
        recycleBin.setGeopoint(locat);
        if(photo != null){
            String base64String = ImageUtil.convert(photo);
            recycleBin.setPhotograph(base64String);
        }

        HashMap<String, Integer> binTypeTrust = recycleBin.getBinTypeTrust();

        String[] tempList = stringBuilder.toString().split(", ");
        for (int i = 0; i < tempList.length; i++){
            for (int j = 0; j< binTypeArray.length; j++){
                if (tempList[i].equals(binTypeArray[j])){
                    Integer count = binTypeTrust.get(tempList[i]) + 1;
                    binTypeTrust.put(tempList[i], count);
                }

            }
        }
        recycleBin.setBinTypeTrust(binTypeTrust);
        recycleBin.setDescription(description);

        CollectionReference dbBin = db.collection("RecycleBin");
        dbBin.add(recycleBin).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {

                binRefID = documentReference.getId();
                ChangeUsersBinList();
                Toast.makeText(AddBinActivity.this, "Product Added", Toast.LENGTH_LONG).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(AddBinActivity.this, e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void allowCamera() {
        if(isCameraPermissionGranted){
            camera_open_id.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    // Create the camera_intent ACTION_IMAGE_CAPTURE
                    // it will open the camera for capture the image
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    // Start the activity with camera_intent,
                    // and request pic id
                    startActivityForResult(camera_intent, pic_id);
                }
            });
        }
    }
    }
