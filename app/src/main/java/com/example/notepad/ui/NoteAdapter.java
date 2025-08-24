package com.example.notepad.ui;

import android.content.Context;
import android.graphics.Typeface;
import android.view.*;
import android.widget.PopupMenu;
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
import java.util.Locale;

public class NoteAdapter extends ListAdapter<Note, NoteAdapter.VH> {

    public interface OnAction {
        void onEdit(Note n);
        void onDelete(Note n);
        void onShare(Note n);
    }

    private OnAction onAction;
    private final Context ctx;

    public NoteAdapter(Context ctx) {
        super(DIFF);
        this.ctx = ctx;
    }

    public void setOnAction(OnAction a){ this.onAction = a; }

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

        h.itemView.setOnClickListener(v -> { if (onAction != null) onAction.onEdit(note); });

        h.itemView.setOnLongClickListener(v -> {
            PopupMenu m = new PopupMenu(ctx, v);
            m.getMenu().add("اشتراک‌گذاری");
            m.getMenu().add("حذف");
            m.setOnMenuItemClickListener(item -> {
                if ("حذف".contentEquals(item.getTitle())) {
                    if (onAction != null) onAction.onDelete(note);
                } else {
                    if (onAction != null) onAction.onShare(note);
                }
                return true;
            });
            m.show();
            return true;
        });
    }

    static class VH extends RecyclerView.ViewHolder {
        TextView tvTitle, tvContent, tvDate;
        VH(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvContent = itemView.findViewById(R.id.tvContent);
            tvDate = itemView.findViewById(R.id.tvDate);
        }
    }

    public Note getAt(int position){ return getItem(position); }
}
