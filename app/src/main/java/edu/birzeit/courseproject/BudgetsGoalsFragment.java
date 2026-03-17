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
import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import edu.birzeit.courseproject.data.BudgetRepo;
import edu.birzeit.courseproject.data.CategoryRepo;
import edu.birzeit.courseproject.utils.PrefManager;

public class BudgetsGoalsFragment extends Fragment {

    private PrefManager pref;
    private CategoryRepo catRepo;
    private BudgetRepo budgetRepo;

    private ArrayList<String> budgets;
    private ArrayAdapter<String> bAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_budgets_goals, container, false);

        pref = new PrefManager(getContext());
        catRepo = new CategoryRepo(getContext());
        budgetRepo = new BudgetRepo(getContext());

        String email = pref.getCurrentEmail();

        Spinner spCat = view.findViewById(R.id.spBudgetCategory);
        EditText etLimit = view.findViewById(R.id.etBudgetLimit);
        Button btnAdd = view.findViewById(R.id.btnAddBudget);
        ListView list = view.findViewById(R.id.listBudgets);

        // load expense categories
        ArrayList<String> cats = catRepo.getCategories(email, "EXPENSE");
        spCat.setAdapter(new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, cats));

        // load budgets list
        budgets = new ArrayList<>();
        bAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, budgets);
        list.setAdapter(bAdapter);
        refresh(email);

        btnAdd.setOnClickListener(v -> {
            if (cats.isEmpty()) {
                Toast.makeText(getContext(), "No expense categories", Toast.LENGTH_SHORT).show();
                return;
            }

            String cat = spCat.getSelectedItem().toString();
            String limStr = etLimit.getText().toString().trim();
            if (limStr.isEmpty()) return;

            double lim = Double.parseDouble(limStr);
            budgetRepo.addBudget(email, cat, lim);
            etLimit.setText("");
            refresh(email);
            Toast.makeText(getContext(), "Budget added", Toast.LENGTH_SHORT).show();
        });


        list.setOnItemClickListener((p, v, pos, id) -> {
            String item = budgets.get(pos);

            String[] parts = item.split("\\|");
            int budId = Integer.parseInt(parts[0].replace("#", "").trim());
            String cat = parts[1].trim();
            double limit = Double.parseDouble(parts[2].trim());

            double spent = budgetRepo.getSpentThisMonth(email, cat);
            double remain = limit - spent;


            if (spent >= limit) {
                Toast.makeText(getContext(), "ALERT: Budget exceeded for " + cat, Toast.LENGTH_LONG).show();
            } else if (spent >= 0.5 * limit) {
                Toast.makeText(getContext(), "Warning: 50% budget reached for " + cat, Toast.LENGTH_LONG).show();
            }

            EditText input = new EditText(getContext());
            input.setHint("New limit");
            input.setText(String.valueOf(limit));

            new AlertDialog.Builder(getContext())
                    .setTitle("Edit Budget (" + cat + ")\nSpent this month: " + spent + "\nRemaining: " + remain)
                    .setView(input)
                    .setPositiveButton("Update", (d, w) -> {
                        String s = input.getText().toString().trim();
                        if (s.isEmpty()) return;
                        double newLimit = Double.parseDouble(s);
                        budgetRepo.updateBudget(budId, newLimit);
                        refresh(email);
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });


        list.setOnItemLongClickListener((p, v, pos, id) -> {
            String item = budgets.get(pos);
            int budId = Integer.parseInt(item.split("\\|")[0].replace("#", "").trim());
            budgetRepo.deleteBudget(budId);
            refresh(email);
            return true;
        });

        return view;
    }

    private void refresh(String email){
        budgets.clear();
        budgets.addAll(budgetRepo.getBudgets(email));
        bAdapter.notifyDataSetChanged();
    }
}
