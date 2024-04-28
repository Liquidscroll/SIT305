package com.example.itubeapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;


@Entity(tableName = "users")
public class UserEntity implements Parcelable
{
    @PrimaryKey(autoGenerate = true)
    public int uid;
    @ColumnInfo(name = "full_name")
    public String fullName;
    @ColumnInfo(name = "username")
    public String username;
    @ColumnInfo(name = "password")
    public String password;
    @ColumnInfo(name = "playlist")
    public String playlist;

    private static final Gson gson = new Gson();

    public UserEntity() {}


    public List<String> getPlaylist()
    {
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(this.playlist, type);
    }
    public void setPlaylist(List<String> newPlaylist)
    {
        this.playlist = gson.toJson(newPlaylist);
    }

    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel parcel, int flags)
    {
        parcel.writeInt(uid);
        parcel.writeString(fullName);
        parcel.writeString(username);
        parcel.writeString(password);
        parcel.writeString(playlist);
    }

    protected UserEntity(Parcel in)
    {
        uid = in.readInt();
        fullName = in.readString();
        username = in.readString();
        password = in.readString();
        playlist = in.readString();
    }

    public static final Creator<UserEntity> CREATOR = new Creator<UserEntity>() {
        @Override
        public UserEntity createFromParcel(Parcel in) {
            return new UserEntity(in);
        }

        @Override
        public UserEntity[] newArray(int size) {
            return new UserEntity[size];
        }
    };
}