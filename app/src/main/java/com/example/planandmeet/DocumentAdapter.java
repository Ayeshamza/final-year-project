package com.example.planandmeet;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

public class DocumentAdapter extends RecyclerView.Adapter<DocumentAdapter.myViewHolder> {

    Context context;
    ArrayList<DocumentModel> documentModelList;
    ArrayList<String> documentIdList, userIDs;
    String eventId;

    public DocumentAdapter(Context context, ArrayList<DocumentModel> documentModelList, ArrayList<String> documentIdList, String eventId, ArrayList<String> userIDs) {
        this.context = context;
        this.documentModelList = documentModelList;
        this.documentIdList = documentIdList;
        this.eventId = eventId;
        this.userIDs = userIDs;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull myViewHolder holder, @SuppressLint("RecyclerView") int position) {

        DocumentModel document = documentModelList.get(position);
        holder.name.setText(document.getFileName());

        //getting current User id
        String currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        if (userIDs.get(0).equals(currentUserID) || currentUserID.equals(document.getSenderId())) {
            holder.deleteDoc.setVisibility(View.VISIBLE);
        } else {
            holder.deleteDoc.setVisibility(View.GONE);
        }

        holder.pdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(holder.pdf.getContext(), ViewPdf.class);
                intent.putExtra("filename", document.getFileName());
                intent.putExtra("fileurl", document.getFileUrl());

                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                holder.pdf.getContext().startActivity(intent);
            }
        });

        holder.downloadDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DownloadManager downloadManager = (DownloadManager) context.
                        getSystemService(Context.DOWNLOAD_SERVICE);
                Uri uri = Uri.parse(document.getFileUrl());

                DownloadManager.Request request = new DownloadManager.Request(uri);

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(context, "PlanAndMeet", document.getFileName() + ".pdf");
                downloadManager.enqueue(request);

            }
        });

        holder.deleteDoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(holder.name.getContext());
                builder.setTitle("Are You Sure?");

                builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StorageReference storageReference = FirebaseStorage.getInstance().getReferenceFromUrl(document.getFileUrl());
                        storageReference.delete()
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        removeDocumentIds();
                                        Toast.makeText(holder.name.getContext(), "Document Deleted Successfully", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull @NotNull Exception e) {
                                        Toast.makeText(holder.name.getContext(), "Deletion Failed!", Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(holder.name.getContext(), "Deletion Cancelled", Toast.LENGTH_SHORT).show();
                    }
                });
                builder.show();
            }

            private void removeDocumentIds() {
                //remove from documents table
                FirebaseDatabase.getInstance().getReference().child("documents").child(eventId)
                        .child(documentIdList.get(position)).removeValue();
            }
        });
    }

    @NonNull
    @NotNull
    @Override
    public myViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.singlerowdesign, parent, false);
        return new myViewHolder(view);
    }

    @Override
    public int getItemCount() {
        return documentModelList.size();
    }

    public static class myViewHolder extends RecyclerView.ViewHolder {

        ImageView pdf;
        TextView name;

        ImageView downloadDoc, deleteDoc;

        public myViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);

            pdf = itemView.findViewById(R.id.pdf);
            name = itemView.findViewById(R.id.header);

            downloadDoc = itemView.findViewById(R.id.downloadDocument);
            deleteDoc = itemView.findViewById(R.id.deleteDocument);
        }
    }
}