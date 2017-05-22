package com.notenow.activities;

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
import android.widget.SeekBar;
import android.widget.Toast;

import com.notenow.R;
import com.notenow.db.DBManager;
import com.notenow.model.Note;
import com.notenow.utils.UtilTypeface;

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
    private boolean isEditEmpty;
    private SeekBar mRankBar;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_note);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(R.string.add_note);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        init();
    }

    private void init() {
        dbManager = new DBManager(this);

        titleEt = (EditText) findViewById(R.id.note_title);
        contentEt = (EditText) findViewById(R.id.note_content);
        mRankBar = (SeekBar) findViewById(R.id.rankbar);

        UtilTypeface.setCustomTypeface(EditNoteActivity.this, titleEt, contentEt);

        backPressedCount = 0;
        progress = -1;
        isEditPressed = false;
        noteID = getIntent().getIntExtra("id", -1);
        if (noteID != -1) {
            showNoteData(noteID);
        }
        seekBarUpdate(mRankBar);
        mRankBar.setEnabled(false);
    }

    private void seekBarUpdate(SeekBar mRankBar) {
        mRankBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                progress = seekBar.getProgress();
            }
        });
    }

    private void showNoteData(int id) {
        Note note = dbManager.readData(id);
        titleEt.setText(note.getTitle());
        contentEt.setText(note.getContent());
        if (progress < 0)
            progress = 4; //default value
        mRankBar.setProgress(progress);

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
                mRankBar.setEnabled(true);
                contentEt.requestFocus();
                isEditPressed = true;
                InputMethodManager imm =
                        (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(contentEt, InputMethodManager.SHOW_IMPLICIT);
                break;

            case R.id.action_save:
                saveNote();
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
            progress = mRankBar.getProgress() + 1;
            dbManager.addToDB(title, content, time, String.valueOf(progress));
        } else {
            progress = mRankBar.getProgress() + 1;
            dbManager.updateNote(noteID, title, content, time, String.valueOf(progress));
        }
        Intent i = new Intent(getBaseContext(), MainActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.putExtra("isSaved", 1);
        startActivity(i);
        this.finish();
        return true;
    }

    private void backPressExec() {
        if (!isEditPressed) {
            //without editing
            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            this.finish();
        } else {
            if (titleEt.getText().toString().isEmpty() && contentEt.getText().toString().isEmpty()) {
                //when both title and content is empty
                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                this.finish();
            } else if (titleEt.getText().toString().isEmpty() || contentEt.getText().toString().isEmpty()) {
                //when one of title or content is empty
                if (backPressedCount == 0)
                    Toast.makeText(this, R.string.enter_both, Toast.LENGTH_LONG).show();
                backPressedCount++;
                isEditEmpty = true;
                if (backPressedCount > 1) {
                    Intent intent = new Intent(getBaseContext(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                    this.finish();
                }

            } else {
                if (backPressedCount == 0 || isEditEmpty) {
                    //both title and content entered and back is pressed once
                    titleEt.setEnabled(false);
                    contentEt.setEnabled(false);
                    isEditEmpty = false;
                    backPressedCount++;
                } else if (backPressedCount > 0) {
                    //back pressed for saving
                    if (saveNote()) {
                        Intent intent = new Intent(getBaseContext(), MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.putExtra("isSaved", 1);
                        startActivity(intent);
                    }
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
