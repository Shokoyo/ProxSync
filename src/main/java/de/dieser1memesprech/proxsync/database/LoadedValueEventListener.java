package de.dieser1memesprech.proxsync.database;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.concurrent.Semaphore;

public class LoadedValueEventListener implements ValueEventListener {
    // create a java.util.concurrent.Semaphore with 0 initial permits
    final Semaphore semaphore = new Semaphore(0);
    DataSnapshot snapshot = null;
    @Override
    public void onDataChange(DataSnapshot dataSnapshot) {
        snapshot = dataSnapshot;
        semaphore.release();
    }

    @Override
    public void onCancelled(DatabaseError databaseError) {
        System.out.println("The read failed: " + databaseError.getCode());
    }
    DataSnapshot getData() {
        try {
            semaphore.acquire();
        } catch (InterruptedException e) {

        }
        return snapshot;
    }
}