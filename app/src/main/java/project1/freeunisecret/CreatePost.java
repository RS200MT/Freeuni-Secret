package project1.freeunisecret;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.realm.Realm;
import io.realm.RealmList;

import static android.R.attr.button;
import static android.R.attr.key;

public class CreatePost extends AppCompatActivity {
    private static final String TAG = "CreatePost";
    @BindView(R.id.add_photo_btn_id) ImageView addPhoto;
    @BindView(R.id.post_text_edit_text_id) TextInputEditText postText;
    @BindView(R.id.new_post_img) ImageView postImg;
    @BindView(R.id.post_btn_id)
    Button addPostButton;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    FirebaseStorage storage;
    StorageReference storageRef ;    //change the url according to your firebase app
    private String imageUrl = null;
    private Bitmap imgBitMap;
    private static final int REQUEST_IMAGE = 2;
    public static final String FIREBASE_STORAGE_URL = "gs://freeuni-secret.appspot.com/";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReferenceFromUrl(FIREBASE_STORAGE_URL);
        addPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
//                intent.addCategory(Intent.CATEGORY_OPENABLE);
//                intent.setType("image/*");
//                startActivityForResult(intent, REQUEST_IMAGE);
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(galleryIntent, REQUEST_IMAGE);
            }
        });
		postText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if(s.toString().trim().length()==0 && imageUrl == null){
                    addPostButton.setEnabled(false);
                } else {
                    addPostButton.setEnabled(true);
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
    }

    public void addPost(View v){
        String text = postText.getText().toString();
        String userId = user.getUid();
        Post post = new Post();
        post.setText(text);
        post.setCreateTime(new Date().getTime());
        post.setNumComments(0);
        post.setNumHearts(0);
        post.setPostAuthorId(userId);
        if(imageUrl == null){
            post.setImageUrl("");
        } else {
            post.setImageUrl(imageUrl);
        }
        String postId = mFirebaseDatabaseReference.push().getKey();
        post.setId(postId);
        mFirebaseDatabaseReference.child(MainActivity.POST_CHILD)
                .push().setValue(post);
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        Heart heart = realm.createObject(Heart.class);
        heart.setPostId(postId);
        heart.setLovers(new RealmList<RealmString>());
        realm.commitTransaction();
        startActivity(new Intent(this,MainActivity.class));

    }


    @Override
    protected void onResume() {
        super.onResume();
        postImg.setImageBitmap(imgBitMap);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    addPostButton.setEnabled(false);
                    final Uri uri = data.getData();
                    try{

                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                        imgBitMap = bitmap;
                        postImg.setImageBitmap(bitmap);
                        StorageReference childRef = storageRef.child(uri.toString());
                        UploadTask uploadTask = childRef.putFile(uri);

                        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            @SuppressWarnings("VisibleForTests")
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageUrl = taskSnapshot.getDownloadUrl().toString();
                                addPostButton.setEnabled(true);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                addPostButton.setEnabled(true);
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
