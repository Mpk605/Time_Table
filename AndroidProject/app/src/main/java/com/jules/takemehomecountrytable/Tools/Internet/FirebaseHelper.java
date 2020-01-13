package com.jules.takemehomecountrytable.Tools.Internet;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;

public class FirebaseHelper {
    private final String TAG = "FirebaseHelper";
    private FirebaseFirestore firestoreInstance;
    private FirebaseStorage storageInstance;
    private FirebaseAuth authInstance;
    private static FirebaseHelper helperInstance;

    private FirebaseHelper(Context context) {
        FirebaseApp.initializeApp(context);
        firestoreInstance = FirebaseFirestore.getInstance();
        storageInstance = FirebaseStorage.getInstance();
        authInstance = FirebaseAuth.getInstance();
    }

    public static FirebaseHelper getHelperInstance(Context context) {
        if (helperInstance != null)
            return helperInstance;

        return new FirebaseHelper(context);
    }

    public void authenticateFirebaseUser(String email, String password) {
        authInstance.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            authInstance.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d(TAG, "signedin:success");
                                    } else {
                                        Log.d(TAG, "signedin:failure");
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void downloadFileFromStorage(Context context, String filePath) {
        StorageReference storageReference = storageInstance.getReference();
        StorageReference imageRef = storageReference.child(filePath);
        File localFile = new File(context.getFilesDir(), filePath);

        try {
            imageRef.getFile(localFile).addOnCompleteListener(new OnCompleteListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<FileDownloadTask.TaskSnapshot> task) {
                    if (!task.isSuccessful()) {
                        task.getException().printStackTrace();
                        return;
                    }

                    Log.d("Storage", task.getResult().toString());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
