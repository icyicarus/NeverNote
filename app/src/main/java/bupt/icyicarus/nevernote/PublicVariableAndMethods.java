package bupt.icyicarus.nevernote;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;

import com.dexafree.materialList.card.Card;
import com.dexafree.materialList.card.CardProvider;
import com.dexafree.materialList.card.OnActionClickListener;
import com.dexafree.materialList.card.action.TextViewAction;
import com.dexafree.materialList.view.MaterialListView;
import com.squareup.picasso.RequestCreator;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import bupt.icyicarus.nevernote.db.NeverNoteDB;
import bupt.icyicarus.nevernote.noteList.NoteListCellData;
import bupt.icyicarus.nevernote.view.NoteView;

public class PublicVariableAndMethods {

    public static final int REQUEST_CODE_GET_PHOTO = 2;
    public static final int REQUEST_CODE_GET_VIDEO = 3;
    public static final int REQUEST_CODE_GET_AUDIO = 4;
    public static final int REQUEST_CODE_GET_LOCATION = 5;

    public static BitmapFactory.Options getBitmapOption(int size) {
        System.gc();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = size;
        return options;
    }

    public static int haveTodayNote(Context context) {
        NeverNoteDB db;
        SQLiteDatabase dbRead;
        int haveTodayNote = -1;

        db = new NeverNoteDB(context);
        dbRead = db.getReadableDatabase();

        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        Date todayDate = new Date(System.currentTimeMillis());
        Date noteDate;

        while (c.moveToNext()) {
            try {
                noteDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)));
                if (noteDate.getYear() == todayDate.getYear() && noteDate.getMonth() == todayDate.getMonth() && noteDate.getDate() == todayDate.getDate()) {
                    haveTodayNote = c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        c.close();
        return haveTodayNote;
    }

    public static void refreshNoteArrayList(Context context, MaterialListView materialListView, Resources resources, ArrayList<NoteListCellData> noteListCellDataArrayList, long noteDate) {
        NeverNoteDB db;
        SQLiteDatabase dbRead;
        db = new NeverNoteDB(context);
        dbRead = db.getReadableDatabase();

        long noteAddDate = -1;
        materialListView.getAdapter().clearAll();
        if (noteListCellDataArrayList != null) {
            noteListCellDataArrayList.clear();
        } else {
            noteListCellDataArrayList = new ArrayList<>();
        }
        Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_NOTES, null, null, null, null, null, null, null);
        while (c.moveToNext()) {
            try {
                noteAddDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE))).getTime();
            } catch (ParseException e) {
                e.printStackTrace();
            }

            if (noteDate == -1 || (noteAddDate >= noteDate && noteAddDate < (noteDate + 86400000))) {
                noteListCellDataArrayList.add(new NoteListCellData(
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_NAME)),
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_DATE)),
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_CONTENT)),
                        c.getInt(c.getColumnIndex(NeverNoteDB.COLUMN_ID)),
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_LATITUDE)),
                        c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_NOTE_LONGITUDE)),
                        context
                ));
            }
        }
        for (NoteListCellData noteListCellData : noteListCellDataArrayList) {
            addCard(context, materialListView, noteListCellDataArrayList, resources, noteListCellData, noteDate);
        }
        c.close();
    }

    private static void addCard(final Context context, final MaterialListView materialListView, final ArrayList<NoteListCellData> noteListCellDataArrayList, final Resources resources, NoteListCellData noteListCellData, final long noteDate) {
        NeverNoteDB db;
        final SQLiteDatabase dbRead, dbWrite;
        db = new NeverNoteDB(context);
        dbRead = db.getReadableDatabase();
        dbWrite = db.getWritableDatabase();
        Card card;

        TextViewAction actionDelete = new TextViewAction(context)
                .setText("Delete")
                .setTextResourceColor(R.color.black_button)
                .setListener(new OnActionClickListener() {
                    @Override
                    public void onActionClicked(View view, final Card card) {
                        new AlertDialog.Builder(context).setTitle("Delete this note?").setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                NoteListCellData data = (NoteListCellData) card.getTag();
                                File f;
                                if (data != null) {
                                    Cursor c = dbRead.query(NeverNoteDB.TABLE_NAME_MEDIA, null, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""}, null, null, null);
                                    while (c.moveToNext()) {
                                        f = new File(c.getString(c.getColumnIndex(NeverNoteDB.COLUMN_NAME_MEDIA_PATH)));
                                        if (!f.delete())
                                            Log.e("file", "delete error");
                                    }
                                    dbWrite.delete(NeverNoteDB.TABLE_NAME_MEDIA, NeverNoteDB.COLUMN_NAME_MEDIA_OWNER_NOTE_ID + "=?", new String[]{data.id + ""});
                                    dbWrite.delete(NeverNoteDB.TABLE_NAME_NOTES, NeverNoteDB.COLUMN_ID + "=?", new String[]{data.id + ""});
                                    c.close();
                                    refreshNoteArrayList(context, materialListView, resources, noteListCellDataArrayList, noteDate);
                                }
                            }
                        }).setNegativeButton("No", null).show();
                    }
                });
        TextViewAction actionEdit = new TextViewAction(context)
                .setText("Edit")
                .setTextResourceColor(R.color.orange_button)
                .setListener(new OnActionClickListener() {
                    @Override
                    public void onActionClicked(View view, Card card) {
                        NoteListCellData data = (NoteListCellData) card.getTag();
                        Intent i = new Intent(context, NoteView.class);
                        if (data != null)
                            i.putExtra(NoteView.EXTRA_NOTE_ID, data.id);
                        context.startActivity(i);
                    }
                });

        if (noteListCellData.havePic) {

            Bitmap bitmap = BitmapFactory.decodeFile(noteListCellData.picturePath, PublicVariableAndMethods.getBitmapOption(16));

            card = new Card.Builder(context)
                    .setTag(noteListCellData)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_image_buttons_card_layout)
                    .setTitle(noteListCellData.name)
                    .setDescription(noteListCellData.date)
                    .setDrawable(new BitmapDrawable(resources, bitmap))
                    .setDrawableConfiguration(new CardProvider.OnImageConfigListener() {
                        @Override
                        public void onImageConfigure(@NonNull RequestCreator requestCreator) {
                            requestCreator.fit();
                        }
                    })
                    .addAction(R.id.left_text_button, actionDelete)
                    .addAction(R.id.right_text_button, actionEdit)
                    .endConfig()
                    .build();
        } else {
            card = new Card.Builder(context)
                    .setTag(noteListCellData)
                    .withProvider(new CardProvider())
                    .setLayout(R.layout.material_basic_buttons_card)
                    .setTitle(noteListCellData.name)
                    .setDescription(noteListCellData.date)
                    .addAction(R.id.left_text_button, actionDelete)
                    .addAction(R.id.right_text_button, actionEdit)
                    .endConfig()
                    .build();
        }
        materialListView.getAdapter().add(card);
    }
}
