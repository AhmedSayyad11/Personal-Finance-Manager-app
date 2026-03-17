package edu.birzeit.courseproject.ui.home;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import edu.birzeit.courseproject.R;
import edu.birzeit.courseproject.data.TransactionRepo;
import edu.birzeit.courseproject.utils.PrefManager;

public class HomeFragment extends Fragment {

    private PrefManager pref;
    private TransactionRepo repo;

    private String startDate;
    private String endDate;

    // Report cache
    private double lastInc = 0, lastExp = 0, lastBal = 0;
    private String lastPeriod = "DAY";

    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);

        pref = new PrefManager(getContext());
        repo = new TransactionRepo(getContext());

        String email = pref.getCurrentEmail();

        Spinner sp = view.findViewById(R.id.spPeriod);
        Button btnStart = view.findViewById(R.id.btnStart);
        Button btnEnd = view.findViewById(R.id.btnEnd);
        Button btnApply = view.findViewById(R.id.btnApply);
        Button btnReport = view.findViewById(R.id.btnReport);

        TextView tvRange = view.findViewById(R.id.tvRange);
        TextView tvIncomeTotal = view.findViewById(R.id.tvIncomeTotal);
        TextView tvExpenseTotal = view.findViewById(R.id.tvExpenseTotal);
        TextView tvBalance = view.findViewById(R.id.tvBalance);

        ListView listIncomeCats = view.findViewById(R.id.listIncomeCats);
        ListView listExpenseCats = view.findViewById(R.id.listExpenseCats);

        PieChart pieChart = view.findViewById(R.id.pieChart);
        BarChart barChart = view.findViewById(R.id.barChart);

        ArrayList<String> periods = new ArrayList<>();
        periods.add("DAY");
        periods.add("WEEK");
        periods.add("MONTH");
        periods.add("CUSTOM");

        ArrayAdapter<String> pAdapter = new ArrayAdapter<>(
                getContext(),
                android.R.layout.simple_spinner_item,
                periods
        );
        pAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sp.setAdapter(pAdapter);

        // default = preference
        String def = pref.getDefaultPeriod();
        int idx = periods.indexOf(def);
        if (idx >= 0) sp.setSelection(idx);

        // init range based on selected
        setRangeByPeriod(periods.get(sp.getSelectedItemPosition()));
        tvRange.setText("Range: " + startDate + " → " + endDate);

        btnStart.setOnClickListener(v -> pickDate(d -> {
            startDate = d;
            tvRange.setText("Range: " + startDate + " → " + endDate);
        }));

        btnEnd.setOnClickListener(v -> pickDate(d -> {
            endDate = d;
            tvRange.setText("Range: " + startDate + " → " + endDate);
        }));

        btnApply.setOnClickListener(v -> {
            String p = sp.getSelectedItem().toString();
            if (!p.equals("CUSTOM")) setRangeByPeriod(p);

            tvRange.setText("Range: " + startDate + " → " + endDate);

            double inc = repo.getTotalBetween(email, "INCOME", startDate, endDate);
            double exp = repo.getTotalBetween(email, "EXPENSE", startDate, endDate);
            double bal = inc - exp;

            // cache for report
            lastInc = inc;
            lastExp = exp;
            lastBal = bal;
            lastPeriod = p;

            tvIncomeTotal.setText(String.format(Locale.getDefault(), "Total Income: %.2f", inc));
            tvExpenseTotal.setText(String.format(Locale.getDefault(), "Total Expenses: %.2f", exp));
            tvBalance.setText(String.format(Locale.getDefault(), "Balance: %.2f", bal));

            ArrayList<String> incCats = repo.getCategoryTotalsBetween(email, "INCOME", startDate, endDate);
            ArrayList<String> expCats = repo.getCategoryTotalsBetween(email, "EXPENSE", startDate, endDate);

            listIncomeCats.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, incCats));
            listExpenseCats.setAdapter(new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, expCats));

            // =======================
            // Pie: Income vs Expenses
            // =======================
            ArrayList<PieEntry> pieEntries = new ArrayList<>();
            pieEntries.add(new PieEntry((float) inc, "Income"));
            pieEntries.add(new PieEntry((float) exp, "Expenses"));

            PieDataSet pieDataSet = new PieDataSet(pieEntries, "Income vs Expenses");
            PieData pieData = new PieData(pieDataSet);
            pieChart.setData(pieData);
            pieChart.getDescription().setEnabled(false);
            pieChart.invalidate();

            // =======================
            // Bar: Expenses by Category
            // =======================
            ArrayList<BarEntry> bars = new ArrayList<>();

            for (int i = 0; i < expCats.size(); i++) {
                // expected format: "Category : total"
                String row = expCats.get(i);
                String[] parts = row.split(":");
                if (parts.length < 2) continue;

                float val;
                try {
                    val = Float.parseFloat(parts[1].trim());
                } catch (Exception ex1) {
                    // fallback if there are extra characters/spaces
                    try {
                        val = Float.parseFloat(parts[1].replaceAll("[^0-9.\\-]", ""));
                    } catch (Exception ex2) {
                        continue;
                    }
                }

                bars.add(new BarEntry(i, val));
            }

            BarDataSet barDataSet = new BarDataSet(bars, "Expenses by Category");
            BarData barData = new BarData(barDataSet);
            barChart.setData(barData);
            barChart.getDescription().setEnabled(false);
            barChart.invalidate();
        });

        // Report button
        btnReport.setOnClickListener(v -> {
            String report =
                    "FINANCIAL REPORT\n" +
                            "-----------------------------\n" +
                            "User: " + email + "\n" +
                            "Period: " + lastPeriod + "\n" +
                            "Range: " + startDate + " → " + endDate + "\n\n" +
                            String.format(Locale.getDefault(), "Total Income: %.2f\n", lastInc) +
                            String.format(Locale.getDefault(), "Total Expenses: %.2f\n", lastExp) +
                            String.format(Locale.getDefault(), "Balance: %.2f\n", lastBal);

            new AlertDialog.Builder(getContext())
                    .setTitle("Report")
                    .setMessage(report)
                    .setPositiveButton("OK", null)
                    .show();
        });

        // auto apply once
        btnApply.performClick();

        return view;
    }

    private void setRangeByPeriod(String p) {
        Calendar cal = Calendar.getInstance();
        endDate = fmt.format(cal.getTime());

        if (p.equals("DAY")) {
            startDate = endDate;
        } else if (p.equals("WEEK")) {
            cal.add(Calendar.DAY_OF_MONTH, -6);
            startDate = fmt.format(cal.getTime());
        } else { // MONTH
            cal.add(Calendar.DAY_OF_MONTH, -29);
            startDate = fmt.format(cal.getTime());
        }
    }

    private interface DateCb { void onDate(String d); }

    private void pickDate(DateCb cb) {
        Calendar cal = Calendar.getInstance();
        new DatePickerDialog(
                getContext(),
                (picker, year, month, day) -> {
                    Calendar c = Calendar.getInstance();
                    c.set(year, month, day);
                    cb.onDate(fmt.format(c.getTime()));
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
        ).show();
    }
}
