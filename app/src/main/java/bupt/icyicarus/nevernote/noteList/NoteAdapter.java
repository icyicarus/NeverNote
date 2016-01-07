package bupt.icyicarus.nevernote.noteList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import bupt.icyicarus.nevernote.R;

public class NoteAdapter extends BaseAdapter {

    private Context context;

    private List<NoteListCellData> list = new ArrayList<>();

    public NoteAdapter(Context context) {
        this.context = context;
    }

    public void Add(NoteListCellData data) {
        list.add(data);
    }

    public void Remove(int id) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i).id == id) {
                list.remove(i);
            }
        }
    }

    public void Clear() {
        list.removeAll(list);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public NoteListCellData getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.notes_list_cell, null);
        }

        NoteListCellData data = getItem(position);

        TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
        TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);

        tvName.setText(data.name);
        tvDate.setText(data.date);

        return convertView;
    }
}
