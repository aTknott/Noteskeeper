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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import io.realm.Realm;

/**
 * Fragment for adding a new NoteEntity
 */
public class AddNoteFragment extends Fragment {

    private Realm realm;
    private NoteRepository noteRepository;

    /**
     * Returns a new instance of an AddNoteFragment
     * @return New fragment
     */
    public static AddNoteFragment getInstance() {
        return new AddNoteFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init realm
        realm = Realm.getDefaultInstance();
        noteRepository = new NoteRepository(realm);

        //ensures keyboard will always be below edit text
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.add_note_fragment, container, false);
        //signal fragment has its own menu
        setHasOptionsMenu(true);
        //create and set toolbar
        Toolbar toolbar = view.findViewById(R.id.fragment_toolbar);
        toolbar.setTitle(R.string.add_note);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        //give toolbar the back button
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        return view;
    }

    private void saveNote() {
        //get body and title text
        final EditText addBody = getView().findViewById(R.id.add_body);
        final EditText addTitle = getView().findViewById(R.id.add_title);
        String titleText = addTitle.getText().toString().trim();
        String bodyText = addBody.getText().toString().trim();

        //use text to create a new object and save it in realm
        NoteEntity newNote = new NoteEntity(titleText, bodyText);
        noteRepository.insert(newNote);
        //tell activity to update its views
        ((MainActivity) getActivity()).updateNotes();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        //clear menu so that it doesn't have activity menu
        menu.clear();
        //inflate fragment menu
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
            //dismiss keyboard
            InputMethodManager inputManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}
