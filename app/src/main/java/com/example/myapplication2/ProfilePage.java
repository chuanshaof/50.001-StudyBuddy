package com.example.myapplication2;

import static com.example.myapplication2.utils.Utils.getDocumentFromPath;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.myapplication2.objectmodel.ModuleModel;
import com.example.myapplication2.utils.FirebaseContainer;
import com.example.myapplication2.utils.LoggedInUser;
import com.example.myapplication2.utils.Utils;
import com.example.myapplication2.viewholder.ProfileRecyclerAdapter;
import com.example.myapplication2.viewholder.ProfileViewModel;

import com.example.myapplication2.objectmodel.ProfileModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Objects;


public class ProfilePage extends AppCompatActivity {
    private static final String TAG = "ProfilePage";

    //Objects to handle data from Firebase
    FirebaseFirestore db;
    String profileDocumentId;
    final FirebaseContainer<ProfileModel> profile = new FirebaseContainer<>(new ProfileModel());
    final FirebaseContainer<ModuleModel> module = new FirebaseContainer<>(new ModuleModel());

    //View UI elements
    ImageView profilePicture;
    TextView profileName;
    TextView pillarValue;
    TextView termValue;
    TextView bioText;

    //Interactive UI elements
    ImageView backArrow;
    Button logOutButton;
    Button editButton;
    RecyclerView recyclerView;

    //RecyclerView components
    ProfileRecyclerAdapter adapter;
    ArrayList<ProfileViewModel> arrModules = new ArrayList<>();

    //Shared Preferences to store Objects as a String
    SharedPreferences sharedPrefs;
    SharedPreferences.Editor prefsEditor;
    DocumentReference profileRef;


    //Button interactions in Profile Page Activity
    class ClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            //initialise Shared Preferences and Editor
            sharedPrefs = getSharedPreferences("PROFILE_PAGE", MODE_PRIVATE);
            prefsEditor = sharedPrefs.edit();
            switch (view.getId()) {
                case R.id.backArrow:
                    startActivity((new Intent(ProfilePage.this, MainPageActivity.class)));
                    break;
                case R.id.logOutButton:
                    prefsEditor.clear();
                    prefsEditor.apply();
                    Intent logOutIntent = new Intent(ProfilePage.this, LoginActivity.class);
                    logOutIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(logOutIntent);
                    finish();
                    break;
                case R.id.editButton:
                    prefsEditor.putString("PROFILE_ID", profileDocumentId);
                    prefsEditor.apply();
                    Log.i(TAG, "PROFILE_ID has been added to prefsEditor");
                    Intent editIntent = new Intent(ProfilePage.this, EditProfilePage.class);
                    startActivity(editIntent);
                    break;
                default:
                    Log.w(TAG, "Button not Found");
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_page);
        Log.i(TAG, "onCreate is called");

        //initialise Firestore db
        db = FirebaseFirestore.getInstance();

        sharedPrefs = getSharedPreferences("PROFILE_PAGE", MODE_PRIVATE);
        prefsEditor = sharedPrefs.edit();

        // getting Profile Id from viewEvents TODO : let issac know about this
        profileRef = getProfileRef();


//        //Fetch Data from Profile Collection
//        //TODO Wire up Profile Document ID from preceding activity
//        profileDocumentId = "Test";
//        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
//        getProfileData(profileRef);

        //initialise UI elements
        backArrow = findViewById(R.id.backArrow);
        logOutButton = findViewById(R.id.logOutButton);
        profilePicture = findViewById(R.id.profilePicture);
        profileName = findViewById(R.id.profileName);
        pillarValue = findViewById(R.id.pillarValue);
        termValue = findViewById(R.id.termValue);
        bioText = findViewById(R.id.bioText);
        editButton = findViewById(R.id.editButton);


        //Check whether user is not checking his own profile
        LoggedInUser user = LoggedInUser.getInstance();
        if (profileDocumentId != user.getUserString()) {
            editButton.setVisibility(View.GONE);
            logOutButton.setVisibility(View.GONE);
        }

