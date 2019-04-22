package com.example.alexdemonative.storage;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Realm repository class for NoteEntity's
 */
public class NoteRepository {

    private Realm realm;

    public NoteRepository(Realm realm) {
        this.realm = realm;
    }

    /**
     * Finds all existing notes
     * @return RealmResults containing all notes
     */
    public RealmResults<NoteEntity> getAll() {
        return realm.where(NoteEntity.class).findAll();
    }

    /**
     * Finds a single NoteEntity by its unique ID
     * @param id to find note by
     * @return Found NoteEntity
     */
    public NoteEntity getById(long id) {
        return realm.where(NoteEntity.class).equalTo("id", id).findFirst();
    }

    /**
     * Inserts a new note into realm
     * @param noteEntity To insert
     */
    public void insert(final NoteEntity noteEntity) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                //get the highest primary key value
                Number maxValue = realm.where(NoteEntity.class).max("id");
                long pk = (maxValue != null) ? maxValue.intValue() + 1 : 0;
                //increment it for the new primary key
                noteEntity.setId(pk++);
                realm.insert(noteEntity);
            }
        });

    }

    /**
     * Updates an existing noteEntity
     * @param noteEntity Entity to update
     * @param body New body to set
     * @param title New Title
     */
    public void update(final NoteEntity noteEntity, final String body, final String title) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                noteEntity.setBody(body);
                noteEntity.setTitle(title);
                realm.copyToRealmOrUpdate(noteEntity);
            }
        });

    }

    /**
     * Deletes an existing entity in realm
     * @param noteEntity Entity to delete
     */
    public void delete(final NoteEntity noteEntity) {
        realm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                noteEntity.deleteFromRealm();
            }
        });
    }

    /**
     * Searches title and body for text matching the query
     * @param query Text to search for
     * @return List of notes with the found text
     */
    public RealmResults<NoteEntity> searchText(final String query){
        return realm.where(NoteEntity.class).contains("body", query, Case.INSENSITIVE).or().contains("title", query, Case.INSENSITIVE).findAll();
    }
}
