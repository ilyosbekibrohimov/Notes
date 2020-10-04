package com.example.notes;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.notes.adapters.NotesRecyclerAdapter;
import com.example.notes.models.Note;
import com.example.notes.persistence.NoteRepository;
import com.example.notes.util.VerticalSpacingItemDecorator;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NotesRecyclerAdapter.ViewHolder.OnNoteClickListener, FloatingActionButton.OnClickListener {
    private static final String TAG = "Message";
    //region UI components
    private RecyclerView mRecyclerView;
    private FloatingActionButton floatingActionButton;
    //endregion
    //region  vars
    private ArrayList<Note> mNotes = new ArrayList<>();
    private NotesRecyclerAdapter mNoteRecyclerAdapter;
    private NoteRepository mNoteRepository;

    //endregion
    //region override(s)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = findViewById(R.id.recyclerView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        floatingActionButton = findViewById(R.id.fab);

        floatingActionButton.setOnClickListener(this);
        mNoteRepository = new NoteRepository(this);


        initRecyclerView();
        retrieveNotes();
        insertFakeNotes();
        setSupportActionBar(toolbar);
        setTitle("Notes");

    }

    @Override
    public void onNoteClick(int position) {
        Intent intent = new Intent(this, NewActivity.class);
        intent.putExtra("notes", mNotes.get(position));
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, NewActivity.class);
        startActivity(intent);
    }


    //endregion
    //region retrieve_notes
    private void retrieveNotes() {
        mNoteRepository.retrieveNotesTask().observe(this, new Observer<List<Note>>() {
            @Override
            public void onChanged(List<Note> notes) {
                if (mNotes.size() > 0) {
                    mNotes.clear();
                }
                if (notes != null) {
                    mNotes.addAll(notes);
                }
                mNoteRecyclerAdapter.notifyDataSetChanged();
            }
        });
    }

    //endregion
    //region utility methods
    private void insertFakeNotes() {
        for (int i = 0; i < 100; i++) {
            Note note = new Note();
            note.setContent("content #" + (i + 1));
            note.setTitle("title #" + (i + 1));
            note.setTimestamp("Jan 2019");
            mNotes.add(note);
        }
        mNoteRecyclerAdapter.notifyDataSetChanged(); //very important!!!
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mNoteRecyclerAdapter = new NotesRecyclerAdapter(mNotes, this);
        VerticalSpacingItemDecorator verticalSpacingItemDecorator = new VerticalSpacingItemDecorator(10);
        mRecyclerView.addItemDecoration(verticalSpacingItemDecorator);
        mRecyclerView.setAdapter(mNoteRecyclerAdapter);
        new ItemTouchHelper(itemToucHelperCallBack).attachToRecyclerView(mRecyclerView);
    }


    //endregion
    //region ItemTouchHelper
    private void deleteNode(Note note) {
        mNotes.remove(note);
        mNoteRecyclerAdapter.notifyDataSetChanged();

    }

    private ItemTouchHelper.SimpleCallback itemToucHelperCallBack = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
            deleteNode(mNotes.get(viewHolder.getAdapterPosition()));
        }

    };
    //endregion
}