package com.example.admin.contentprovider;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int MY_PERMISSIONS_REQUEST_READ_CONTACTS = 10;
    private static final String TAG = "mainActivity";
    RecyclerView rvContacts;
    RecyclerView.LayoutManager layoutManager;
    RecyclerViewAdapter recyclerViewAdapter;
    List<Contact> contactList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvContacts = findViewById(R.id.rvContacts);
        layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new RecyclerViewAdapter(contactList);

        rvContacts.setLayoutManager(layoutManager);
        rvContacts.setAdapter(recyclerViewAdapter);

        checkPermission();
    }


    private void retrievePhoneContacts(){
        Uri ContentURL = ContactsContract.Contacts.CONTENT_URI;
        String DISPLAY_NAME = ContactsContract.Contacts.DISPLAY_NAME;
        String HAS_PHONE_NUMBER = ContactsContract.Contacts.HAS_PHONE_NUMBER;

        Cursor cursor = getContentResolver().query(ContentURL, null, null, null, DISPLAY_NAME + " ASC");
        int hasPhone = 0;

        while(cursor.moveToNext()){
            String contactName = cursor.getString(cursor.getColumnIndex(DISPLAY_NAME));
            hasPhone = Integer.parseInt(cursor.getString(cursor.getColumnIndex(HAS_PHONE_NUMBER)));

            if(hasPhone>0){
                Log.d(TAG, "retrievePhoneContacts: " + contactName);

                Uri PhoneURI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                String NUMBER = ContactsContract.CommonDataKinds.Phone.NUMBER;

                String[] projection = new String[]{NUMBER};
                String selection = DISPLAY_NAME + "=?";
                String[] selectArgs = new String[]{contactName};

                Cursor phoneCursor = getContentResolver().query(PhoneURI, projection, selection, selectArgs, null);
                List<String> phoneNumbers = new ArrayList<>();;

                while(phoneCursor.moveToNext()){
                    String phoneNumber = phoneCursor.getString(phoneCursor.getColumnIndex(NUMBER));
                    Log.d(TAG, "retrievePhoneContacts: " + phoneNumber);
                    phoneNumbers.add(phoneNumber);
                }

                contactList.add(new Contact(contactName, phoneNumbers));
            }

            Log.d(TAG, "retrievePhoneContacts: " + contactList.size());
            recyclerViewAdapter.notifyDataSetChanged();
        }
    }


    private void checkPermission() {
        // Here, thisActivity is the current activity
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "onCreate: Does not have permission");
        // Should we show an explanation?
               if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_CONTACTS)) {
                   showRequestRationals();

            // Show an explanation to the user *asynchronously* -- don't block
            // this thread waiting for the user's response! After the user
            // sees the explanation, try again to request the permission.

                } else {

                 // No explanation needed, we can request the permission.

                   requestContactsPermission();
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                   // app-defined int constant. The callback method gets the
                    // result of the request.
             }
        } else {
            Log.d(TAG, "onCreate: Permisson is already granted");
            retrievePhoneContacts();
        }
    }

    private void showRequestRationals() {
        Log.d(TAG, "onCreate: Should show rationale");

        AlertDialog alertDialog = new AlertDialog.Builder(this)
                .setTitle("Explination")
                .setMessage("Please allow this Permission to read contacts")
                .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestContactsPermission();
                    }
                }).setNegativeButton("Deny", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(MainActivity.this, "Tink Again", Toast.LENGTH_SHORT).show();
                    }
                })
                .create();
        alertDialog.show();
    }

    private void requestContactsPermission() {
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, MY_PERMISSIONS_REQUEST_READ_CONTACTS);
        Log.d(TAG, "onCreate: Requesting permission");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_CONTACTS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    retrievePhoneContacts();
                    Log.d(TAG, "onRequestPermissionsResult: Permisson granted");
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {
                    Log.d(TAG, "onRequestPermissionsResult: Permission denied");
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}
