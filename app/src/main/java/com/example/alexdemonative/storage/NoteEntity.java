package com.example.alexdemonative.storage;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class NoteEntity extends RealmObject {

    @PrimaryKey
    private long id;

    private String title;

    private String body;

    public NoteEntity() {
        //default constructor for Realm
    }

    public NoteEntity(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public long getId() {
        return id;
    }

    void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
