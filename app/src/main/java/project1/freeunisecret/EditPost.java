package project1.freeunisecret;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;

import static android.R.attr.button;
import static android.R.attr.data;
import static project1.freeunisecret.CreatePost.FIREBASE_STORAGE_URL;
import static project1.freeunisecret.MainActivity.POST_CHILD;

/**
 * Created by Tornike on 01-Aug-17.
 */

public class EditPost extends AppCompatActivity {

    private static final int REQUEST_IMAGE = 2;
    @BindView(R.id.edit_post_text)
    EditText postTextET;
    @BindView(R.id.edit_post_image)
    ImageView postImageView;
    @BindView(R.id.edit_post_add_image) ImageView addImage;
    @BindView(R.id.save_button)
    Button save;

    String postText;
    String imageUrl;
    String postId;
    String  initImageUrl;
    FirebaseStorage storage;
    StorageReference storageRef ;
    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstances){
        super.onCreate(savedInstances);
        setContentView(R.layout.activity_edit_post);
        ButterKnife.bind(this);
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(CreatePost.FIREBASE_STORAGE_URL);
        postText = getIntent().getStringExtra("postText");
        imageUrl = getIntent().getStringExtra("imageUrl");
        initImageUrl = imageUrl;
        postTextET.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0 && imageUrl.isEmpty()){
                    save.setEnabled(false);
                } else {
                    save.setEnabled(true);
                }


            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });
        postId = getIntent().getStringExtra("postId");
        if(postText != null)
            postTextET.setText(postText);
        if(!imageUrl.isEmpty()){
            postImageView.setVisibility(ImageView.VISIBLE);
            Picasso.with(this).load(imageUrl).into(postImageView);
        }
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                savePost();
            }
        });
    }



    private void savePost() {
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        final Query query = mFirebaseDatabaseReference.child(POST_CHILD).orderByChild("id");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                String key = dataSnapshot.getKey();
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    String value = child.getValue().toString();
                    if(postId.equals(value)){
                        mFirebaseDatabaseReference.child(MainActivity.POST_CHILD).child(key).child("text").setValue(postTextET.getText().toString());
                        mFirebaseDatabaseReference.child(MainActivity.POST_CHILD).child(key).child("imageUrl").setValue(imageUrl);
                        startActivity(new Intent(EditPost.this,MainActivity.class));
                    }
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String TAG = "EditPost";
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    save.setEnabled(false);
                    final Uri uri = data.getData();
                        try{

                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                            postImageView.setVisibility(ImageView.VISIBLE);
                            postImageView.setImageBitmap(bitmap);
                            StorageReference childRef = storageRef.child(uri.toString());
                            UploadTask uploadTask = childRef.putFile(uri);

                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                @SuppressWarnings("VisibleForTests")
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    imageUrl = taskSnapshot.getDownloadUrl().toString();
                                    save.setEnabled(true);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    save.setEnabled(true);
                                }
                            });
                        } catch (Exception ex){

                        }
                    Log.d(TAG, "Uri: " + uri.toString());
                }
            }
        }
    }
}
