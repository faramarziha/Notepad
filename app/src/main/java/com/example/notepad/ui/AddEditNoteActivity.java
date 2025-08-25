package com.example.notepad.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.notepad.R;
import com.example.notepad.data.Note;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditNoteActivity extends AppCompatActivity {

    private NoteViewModel vm;
    private TextInputEditText etTitle, etContent;
    private LinearLayout layoutRoot;

    private int editingId = -1;
    private long createdAt = -1;
    private long updatedAt = -1;
    private int color = Color.WHITE;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        vm = new ViewModelProvider(this).get(NoteViewModel.class);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        layoutRoot = findViewById(R.id.layoutRoot);

        MaterialToolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent() != null && getIntent().hasExtra("id")){
            editingId = getIntent().getIntExtra("id", -1);
            String t = getIntent().getStringExtra("title");
            String c = getIntent().getStringExtra("content");
            createdAt = getIntent().getLongExtra("createdAt", System.currentTimeMillis());
            updatedAt = getIntent().getLongExtra("updatedAt", createdAt);
            color = getIntent().getIntExtra("color", Color.WHITE);
            etTitle.setText(t);
            etContent.setText(c);
            layoutRoot.setBackgroundColor(color);
            setTitle("ویرایش یادداشت");
        } else {
            setTitle("یادداشت جدید");
        }

        applyFontSettings();

        layoutRoot.setBackgroundColor(color);
    }

    private void applyFontSettings() {
        int sp = SettingsManager.getFontSizeSp(this);
        Typeface tf = SettingsManager.getTypeface(this);
        etTitle.setTextSize(sp + 2);
        etContent.setTextSize(sp);
        etTitle.setTypeface(tf);
        etContent.setTypeface(tf);
    }

    @Override public void onBackPressed() {
        save();
    }

    @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            save();
            return true;
        } else if (id == R.id.action_delete) {
            new AlertDialog.Builder(this)
                    .setMessage(getString(R.string.confirm_delete_single))
                    .setPositiveButton("بله", (d, w) -> {
                        if (editingId != -1) {
                            Note n = new Note(etTitle.getText().toString(), etContent.getText().toString(), createdAt, updatedAt, color);
                            n.id = editingId;
                            vm.delete(n);
                        }
                        Toast.makeText(this, getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
                        finish();
                    })
                    .setNegativeButton("خیر", null)
                    .show();
            return true;
        } else if (id == R.id.action_share) {
            share();
            return true;
        } else if (id == R.id.action_color) {
            pickColor();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_edit, menu);
        return true;
    }

    private void share() {
        String t = etTitle.getText() == null ? "" : etTitle.getText().toString().trim();
        String c = etContent.getText() == null ? "" : etContent.getText().toString().trim();
        if (TextUtils.isEmpty(t) && TextUtils.isEmpty(c)) return;
        Intent s = new Intent(Intent.ACTION_SEND);
        s.setType("text/plain");
        s.putExtra(Intent.EXTRA_SUBJECT, t);
        s.putExtra(Intent.EXTRA_TEXT, t + "\n\n" + c);
        startActivity(Intent.createChooser(s, "اشتراک‌گذاری یادداشت"));
    }

    private void pickColor() {
        final int[] colors = {Color.WHITE, 0xFFFFFF99, 0xFFB3E5FC, 0xFFD7CCC8, 0xFFFFCCBC};
        String[] names = {"سفید", "زرد", "آبی", "قهوه‌ای روشن", "نارنجی"};
        new AlertDialog.Builder(this)
                .setTitle("انتخاب رنگ")
                .setItems(names, (d, which) -> {
                    color = colors[which];
                    layoutRoot.setBackgroundColor(color);
                }).show();
    }

    private void save(){
        String t = etTitle.getText() == null ? "" : etTitle.getText().toString().trim();
        String c = etContent.getText() == null ? "" : etContent.getText().toString().trim();

        if (TextUtils.isEmpty(t) && TextUtils.isEmpty(c)) {
            finish();
            return;
        }

        long now = System.currentTimeMillis();

        if (editingId == -1) {
            Note n = new Note(t, c, now, now, color);
            vm.insert(n);
        } else {
            Note n = new Note(t, c, (createdAt<=0?now:createdAt), now, color);
            n.id = editingId;
            vm.update(n);
        }

        finish();
    }
}


