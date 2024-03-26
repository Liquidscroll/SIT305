package com.example.unitconverter;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.text.DecimalFormat;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    //Declare UI elements.
    Spinner selSpinner, srcSpinner, destSpinner;
    Button convButton;
    EditText userInput;
    TextView results;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Init UI by finding each component in the layout.
        selSpinner = findViewById(R.id.conversionSpinner);
        srcSpinner = findViewById(R.id.srcSpinner);
        destSpinner = findViewById(R.id.destSpinner);
        convButton = findViewById(R.id.button);
        userInput = findViewById(R.id.userInput);
        results = findViewById(R.id.resText);

        ArrayAdapter<CharSequence> selAdapter = ArrayAdapter.createFromResource(
                this,
                R.array.conversions_array,
                R.layout.custom_spinner // Custom theming for spinner
        );

        // Custom theming for spinner
        selAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
        selSpinner.setOnItemSelectedListener(this);
        selSpinner.setAdapter(selAdapter);

        // Apply insets to avoid overlap with system
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Set onClickListener on the convert button to actually perform conversion.
        convButton.setOnClickListener(v -> performConversion());
    }
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
    {
        // This method is called when the conversion selection spinner is used.
        // It will switch the other spinners based on the desired unit to convert.
        ArrayAdapter<CharSequence> spinnerAdapter = null;
        switch(parent.getItemAtPosition(pos).toString())
        {
            case "Length":
                spinnerAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.length_units_array,
                        R.layout.custom_spinner
                );
                break;
            case "Weight":
                spinnerAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.weight_units_array,
                        R.layout.custom_spinner
                );
                break;
            case "Temperature":
                spinnerAdapter = ArrayAdapter.createFromResource(
                        this,
                        R.array.temp_units_array,
                        R.layout.custom_spinner
                );
                break;
        }
        // If something has been selected then we will set the new adapter to the
        // src and dest spinners.
        if(spinnerAdapter != null) {
            spinnerAdapter.setDropDownViewResource(R.layout.custom_spinner_dropdown);
            srcSpinner.setAdapter(spinnerAdapter);
            destSpinner.setAdapter(spinnerAdapter);

            results.setText("");
        }
    }
    @Override
    public void onNothingSelected(AdapterView<?> parent)
    {
    }

    private void performConversion()
    {
        String userInputValue = userInput.getText().toString();
        if(!userInputValue.isEmpty())
        {
            try
            {
                double value = Double.parseDouble(userInputValue);
                double res = convert(value, srcSpinner.getSelectedItem().toString(),
                                    destSpinner.getSelectedItem().toString());

                // We use DecimalFormat so that we can drop any trailing zeros
                // and ensure there is a number before the decimal point.
                DecimalFormat decFormat = new DecimalFormat("0.######");
                String formattedResult = decFormat.format(res);
                results.setText(formattedResult);
            } catch(NumberFormatException e)
            {
                results.setText(getString(R.string.invalid_input));
            }
        }
        else
        {
            results.setText(getString(R.string.no_input));
        }
    }

    private double convert(double value, String srcUnit, String destUnit)
    {
        // We convert differently depending on the currently selected unit type.
        // For linear relationships like length and weight, we initially convert
        // the source item into an intermediary unit which is then converted into
        // the final unit.

        // In the case of temperature we do not have linear relationships, and so
        // we cannot use an intermediary.
        switch(selSpinner.getSelectedItem().toString())
        {
            case "Length":
                double valInMetres = convertToMetres(value, srcUnit);
                return convertFromMetres(valInMetres, destUnit);
            case "Weight":
                double valInKG = convertToKilo(value, srcUnit);
                return convertFromKilo(valInKG, destUnit);
            case "Temperature":
                return convertTemperature(value, srcUnit, destUnit);
        }
        return 0;
    }

    private double convertToMetres(double value, String unit)
    {
        // Conversion factor for the metre was calculated from
        // the units provided in the task sheet.
        // 1 inch = 2.54cm = 0.0254 m
        // 1 foot = 30.48 cm = 0.3048 m
        // 1 yard = 91.44 cm = 0.9144 m
        // 1 mile = 1.60934 km = 1609.34 m

        switch(unit)
        {
            case "Inch": return value * 0.0254;
            case "Foot": return value * 0.3048;
            case "Yard": return value * 0.9144;
            case "Mile": return value * 1609.34;
            case "Millimetre": return value * 0.001;
            case "Centimetre": return value * 0.01;
            case "Metre": return value;
            case "Kilometre": return value * 1000;
            default: return -1;
        }
    }

    private double convertFromMetres(double value, String unit)
    {
        switch(unit)
        {
            case "Inch": return value / 0.0254;
            case "Foot": return value / 0.3048;
            case "Yard": return value / 0.9144;
            case "Mile": return value / 1609.34;
            case "Millimetre": return value / 0.001;
            case "Centimetre": return value / 0.01;
            case "Metre": return value;
            case "Kilometre": return value / 1000;
            default: return -1;
        }

    }

    private double convertToKilo(double value, String unit)
    {
        // Like above:
        // 1 pound = 0.453592 kg
        // 1 ounce = 28.3495 g = 0.0283495 kg
        // 1 ton = 907.185 kg
        switch(unit)
        {
            case "Pound": return value * 0.453592;
            case "Ounce": return value * 0.0283495;
            case "Ton": return value * 907.185;
            case "Gram": return value * 0.001;
            case "Kilogram": return value;
            default: return -1;
        }
    }

    private double convertFromKilo(double value, String unit)
    {
        switch(unit)
        {
            case "Pound": return value / 0.453592;
            case "Ounce": return value / 0.0283495;
            case "Ton": return value / 907.185;
            case "Gram": return value / 0.001;
            case "Kilogram": return value;
            default: return -1;
        }
    }

    private double convertTemperature(double value, String srcUnit, String destUnit)
    {
        switch(srcUnit)
        {
            case "Celsius":
                switch(destUnit)
                {
                    case "Fahrenheit": return (value * 1.8) + 32;
                    case "Kelvin": return value + 273.15;
                    default: return value;

                }
            case "Fahrenheit":
                switch(destUnit)
                {
                    case "Celsius": return (value - 32) / 1.8;
                    case "Kelvin": return ((value - 32) / 1.8) + 273.15;
                    default: return value;
                }
            case "Kelvin":
                switch(destUnit)
                {
                    case "Celsius": return value - 273.15;
                    case "Fahrenheit": return ((value - 273.15) * 1.8) + 32;
                    default: return value;
                }
            default:
                return value;
        }
    }
}

