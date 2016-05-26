package bupt.icyicarus.nevernote.noteList;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.init.Initialization;

public class NoteList extends Initialization {
    private MaterialListView mlvNoteList;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private long noteDate = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.aty_note_list);

        Toolbar tbMain = (Toolbar) findViewById(R.id.tbNoteList);
        setSupportActionBar(tbMain);

        mlvNoteList = (MaterialListView) findViewById(R.id.mlvNoteList);

        noteDate = getIntent().getLongExtra("noteDate", -1);

        findViewById(R.id.fabmNoteListAddNote).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddPhoto).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddAudio).setOnClickListener(fabClickHandler);
        findViewById(R.id.fabmNoteListAddVideo).setOnClickListener(fabClickHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        PublicVariableAndMethods.refreshNoteArrayList(NoteList.this, mlvNoteList, getResources(), noteListCellDataArrayList, noteDate);
        if (customBackground) {
            findViewById(R.id.cNoteList).setBackgroundColor(Integer.parseInt(backgroundColor));
        } else {
            findViewById(R.id.cNoteList).setBackgroundColor(Color.WHITE);
        }
    }
}
