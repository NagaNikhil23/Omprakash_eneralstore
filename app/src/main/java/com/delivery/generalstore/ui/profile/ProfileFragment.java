package com.delivery.generalstore.ui.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import com.delivery.generalstore.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ProfileFragment extends Fragment {

    private ProfileViewModel profileViewModel;
    FirebaseAuth mAuth;
    FirebaseFirestore db ;
    List<String> address_list;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        db= FirebaseFirestore.getInstance();
        address_list=new ArrayList();
        Getuserdetails_from_firestore(mAuth.getCurrentUser().getPhoneNumber()+"@generalstore");
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        profileViewModel =
                ViewModelProviders.of(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView tName = root.findViewById(R.id.text_name);
        final TextView tEmail = root.findViewById(R.id.text_email);
        final TextView tPhone = root.findViewById(R.id.text_phone);
        final Button add_a_address=root.findViewById(R.id.add_address);
        final ListView lAddressList= root.findViewById(R.id.addresses_list);
        profileViewModel.getText().observe(getViewLifecycleOwner(), new Observer<Map<String,Object>>() {
            @Override
            public void onChanged(@Nullable Map<String,Object> map) {
                tName.setText(map.get("Name").toString());
                tEmail.setText(map.get("Email").toString());
                tPhone.setText(map.get("Phone").toString());
                if(map.get("Address")!=null) {
                    Map<String, Object> fromdb_address_map = (Map<String, Object>) map.get("Address");
                    for(String addressname:fromdb_address_map.keySet()){
                            String address=addressname+":"+fromdb_address_map.get(addressname);
                            address_list.add(address);
                    }
                   Log.e("Address",address_list.toString());
                    final ArrayAdapter<String> adapter = new ArrayAdapter<>
                            (getActivity(), android.R.layout.simple_list_item_1, address_list);
                    lAddressList.setAdapter(adapter);
                }
                else{
                    //if No addresses where addred
                    Log.e("No","Address");
                }
            }
        });

        add_a_address.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View view) {
                final AlertDialog.Builder alert = new AlertDialog.Builder(getActivity());
                LinearLayout layout = new LinearLayout(getContext());
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView dialogname=new TextView(getContext());
                dialogname.setText("Add Address");
                dialogname.setTextColor(Color.parseColor("#008040"));
                dialogname.setTextSize(20);
                layout.addView(dialogname);

                final EditText etName = new EditText(getContext());
                etName.setHint("Name for address");
                layout.addView(etName);

                final EditText etHNo = new EditText(getContext());
                etHNo.setHint("House No");
                layout.addView(etHNo);

                final EditText etStreet = new EditText(getContext());
                etStreet.setHint("Street Name/Locality");
                layout.addView(etStreet);

                final EditText etCity = new EditText(getContext());
                etCity.setHint("City");
                etCity.setText("Warangal");
                layout.addView(etCity);

                alert.setView(layout);
                alert.setCancelable(false);

                alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(etHNo.getText().toString().isEmpty() || etStreet.getText().toString().isEmpty() || etCity.getText().toString().isEmpty() || etName.getText().toString().isEmpty()){
                            alert.show();
                        }
                        String address=etHNo.getText().toString()+", "+etStreet.getText().toString()+", "+etCity.getText().toString();
                        Log.e("Address",etHNo.getText().toString()+etStreet.getText().toString()+etCity.getText().toString());
                        db.collection("users").document(mAuth.getCurrentUser().getPhoneNumber()+"@generalstore").update(
                                "address." + etName.getText().toString(),address
                        ).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Log.e("added", "Successfully");
                                Toast.makeText(getContext(), "Added :" + etName.getText() + "", Toast.LENGTH_LONG).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("failed", e + "");
                                Toast.makeText(getContext(), "Unable to add" + e.toString(), Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
                alert.show();
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
                        profileViewModel.setdetails(data.get("Name").toString(),data.get("Email").toString(),data.get("Phone").toString(),(Map<String,Object>)data.get("address"));
                    } else {
                        //Will get this executed if no document was found.
                        Log.d("Get Data", "No such document");
                        profileViewModel.setdetails("No Details found", null, null,null);
                    }
                } else {
                    //Will get this executed if task gets failed due to any reason.
                    Log.d("Get Data", "get failed with ", task.getException());
                    profileViewModel.setdetails("No Details found", null, null,null);
                }
            }
        });
    }

}