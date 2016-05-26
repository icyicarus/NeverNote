package bupt.icyicarus.nevernote.fragment;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.view.MaterialListView;

import java.util.ArrayList;

import bupt.icyicarus.nevernote.PublicVariableAndMethods;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.noteList.NoteListCellData;

public class NoteListFragment extends Fragment {

    private MaterialListView mlvOverView;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private NeverNoteDB db;
    private SQLiteDatabase dbRead, dbWrite;

    public static NoteListFragment newInstance() {
        return new NoteListFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_note_list, container, false);
        mlvOverView = (MaterialListView) root.findViewById(R.id.mlvNoteListFragment);

        return root;
    }

    @Override
    public void onResume() {
        db = new NeverNoteDB(getContext());
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        PublicVariableAndMethods.refreshNoteArrayList(getContext(), mlvOverView, getResources(), noteListCellDataArrayList, dbRead, dbWrite, -1);
        super.onResume();
    }
}
