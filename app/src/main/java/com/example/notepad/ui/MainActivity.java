package com.example.notepad.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notepad.R;
import com.example.notepad.data.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel vm;
    private NoteAdapter adapter;
    private List<Note> allNotes = new ArrayList<>();

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
        vm.getNotes().observe(this, notes -> {
            allNotes = new ArrayList<>(notes);
            adapter.submitList(new ArrayList<>(allNotes));
        });

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

    @Override public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView sv = (SearchView) searchItem.getActionView();
        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override public boolean onQueryTextSubmit(String query) { return false; }
            @Override public boolean onQueryTextChange(String newText) {
                filterNotes(newText);
                return true;
            }
        });
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override public boolean onMenuItemActionExpand(MenuItem item) { return true; }
            @Override public boolean onMenuItemActionCollapse(MenuItem item) {
                adapter.submitList(new ArrayList<>(allNotes));
                return true;
            }
        });
        return true;
    }

    @Override public boolean onOptionsItemSelected(MenuItem item) {
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

    private void filterNotes(String q) {
        if (q == null || q.trim().isEmpty()) {
            adapter.submitList(new ArrayList<>(allNotes));
            return;
        }
        String lower = q.toLowerCase();
        List<Note> filtered = new ArrayList<>();
        for (Note n : allNotes) {
            String t = n.title != null ? n.title.toLowerCase() : "";
            String c = n.content != null ? n.content.toLowerCase() : "";
            if (t.contains(lower) || c.contains(lower)) {
                filtered.add(n);
            }
        }
        adapter.submitList(filtered);
    }

    @Override protected void onResume() {
        super.onResume();
        // اگر کاربر در Settings فونت/سورت تغییر داد، سورت را دوباره اعمال کن
        vm.setSortMode(SettingsManager.getSortMode(this));
        adapter.notifyDataSetChanged();
    }
}
