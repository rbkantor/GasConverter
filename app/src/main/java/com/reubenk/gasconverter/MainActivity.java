package com.reubenk.gasconverter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends Activity {

    private static final String LOG_TAG = "com.reubenk.gasconverter";
    double exchangeRateUSCan = 1.1;
    double literGallon = 3.7854;
    TextView tv;
    EditText et;
    TextView exchangeRate;
    MyTask mt;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv = (TextView) findViewById(R.id.resultView);
        et = (EditText) findViewById(R.id.editText);
        exchangeRate = (TextView) findViewById(R.id.exchangeRate);
        et.setText("1.30");
        exchangeRate.setText(String.valueOf(exchangeRateUSCan));

        try {

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String priceValue = sharedPref.getString("value", "1.30");
        String exchangeRateValue = sharedPref.getString("exchange", "1.1");

            exchangeRateUSCan = Double.parseDouble(exchangeRateValue);
            et.setText(priceValue);
            exchangeRate.setText(String.valueOf(exchangeRateUSCan));
        } catch (Exception ex) {

        }
    }


    public void onDestroy() {


        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("value", et.getText().toString());
        editor.putString("exchange", exchangeRate.getText().toString());
        editor.commit();

        super.onDestroy();


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
        switch(item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_exchange_rates:
                exchangeBtnClick(null);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
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

    public void BtnPlsMnsClick(View v)
    {
        Button btn = (Button)v;
        try {
            double price = Double.parseDouble(et.getText().toString());
            if (btn.getText().equals("+")) {
                price += 0.01;
            }
            else
            {
                price -= 0.01;
            }
            et.setText(String.format("%.2f", price));
        }
        catch (Exception ex) {
        }
    }

    public void exchangeBtnClick(View v) {
        //   Intent intent = new Intent(Intent.ACTION_MAIN);
        //intent.addCategory(Intent.CATEGORY_HOME);
        //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);

        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo i = conMgr.getActiveNetworkInfo();
        if (i == null) {
            Toast.makeText(getApplicationContext(), "No internet...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!i.isConnected()) {
            Toast.makeText(getApplicationContext(), "Not connected internet...", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!i.isAvailable()) {
            Toast.makeText(getApplicationContext(), "Not available internet...", Toast.LENGTH_SHORT).show();
            return;
        }

        mt = new MyTask();
        mt.execute();
    }

    class MyTask extends AsyncTask<Void, Void, Void> {

        String exchangeRateValue;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Toast.makeText(getApplicationContext(), "Updating...", Toast.LENGTH_SHORT).show();

        }

        @Override
        protected Void doInBackground(Void... params) {
            try {
                    URL url = new URL("http://www.bankofcanada.ca/stats/assets/xml/noon-five-day.xml");

                    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
                    DocumentBuilder db = dbf.newDocumentBuilder();
                    Document doc = db.parse(new InputSource(url.openStream()));
                    doc.getDocumentElement().normalize();
                    NodeList nodeList = doc.getElementsByTagName("Observation_data");
                    Element el = (Element)nodeList.item(4);

                    exchangeRateValue = el.getTextContent();

                    }
                    catch (Exception e) {

                    }

            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            Toast.makeText(getApplicationContext(), "Exchange Rate Updated: " + exchangeRateValue + " CAD in 1 US$", Toast.LENGTH_SHORT).show();
            exchangeRateUSCan = Double.parseDouble(exchangeRateValue);
            exchangeRate.setText(exchangeRateValue);
        }
    }

}
