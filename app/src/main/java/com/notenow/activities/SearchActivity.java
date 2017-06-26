package com.notenow.activities;

import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;

import com.notenow.R;
import com.notenow.db.DBManager;
import com.notenow.model.Note;
import com.notenow.recyclerview.RecyclerViewAdapter;

import java.util.ArrayList;
import java.util.List;

import github.nisrulz.recyclerviewhelper.RVHItemClickListener;
import github.nisrulz.recyclerviewhelper.RVHItemDividerDecoration;
import github.nisrulz.recyclerviewhelper.RVHItemTouchHelperCallback;

public class SearchActivity extends AppCompatActivity {

    private String query;
    private DBManager dm;
    private List<Note> noteDataList = new ArrayList<>();
    private List<Note> temp = new ArrayList<>();
    private RecyclerViewAdapter adapter;
    private RecyclerView myrecyclerview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_search);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Search Results");
        }

        init();
    }

    private void init() {
        dm = new DBManager(this);
        dm.readFromDB(noteDataList);
        myrecyclerview = (RecyclerView) findViewById(R.id.rv_list);
        Intent searchIntent = getIntent();
        if (Intent.ACTION_SEARCH.equals(searchIntent.getAction()))
            query = searchIntent.getStringExtra(SearchManager.QUERY);
        myrecyclerview.hasFixedSize();
        myrecyclerview.setLayoutManager(new LinearLayoutManager(this));

        for (int i = 0; i < noteDataList.size(); i++) {
            if (noteDataList.get(i).getTitle().toLowerCase().contains(query.toLowerCase()))
                temp.add(noteDataList.get(i));
        }
        adapter = new RecyclerViewAdapter(this, temp);
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
                        Intent intent = new Intent(SearchActivity.this, EditNoteActivity.class);
                        intent.putExtra("id", noteId);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finish();
                    }
                }));
    }
}
