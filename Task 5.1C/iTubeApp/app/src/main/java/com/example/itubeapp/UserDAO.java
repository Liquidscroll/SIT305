package com.example.itubeapp;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

@Dao
public interface UserDAO
{
    @Insert
    void insert(UserEntity user);
    @Update
    void update(UserEntity user);
    @Query("SELECT * FROM users WHERE username = :username")
    UserEntity getUserByUsername(String username);
}
