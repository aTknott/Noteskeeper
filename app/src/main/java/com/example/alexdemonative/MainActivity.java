package com.example.alexdemonative;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.SearchView;

import com.example.alexdemonative.fragment.AddNoteFragment;
import com.example.alexdemonative.fragment.EditNoteFragment;
import com.example.alexdemonative.recyclerview.NoteListAdapter;
import com.example.alexdemonative.storage.NoteEntity;
import com.example.alexdemonative.storage.NoteRepository;

import java.util.ArrayList;
import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.transition.Fade;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;
import io.realm.Realm;

/**
 * Main and only activity for the application
 */
public class MainActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private NoteListAdapter mAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Realm realm;
    private NoteRepository noteRepository;
    private List<NoteEntity> noteEntities;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //create and set toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //initialize realm
        Realm.init(this);
        realm = Realm.getDefaultInstance();
        noteRepository = new NoteRepository(realm);

        //then init the recyclerview to show data
        initRecyclerView();
    }

    private void initRecyclerView() {
        //get the recyclerview
        recyclerView = findViewById(R.id.note_recycler_view);

        //set a callback that will delete an item when it is swiped away
        ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                NoteListAdapter.NoteViewHolder vh = (NoteListAdapter.NoteViewHolder) viewHolder;
                //find a note by its id
                long noteId = vh.getNoteId();
                NoteEntity toDelete = noteRepository.getById(noteId);
                if (toDelete != null) {
                    //if found, delete the note
                    noteRepository.delete(toDelete);
                    //update views
                    updateNotes();
                }

            }
        };

        // attaching the touch helper to recycler view
        new ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(recyclerView);

        noteEntities = new ArrayList<>();
        // use a linear layout manager
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        // specify an adapter (see also next example)
        mAdapter = new NoteListAdapter(noteEntities);
        recyclerView.setAdapter(mAdapter);
        updateNotes();
    }

    /**
     * Updates the recyclerview with the current notes
     */
    public void updateNotes() {
        noteEntities = noteRepository.getAll();
        mAdapter.updateNotes(noteEntities);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        //check if this exists. it may not in a fragment view
        MenuItem searchViewMenuItem = menu.findItem(R.id.search);
        if(searchViewMenuItem == null) {
            return false;
        }

        //setup note search
        final SearchView searchView = (SearchView) searchViewMenuItem.getActionView();
        if(searchView == null){
            return false;
        }

        //set initial search state
        String searchFor = searchView.getQuery().toString();
        if (!searchFor.isEmpty()) {
            searchView.setIconified(false);
            searchView.setQuery(searchFor, false);
        }

        searchView.setQueryHint(getString(R.string.search));
        //create text listener for searches
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                if(query.isEmpty()) {
                    //if empty, show all notes again
                    updateNotes();
                } else {
                    //otherwise use the query
                    noteEntities = noteRepository.searchText(query);
                    mAdapter.updateNotes(noteEntities);
                    //dismiss keyboard on submit
                    hideKeyboard();
                }

                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //listen for text changes
                if(newText.isEmpty()) {
                    updateNotes();
                } else {
                    noteEntities = noteRepository.searchText(newText);
                    mAdapter.updateNotes(noteEntities);
                }

                return false;
            }
        });

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_add_note) {
            showAddNoteFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //check if there are any fragments in the stack
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            //if there are, get rid of it and update view
            getFragmentManager().popBackStack();
            updateNotes();
        } else {
            //otherwise let Android decide
            super.onBackPressed();
        }
    }

    private void showAddNoteFragment() {
        //Creates a new AddNoteFragment and adds it to the view
        FragmentManager fm = getSupportFragmentManager();
        AddNoteFragment addNoteFragment = AddNoteFragment.getInstance();
        addTransition(addNoteFragment);
        fm.beginTransaction().add(android.R.id.content, addNoteFragment, "addNote").addToBackStack(null).commit();
    }

    /**
     * Creates a new fragment for editing an existing note
     *
     * @param noteId Id to create the fragment with
     */
    public void showEditNoteFragment(long noteId) {
        //Creates a new AddNoteFragment and adds it to the view
        FragmentManager fm = getSupportFragmentManager();
        EditNoteFragment editNoteFragment = EditNoteFragment.getInstance(noteId);
        addTransition(editNoteFragment);
        fm.beginTransaction().add(android.R.id.content, editNoteFragment, "editNote").addToBackStack(null).commit();
    }

    /**
     * adds a quick fade to fragment transitions
     * @param fragment Fragment to add animation to
     */
    private void addTransition(Fragment fragment){
        Fade enterFade = new Fade();
        enterFade.setDuration(200);
        fragment.setEnterTransition(enterFade);
    }

    private void hideKeyboard() {
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
