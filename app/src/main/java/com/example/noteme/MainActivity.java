package com.example.noteme;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView mTVNoText;
    private ImageView mIVAddNote;
    private LinearLayout mLL;

    private Dialog mDialog;

    private EditText mETHeadLine, mETText;
    private ImageView mIVCloseDialog, mIVSaveNote;

    private DBHelper dbHelper;
    private SQLiteDatabase db;

    private long rowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTVNoText = (TextView) findViewById(R.id.mTVNoText);
        mIVAddNote = (ImageView) findViewById(R.id.mIVAddNote);
        mLL = (LinearLayout) findViewById(R.id.mLL);

        mDialog = new Dialog(MainActivity.this);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.setContentView(R.layout.dialog_window_note);
        mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialog.setCancelable(false);

        mETHeadLine = (EditText) mDialog.findViewById(R.id.mETHeadLine);
        mETText = (EditText) mDialog.findViewById(R.id.mETText);
        mIVCloseDialog = (ImageView) mDialog.findViewById(R.id.mIVCloseDialog);
        mIVSaveNote = (ImageView) mDialog.findViewById(R.id.mIVSaveNote);

        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        rowCount = DatabaseUtils.queryNumEntries(db, "NotesTable");

        Log.d("DataBase", "RowCount = " + Long.toString(rowCount));

        if (rowCount != 0) {
            mTVNoText.setVisibility(View.INVISIBLE);

            showAllDataBase();
        }
        else
            mTVNoText.setVisibility(View.VISIBLE);

        mIVAddNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mETHeadLine.setText("");
                mETText.setText("");

                mDialog.show();

                mIVSaveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer HeadLine = new StringBuffer(mETHeadLine.getText());
                        StringBuffer Text = new StringBuffer(mETText.getText());

                        if ((HeadLine.toString().length() != 0) && (Text.toString().length() != 0)) {
                            mTVNoText.setVisibility(View.INVISIBLE);

                            long ID = addNoteToDataBase(HeadLine, Text);

                            StringBuffer HeadLineCopy = new StringBuffer();
                            if (HeadLine.toString().length() > 19)
                                HeadLineCopy.append(HeadLine.substring(0,16) + "...");
                            else
                                HeadLineCopy.append(HeadLine);

                            addNoteToLL(new StringBuffer(HeadLineCopy), new StringBuffer(Text), ID);

                            mDialog.dismiss();

                            rowCount = rowCount + 1;

                            Toast.makeText(MainActivity.this, "Успішно було додано нову нотатку", Toast.LENGTH_LONG).show();
                        }
                        else
                           Toast.makeText(MainActivity.this, "Не всі поля заповнені!", Toast.LENGTH_SHORT).show();
                    }
                });

                mIVCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });
            }
        });
    }

    // Метод для добавления нового элемента в БД
    private long addNoteToDataBase(StringBuffer mSBHeadLine, StringBuffer mSBText) {
        ContentValues cv = new ContentValues();

        cv.put("HeadLine", mSBHeadLine.toString());
        cv.put("Text", mSBText.toString());

        long rowID = db.insert("NotesTable", null, cv);

        Log.d("DataBase"," ");
        Log.d("DataBase", "Inserted In DataBase :");
        Log.d("DataBase", "Row Inserted : ID = " + rowID);
        Log.d("DataBase", "HeadLine - " + mSBHeadLine.toString());
        Log.d("DataBase", "Text - " + mSBText.toString());
        Log.d("DataBase"," ");

        return rowID;
    }

    // Метод для формирования LLCopy с элементом БД и добавлением его в LL
    private void addNoteToLL(final StringBuffer mSBHeadLine, final StringBuffer mSBText, final long ID) {
        LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams0.setMargins(15,0,15,0);
        layoutParams0.weight = 1;

        TextView mTVHeadLine = new TextView(MainActivity.this);
        mTVHeadLine.setTextSize(18);
        mTVHeadLine.setText(mSBHeadLine.toString());
        mTVHeadLine.setLayoutParams(layoutParams0);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(120, 120);
        layoutParams1.setMargins(15,0,15,0);

        ImageView mIVDeleteNote = new ImageView(MainActivity.this);
        mIVDeleteNote.setBackground(getDrawable(R.drawable.rubbish_bin_sign));
        mIVDeleteNote.setLayoutParams(layoutParams1);

        ImageView mIVRefactorNote = new ImageView(MainActivity.this);
        mIVRefactorNote.setBackground(getDrawable(R.drawable.refactor_sign));
        mIVRefactorNote.setLayoutParams(layoutParams1);

        LinearLayout.LayoutParams layoutParams2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams2.setMargins(15,0,15,100);

        final LinearLayout mLLCopy = new LinearLayout(MainActivity.this);
        mLLCopy.setOrientation(LinearLayout.HORIZONTAL);
        mLLCopy.addView(mTVHeadLine);
        mLLCopy.addView(mIVDeleteNote);
        mLLCopy.addView(mIVRefactorNote);
        mLLCopy.setLayoutParams(layoutParams2);

        mLL.addView(mLLCopy, 0);

        mIVDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLL.removeView(mLLCopy);

                deleteNoteFromDataBase(ID);

                rowCount = rowCount - 1;

                if (rowCount == 0 )
                    mTVNoText.setVisibility(View.VISIBLE);
            }
        });

        mIVRefactorNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mETHeadLine.setText(mSBHeadLine.toString());
                mETText.setText(mSBText.toString());

                mDialog.show();

                mIVCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.dismiss();
                    }
                });

                mIVSaveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer headLine = new StringBuffer(mETHeadLine.getText());
                        StringBuffer text = new StringBuffer(mETText.getText());

                        if ((headLine.toString().length() != 0) && (text.toString().length() != 0)) {
                            ContentValues cv = new ContentValues();

                            cv.put("HeadLine", mETHeadLine.getText().toString());
                            cv.put("Text", mETText.getText().toString());

                            db.update("NotesTable", cv, "ID = " + ID, null);

                            mLL.removeAllViews();

                            showAllDataBase();

                            mDialog.dismiss();

                            Toast.makeText(MainActivity.this, "Зміни в записі успішно збережені", Toast.LENGTH_SHORT).show();
                        }
                        else
                            Toast.makeText(MainActivity.this, "Не всі поля заповнені!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    // Метод для удаления определенного элемента БД по ID
    private void deleteNoteFromDataBase(long ID) {
        db.delete("NotesTable", "ID = " + ID, null);

        Log.d("DataBase"," ");
        Log.d("DataBase", "Deleted Note From DataBase");
        Log.d("DataBase", "Row Deleted : ID = " + Long.toString(ID));
        Log.d("DataBase"," ");
    }

    // Метод для регулировки вывода всех сохранненых элементов БД в LL
    private void showAllDataBase() {
        Cursor cursor = db.query("NotesTable", null, null, null, null, null, null);

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("ID");
            int headLineColIndex  = cursor.getColumnIndex("HeadLine");
            int textColIndex  = cursor.getColumnIndex("Text");

            do {
                long id = cursor.getLong(idColIndex);
                StringBuffer headLine = new StringBuffer(cursor.getString(headLineColIndex));
                StringBuffer text = new StringBuffer(cursor.getString(textColIndex));

                StringBuffer headLineCopy = new StringBuffer();
                if (headLine.toString().length() > 19)
                    headLineCopy.append(headLine.substring(0, 16) + "...");
                else
                    headLineCopy.append(headLine);

                addNoteToLL(new StringBuffer(headLineCopy), text, id);
            }
            while (cursor.moveToNext());

            cursor.close();
        }
    }

    // Вспомогательный метод для вывода всей информации о БД
    private void readAllDataBase() {
        Cursor cursor = db.query("NotesTable", null, null, null, null, null, null);

        Log.d("DataBase", " ");
        Log.d("DataBase", "Rows in NotesTable : ");

        if (cursor.moveToFirst()) {
            int idColIndex = cursor.getColumnIndex("ID");
            int headLineColIndex = cursor.getColumnIndex("HeadLine");
            int textColIndex = cursor.getColumnIndex("Text");

            do {
                Log.d("DataBase", " ");
                Log.d("DataBase", "ID = " + cursor.getLong(idColIndex));
                Log.d("DataBase", "HeadLine = " + cursor.getString(headLineColIndex));
                Log.d("DataBase", "Text = " + cursor.getString(textColIndex));
                Log.d("DataBase", " ");
            }
            while (cursor.moveToNext());
        }
        else
            Log.d("DataBase", "0 Rows");

        cursor.close();

        Log.d("DataBase", " ");
    }
}
