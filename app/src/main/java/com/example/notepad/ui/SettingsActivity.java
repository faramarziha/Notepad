package com.example.notepad.ui;

import android.app.AlertDialog;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView;

import androidx.core.content.res.ResourcesCompat;
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

        String[] labels = {"IRANSansDN_Fa_Num","Tahrir_Bold","IRANSansDN_FaNum_Bold","Anjoman_SemiBold","javan","IRANYekanBold"};
        String[] families = {"iransansdn_fa_num","tahrir_bold","iransansdn_fanum_bold","anjoman_semibold","javan","iranyekan_bold"};
        spFamily.setAdapter(new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, labels));
        String curFamily = SettingsManager.getFontFamily(this);
        int sel = 0;
        for (int i = 0; i < families.length; i++) {
            if (families[i].equals(curFamily)) { sel = i; break; }
        }
        spFamily.setSelection(sel);
        int fontId = getResources().getIdentifier(families[sel], "font", getPackageName());
        if(fontId != 0) tvPreview.setTypeface(ResourcesCompat.getFont(this, fontId));

        spFamily.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override public void onItemSelected(AdapterView<?> parent, android.view.View view, int position, long id) {
                int fid = getResources().getIdentifier(families[position], "font", getPackageName());
                if(fid != 0) tvPreview.setTypeface(ResourcesCompat.getFont(SettingsActivity.this, fid));
            }
            @Override public void onNothingSelected(AdapterView<?> parent) { }
        });

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
            SettingsManager.setFontFamily(this, families[spFamily.getSelectedItemPosition()]);
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
