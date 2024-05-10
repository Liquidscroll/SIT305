package com.example.lostandfoundapp;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.icu.util.Calendar;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.util.Locale;

public class NewAdvertActivity extends AppCompatActivity
{
    private EditText name, phone_number, description, date, location;
    private RadioGroup post_type_group;
    private ItemDatabase dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_advert);

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.new_advert_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initElements();
    }
    // Setup UI components and listeners.
    private void initElements()
    {
        dbHelper = new ItemDatabase(this);

        post_type_group = findViewById(R.id.post_type_group);
        name = findViewById(R.id.name_entry);
        phone_number = findViewById(R.id.phone_entry);
        description = findViewById(R.id.description_entry);
        date = findViewById(R.id.date_entry);
        location = findViewById(R.id.location_entry);

        phone_number.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
        findViewById(R.id.btn_save_advert).setOnClickListener(v -> saveAdvert());

        date.setOnClickListener(v -> showDatePicker());
    }
    private void saveAdvert()
    {
        int selectedRadioID = post_type_group.getCheckedRadioButtonId();
        String post_type;
        if(selectedRadioID == -1) { post_type = ""; }
        else { post_type = selectedRadioID == R.id.lost_radio ? "Lost" : "Found"; }

        String name_text = name.getText().toString();
        String phone_number_text = phone_number.getText().toString();
        String description_text = description.getText().toString();
        String date_text = date.getText().toString();
        String location_text = location.getText().toString();

        if (fieldsAreEmpty(post_type, name_text, phone_number_text, description_text, date_text, location_text))
        {
            Toast.makeText(this, "Please fill in all required fields", Toast.LENGTH_LONG).show();
            return;
        }
        if(insertAdvertIntoDatabase(post_type, name_text, phone_number_text, description_text, date_text, location_text))
        {
            Toast.makeText(this, "Advert saved", Toast.LENGTH_SHORT).show();
            finish();
        }
        else
        {
            Toast.makeText(this, "Error, could not save advert", Toast.LENGTH_SHORT).show();
        }
    }
    // Check if any fields are empty.
    private boolean fieldsAreEmpty(String... fields)
    {
        for(String field : fields) { if(field.isEmpty()) { return true; } }
        return false;
    }
    // Insert advert into database. Returns false if newRowId == -1 indicating that insertion
    // was not successfull.
    private boolean insertAdvertIntoDatabase(String post_type, String name, String phone_number,
                                             String description, String date, String location)
    {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("post_type", post_type);
        values.put("name", name);
        values.put("phone_number", phone_number);
        values.put("description", description);
        values.put("date", date);
        values.put("location", location);

        long newRowId = db.insert("adverts", null, values);
        return newRowId != -1;
    }
    // Display date picker dialog
    private void showDatePicker()
    {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpg = new DatePickerDialog(this, (view, newYear, monthOfYear, dayOfMonth) -> {
            date.setText(String.format(Locale.getDefault(), "%02d/%02d/%d", dayOfMonth, monthOfYear + 1, newYear));
        }, year, month, day);

        dpg.getDatePicker().setMaxDate(c.getTimeInMillis());
        dpg.show();

    }
    // Hide keyboard when user presses on a non-EditText area.
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev)
    {
        if(getCurrentFocus() != null)
        {
            InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }
}
