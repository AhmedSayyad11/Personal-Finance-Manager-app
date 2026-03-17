package edu.birzeit.courseproject;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import edu.birzeit.courseproject.data.UserRepo;
import edu.birzeit.courseproject.utils.PrefManager;

public class ProfileFragment extends Fragment {

    private PrefManager pref;
    private UserRepo userRepo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        pref = new PrefManager(getContext());
        userRepo = new UserRepo(getContext());

        String email = pref.getCurrentEmail();

        TextView tvEmail = view.findViewById(R.id.tvEmail);
        EditText etFirst = view.findViewById(R.id.etFirstName);
        EditText etLast = view.findViewById(R.id.etLastName);
        Button btnSaveName = view.findViewById(R.id.btnSaveName);

        EditText etOld = view.findViewById(R.id.etOldPassword);
        EditText etNew = view.findViewById(R.id.etNewPassword);
        EditText etCon = view.findViewById(R.id.etConfirmPassword);
        Button btnChange = view.findViewById(R.id.btnChangePassword);

        tvEmail.setText("Email: " + email);

        // load name
        String[] name = userRepo.getUserName(email);
        etFirst.setText(name[0]);
        etLast.setText(name[1]);

        btnSaveName.setOnClickListener(v -> {
            String f = etFirst.getText().toString().trim();
            String l = etLast.getText().toString().trim();

            if (f.isEmpty() || l.isEmpty()){
                Toast.makeText(getContext(), "First & last name required", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = userRepo.updateUserName(email, f, l);
            Toast.makeText(getContext(), ok ? "Saved" : "Failed", Toast.LENGTH_SHORT).show();
        });

        btnChange.setOnClickListener(v -> {
            String oldP = etOld.getText().toString().trim();
            String newP = etNew.getText().toString().trim();
            String conP = etCon.getText().toString().trim();

            if (oldP.isEmpty() || newP.isEmpty() || conP.isEmpty()){
                Toast.makeText(getContext(), "Fill all password fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if (!newP.equals(conP)){
                Toast.makeText(getContext(), "New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }
            if (newP.length() < 4){
                Toast.makeText(getContext(), "Password too short", Toast.LENGTH_SHORT).show();
                return;
            }

            boolean ok = userRepo.changePassword(email, oldP, newP);
            if (ok){
                Toast.makeText(getContext(), "Password changed", Toast.LENGTH_SHORT).show();
                etOld.setText("");
                etNew.setText("");
                etCon.setText("");
            } else {
                Toast.makeText(getContext(), "Wrong old password", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }
}
