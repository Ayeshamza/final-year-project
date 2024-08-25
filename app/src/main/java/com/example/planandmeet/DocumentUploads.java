package com.example.planandmeet;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

public class DocumentUploads extends AppCompatActivity {

    ImageView imageBrowse, fileLogo, cancelFile;
    Button uploadBtn;
    Uri filepath;
    EditText fileTitle;

    Event event;

    StorageReference storageReference;
    DatabaseReference databaseReference, eventsReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_document_uploads);

        ImageButton back = findViewById(R.id.backToMain);
        TextView title = findViewById(R.id.title);
        title.setText(R.string.uploadDocumentTitle);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DocumentUploads.this, DocumentsView.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        });

        event = new Event();

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        storageReference = FirebaseStorage.getInstance().getReference();
        databaseReference = FirebaseDatabase.getInstance().getReference("documents");
        eventsReference = FirebaseDatabase.getInstance().getReference("events");

        fileTitle = findViewById(R.id.filetitle);

        imageBrowse = findViewById(R.id.imagebrowse);
        uploadBtn = findViewById(R.id.uploadBtn);

        fileLogo = findViewById(R.id.filelogo);
        cancelFile = findViewById(R.id.cancelfile);

        fileLogo.setVisibility(View.INVISIBLE);
        cancelFile.setVisibility(View.INVISIBLE);

        cancelFile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fileLogo.setVisibility(View.INVISIBLE);
                cancelFile.setVisibility(View.INVISIBLE);
                imageBrowse.setVisibility(View.VISIBLE);
            }
        });

        imageBrowse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Dexter.withContext(getApplicationContext())
                        .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        .withListener(new PermissionListener() {
                            @Override
                            public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                                Intent intent = new Intent();
                                intent.setType("application/pdf");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Pdf Files"), 101);
                            }

                            @Override
                            public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                                permissionToken.continuePermissionRequest();
                            }
                        }).check();
            }
        });

        uploadBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (filepath != null && !filepath.equals(Uri.EMPTY)) {
                    processUpload(filepath);
                    filepath = null;
                } else {
                    Toast.makeText(DocumentUploads.this, "File not Selected!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101 && resultCode == RESULT_OK) {
            filepath = data.getData();
            fileLogo.setVisibility(View.VISIBLE);
            cancelFile.setVisibility(View.VISIBLE);
            imageBrowse.setVisibility(View.INVISIBLE);
        }
    }

    public void processUpload(Uri filepath) {

        ProgressDialog pd = new ProgressDialog(this);

        pd.setTitle("Uploading...");
        pd.setCanceledOnTouchOutside(false);
        pd.show();

        final StorageReference reference = storageReference.child("uploads/" + System.currentTimeMillis() + ".pdf");
        reference.putFile(filepath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {

                                //getting current User id
                                String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

                                //getting event id from clicked card view
                                SharedPreferences sharedPreferences = getSharedPreferences("eventId", MODE_PRIVATE);
                                String eventId = sharedPreferences.getString("eventID", "");

                                DocumentModel documentModel = new DocumentModel();
                                documentModel.setFileName(fileTitle.getText().toString());
                                documentModel.setFileUrl(uri.toString());
                                documentModel.setSenderId(currentUserID);

                                databaseReference.child(eventId).push().setValue(documentModel);

                                fileLogo.setVisibility(View.INVISIBLE);
                                cancelFile.setVisibility(View.INVISIBLE);
                                imageBrowse.setVisibility(View.VISIBLE);
                                fileTitle.setText("");

                                pd.dismiss();
                                toDocumentsView();
                            }
                        });
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        float percent = (100 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
                        pd.setMessage("Uploaded: " + (int) percent + "%");
                    }
                });
    }

    public void toDocumentsView() {
        Toast.makeText(getApplicationContext(), "File Uploaded", Toast.LENGTH_LONG).show();

        Intent intent = new Intent(DocumentUploads.this, DocumentsView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(DocumentUploads.this, DocumentsView.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }
}