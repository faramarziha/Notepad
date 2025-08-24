package com.example.notepad.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.notepad.R;
import com.example.notepad.data.Note;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class AddEditNoteActivity extends AppCompatActivity {

    private NoteViewModel vm;
    private TextInputEditText etTitle, etContent;

    private int editingId = -1;
    private long createdAt = -1;
    private long updatedAt = -1;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edit_note);

        vm = new ViewModelProvider(this).get(NoteViewModel.class);
        etTitle = findViewById(R.id.etTitle);
        etContent = findViewById(R.id.etContent);
        MaterialButton btnSave = findViewById(R.id.btnSave);

        if (getIntent() != null && getIntent().hasExtra("id")){
            editingId = getIntent().getIntExtra("id", -1);
            String t = getIntent().getStringExtra("title");
            String c = getIntent().getStringExtra("content");
            createdAt = getIntent().getLongExtra("createdAt", System.currentTimeMillis());
            updatedAt = getIntent().getLongExtra("updatedAt", createdAt);
            etTitle.setText(t);
            etContent.setText(c);
            setTitle("ویرایش یادداشت");
        } else {
            setTitle("یادداشت جدید");
        }

        btnSave.setOnClickListener(v -> save());
    }

    private void save(){
        String t = etTitle.getText() == null ? "" : etTitle.getText().toString().trim();
        String c = etContent.getText() == null ? "" : etContent.getText().toString().trim();

        if (TextUtils.isEmpty(t) && TextUtils.isEmpty(c)){
            Toast.makeText(this, "عنوان یا متن را وارد کنید", Toast.LENGTH_SHORT).show();
            return;
        }

        long now = System.currentTimeMillis();

        if (editingId == -1) {
            Note n = new Note(t, c, now, now);
            vm.insert(n);
            Toast.makeText(this, "ذخیره شد", Toast.LENGTH_SHORT).show();
        } else {
            Note n = new Note(t, c, (createdAt<=0?now:createdAt), now);
            n.id = editingId;
            vm.update(n);
            Toast.makeText(this, "ویرایش شد", Toast.LENGTH_SHORT).show();
        }

        finish();
    }
}
