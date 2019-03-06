package com.example.abhijeetsingh.learningfirebase1;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private StorageReference mChatPhotosStorageReference;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseStorage mFireBaseStorage;
    static final int RC_SIGN_IN=1;

    Button mPhotoPickerButton;
    private static final int RC_PHOTO_PICKER =  2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        Toolbar mytoolbar=(Toolbar)findViewById(R.id.my_toolbar);
        setSupportActionBar(mytoolbar);

        mFirebaseDatabase=FirebaseDatabase.getInstance();
        mFirebaseAuth=FirebaseAuth.getInstance();
        mFireBaseStorage=FirebaseStorage.getInstance();

        mPhotoPickerButton=(Button)findViewById(R.id.imagepickerbutton);


        mChatPhotosStorageReference=mFireBaseStorage.getReference().child("profile_photos");



//        ImagePickerButton shows an image picker to upload a image for a message
        mPhotoPickerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/jpeg");
                intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);
                startActivityForResult(Intent.createChooser(intent, "Complete action using"), RC_PHOTO_PICKER);
            }
        });







    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        Toast.makeText(MainActivity.this,"menu",Toast.LENGTH_LONG).show();

        getMenuInflater().inflate(R.menu.toolbar_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }






    private FirebaseAuth.AuthStateListener mAuthStateListener = new FirebaseAuth.AuthStateListener() {
        @Override
        public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
            FirebaseUser user=firebaseAuth.getCurrentUser();
            if(user!=null)
            {
//                Toast.makeText(MainActivity.this,"you are now signed in",Toast.LENGTH_SHORT).show();

            }
            else
                {
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder().setIsSmartLockEnabled(false)
                                    .setAvailableProviders(Arrays.asList(
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()
                                            ))
                                    .build(),
                            RC_SIGN_IN);

            }

        }
    };


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==RC_SIGN_IN)
        {
            if(resultCode==RESULT_OK)
                Toast.makeText(this,"signed in",Toast.LENGTH_SHORT).show();
            else if(resultCode==RESULT_CANCELED){
                Toast.makeText(this,"sign in cancelled",Toast.LENGTH_SHORT).show();
            finish();


        }

            else if(requestCode==RC_PHOTO_PICKER &&resultCode==RESULT_OK)
            {
                Uri selectedImageUri=data.getData();
                StorageReference photoref=mChatPhotosStorageReference.child(selectedImageUri.getLastPathSegment());

            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {
            case R.id.action_sign_out:
                {
                    AuthUI.getInstance().signOut(this);
                    return true;

                }

            default: super.onOptionsItemSelected(item);
        }

        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}
