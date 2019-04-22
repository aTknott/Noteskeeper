package com.example.alexdemonative.recyclerview;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.alexdemonative.MainActivity;
import com.example.alexdemonative.R;
import com.example.alexdemonative.storage.NoteEntity;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoteListAdapter extends RecyclerView.Adapter<NoteListAdapter.NoteViewHolder> {

    private List<NoteEntity> noteList;

    public NoteListAdapter(List<NoteEntity> noteList) {
        this.noteList = noteList;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.note_view, viewGroup, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder noteViewHolder, final int i) {
        //get note entity by its position in the list
        NoteEntity noteEntity = noteList.get(i);
        //set text for body and text views
        noteViewHolder.getBodyView().setText(noteEntity.getBody());
        //hide the body if text is empty
        if (!noteEntity.getBody().isEmpty()) {
            noteViewHolder.getBodyView().setVisibility(View.VISIBLE);
        } else {
            noteViewHolder.getBodyView().setVisibility(View.GONE);
        }
        //hide the title if text is empty
        if (!noteEntity.getTitle().isEmpty()) {
            noteViewHolder.getTitleView().setVisibility(View.VISIBLE);
        } else {
            noteViewHolder.getTitleView().setVisibility(View.GONE);
        }

        noteViewHolder.getTitleView().setText(noteEntity.getTitle());
        //we set the id here and not in onCreateViewHolder because the views can be recycled and they would hold onto new ids
        noteViewHolder.setNoteId(noteList.get(i).getId());

        noteViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) v.getContext();
                mainActivity.showEditNoteFragment(noteList.get(i).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    public void updateNotes(List<NoteEntity> noteEntities) {
        this.noteList = noteEntities;
        notifyDataSetChanged();
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {

        private long id;
        private TextView titleView;
        private TextView bodyView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            this.titleView = itemView.findViewById(R.id.titleView);
            this.bodyView = itemView.findViewById(R.id.bodyView);
        }

        public long getNoteId() {
            return id;
        }

        public void setNoteId(long id) {
            this.id = id;
        }

        public TextView getTitleView() {
            return titleView;
        }

        public TextView getBodyView() {
            return bodyView;
        }
    }
}
