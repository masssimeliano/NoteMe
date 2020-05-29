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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity {

    private TextView mTVNoText;
    private ImageView mIVAddNote;
    private LinearLayout mLL;

    private Dialog mDialogNote, mDialogDelete;

    private EditText mETHeadLine, mETText;
    private ImageView mIVCloseDialog, mIVSaveNote;

    private Button mBYes, mBNo;

    // Объект для создания и управления версиями БД
    private DBHelper dbHelper;
    // Подключание к БД
    private SQLiteDatabase db;

    private long rowCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mTVNoText = (TextView) findViewById(R.id.mTVNoText);
        mIVAddNote = (ImageView) findViewById(R.id.mIVAddNote);
        mLL = (LinearLayout) findViewById(R.id.mLL);

        mDialogNote = new Dialog(MainActivity.this);
        mDialogNote.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogNote.setContentView(R.layout.dialog_window_note);
        mDialogNote.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogNote.setCancelable(false);

        mETHeadLine = (EditText) mDialogNote.findViewById(R.id.mETHeadLine);
        mETText = (EditText) mDialogNote.findViewById(R.id.mETText);
        mIVCloseDialog = (ImageView) mDialogNote.findViewById(R.id.mIVCloseDialog);
        mIVSaveNote = (ImageView) mDialogNote.findViewById(R.id.mIVSaveNote);

        mDialogDelete = new Dialog(MainActivity.this);
        mDialogDelete.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialogDelete.setContentView(R.layout.dialog_window_delete_note);
        mDialogDelete.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        mDialogDelete.setCancelable(false);

        mBYes = (Button) mDialogDelete.findViewById(R.id.mBYes);
        mBNo = (Button) mDialogDelete.findViewById(R.id.mBNo);

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

                mDialogNote.show();

                mIVSaveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer HeadLine = new StringBuffer(mETHeadLine.getText());
                        StringBuffer Text = new StringBuffer(mETText.getText());

                        if ((HeadLine.toString().length() != 0) && (Text.toString().length() != 0)) {
                            mTVNoText.setVisibility(View.INVISIBLE);

                            long id = addNoteToDataBase(HeadLine, Text);

                            addNoteToLL(new StringBuffer(HeadLine), id);

                            mDialogNote.dismiss();

                            rowCount = rowCount + 1;

                            Toast.makeText(MainActivity.this, "Успішно додано нотатку", Toast.LENGTH_LONG).show();
                        }
                        else
                           Toast.makeText(MainActivity.this, "Не всі поля заповнені!", Toast.LENGTH_SHORT).show();
                    }
                });

                mIVCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogNote.dismiss();
                    }
                });
            }
        });
    }

    // Метод для добавления нового элемента в БД (Возвращает ID вставленного ряда)
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
    private void addNoteToLL(final StringBuffer mSBHeadLine, final long id) {
        // Формулирование LL с заметкой - Начало
        LinearLayout.LayoutParams layoutParams0 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams0.setMargins(15,0,15,0);
        layoutParams0.weight = 1;

        StringBuffer mSBHeadLineCopy = new StringBuffer();
        if (mSBHeadLine.length() > 20)
            mSBHeadLineCopy.append(mSBHeadLine.substring(0,16) + "...");
        else
            mSBHeadLineCopy.append(mSBHeadLine);

        final TextView mTVHeadLine = new TextView(MainActivity.this);
        mTVHeadLine.setTextSize(18);
        mTVHeadLine.setText(mSBHeadLineCopy);
        mTVHeadLine.setLayoutParams(layoutParams0);

        LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(120, 120);
        layoutParams1.setMargins(15,0,15,0);

        ImageView mIVDeleteNote = new ImageView(MainActivity.this);
        mIVDeleteNote.setLayoutParams(layoutParams1);
        mIVDeleteNote.setBackground(getDrawable(R.drawable.rubbish_bin_sign));

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

        // Формулирование LL с заметкой - Конец


        // Кнопка "Удалить"
        mIVDeleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialogDelete.show();

                mBYes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mLL.removeView(mLLCopy);

                        deleteNoteFromDataBase(id);

                        rowCount = rowCount - 1;

                        if (rowCount == 0 )
                            mTVNoText.setVisibility(View.VISIBLE);

                        mDialogDelete.dismiss();
                    }
                });

                mBNo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogDelete.dismiss();
                    }
                });
            }
        });

        // Кнопка "Изменить"
        mIVRefactorNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Запрос всех данных из таблицы
                // Получение Cursor
                Cursor cursor = db.query("NotesTable", null, null, null, null, null, null);

                int idColumnIndex = cursor.getColumnIndex("ID");
                int headLineColumnIndex = cursor.getColumnIndex("HeadLine");
                int textColumnIndex = cursor.getColumnIndex("Text");

                // Нахождения подходящего ID заметки
                do {
                }
                while ((cursor.moveToNext()) && (cursor.getLong(idColumnIndex) != id));

                mETHeadLine.setText(cursor.getString(headLineColumnIndex));
                mETText.setText(cursor.getString(textColumnIndex));

                mDialogNote.show();

                mIVCloseDialog.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialogNote.dismiss();
                    }
                });

                mIVSaveNote.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        StringBuffer headLine = new StringBuffer(mETHeadLine.getText());
                        StringBuffer text = new StringBuffer(mETText.getText());

                        if ((headLine.toString().length() != 0) && (text.toString().length() != 0)) {
                            // Объект для данных
                            ContentValues cv = new ContentValues();

                            cv.put("HeadLine", headLine.toString());
                            cv.put("Text", text.toString());

                            db.update("NotesTable", cv, "ID = " + id, null);

                            if (headLine.length() > 20)
                                mTVHeadLine.setText(headLine.substring(0, 16) + "...");
                            else
                                mTVHeadLine.setText(headLine);

                            mDialogNote.dismiss();

                            Toast.makeText(MainActivity.this, "Успішно збережено зміни", Toast.LENGTH_LONG).show();
                        }
                        else
                            Toast.makeText(MainActivity.this, "Не всі поля заповнені!", Toast.LENGTH_SHORT).show();

                    }
                });
            }
        });
    }

    // Метод для удаления определенного элемента БД по ID
    private void deleteNoteFromDataBase(long id) {
        db.delete("NotesTable", "ID = " + id, null);

        // Вывод в Log ID удаленного ряда
        Log.d("DataBase"," ");
        Log.d("DataBase", "Deleted Note From DataBase");
        Log.d("DataBase", "Row Deleted : ID = " + Long.toString(id));
        Log.d("DataBase"," ");
    }

    // Метод для регулировки вывода всех сохранненых элементов БД в LL
    private void showAllDataBase() {
        Cursor cursor = db.query("NotesTable", null, null, null, null, null, null);

        // Становление позиции курсора на первую строку выборки
        // Получение значения по номерам столбцов
        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("ID");
            int headLineColumnIndex  = cursor.getColumnIndex("HeadLine");

            do {
                long id = cursor.getLong(idColumnIndex);
                StringBuffer headLine = new StringBuffer(cursor.getString(headLineColumnIndex));

                // Формирования названия заметки (С длинной <=20)
                StringBuffer headLineCopy = new StringBuffer();
                if (headLine.toString().length() > 20)
                    headLineCopy.append(headLine.substring(0, 16) + "...");
                else
                    headLineCopy.append(headLine);

                addNoteToLL(new StringBuffer(headLineCopy), id);
            }
            while (cursor.moveToNext());
        }

        cursor.close();
    }

    // Вспомогательный метод для вывода всей информации о БД
    private void readAllDataBase() {
        Cursor cursor = db.query("NotesTable", null, null, null, null, null, null);

        Log.d("DataBase", " ");
        Log.d("DataBase", "Rows in NotesTable : ");

        // Чтение до всей БД и вывод все в Log
        if (cursor.moveToFirst()) {
            int idColumnIndex = cursor.getColumnIndex("ID");
            int headLineColumnIndex = cursor.getColumnIndex("HeadLine");
            int textColumnIndex = cursor.getColumnIndex("Text");

            do {
                long id = cursor.getLong(idColumnIndex);
                StringBuffer headLine = new StringBuffer(cursor.getString(headLineColumnIndex));
                StringBuffer text = new StringBuffer(cursor.getString(textColumnIndex));

                Log.d("DataBase", " ");
                Log.d("DataBase", "ID = " + Long.toString(id));
                Log.d("DataBase", "HeadLine = " + headLine);
                Log.d("DataBase", "Text = " + text);
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
