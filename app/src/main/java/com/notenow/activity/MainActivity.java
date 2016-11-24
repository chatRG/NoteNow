package com.notenow.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;

public class MainActivity extends AppCompatActivity
        implements View.OnClickListener {

    long waitTime = 2000;
    long touchTime = 0;
    private FloatingActionButton mFAB;
    private DBManager dm;
    private List<Note> noteDataList = new ArrayList<>();
    private RecyclerViewAdapter mAdapter;
    private RecyclerView mRecyclerView;
    private TextView mEmptyList;
    private RelativeLayout mainRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar((Toolbar) this.findViewById(R.id.toolbar_main));
        getSupportActionBar().setTitle(R.string.app_name);

        firstInit();
    }

    private void firstInit() {
        mainRelativeLayout = (RelativeLayout) findViewById(R.id.mainRelativeLayout);
        int value = getIntent().getIntExtra("isSaved", -1);
        if (value != -1)
            showSnackBar(R.string.note_saved, true);
        init();
        initFab();
    }

    private void init() {
        dm = new DBManager(this);
        mRecyclerView = (RecyclerView) this.findViewById(R.id.rv_list);
        dm.readFromDB(noteDataList);
        mAdapter = new RecyclerViewAdapter(this, noteDataList);
        mRecyclerView.setAdapter(mAdapter);
        mEmptyList = (TextView) this.findViewById(R.id.empty);
        mRecyclerView.hasFixedSize();
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Setup onItemTouchHandler
        ItemTouchHelper.Callback callback = new RVHItemTouchHelperCallback(mAdapter, true, true, true);
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);

        // Set the divider
        /*mRecyclerView.addItemDecoration(
                new RVHItemDividerDecoration(this, LinearLayoutManager.VERTICAL));*/

        // Set On Click
        mRecyclerView.addOnItemTouchListener(
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

    private void initFab() {
        mFAB = (FloatingActionButton) this.findViewById(R.id.add);
        mFAB.setOnClickListener(this);
        mFAB.attachToRecyclerView(mRecyclerView);
        mFAB.show();
    }

    public void updateView() {
        if (noteDataList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmptyList.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmptyList.setVisibility(View.GONE);
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


    public void showSnackBar(int showSnack, boolean lengthLong) {
        if (lengthLong)
            Snackbar.make(mainRelativeLayout, showSnack, Snackbar.LENGTH_LONG)
                    .show();
        else
            Snackbar.make(mainRelativeLayout, showSnack, Snackbar.LENGTH_SHORT)
                    .show();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(new ComponentName(getApplicationContext(),
                        SearchActivity.class)));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                newText = newText.toLowerCase();
                List<Note> tempList = new ArrayList<>();
                String tmpTitle;
                for (Note note : noteDataList) {
                    tmpTitle = note.getTitle().toLowerCase();
                    if (tmpTitle.contains(newText))
                        tempList.add(note);
                }
                mAdapter.setFilter(tempList);
                return true;
            }
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_about:
                Intent intent = new Intent(this, AboutActivity.class);
                startActivity(intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                break;
            case R.id.action_clean:
                new MaterialDialog.Builder(MainActivity.this)
                        .content(R.string.are_you_sure)
                        .icon(ContextCompat.getDrawable(this, R.mipmap.ic_launcher))
                        .buttonRippleColor(ContextCompat.getColor(this, R.color.ripple))
                        .positiveText(R.string.yes)
                        .negativeText(R.string.no)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                new DBManager(getBaseContext())
                                        .getInstance(getBaseContext())
                                        .deleteAllNote();
                                mAdapter.removeAllItem();
                                updateView();
                            }
                        }).show();
                break;
            case R.id.action_sort:
                new MaterialDialog.Builder(this)
                        .items(R.array.sort_order)
                        .buttonRippleColor(ContextCompat.getColor(this, R.color.ripple))
                        .autoDismiss(true)
                        .itemsCallbackSingleChoice(-1, new MaterialDialog.ListCallbackSingleChoice() {
                            @Override
                            public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                                /*String orderBy;
                                switch (which) {
                                    case 1:
                                        orderBy = NoteDBOpenHelper.TIME;
                                        break;
                                    case 2:
                                        orderBy = NoteDBOpenHelper.RANK;
                                        break;
                                    default:
                                        orderBy = NoteDBOpenHelper.TITLE;
                                        break;
                                }
                                if (new DBManager(getBaseContext()).getInstance(getBaseContext())
                                        .sortby(orderBy)) {
                                    //mAdapter.removeAllItem();
                                    //init();
                                    //dm.readFromDB(noteDataList);
                                    mAdapter.notifyDataSetChanged();
                                    }
                                    */
                                return true;
                            }
                        })
                        .positiveText(R.string.ok)
                        .show();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    public void onBackPressed() {
        long currentTime = System.currentTimeMillis();
        if ((currentTime - touchTime) >= waitTime) {
            showSnackBar(R.string.exit, false);
            touchTime = currentTime;
        } else {
            this.finish();
        }
    }
}
