package com.example.notepad.ui;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.example.notepad.R;
import com.example.notepad.data.Note;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private NoteViewModel vm;
    private NoteAdapter adapter;
    private List<Note> allNotes = new ArrayList<>();
    private Toolbar selectionBar;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar tb = findViewById(R.id.toolbar);
        setSupportActionBar(tb);

        selectionBar = findViewById(R.id.selectionBar);
        selectionBar.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_delete_sel) {
                int count = adapter.getSelectedCount();
                String msg = count == 1 ? getString(R.string.confirm_delete_single)
                        : getString(R.string.confirm_delete_multiple, count);
                new AlertDialog.Builder(this)
                        .setMessage(msg)
                        .setPositiveButton("بله", (d, w) -> {
                            for (Note n : adapter.getSelectedNotes()) { vm.delete(n); }
                            adapter.clearSelection();
                            String toast = count == 1 ? MainActivity.this.getString(R.string.note_deleted)
                                    : MainActivity.this.getString(R.string.notes_deleted, count);
                            Toast.makeText(MainActivity.this, toast, Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("خیر", null)
                        .show();
                return true;
            } else if (id == R.id.action_share_sel) {
                StringBuilder sb = new StringBuilder();
                for (Note n : adapter.getSelectedNotes()) {
                    sb.append(n.title).append("\n").append(n.content).append("\n\n");
                }
                Intent s = new Intent(Intent.ACTION_SEND);
                s.setType("text/plain");
                s.putExtra(Intent.EXTRA_TEXT, sb.toString().trim());
                startActivity(Intent.createChooser(s, "اشتراک‌گذاری یادداشت‌ها"));
                adapter.clearSelection();
                return true;
            } else if (id == R.id.action_select_all) {
                adapter.toggleSelectAll();
                if (adapter.isSelectionMode()) {
                    selectionBar.setTitle(adapter.getSelectedCount() + " انتخاب شده");
                }
                return true;
            }
            return false;
        });

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

        adapter.setCallback(new NoteAdapter.Callback() {
            @Override public void onEdit(Note n) {
                Intent i = new Intent(MainActivity.this, AddEditNoteActivity.class);
                i.putExtra("id", n.id);
                i.putExtra("title", n.title);
                i.putExtra("content", n.content);
                i.putExtra("createdAt", n.createdAt);
                i.putExtra("updatedAt", n.updatedAt);
                i.putExtra("color", n.color);
                startActivity(i);
            }

            @Override public void onSelectionChanged(int count) {
                if (count == 0) {
                    selectionBar.setVisibility(View.GONE);
                    selectionBar.setTitle("");
                } else {
                    selectionBar.setVisibility(View.VISIBLE);
                    selectionBar.setTitle(count + " انتخاب شده");
                }
            }
        });

        // سوایپ برای حذف
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override public boolean onMove(RecyclerView r, RecyclerView.ViewHolder vH, RecyclerView.ViewHolder t) { return false; }

            @Override public void onSwiped(RecyclerView.ViewHolder vH, int dir) {
                int pos = vH.getAdapterPosition();
                Note n = adapter.getAt(pos);
                adapter.notifyItemChanged(pos);
                new AlertDialog.Builder(MainActivity.this)
                        .setMessage(MainActivity.this.getString(R.string.confirm_delete_single))
                        .setPositiveButton("بله", (d, w) -> {
                            vm.delete(n);
                            Toast.makeText(MainActivity.this, MainActivity.this.getString(R.string.note_deleted), Toast.LENGTH_SHORT).show();
                        })
                        .setNegativeButton("خیر", null)
                        .show();
            }

            @Override public boolean isItemViewSwipeEnabled() {
                return !adapter.isSelectionMode();
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
        if (id == R.id.action_sort) {
            pickSort();
            return true;
        } else if (id == R.id.action_creator) {
            Toast.makeText(this, "پیروزی", Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void pickSort() {
        String[] names = {"زمان ساخت", "آخرین ویرایش"};
        String[] modes = {"created", "updated"};
        new AlertDialog.Builder(this)
                .setTitle("مرتب‌سازی براساس")
                .setItems(names, (d, which) -> {
                    SettingsManager.setSortMode(this, modes[which]);
                    vm.setSortMode(modes[which]);
                }).show();
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
        vm.setSortMode(SettingsManager.getSortMode(this));
        adapter.notifyDataSetChanged();
    }

    @Override public void onBackPressed() {
        if (adapter != null && adapter.isSelectionMode()) {
            adapter.clearSelection();
        } else {
            super.onBackPressed();
        }
    }
}
