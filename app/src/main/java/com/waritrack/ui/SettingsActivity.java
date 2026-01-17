package com.waritrack.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.content.FileProvider;

import com.waritrack.BuildConfig;
import com.waritrack.R;
import com.waritrack.WariTrackApp;
import com.waritrack.data.Expense;
import com.waritrack.data.ExpenseRepository;
import com.waritrack.util.DateUtils;
import com.waritrack.util.PreferenceUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SettingsActivity extends AppCompatActivity {
    private static final String[] CURRENCIES = {"MAD", "EUR", "USD"};
    private static final String[] DATE_FORMATS = {"dd/MM/yyyy", "yyyy-MM-dd"};

    private ExpenseRepository repository;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setTitle(R.string.settings_title);

        repository = ((WariTrackApp) getApplication()).getRepository();

        Spinner currencySpinner = findViewById(R.id.spinner_currency);
        Spinner dateFormatSpinner = findViewById(R.id.spinner_date_format);
        SwitchCompat privacySwitch = findViewById(R.id.switch_privacy);
        Button exportMonthButton = findViewById(R.id.button_export_month);
        Button exportAllButton = findViewById(R.id.button_export_all);

        ArrayAdapter<String> currencyAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, CURRENCIES);
        currencyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        currencySpinner.setAdapter(currencyAdapter);

        ArrayAdapter<String> dateFormatAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, DATE_FORMATS);
        dateFormatAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateFormatSpinner.setAdapter(dateFormatAdapter);

        String currentCurrency = PreferenceUtils.getCurrency(this);
        currencySpinner.setSelection(indexOf(CURRENCIES, currentCurrency));

        String currentDateFormat = PreferenceUtils.getDateFormat(this);
        dateFormatSpinner.setSelection(indexOf(DATE_FORMATS, currentDateFormat));

        privacySwitch.setChecked(PreferenceUtils.isPrivacyModeEnabled(this));

        currencySpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(position ->
                PreferenceUtils.getPreferences(this)
                        .edit()
                        .putString(PreferenceUtils.KEY_CURRENCY, CURRENCIES[position])
                        .apply()));

        dateFormatSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener(position ->
                PreferenceUtils.getPreferences(this)
                        .edit()
                        .putString(PreferenceUtils.KEY_DATE_FORMAT, DATE_FORMATS[position])
                        .apply()));

        privacySwitch.setOnCheckedChangeListener((buttonView, isChecked) ->
                PreferenceUtils.getPreferences(this)
                        .edit()
                        .putBoolean(PreferenceUtils.KEY_PRIVACY_MODE, isChecked)
                        .apply());

        exportMonthButton.setOnClickListener(v -> exportCurrentMonth());
        exportAllButton.setOnClickListener(v -> exportAll());
    }

    private void exportCurrentMonth() {
        long[] range = getCurrentMonthRange();
        repository.getExpensesBetween(range[0], range[1], expenses -> exportCsv(expenses));
    }

    private void exportAll() {
        repository.getAllExpenses(this::exportCsv);
    }

    private void exportCsv(List<Expense> expenses) {
        File file = createExportFile();
        if (file == null) {
            Toast.makeText(this, R.string.export_failed, Toast.LENGTH_SHORT).show();
            return;
        }

        String currency = PreferenceUtils.getCurrency(this);
        String dateFormat = PreferenceUtils.getDateFormat(this);
        StringBuilder builder = new StringBuilder();
        builder.append("id,date,amount,currency,category,note\n");

        for (Expense expense : expenses) {
            String date = DateUtils.formatDate(expense.getDate(), dateFormat);
            builder.append(expense.getId()).append(',')
                    .append(escapeCsv(date)).append(',')
                    .append(expense.getAmount()).append(',')
                    .append(escapeCsv(currency)).append(',')
                    .append(escapeCsv(expense.getCategory())).append(',')
                    .append(escapeCsv(expense.getNote()))
                    .append('\n');
        }

        try (FileOutputStream outputStream = new FileOutputStream(file)) {
            outputStream.write(builder.toString().getBytes(StandardCharsets.UTF_8));
            shareCsv(file);
            Toast.makeText(this, R.string.export_done, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    private File createExportFile() {
        File exportDir = new File(getExternalFilesDir(null), "exports");
        if (!exportDir.exists() && !exportDir.mkdirs()) {
            return null;
        }
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        return new File(exportDir, "expenses_" + timestamp + ".csv");
    }

    private void shareCsv(File file) {
        Uri uri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".fileprovider", file);
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/csv");
        share.putExtra(Intent.EXTRA_STREAM, uri);
        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(share, getString(R.string.share_csv)));
    }

    private long[] getCurrentMonthRange() {
        Calendar start = Calendar.getInstance();
        start.set(Calendar.DAY_OF_MONTH, 1);
        start.set(Calendar.HOUR_OF_DAY, 0);
        start.set(Calendar.MINUTE, 0);
        start.set(Calendar.SECOND, 0);
        start.set(Calendar.MILLISECOND, 0);

        Calendar end = (Calendar) start.clone();
        end.add(Calendar.MONTH, 1);
        end.add(Calendar.MILLISECOND, -1);

        return new long[]{start.getTimeInMillis(), end.getTimeInMillis()};
    }

    private String escapeCsv(String value) {
        if (value == null) {
            return "";
        }
        String escaped = value.replace("\"", "\"\"");
        return '"' + escaped + '"';
    }

    private int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].equalsIgnoreCase(value)) {
                return i;
            }
        }
        return 0;
    }
}
