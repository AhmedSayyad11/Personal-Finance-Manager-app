package edu.birzeit.courseproject;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import edu.birzeit.courseproject.data.CategoryRepo;
import edu.birzeit.courseproject.utils.PrefManager;

public class SettingsFragment extends Fragment {

    private PrefManager pref;
    private CategoryRepo catRepo;

    private ArrayAdapter<String> catAdapter;
    private ArrayList<String> catList;

    private String email;
    private String currentType = "EXPENSE";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        pref = new PrefManager(getContext());
        catRepo = new CategoryRepo(getContext());

        email = pref.getCurrentEmail();

        Spinner spDefault = view.findViewById(R.id.spDefaultPeriod);
        Button btnSavePeriod = view.findViewById(R.id.btnSavePeriod);

        Spinner spTheme = view.findViewById(R.id.spTheme);
        Button btnApplyTheme = view.findViewById(R.id.btnApplyTheme);

        Spinner spCatType = view.findViewById(R.id.spCatType);
        EditText etNew = view.findViewById(R.id.etNewCategory);
        Button btnAdd = view.findViewById(R.id.btnAddCategory);
        ListView list = view.findViewById(R.id.listCategories);


        List<String> periods = Arrays.asList("DAY", "WEEK", "MONTH");
        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, periods);
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spDefault.setAdapter(pAdapter);

        String def = pref.getDefaultPeriod();
        int idx = periods.indexOf(def);
        if (idx >= 0) spDefault.setSelection(idx);

        btnSavePeriod.setOnClickListener(v -> {
            String selected = spDefault.getSelectedItem().toString();
            pref.setDefaultPeriod(selected);
            Toast.makeText(getContext(), "Saved: " + selected, Toast.LENGTH_SHORT).show();
        });


        List<String> themes = Arrays.asList("LIGHT", "DARK");
        ArrayAdapter<String> tAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, themes);
        tAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spTheme.setAdapter(tAdapter);

        String savedTheme = pref.getTheme();
        int tidx = themes.indexOf(savedTheme);
        if (tidx >= 0) spTheme.setSelection(tidx);

        btnApplyTheme.setOnClickListener(v -> {
            String th = spTheme.getSelectedItem().toString();
            pref.setTheme(th);

            if (th.equals("DARK")) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }

            requireActivity().recreate();
        });


        ArrayList<String> types = new ArrayList<>();
        types.add("EXPENSE");
        types.add("INCOME");
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, types);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCatType.setAdapter(typeAdapter);

        catList = new ArrayList<>();
        catAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, catList);
        list.setAdapter(catAdapter);


        spCatType.setSelection(0);
        loadCats("EXPENSE");

        spCatType.setOnItemSelectedListener(new android.widget.AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(android.widget.AdapterView<?> parent, View v, int position, long id) {
                currentType = types.get(position);
                loadCats(currentType);
            }
            @Override public void onNothingSelected(android.widget.AdapterView<?> parent) { }
        });

        btnAdd.setOnClickListener(v -> {
            String name = etNew.getText().toString().trim();
            if (name.isEmpty()) return;

            catRepo.addCategory(email, currentType, name);
            etNew.setText("");
            loadCats(currentType);
        });


        list.setOnItemClickListener((parent, v, position, id) -> {
            String oldName = catList.get(position);

            EditText input = new EditText(getContext());
            input.setText(oldName);

            new AlertDialog.Builder(getContext())
                    .setTitle("Edit Category")
                    .setView(input)
                    .setPositiveButton("Update", (d, w) -> {
                        String newName = input.getText().toString().trim();
                        if (newName.isEmpty()) return;
                        catRepo.updateCategory(email, currentType, oldName, newName);
                        loadCats(currentType);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        list.setOnItemLongClickListener((parent, v, position, id) -> {
            String name = catList.get(position);
            catRepo.deleteCategory(email, currentType, name);
            loadCats(currentType);
            return true;
        });

        return view;
    }

    private void loadCats(String type){
        catList.clear();
        catList.addAll(catRepo.getCategories(email, type));
        catAdapter.notifyDataSetChanged();
    }
}
