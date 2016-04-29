package bupt.icyicarus.nevernote.views;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import java.util.ArrayList;

import bupt.icyicarus.nevernote.NoteAdapter;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.SetPortrait;
import bupt.icyicarus.nevernote.noteList.NoteListCellData;

/**
 * Created by IcyIcarus on 2016/4/28.
 */
public class DebugView extends SetPortrait {
    private ArrayList<NoteListCellData> noteList;
    private RecyclerView mRecyclerView;
    private NoteAdapter mNoteAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_debug);
        mRecyclerView = (RecyclerView) findViewById(R.id.mRecyclerView);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mNoteAdapter = new NoteAdapter(generateData());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mNoteAdapter);
        mNoteAdapter.setOnItemClickListener(new NoteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view) {
                Log.e("Click", ((NoteListCellData) view.getTag()).id + "");
            }
        });
        mNoteAdapter.setOnItemLongClickListener(new NoteAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(View view) {
                Log.e("LongClick", ((NoteListCellData) view.getTag()).content);
            }
        });
    }

    public ArrayList<NoteListCellData> generateData() {
        ArrayList<NoteListCellData> list = new ArrayList<>();
        for (int i = 1; i < 10; i++)
            list.add(new NoteListCellData("name" + i, "date" + i, "content" + 1, i));
        return list;
    }
}
