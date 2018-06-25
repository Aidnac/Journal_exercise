package inc.can_a.journalapp.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;

import inc.can_a.journalapp.Database.DatabaseHandler;
import inc.can_a.journalapp.Model.Entry;
import inc.can_a.journalapp.R;
import inc.can_a.journalapp.adapter.EntryListAdapter;

public class ScrollingActivity extends AppCompatActivity implements EntryListAdapter.EntryListAdapterListener {
    private RecyclerView recyclerView;
    private ArrayList<Entry> entryArrayList;
    private EntryListAdapter mAdapter;
    private DatabaseHandler db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = new DatabaseHandler(this);

        recyclerView =  findViewById(R.id.recycler_view);
        entryArrayList = new ArrayList<>();
        mAdapter = new EntryListAdapter(getApplicationContext(), entryArrayList,this);

        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });


        entryArrayList.addAll(db.getAllJournalEntries());

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                LayoutInflater inflater = getLayoutInflater();
                final View itemView = inflater.inflate(R.layout.add_entry_dialog,null);

                AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
                builder.setView(itemView);
                builder.setMessage("Add Entry")
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                EditText title = itemView.findViewById(R.id.et_title);
                                EditText note = itemView.findViewById(R.id.et_note);

                                Entry  entry = new Entry();
                                entry.setTitle(title.getText().toString());
                                entry.setNote(note.getText().toString());
                                entryArrayList.add(entry);
                                db.addEntry(entry);
                                mAdapter.notifyDataSetChanged();

                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                //Do nothing
                            }
                        });

                builder.show();
            }
        });
    }

    @Override
    public void onDeletePressed(final Entry entry,final int position) {

        AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
        builder.setTitle("Delete");
        builder.setMessage("Are you sure you want to delete?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                db.deleteEntry(entry);
                entryArrayList.remove(position);
                mAdapter.notifyItemRemoved(position);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    @Override
    public void onEditPressed(final Entry entry,final int position) {

        LayoutInflater inflater = getLayoutInflater();
        final View itemView = inflater.inflate(R.layout.add_entry_dialog,null);
        final EditText title = itemView.findViewById(R.id.et_title);
        final EditText note = itemView.findViewById(R.id.et_note);

        title.setText(entry.getTitle());
        note.setText(entry.getNote());

        AlertDialog.Builder builder = new AlertDialog.Builder(ScrollingActivity.this);
        builder.setView(itemView);
        builder.setMessage("Add Entry")
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        entry.setTitle(title.getText().toString());
                        entry.setNote(note.getText().toString());
                        // updating note in db
                        db.updateEntry(entry);
                        // refreshing the list
                        entryArrayList.set(position, entry);
                        mAdapter.notifyItemChanged(position);

                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Do nothing
                    }
                });

        builder.show();
    }

    @Override
    public  void onViewPressed(Entry entry){
        //entry = entryArrayList.get(getAdapterPosition());
        Intent in = new Intent(getApplicationContext(), DetailActivity.class);
        in.putExtra("title",entry.getTitle());
        in.putExtra("note",entry.getNote());
        in.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(in);
    }
}
