package com.example.myapplication.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface BookDao {
    @Query("SELECT * FROM books ORDER BY title ASC")
    List<Book> getAll();

    @Query("SELECT * FROM books WHERE id = :id")
    Book getById(long id);

    @Insert
    long insert(Book book);

    @Query("UPDATE books SET read = :read WHERE id = :id")
    void setRead(long id, boolean read);
}
