package bupt.icyicarus.nevernote.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.util.ArrayList;

import bupt.icyicarus.nevernote.EditNote;
import bupt.icyicarus.nevernote.R;
import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.noteList.NoteListCellData;

public class OverviewFragment extends Fragment {

    private MaterialListView mlvOverView;
    private ArrayList<NoteListCellData> noteListCellDataArrayList = null;
    private NeverNoteDB db;
    private SQLiteDatabase dbRead, dbWrite;
    private long noteDate = -1;
    private long noteAddDate = -1;

    public static OverviewFragment newInstance() {
        return new OverviewFragment();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_overview, container, false);
        mlvOverView = (MaterialListView) root.findViewById(R.id.mlvOverView);

        db = new NeverNoteDB(getContext());
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();

        refreshNoteArrayList();
        return root;
    }

    private void addCard(NoteListCellData noteListCellData) {
        Card card;
        if (noteListCellData.havePic) {

            Bitmap bitmap = BitmapFactory.decodeFile(noteListCellData.picturePath, getBitmapOption(16));

            card = new Card.Builder(getContext())
                    .setTag(noteListCellData)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(noteListCellData.name)
                    .setDescription(noteListCellData.date)
                    .setDrawable(new BitmapDrawable(getResources(), bitmap))
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, new TextViewAction(getContext())
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, final Card card) {
                                    new AlertDialog.Builder(getContext()).setTitle("Delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NoteListCellData data = (NoteListCellData) card.getTag();
                                            File f;

                                            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""}, null, null, null);
                                            while (c.moveToNext()) {
                                                f = new File(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)));
                                                if (!f.delete())
                                                    Log.e("file", "delete error");
                                            }
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""});
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_NOTES, NeverNoteDB.COLUMN_ID + "=?", new String[]{data.id + ""});
                                            c.close();
                                            refreshNoteArrayList();
                                        }
                                    }).setNegativeButton("No", null).show();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(getContext())
                            .setText("Edit")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    NoteListCellData data = (NoteListCellData) card.getTag();
                                    Intent i = new Intent(getContext(), EditNote.class);

                                    if (data != null) {
                                        i.putExtra(EditNote.EXTRA_NOTE_ID, data.id);
                                        i.putExtra(EditNote.EXTRA_NOTE_NAME, data.name);
                                        i.putExtra(EditNote.EXTRA_NOTE_CONTENT, data.content);
                                    }
                                    startActivity(i);
                                }
                            }))
                    .endConfig()
                    .build();
        } else {
            card = new Card.Builder(getContext())
                    .setTag(noteListCellData)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_buttons_card)
                    .setTitle(noteListCellData.name)
                    .setDescription(noteListCellData.date)
                    .addAction(R.id.left_text_button, new TextViewAction(getContext())
                            .setText("Delete")
                            .setTextResourceColor(R.color.black_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, final Card card) {
                                    new AlertDialog.Builder(getContext()).setTitle("Delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            NoteListCellData data = (NoteListCellData) card.getTag();
                                            File f;

                                            Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""}, null, null, null);
                                            while (c.moveToNext()) {
                                                f = new File(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)));
                                                if (!f.delete())
                                                    Log.e("file", "delete error");
                                            }
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""});
                                            dbWrite.delete(NeverNoteDB.TABLE_NAME_NOTES, NeverNoteDB.COLUMN_ID + "=?", new String[]{data.id + ""});
                                            c.close();
                                            refreshNoteArrayList();
                                        }
                                    }).setNegativeButton("No", null).show();
                                }
                            }))
                    .addAction(R.id.right_text_button, new TextViewAction(getContext())
                            .setText("Edit")
                            .setTextResourceColor(R.color.orange_button)
                            .setListener(new OnActionClickListener() {
                                @Override
                                public void onActionClicked(View view, Card card) {
                                    NoteListCellData data = (NoteListCellData) card.getTag();
                                    Intent i = new Intent(getContext(), EditNote.class);

                                    if (data != null) {
                                        i.putExtra(EditNote.EXTRA_NOTE_ID, data.id);
                                        i.putExtra(EditNote.EXTRA_NOTE_NAME, data.name);
                                        i.putExtra(EditNote.EXTRA_NOTE_CONTENT, data.content);
                                    }
                                    startActivity(i);
                                }
                            }))
                    .endConfig()
                    .build();
        }
        mlvOverView.getAdapter().add(card);
    }

    public BitmapFactory.Options getBitmapOption(int size) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPurgeable = true;
        options.inSampleSize = size;
        return options;
    }

    public void refreshNoteArrayList() {
        mlvOverView.getAdapter().clearAll();
        if (noteListCellDataArrayList != null) {
            noteListCellDataArrayList.clear();
        } else {
            noteListCellDataArrayList = new ArrayList<>();
        }
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
//            try {
//                noteAddDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE))).getTime();
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            if (noteDate == -1 || (noteDate != -1 && noteAddDate >= noteDate && noteAddDate < (noteDate + 86400000))) {
            noteListCellDataArrayList.add(new NoteListCellData(
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)),
                    c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)),
                    c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID)),
                    getContext()
            ));
//            }
        }
        for (NoteListCellData noteListCellData : noteListCellDataArrayList) {
            addCard(noteListCellData);
        }
        c.close();
    }
}
