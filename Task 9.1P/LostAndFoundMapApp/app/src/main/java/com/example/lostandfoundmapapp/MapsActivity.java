package com.example.lostandfoundmapapp;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    // Default location: Deakin Burwood Campus
    private static final LatLng DEFAULT_LOCATION = new LatLng(-37.84742957418614, 145.11493849076948);
    private static final int MAP_PADDING = 100;
    // HashMap to associate Markers with their Items
    private final HashMap<Marker, Item> markerItemMap = new HashMap<>();
    private ItemDatabase dbHelper;
    private List<Item> items;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        dbHelper = new ItemDatabase(this);

        // Set fragment result listener to reload data when item has been deleted
        getSupportFragmentManager().setFragmentResultListener("ITEM_DEL", this, (result, bundle) -> {
            if (result.equals("ITEM_DEL") && bundle.isEmpty()) {
                items = ItemDatabaseUtils.getItemsFromDB(dbHelper.getReadableDatabase());
                updateMarkers();
            }
        });

        // Load initial list of items
        items = ItemDatabaseUtils.getItemsFromDB(dbHelper.getReadableDatabase());

        initMapFragment();

    }
    private void initMapFragment()
    {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                                                              .findFragmentById(R.id.item_map);
        if(mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }
    // Update markers on map from the list of items
    private void updateMarkers()
    {
        if (mMap == null) return;
        // Clear existing markers
        mMap.clear();
        markerItemMap.clear();

        if(items.isEmpty())
        {
            Toast.makeText(this, "No items found.", Toast.LENGTH_LONG).show();
            moveCameraToDefaultLocation();
            return;
        }

        // Build bounds for the map to show all markers.
        LatLngBounds.Builder builder = new LatLngBounds.Builder();

        for(Item item : items)
        {
            Marker marker = addMarker(item);
            builder.include(marker.getPosition());
            markerItemMap.put(marker, item);
        }

        moveCameraToBounds(builder.build());
    }
    // Add marker for given item on map.
    private Marker addMarker(Item item)
    {
        LatLng pos = new LatLng(item.latitude, item.longitude);
        MarkerOptions markerOptions = new MarkerOptions().position(pos).title(item.post_type)
                                                         .snippet(item.name);

        // If item is "Found", then change the colour to blue.
        if(Objects.equals(item.post_type, "Found"))
        {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));
        }

        return mMap.addMarker(markerOptions);
    }
    private void moveCameraToBounds(LatLngBounds bounds)
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, MAP_PADDING));
    }
    private void moveCameraToDefaultLocation()
    {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(DEFAULT_LOCATION, 15));
    }
    private void setupInfoWindowAdapter()
    {
        mMap.setInfoWindowAdapter(new InfoWindowAdapter(this));
    }
    private void setupInfoWindowClickListener()
    {
        mMap.setOnInfoWindowClickListener(m -> {
            Item item = markerItemMap.get(m);
            if(item != null) { showItemDetailFragment(item); }
        });
    }
    // Show detail fragment for given item
    private void showItemDetailFragment(Item item)
    {
        ItemDetailFragment fragment = new ItemDetailFragment();
        Bundle args = new Bundle();
        args.putParcelable("CURR_ITEM", item);
        fragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.item_map, fragment)
                .addToBackStack(null)
                .commit();
    }
    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        this.mMap = googleMap;
        // Setup map with markers and listeners once map is ready.
        updateMarkers();
        setupInfoWindowAdapter();
        setupInfoWindowClickListener();
    }
}