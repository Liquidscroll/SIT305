package com.example.lostandfoundmapapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class InfoWindowAdapter  implements GoogleMap.InfoWindowAdapter
{
    private final View wind;
    public InfoWindowAdapter(Context context)
    {
        wind = LayoutInflater.from(context).inflate(R.layout.marker_item_info, null);
    }
    private void setInfoText(Marker marker, View view)
    {
        setText(view, R.id.info_type, marker.getTitle());
        setText(view, R.id.info_name, marker.getSnippet());
    }
    private void setText(View view, int viewId, String text)
    {
        TextView textView = view.findViewById(viewId);
        if(text != null && !text.isEmpty())
        {
            textView.setText(text);
        }
        else
        {
            textView.setText("");
        }
    }
    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        setInfoText(marker, wind);
        return wind;
    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        setInfoText(marker, wind);
        return wind;
    }
}
