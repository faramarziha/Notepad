package com.example.notepad.ui;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.notepad.R;

public class SettingsActivity extends AppCompatActivity {

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        SeekBar sb = findViewById(R.id.seekFont);
        TextView tvPreview = findViewById(R.id.tvPreview);
        Spinner spFamily = findViewById(R.id.spFamily);
        RadioButton rbCreated = findViewById(R.id.rbCreated);
        RadioButton rbUpdated = findViewById(R.id.rbUpdated);
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

        String sort = SettingsManager.getSortMode(this);
        if ("created".equals(sort)) rbCreated.setChecked(true); else rbUpdated.setChecked(true);

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
            SettingsManager.setSortMode(this, rbCreated.isChecked() ? "created" : "updated");
            finish();
        });
    }
}
