package com.example.notepad.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notepad.R;
import com.google.android.material.appbar.MaterialToolbar;

public class SettingsActivity extends AppCompatActivity {

    private String sortMode;
    private Button btnSort;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SeekBar sb = findViewById(R.id.seekFont);
        TextView tvPreview = findViewById(R.id.tvPreview);
        Spinner spFamily = findViewById(R.id.spFamily);
        btnSort = findViewById(R.id.btnSort);
        Button btnSave = findViewById(R.id.btnSave);

        // init
        sb.setMax(30);
        sb.setProgress(SettingsManager.getFontSizeSp(this));
        tvPreview.setTextSize(sb.getProgress());

        String[] families = {"sans","serif","monospace"};
        spFamily.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, families));
        String curFamily = SettingsManager.getFontFamily(this);
        for (int i = 0; i < families.length; i++) {
            if (families[i].equals(curFamily)) spFamily.setSelection(i);
        }

        sortMode = SettingsManager.getSortMode(this);
        btnSort.setText("created".equals(sortMode) ? "زمان ساخت" : "آخرین ویرایش");
        btnSort.setOnClickListener(v -> pickSort());

        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tvPreview.setTextSize(progress);
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) { }
            @Override public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        btnSave.setOnClickListener(v -> {
            SettingsManager.setFontSizeSp(this, sb.getProgress());
            SettingsManager.setFontFamily(this, (String) spFamily.getSelectedItem());
            SettingsManager.setSortMode(this, sortMode);
            finish();
        });
    }

    private void pickSort() {
        String[] names = {"زمان ساخت", "آخرین ویرایش"};
        String[] modes = {"created", "updated"};
        new AlertDialog.Builder(this)
                .setTitle("مرتب‌سازی براساس")
                .setItems(names, (d, which) -> {
                    sortMode = modes[which];
                    btnSort.setText(names[which]);
                }).show();
    }

    @Override public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
