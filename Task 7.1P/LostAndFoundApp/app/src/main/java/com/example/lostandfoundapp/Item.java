package com.example.lostandfoundapp;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Item implements Parcelable
{
    public int id;
    public String post_type;
    public String name;
    public String phone_number;
    public String description;
    public Date date;
    public String location;
    public Item(int id, String post_type, String name, String phone_number, String description, String date, String location)
    {
        this.id = id;
        this.post_type = post_type;
        this.name = name;
        this.phone_number = phone_number;
        this.description = description;

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try
        {
            this.date = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }

        this.location = location;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags)
    {
        dest.writeInt(id);
        dest.writeString(post_type);
        dest.writeString(name);
        dest.writeString(phone_number);
        dest.writeString(description);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        String dateString = (date != null) ? sdf.format(date) : null;
        dest.writeString(dateString);
        dest.writeString(location);
    }

    protected Item(Parcel in)
    {
        id = in.readInt();
        post_type = in.readString();
        name = in.readString();
        phone_number = in.readString();
        description = in.readString();
        String dateStr = in.readString();
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try
        {
            date = (dateStr != null) ? sdf.parse(dateStr) : null;
        }
        catch (ParseException e)
        {
            throw new RuntimeException(e);
        }
        location = in.readString();
    }

    public static final Creator<Item> CREATOR = new Creator<Item>()
    {
        @Override
        public Item createFromParcel(Parcel in) { return new Item(in); }
        @Override
        public Item[] newArray(int size) { return new Item[size];}
    };
}
