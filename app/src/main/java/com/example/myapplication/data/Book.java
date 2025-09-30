package com.example.myapplication.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "books")
public class Book {
    @PrimaryKey(autoGenerate = true)
    public long id;

    public String title;
    public String author;
    public String description;
    public boolean read;

    public String imageRes;

    public Book(String title, String author, String description, String imageRes) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.read = false;
        this.imageRes = imageRes;
    }
}