        //Get Profile Data from Firestore
//      Note to issac: I have refactored this out in function getProfileRef()  DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        getProfileData(profileRef);

        //initialise RecyclerView elements for Modules Section
        recyclerView = findViewById(R.id.recyclerProfile);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new ProfileRecyclerAdapter(this, arrModules);
        recyclerView.setAdapter(adapter);

        //initialise buttons
        backArrow.setOnClickListener(new ClickListener());
        logOutButton.setOnClickListener(new ClickListener());
        editButton.setOnClickListener(new ClickListener());
    }

    private DocumentReference getProfileRef() {
        profileDocumentId = getIntent().getStringExtra("PROFILE_ID");

        //Fetch ProfileDocumentId from Intent, Return to previous activity if String is null
        if (profileDocumentId == null) {
            Log.w(TAG, "Profile Document ID is null");
            profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
            finish();
        } else {
            Log.i(TAG, "Profile Document ID Retrieved: " + profileDocumentId);
            profileRef = getDocumentReference(ProfileModel.getCollectionId(), getDocumentFromPath(profileDocumentId));
            Log.i(TAG, "Profile Name : " + profileRef);
        }
        return profileRef;
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart is called");
        prefsEditor.putString("PROFILE_ID", profileDocumentId);
        prefsEditor.apply();
        Log.i(TAG, "PROFILE_ID has been added to prefsEditor");

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart is called");
        arrModules.clear();

        DocumentReference profileRef = getDocumentReference(ProfileModel.getCollectionId(), profileDocumentId);
        getProfileData(profileRef);
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "onResume is called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.i(TAG, "onPause is called");
    }


    //Set UI Elements using data from Firebase
    public void setUIElements(ProfileModel profile) {
        //Set Text
        profileName.setText(profile.getName());
        pillarValue.setText(profile.getPillar());
        termValue.setText(String.valueOf(profile.getTerm()));
        bioText.setText(profile.getBio());

        //Set Image
        setImage(profile.getImagePath());
    }

    //Set Image for ImageView
    public void setImage(String imageURL) {
        Picasso.get().load(imageURL).resize(120, 120).centerCrop().transform(new Utils.CircleTransform()).into(profilePicture);
        Log.i(TAG, "Profile Picture set");
    }

    //Firebase-Specific Methods
    public DocumentReference getDocumentReference(String collectionId, String documentId) {
        return db.collection(collectionId).document(documentId);
    }


    //Get Data from Profiles Collection
    public void getProfileData(DocumentReference profileRef) {
        profileRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot document) {
                if (document.exists()) {
                    Log.i(TAG, "File Path in Firebase: " + profileRef.getPath());
                    Log.i(ProfileModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ProfileModel.class)));
                    ProfilePage.this.profile.set(document.toObject(ProfileModel.class));
                    ProfilePage.this.setUIElements(ProfilePage.this.profile.get());
                    addModuleToRecyclerView();
                } else {
                    Log.w(TAG, "Document does not exist");
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error retrieving document from Firestore", e);
            }
        });
    }

    //Add Module Data Retrieved
    public void addModuleToRecyclerView() {
        for (DocumentReference moduleRef : profile.get().getModules()) {
            if (moduleRef != null) {
                moduleRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot document) {
                        if (document.exists()) {
                            Log.i(TAG, "File Path in Firebase: " + moduleRef.getPath());
                            Log.i(ModuleModel.TAG, "Contents of Firestore Document: " + Objects.requireNonNull(document.toObject(ModuleModel.class)));
                            ProfilePage.this.module.set(document.toObject(ModuleModel.class));

                            //Add modules from Firestore DocumentReference to Recycler View
                            arrModules.add(new ProfileViewModel(ProfilePage.this.module.get()));
                            ProfilePage.this.adapter.notifyItemInserted(arrModules.size());
                        } else {
                            Log.w(TAG, "Document does not exist");
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error retrieving document from Firestore", e);
                    }
                });
            }

        }
    }
}