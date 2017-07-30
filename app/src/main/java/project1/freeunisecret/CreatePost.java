package project1.freeunisecret;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnCompleteListener;
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

import static android.R.attr.key;

public class CreatePost extends AppCompatActivity {
    private static final String TAG = "CreatePost";
    @BindView(R.id.add_photo_btn_id) ImageView addPhoto;
    @BindView(R.id.post_text_edit_text_id) TextInputEditText postText;
    @BindView(R.id.new_post_img) ImageView postImg;
    private DatabaseReference mFirebaseDatabaseReference;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    private String imageUrl = null;

    private static final int REQUEST_IMAGE = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_post);
        ButterKnife.bind(this);
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mFirebaseAuth = FirebaseAuth.getInstance();
        user = mFirebaseAuth.getCurrentUser();
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
        post.setImageUrl(imageUrl);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: requestCode=" + requestCode + ", resultCode=" + resultCode);

        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    final Uri uri = data.getData();
                    String imgPath = getRealPathFromURI(uri);
                    File img = new File(imgPath);
                    if(img.exists()) {
                        Bitmap bitmap = BitmapFactory.decodeFile(img.getAbsolutePath());
                        postImg.setImageBitmap(bitmap);
                        postImg.setVisibility(ImageView.VISIBLE);
                    }
                    Log.d(TAG, "Uri: " + uri.toString());
                    StorageReference storageReference =
                            FirebaseStorage.getInstance()
                                    .getReference(user.getUid())
                                    .child(uri.getLastPathSegment());
                    putImageInStorage(storageReference, uri);
                }
            }
        }
    }

    private String getRealPathFromURI(Uri contentURI) {
        String result;
        Cursor cursor = getContentResolver().query(contentURI, null, null, null, null);
        if (cursor == null) { // Source is Dropbox or other similar local file path
            result = contentURI.getPath();
        } else {
            cursor.moveToFirst();
            int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
            result = cursor.getString(idx);
            cursor.close();
        }
        return result;
    }

    @SuppressWarnings("VisibleForTests")
    private void putImageInStorage(StorageReference storageReference, Uri uri) {
        storageReference.putFile(uri).addOnCompleteListener(CreatePost.this,
                new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                             imageUrl = task.getResult().getMetadata().getDownloadUrl()
                                    .toString();
                        } else {
                            Log.w(TAG, "Image upload task was not successful.",
                                    task.getException());
                        }
                    }
                });
    }

}
