package bupt.icyicarus.nevernote;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import bupt.icyicarus.nevernote.noteList.NoteListCellData;

/**
 * Created by IcyIcarus on 2016/4/28.
 */
public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.ViewHolder> {
    private ArrayList<NoteListCellData> mNoteList = null;
    private OnItemClickListener mOnItemClickListener = null;
    private OnItemLongClickListener mOnItemLongClickListener = null;

    public NoteAdapter(ArrayList<NoteListCellData> noteList) {
        this.mNoteList = noteList;
    }


    @Override
    public NoteAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.onItemClick(v);
            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (mOnItemLongClickListener != null)
                    mOnItemLongClickListener.onItemLongClick(v);
                return true;
            }
        });
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(NoteAdapter.ViewHolder holder, int position) {
        NoteListCellData noteData = mNoteList.get(position);
        holder.bindData(noteData);
        holder.itemView.setTag(noteData);
    }

    @Override
    public int getItemCount() {
        return mNoteList.size();
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener listener) {
        this.mOnItemLongClickListener = listener;
    }

    public interface OnItemClickListener {
        void onItemClick(View view);
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(View view);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private TextView noteTitle;
        private TextView noteDate;

        public ViewHolder(View itemView) {
            super(itemView);
            noteTitle = (TextView) itemView.findViewById(R.id.noteTitle);
            noteDate = (TextView) itemView.findViewById(R.id.noteDate);
        }

        public void bindData(NoteListCellData noteData) {
            if (noteData != null) {
                noteTitle.setText(noteData.name);
                noteDate.setText(noteData.date);
            }
        }
    }
}
