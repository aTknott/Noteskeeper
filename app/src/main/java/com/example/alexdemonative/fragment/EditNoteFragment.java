package com.example.alexdemonative.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.example.alexdemonative.MainActivity;
import com.example.alexdemonative.R;
import com.example.alexdemonative.storage.NoteEntity;
import com.example.alexdemonative.storage.NoteRepository;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import io.realm.Realm;

/**
 * Fragment for editing a note that already exists
 */
public class EditNoteFragment extends Fragment {

    private Realm realm;
    private NoteEntity noteEntity;
    private NoteRepository noteRepository;
    private static final String NOTE_ID = "noteId";

    public static EditNoteFragment getInstance(long noteId) {
        EditNoteFragment fragment = new EditNoteFragment();

        Bundle args = new Bundle();
        args.putLong(NOTE_ID, noteId);
        fragment.setArguments(args);

        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //get note id from arguments
        long id = getArguments().getLong(NOTE_ID);
        //init realm
        realm = Realm.getDefaultInstance();
        noteRepository = new NoteRepository(realm);
        //get note to be editied
        noteEntity = noteRepository.getById(id);

        //ensures keyboard will always be below edit text
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_note_fragment, container, false);

        EditText titleText = view.findViewById(R.id.add_title);
        titleText.setText(noteEntity.getTitle());
        EditText bodyText = view.findViewById(R.id.add_body);
        bodyText.setText(noteEntity.getBody());

        setHasOptionsMenu(true);
        Toolbar toolbar = view.findViewById(R.id.fragment_toolbar);
        toolbar.setTitle(R.string.edit_note);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }

    private void saveNote() {
        final EditText addBody = getView().findViewById(R.id.add_body);
        final EditText addTitle = getView().findViewById(R.id.add_title);

        String titleText = addTitle.getText().toString().trim();
        String bodyText = addBody.getText().toString().trim();

        if (titleText.isEmpty() && bodyText.isEmpty()) {
            //if both body and title text are empty, delete the note
            noteRepository.delete(noteEntity);
            //show snackbar to indicate note was deleted. we want to use the root activity view because the fragment view will disappear
            Snackbar.make(getActivity().findViewById(android.R.id.content), R.string.note_deleted, Snackbar.LENGTH_LONG).show();
        } else {
            noteRepository.update(noteEntity, bodyText, titleText);
        }
        ((MainActivity) getActivity()).updateNotes();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.menu_fragment, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                saveNote();
                hideKeyboard();
                getActivity().onBackPressed();
                break;
            case R.id.menu_confirm_note:
                saveNote();
                hideKeyboard();
                getActivity().onBackPressed();
                break;
        }
        return true;
    }

    private void hideKeyboard() {
        // Check if no view has focus
        View view = getActivity().getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
