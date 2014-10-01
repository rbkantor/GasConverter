package com.reubenk.gasconverter;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


public class MainActivity extends Activity {

    double exchangeRateUSCan = 1.1196;
    double literGallon = 3.7854;
    TextView tv;
    EditText et;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.resultView);
        et = (EditText) findViewById(R.id.editText);

        et.setText("1.30");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void convertBtnClickUsCan(View v)
    {
        try
        {
            double priceGalUs = Double.parseDouble(et.getText().toString());
            double priceLiterCan = priceGalUs / literGallon * exchangeRateUSCan;

            String result = "US price of $" + et.getText().toString() + " per gallon " +
            " corresponds to Canadian price of $" + String.format("%.2f", priceLiterCan) + " per liter";

            tv.setText(result);
        }
        catch (Exception ex)
        {
            tv.setText("error");
        }
    }

    public void convertBtnClickCanUs(View v)
    {
        try {
            double priceGalUs = Double.parseDouble(et.getText().toString());
            double priceLiterCan = priceGalUs * literGallon / exchangeRateUSCan;

            String result = "Canadian price of $" + et.getText().toString() + " per liter " +
                    " corresponds to US price of $" + String.format("%.2f", priceLiterCan) + " per gallon";

            tv.setText(result);

        }
        catch (Exception ex) {
            tv.setText("error");
        }
    }

    public void quitBtnClick(View v) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
