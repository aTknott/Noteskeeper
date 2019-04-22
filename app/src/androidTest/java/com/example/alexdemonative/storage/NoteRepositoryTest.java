package com.example.alexdemonative.storage;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.UUID;

import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertNull;

@RunWith(AndroidJUnit4.class)
public class NoteRepositoryTest {

    private NoteRepository noteRepository;
    @Before
    public void setup(){
        Realm.init(InstrumentationRegistry.getInstrumentation().getTargetContext());
        //create realm configuration. we use an in-memory database with a random name to ensure that there is no cross-contamination
        RealmConfiguration testConfig = new RealmConfiguration.Builder().inMemory().name(UUID.randomUUID().toString()).build();
        Realm testRealm = Realm.getInstance(testConfig);
        noteRepository = new NoteRepository(testRealm);
    }

    @Test
    public void addNoteTest(){
        NoteEntity noteEntity = new NoteEntity("title", "body");
        noteRepository.insert(noteEntity);

        //id will be 0 because insert handles the ids
        NoteEntity foundEntity = noteRepository.getById(0);
        assertNotNull(foundEntity);
        assertEquals(noteEntity.getBody(), foundEntity.getBody());
        assertEquals(noteEntity.getTitle(), foundEntity.getTitle());
    }

    @Test
    public void deleteNoteTest(){
        NoteEntity noteEntity = new NoteEntity("title", "body");
        noteRepository.insert(noteEntity);

        //id will be 0 because insert handles the ids
        NoteEntity foundEntity = noteRepository.getById(0);
        assertNotNull(foundEntity);
        assertEquals(noteEntity.getBody(), foundEntity.getBody());
        assertEquals(noteEntity.getTitle(), foundEntity.getTitle());

        noteRepository.delete(foundEntity);
        NoteEntity deletedEntity = noteRepository.getById(0);
        assertNull(deletedEntity);
    }

    @Test
    public void updateNoteTest(){
        NoteEntity noteEntity = new NoteEntity("title", "body");
        noteRepository.insert(noteEntity);

        //id will be 0 because insert handles the ids
        NoteEntity foundEntity = noteRepository.getById(0);
        assertNotNull(foundEntity);
        assertEquals(noteEntity.getBody(), foundEntity.getBody());
        assertEquals(noteEntity.getTitle(), foundEntity.getTitle());

        String newTitle = "newTitle";
        String newBody = "newBody";

        noteRepository.update(foundEntity, newBody, newTitle);
        NoteEntity updatedEntity = noteRepository.getById(foundEntity.getId());
        assertNotNull(updatedEntity);
        assertEquals(newTitle, updatedEntity.getTitle());
        assertEquals(newBody, updatedEntity.getBody());
    }

    @Test
    public void getAllTest(){
        NoteEntity noteEntity = new NoteEntity("title", "body");
        noteRepository.insert(noteEntity);
        noteRepository.insert(noteEntity);
        noteRepository.insert(noteEntity);
        noteRepository.insert(noteEntity);
        noteRepository.insert(noteEntity);

        RealmResults<NoteEntity> foundEntities = noteRepository.getAll();
        assertNotNull(foundEntities);
        assertEquals(5, foundEntities.size());
    }

    @Test
    public void searchTest(){
        NoteEntity noteEntity1 = new NoteEntity("AAAA", "BBBB");
        NoteEntity noteEntity2 = new NoteEntity("aaaa", "bbbb");
        NoteEntity noteEntity3 = new NoteEntity("bbbb", "bbbb");
        NoteEntity noteEntity4 = new NoteEntity("aaac", "cccc");
        NoteEntity noteEntity5 = new NoteEntity("dddd", "eeee");

        noteRepository.insert(noteEntity1);
        noteRepository.insert(noteEntity2);
        noteRepository.insert(noteEntity3);
        noteRepository.insert(noteEntity4);
        noteRepository.insert(noteEntity5);

        RealmResults<NoteEntity> foundEntities = noteRepository.searchText("aaa");
        assertNotNull(foundEntities);
        assertEquals(3, foundEntities.size());
    }
}
