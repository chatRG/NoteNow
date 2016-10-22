package com.notenow.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Selection;
import android.text.Spannable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.notenow.R;
import com.notenow.db.DBManager;
import com.notenow.model.Note;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class EditNoteActivity extends AppCompatActivity
        implements View.OnClickListener {
    private EditText titleEt;
    private EditText contentEt;
    private int noteID = -1;
    private DBManager dbManager;
    private int backPressedCount;
    private boolean isEditPressed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        getSupportActionBar().setTitle(R.string.add_note);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        init();
    }

    private void init() {
        dbManager = new DBManager(this);
        titleEt = (EditText) findViewById(R.id.note_title);
        contentEt = (EditText) findViewById(R.id.note_content);
        backPressedCount = 0;
        isEditPressed = false;
        noteID = getIntent().getIntExtra("id", -1);
        if (noteID != -1) {
            showNoteData(noteID);
        }
    }

    private void showNoteData(int id) {
        Note note = dbManager.readData(id);
        titleEt.setText(note.getTitle());
        contentEt.setText(note.getContent());

        Spannable spannable = titleEt.getText();
        Selection.setSelection(spannable, titleEt.getText().length());
    }

    @Override
    public void onClick(View view) {
    }

    private String getTime() {
        SimpleDateFormat format =
                new SimpleDateFormat("HH:mm MM-dd E", Locale.getDefault());
        Date curDate = new Date();
        return format.format(curDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_edit, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
                break;

            case R.id.action_edit:
                titleEt.setEnabled(true);
                contentEt.setEnabled(true);
                contentEt.requestFocus();
                isEditPressed = true;
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(contentEt, InputMethodManager.SHOW_IMPLICIT);
                break;

            case R.id.action_save:
                if (saveNote())
                    Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private boolean saveNote() {
        String title = titleEt.getText().toString();
        String content = contentEt.getText().toString();
        String time = getTime();
        if (title.isEmpty() || content.isEmpty()) {
            Toast.makeText(this, R.string.enter_both, Toast.LENGTH_LONG).show();
            return false;
        }
        if (noteID == -1) {

            dbManager.addToDB(title, content, time);
        } else {

            dbManager.updateNote(noteID, title, content, time);
        }
        Intent i = new Intent(EditNoteActivity.this, MainActivity.class);
        startActivity(i);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        this.finish();
        return true;
    }

    private void backPressExec() {
        if (!isEditPressed) {

            Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        } else {
            if (titleEt.getText().toString().isEmpty() && contentEt.getText().toString().isEmpty()) {

                Intent intent = new Intent(EditNoteActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
            } else if (titleEt.getText().toString().isEmpty() || contentEt.getText().toString().isEmpty()) {

                Toast.makeText(this, R.string.enter_both, Toast.LENGTH_LONG).show();
            } else {
                if (backPressedCount == 0) {

                    titleEt.setEnabled(false);
                    contentEt.setEnabled(false);
                    backPressedCount++;
                } else if (backPressedCount > 0) {

                    if (saveNote())
                        Toast.makeText(this, R.string.note_saved, Toast.LENGTH_SHORT).show();
                    this.finish();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        backPressExec();
    }
}
