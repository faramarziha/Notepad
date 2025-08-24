package com.example.notepad.ui;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notepad.R;
import com.example.notepad.data.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel vm;
    private NoteAdapter adapter;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        RecyclerView rv = findViewById(R.id.recyclerNotes);
        FloatingActionButton fab = findViewById(R.id.fabAdd);

        adapter = new NoteAdapter(this);
        rv.setAdapter(adapter);

        vm = new ViewModelProvider(this).get(NoteViewModel.class);
        vm.getNotes().observe(this, notes -> adapter.submitList(notes));

        fab.setOnClickListener(v -> startActivity(new Intent(this, AddEditNoteActivity.class)));

        adapter.setOnAction(new NoteAdapter.OnAction() {
            @Override public void onEdit(Note n) {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                i.putExtra("id", n.id);
                i.putExtra("title", n.title);
                i.putExtra("content", n.content);
                i.putExtra("createdAt", n.createdAt);
                i.putExtra("updatedAt", n.updatedAt);
                startActivity(i);
            }

            @Override public void onDelete(Note n) {
                vm.delete(n);
                Snackbar.make(rv, "یادداشت حذف شد", Snackbar.LENGTH_LONG)
                        .setAction("برگردان", v -> vm.insert(n)).show();
            }

            @Override public void onShare(Note n) {
                Intent s = new Intent(Intent.ACTION_SEND);
                s.setType("text/plain");
                s.putExtra(Intent.EXTRA_SUBJECT, n.title);
                s.putExtra(Intent.EXTRA_TEXT, n.title + "\n\n" + n.content);
                startActivity(Intent.createChooser(s, "اشتراک‌گذاری یادداشت"));
            }
        });

        // سوایپ برای حذف
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder vH, RecyclerView.ViewHolder t) { return false; }
            @Override public void onSwiped(RecyclerView.ViewHolder vH, int dir) {
                Note n = adapter.getAt(vH.getAdapterPosition());
                vm.delete(n);
                Snackbar.make(rv, "یادداشت حذف شد", Snackbar.LENGTH_LONG)
                        .setAction("برگردان", v -> vm.insert(n)).show();
            }
        }).attachToRecyclerView(rv);
    }

    @Override public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_sort_created) {
            SettingsManager.setSortMode(this, "created");
            vm.setSortMode("created");
            return true;
        } else if (id == R.id.action_sort_updated) {
            SettingsManager.setSortMode(this, "updated");
            vm.setSortMode("updated");
            return true;
        } else if (id == R.id.action_delete_all) {
            vm.deleteAll();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override protected void onResume() {
        super.onResume();
        // اگر کاربر در Settings فونت/سورت تغییر داد، سورت را دوباره اعمال کن
        vm.setSortMode(SettingsManager.getSortMode(this));
        adapter.notifyDataSetChanged();
    }
}
