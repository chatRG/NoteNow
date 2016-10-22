package com.notenow.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;
import com.notenow.R;
import com.notenow.adapter.RecyclerViewAdapter;
import com.notenow.db.DBManager;
import com.notenow.model.Note;

import java.util.ArrayList;
import java.util.List;

import github.nisrulz.recyclerviewhelper.RVHItemClickListener;
import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    long waitTime = 2000;
    long touchTime = 0;
    private FloatingActionButton addBtn;
    private DBManager dm;
    private List<Note> noteDataList = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private RecyclerView myrecyclerview;
    private TextView emptyListTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) findViewById(R.id.toolbar_main));
        getSupportActionBar().setTitle(R.string.app_name);

        init();
    }

    private void init() {
        dm = new DBManager(this);
        dm.readFromDB(noteDataList);
        myrecyclerview = (RecyclerView) findViewById(R.id.rv_list);
        addBtn = (FloatingActionButton) findViewById(R.id.add);
        emptyListTextView = (TextView) findViewById(R.id.empty);
        addBtn.setOnClickListener(this);
        addBtn.attachToRecyclerView(myrecyclerview);
        addBtn.show();
        adapter = new RecyclerViewAdapter(this, noteDataList);
        myrecyclerview.hasFixedSize();
        myrecyclerview.setLayoutManager(new LinearLayoutManager(this));
        myrecyclerview.setAdapter(adapter);

        // Setup onItemTouchHandler
        ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(adapter, true, true, true);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(myrecyclerview);

        // Set the divider
        myrecyclerview.addItemDecoration(
                new RVHItemDividerDecoration(this, LinearLayoutManager.VERTICAL));

        // Set On Click
        myrecyclerview.addOnItemTouchListener(
                new RVHItemClickListener(this, new RVHItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        int noteId = noteDataList.get(position).getId();
                        Intent intent = new Intent(MainActivity.this, EditNoteActivity.class);
                        intent.putExtra("id", noteId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }));
        updateView();
    }

    public void updateView() {
        if (noteDataList.isEmpty()) {
            myrecyclerview.setVisibility(View.GONE);
            emptyListTextView.setVisibility(View.VISIBLE);
        } else {
            myrecyclerview.setVisibility(View.VISIBLE);
            emptyListTextView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent i = new Intent(this, EditNoteActivity.class);
        switch (view.getId()) {
            case R.id.add:
                startActivity(i);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    @TargetApi(21)
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                MaterialDialog dialog = new MaterialDialog.Builder(this)
                        .title(R.string.about)
                        .customView(R.layout.dialog_webview, false)
                        .positiveText(android.R.string.ok)
                        .build();
                WebView webView = (WebView) dialog.getCustomView().findViewById(R.id.webview);
                webView.loadUrl("file:///android_asset/webview.html");
                dialog.show();
                break;
            case R.id.action_clean:
                new MaterialDialog.Builder(MainActivity.this)
                        .content(R.string.are_you_sure)
                        .icon(getDrawable(R.mipmap.ic_launcher))
                        .buttonRippleColor(getResources().getColor(R.color.ripple))
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                for (int id = 0; id < 100; id++)
                                    DBManager.getInstance(MainActivity.this).deleteNote(id);
                                adapter.removeAllItem();
                                updateView();
                            }
                        }).show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= waitTime) {
            Toast.makeText(this, R.string.exit, Toast.LENGTH_SHORT).show();
            touchTime = currentTime;
        } else {
            this.finish();
        }
    }
}
