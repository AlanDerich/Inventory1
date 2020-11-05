package com.nduta.inventory;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Loginpage extends AppCompatActivity {
    private Context mContext;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private List<UserDetails> mUserr;
    private static final int RC_SIGN_IN = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loginpage);
        mContext =this;
        // getConnectivityStatus(mContext);
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        if (mUser==null){
            if (getConnectivityStatus(mContext)){
                themeAndLogo();
            }
            else {
                Toast.makeText(this, "No network connection!", Toast.LENGTH_SHORT).show();
            }}
        else {
            Toast.makeText(this, "Welcome back " + mUser.getEmail(), Toast.LENGTH_SHORT).show();
            checkUser();
        }
    }
    public static boolean getConnectivityStatus(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (null != activeNetwork) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
                return true;
            if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
                return true;
        }
        Toast.makeText(context, "No network connection!", Toast.LENGTH_SHORT).show();
        return false;
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                checkUser();
            }
            if(resultCode == RESULT_CANCELED){
                displayMessage(("Sign in failed. Please try again later."));
                finish();
            }
            return;
        }
        displayMessage("Unknown response");
    }

    private void checkUser() {
        mUserr= new ArrayList<>();
        final FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        db.collectionGroup("registeredUsers").whereEqualTo("username",mUser.getEmail()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    for (DocumentSnapshot snapshot : queryDocumentSnapshots) {
                        mUserr.add(snapshot.toObject(UserDetails.class));
                    }
                    int size = mUserr.size();
                    int position;
                    if (size==1){
                        position=0;
                        UserDetails userDetails= mUserr.get(position);
                        String namee=userDetails.getUsername();
                        String section=userDetails.getSection();
                        if (section.equals("admin")){
                            Intent intent=new Intent(Loginpage.this, NormalUserActivity.class);
                            startActivity(intent);
                        }
                        else if (section.equals("simpleUser")){
                            Intent intentLogin=new Intent(Loginpage.this, MainActivity.class);
                            startActivity(intentLogin);
                        }
                        else {
                            Toast.makeText(Loginpage.this,"Error validating details. Please login again",Toast.LENGTH_LONG).show();
                            finish();
                        }
                    }

                } else {
                    String username =mUser.getEmail();
                    String section = "simpleUser";
                    UserDetails newUser = new UserDetails(username,section);
                    Toast.makeText(Loginpage.this,"No data found.",Toast.LENGTH_LONG).show();
                    db.collection("users").document("all users").collection("registeredUsers").document(mUser.getEmail())
                            .set(newUser)
                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    startActivity(new Intent(Loginpage.this, MainActivity.class));
                                    Toast.makeText(Loginpage.this,"User added successfully",Toast.LENGTH_LONG).show();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(Loginpage.this,"Not saved. Try again later.",Toast.LENGTH_LONG).show();
                                }
                            });

                }
            }
        })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(Loginpage.this,"Something went terribly wrong." + e,Toast.LENGTH_LONG).show();
                        Log.d("LoginAct","Error" + e);
                    }
                });
    }

    private void displayMessage(String message){
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }

    // [END auth_fui_result]
    private void themeAndLogo() {
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());
        // [START auth_fui_theme_logo]
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setLogo(R.drawable.med)      // Set logo drawable
                        .setTosAndPrivacyPolicyUrls(
                                "https://example.com/terms.html",
                                "https://example.com/privacy.html")
                        .build(),
                RC_SIGN_IN);
        // [END auth_fui_theme_logo]
    }
}