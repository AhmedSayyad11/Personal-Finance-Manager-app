package edu.birzeit.courseproject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import edu.birzeit.courseproject.data.CategoryRepo;
import edu.birzeit.courseproject.data.TransactionRepo;
import edu.birzeit.courseproject.utils.PrefManager;

public class ExpensesFragment extends Fragment {

    private TransactionRepo repo;
    private PrefManager pref;
    private ArrayAdapter<String> adapter;
    private ArrayList<String> list;

    private ArrayAdapter<String> catAdapter;
    private ArrayList<String> cats;

    private String selectedDate;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_expenses, container, false);

        repo = new TransactionRepo(getContext());
        pref = new PrefManager(getContext());

        Spinner spCat = view.findViewById(R.id.spCategoryExpense);

        EditText etAmount = view.findViewById(R.id.etAmountExp);
        EditText etDesc = view.findViewById(R.id.etDescExp);

        Button btnPickDate = view.findViewById(R.id.btnPickDateExp);
        TextView tvDate = view.findViewById(R.id.tvDateExp);

        Button btnAdd = view.findViewById(R.id.btnAddExpense);
        ListView listView = view.findViewById(R.id.listExpenses);

        String email = pref.getCurrentEmail();

        CategoryRepo catRepo = new CategoryRepo(getContext());
        catRepo.seedDefaultsIfEmpty(email);

        cats = catRepo.getCategories(email, "EXPENSE");
        catAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, cats);
        catAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spCat.setAdapter(catAdapter);

        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText("Date: " + selectedDate);

        btnPickDate.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(
                    getContext(),
                    (picker, year, month, day) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        selectedDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime());
                        tvDate.setText("Date: " + selectedDate);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dp.show();
        });

        list = repo.getTransactionsSortedByDate(email, "EXPENSE");
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, list);
        listView.setAdapter(adapter);

        btnAdd.setOnClickListener(v -> {
            if (cats.isEmpty()) {
                Toast.makeText(getContext(), "Add categories in Settings first", Toast.LENGTH_SHORT).show();
                return;
            }

            String cat = spCat.getSelectedItem().toString();
            String amtStr = etAmount.getText().toString().trim();
            String desc = etDesc.getText().toString().trim();

            if (amtStr.isEmpty()) {
                Toast.makeText(getContext(), "Amount required", Toast.LENGTH_SHORT).show();
                return;
            }

            double amt = Double.parseDouble(amtStr);

            repo.addTransaction(email, "EXPENSE", cat, amt, desc, selectedDate);

            refresh(email);

            etAmount.setText("");
            etDesc.setText("");
        });

        listView.setOnItemClickListener((parent, v, pos, id) -> {
            String item = list.get(pos);
            String[] parts = item.split("\\|");
            int transId = Integer.parseInt(parts[0].replace("#", "").trim());
            String oldCat = parts[1].trim();
            String oldAmt = parts[2].trim();
            String oldDate = parts[3].trim();
            String oldNote = parts.length >= 5 ? parts[4].trim() : "";

            showEditDialog(transId, oldCat, oldAmt, oldDate, oldNote, email);
        });

        listView.setOnItemLongClickListener((parent, v, pos, id) -> {
            String item = list.get(pos);
            int transId = Integer.parseInt(item.split("\\|")[0].replace("#", "").trim());

            repo.deleteTransaction(transId);
            refresh(email);
            return true;
        });

        return view;
    }

    private void refresh(String email) {
        list.clear();
        list.addAll(repo.getTransactionsSortedByDate(email, "EXPENSE"));
        adapter.notifyDataSetChanged();
    }

    private void showEditDialog(int transId, String oldCat, String oldAmt, String oldDate,
                                String oldNote, String email) {

        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_edit_transaction, null);

        EditText etCat = dialogView.findViewById(R.id.etEditCategory);
        EditText etAmt = dialogView.findViewById(R.id.etEditAmount);
        EditText etNote = dialogView.findViewById(R.id.etEditDesc);
        TextView tvDate = dialogView.findViewById(R.id.tvEditDate);
        Button btnPick = dialogView.findViewById(R.id.btnEditPickDate);

        etCat.setText(oldCat);
        etAmt.setText(oldAmt);
        etNote.setText(oldNote);

        final String[] chosenDate = {oldDate};
        tvDate.setText("Date: " + chosenDate[0]);

        btnPick.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dp = new DatePickerDialog(
                    getContext(),
                    (picker, year, month, day) -> {
                        Calendar c = Calendar.getInstance();
                        c.set(year, month, day);
                        chosenDate[0] = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(c.getTime());
                        tvDate.setText("Date: " + chosenDate[0]);
                    },
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
            );
            dp.show();
        });

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Expense")
                .setView(dialogView)
                .setPositiveButton("Update", (d, which) -> {
                    String newCat = etCat.getText().toString().trim();
                    String newAmtStr = etAmt.getText().toString().trim();
                    String newNote = etNote.getText().toString().trim();

                    if (newCat.isEmpty() || newAmtStr.isEmpty()) {
                        Toast.makeText(getContext(), "Category/Amount required", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    double newAmt = Double.parseDouble(newAmtStr);
                    repo.updateTransaction(transId, newCat, newAmt, newNote, chosenDate[0]);
                    refresh(email);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }
}
