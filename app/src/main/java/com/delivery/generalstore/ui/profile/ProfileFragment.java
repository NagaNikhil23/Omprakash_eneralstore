package com.delivery.generalstore.ui.profile;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.delivery.generalstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    FirebaseAuth mAuth;
    FirebaseFirestore db ;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        Getuserdetails_from_firestore(mAuth.getCurrentUser().getPhoneNumber()+"@generalstore");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });
        return root;
    }

    private void Getuserdetails_from_firestore(String username) {
        db.collection("users").document(username).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Map<String, Object> data= document.getData();
                        //setting details to profile view model.
                        Log.e("profile",data.toString());
                        profileViewModel.setdetails(data.get("Name").toString(),data.get("Email").toString(),data.get("Phone").toString());
                    } else {
                        //Will get this executed if no document was found.
                        Log.d("Get Data", "No such document");
                        profileViewModel.setdetails("No Details found", null, null);
                    }
                } else {
                    //Will get this executed if task gets failed due to any reason.
                    Log.d("Get Data", "get failed with ", task.getException());
                    profileViewModel.setdetails("No Details found", null, null);
                }
            }
        });
    }

}