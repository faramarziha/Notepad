package com.example.notepad.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.*;
import android.widget.CheckBox;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import com.example.notepad.R;
import com.example.notepad.data.Note;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.VH> {

    public interface Callback {
        void onEdit(Note n);
        void onSelectionChanged(int count);
    }

    private Callback callback;
    private final Context ctx;
    private boolean selectionMode = false;
    private final Set<Integer> selected = new HashSet<>();

    public NoteAdapter(Context ctx) {
        super(DIFF);
        this.ctx = ctx;
    }

    public void setCallback(Callback c){ this.callback = c; }

    private static final DiffUtil.ItemCallback<Note> DIFF = new DiffUtil.ItemCallback<Note>() {
        @Override public boolean areItemsTheSame(@NonNull Note o, @NonNull Note n) { return o.id == n.id; }
        @Override public boolean areContentsTheSame(@NonNull Note o, @NonNull Note n) {
            return o.title.equals(n.title) && o.content.equals(n.content)
                    && o.createdAt==n.createdAt && o.updatedAt==n.updatedAt
                    && o.color == n.color;
        }
    };

    @NonNull @Override public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new VH(v);
    }

    @Override public void onBindViewHolder(@NonNull VH h, int pos) {
        Note note = getItem(pos);
        h.tvTitle.setText(note.title);
        h.tvContent.setText(note.content);

        String when = note.updatedAt > 0 && note.updatedAt != note.createdAt
                ? new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date(note.updatedAt)) + " • ویرایش‌شده"
                : new SimpleDateFormat("yyyy/MM/dd HH:mm", Locale.getDefault()).format(new Date(note.createdAt));
        h.tvDate.setText(when);

        // اعمال تنظیمات فونت
        int sp = SettingsManager.getFontSizeSp(ctx);
        h.tvTitle.setTextSize(sp + 2);
        h.tvContent.setTextSize(sp);
        h.tvDate.setTextSize(Math.max(sp - 2, 10));

        Typeface tf = SettingsManager.getTypeface(ctx);
        h.tvTitle.setTypeface(tf);
        h.tvContent.setTypeface(tf);
        h.tvDate.setTypeface(tf);

        ((MaterialCardView) h.itemView).setCardBackgroundColor(note.color);

        h.cb.setVisibility(selectionMode ? View.VISIBLE : View.GONE);
        h.cb.setChecked(selected.contains(note.id));

        h.itemView.setOnClickListener(v -> {
            if (selectionMode) {
                toggleSelection(note.id);
            } else if (callback != null) {
                callback.onEdit(note);
            }
        });

        h.itemView.setOnLongClickListener(v -> {
            if (!selectionMode) {
                selectionMode = true;
                selected.add(note.id);
                notifyDataSetChanged();
                if (callback != null) callback.onSelectionChanged(selected.size());
                return true;
            }
            return false;
        });

        h.cb.setOnClickListener(v -> toggleSelection(note.id));
    }

    private void toggleSelection(int id) {
        if (selected.contains(id)) {
            selected.remove(id);
            if (selected.isEmpty()) {
                selectionMode = false;
            }
        } else {
            selected.add(id);
        }
        notifyDataSetChanged();
        if (callback != null) callback.onSelectionChanged(selected.size());
    }

    public void clearSelection() {
        selectionMode = false;
        selected.clear();
        notifyDataSetChanged();
        if (callback != null) callback.onSelectionChanged(0);
    }

    public List<Note> getSelectedNotes() {
        List<Note> list = new java.util.ArrayList<>();
        for (int i = 0; i < getItemCount(); i++) {
            Note n = getItem(i);
            if (selected.contains(n.id)) list.add(n);
        }
        return list;
    }

    public boolean isSelectionMode(){ return selectionMode; }

    public int getSelectedCount() { return selected.size(); }

    public boolean areAllSelected() {
        return selectionMode && selected.size() == getItemCount() && getItemCount() > 0;
    }

    public void toggleSelectAll() {
        if (areAllSelected()) {
            clearSelection();
            return;
        }
        selectionMode = true;
        selected.clear();
        for (int i = 0; i < getItemCount(); i++) {
            selected.add(getItem(i).id);
        }
        notifyDataSetChanged();
        if (callback != null) callback.onSelectionChanged(selected.size());
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;
        CheckBox cb;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
            cb = itemView.findViewById(R.id.cbSelect);
        }
    }

    public Note getAt(int position){ return getItem(position); }
}
