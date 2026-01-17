package com.waritrack.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.waritrack.R;
import com.waritrack.WariTrackApp;
import com.waritrack.data.models.Category;

public class AddEditCategoryActivity extends AppCompatActivity {
    private AddEditCategoryViewModel viewModel;
    private EditText nameInput;
    private Spinner colorSpinner;
    private long editingCategoryId = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_category);

        ViewModelFactory factory = new ViewModelFactory(((WariTrackApp) getApplication()).getRepository());
        viewModel = new ViewModelProvider(this, factory).get(AddEditCategoryViewModel.class);

        nameInput = findViewById(R.id.edit_category_name);
        colorSpinner = findViewById(R.id.spinner_category_color);
        Button saveButton = findViewById(R.id.button_save_category);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category_color_names,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        colorSpinner.setAdapter(adapter);

        if (getIntent() != null && getIntent().hasExtra(CategoriesActivity.EXTRA_CATEGORY_ID)) {
            editingCategoryId = getIntent().getLongExtra(CategoriesActivity.EXTRA_CATEGORY_ID, -1);
            setTitle(R.string.edit_category_title);
            viewModel.getCategory(editingCategoryId).observe(this, category -> {
                if (category != null) {
                    populate(category);
                }
            });
        } else {
            setTitle(R.string.add_category_title);
        }

        saveButton.setOnClickListener(v -> saveCategory());
    }

    private void populate(Category category) {
        nameInput.setText(category.getName());
        String[] colorValues = getResources().getStringArray(R.array.category_color_values);
        for (int i = 0; i < colorValues.length; i++) {
            if (colorValues[i].equalsIgnoreCase(category.getColorHex())) {
                colorSpinner.setSelection(i);
                return;
            }
        }
    }

    private void saveCategory() {
        String name = nameInput.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            nameInput.setError(getString(R.string.error_category_name_required));
            return;
        }

        String[] colorValues = getResources().getStringArray(R.array.category_color_values);
        String colorHex = colorValues[colorSpinner.getSelectedItemPosition()];

        viewModel.isNameExists(name, editingCategoryId, exists -> {
            if (exists) {
                nameInput.setError(getString(R.string.error_category_name_exists));
                return;
            }

            if (editingCategoryId > 0) {
                viewModel.update(editingCategoryId, name, colorHex);
            } else {
                viewModel.insert(name, colorHex);
            }

            Toast.makeText(this, R.string.category_saved, Toast.LENGTH_SHORT).show();
            finish();
        });
    }
}
