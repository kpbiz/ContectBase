package com.example.thong.contectbase;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;


public class MainActivity extends ActionBarActivity {

    private TextView tv;

    private File file;
    private static final String FILENAME = "customers.json";

    // List view
    private ListView lv;

    // Listview Adapter
    ArrayAdapter<String> adapter;

    // Search EditText
    EditText inputSearch;
    private String cusText[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        File extFile = getExternalFilesDir(null);
        String path = extFile.getAbsolutePath();

        file = new File(extFile,FILENAME);
        file.setWritable(true);

        //StringBuilder finalString = new StringBuilder();
        //tv = (TextView) findViewById(R.id.text1);
        //tv.setText("Path... : "+file.toString());
        lv = (ListView) findViewById(R.id.list_view);
        inputSearch = (EditText) findViewById(R.id.inputSearch);

        try{
            if( ! file.exists() ){
                /*AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Reset...");
                alertDialog.setMessage(" empty ");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });
                alertDialog.show();*/
                createFile(tv);
            }
            else {
                /*AlertDialog alertDialog = new AlertDialog.Builder(this).create();
                alertDialog.setTitle("Reset...");
                alertDialog.setMessage(" not empty ");
                alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // here you can add functions
                    }
                });
                alertDialog.show();*/
            }

            readFile();
        }catch (IOException ioe){
            ioe.printStackTrace();
            //tv.setText("Error io");
        }catch (JSONException je){
            je.printStackTrace();
            //tv.setText("Error Json");
        }
        // Adding items to listview
        adapter = new ArrayAdapter<String>(this, R.layout.list_item, R.id.customers, cusText);
        lv.setAdapter(adapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<? > parent, View view, int position, long id) {
                TextView txt = (TextView) parent.getChildAt(position - lv.getFirstVisiblePosition()).findViewById(R.id.customers);
                String keyword = txt.getText().toString();
                keyword = keyword.substring(keyword.indexOf(":")+2, keyword.length());
                //String keyword = Integer.toString(txt.getId());
                //Log.v("value ", "result is " + keyword);
                //AlertDialog.Builder builder = new AlertDialog.Builder(parent.getContext());
                //builder.setMessage("You pressed item #" + (position)+"\n View : "+keyword);
                //builder.setPositiveButton("OK", null);
                //builder.show();
                launchDialer(keyword);

            }
        });
        /**
         * Enabling Search Filter
         * */
        inputSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                // When user changed the Text
                MainActivity.this.adapter.getFilter().filter( cs);
            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,  int arg3) {
                // TODO Auto-generated method stub

            }

            @Override
            public void afterTextChanged(Editable arg0) {
                // TODO Auto-generated method stub
            }
        });
    }

    public void launchDialer(String number){
        String numberToDial = "tel:"+number;
        startActivity(new Intent(Intent.ACTION_DIAL, Uri.parse(numberToDial)));
    }

    private JSONArray getNewJSONData()throws JSONException{
        JSONArray data = new JSONArray();
        JSONObject cus;

        String cusData[][]= new String[][] {
                { "0808880000",  "\u0e15\u0e39\u0e48"},

        };

        for (int row=0;row<cusData.length;row++) {
            cus = new JSONObject();
            cus.put("tel",cusData[row][0]);
            cus.put("name", cusData[row][1]);
            data.put(cus);
        }

        return data;
    }

    public boolean checkExternalStorage(){
        String state = Environment.getExternalStorageState();
        if(state.equals(Environment.MEDIA_MOUNTED)){
            return true;
        }
        else if(state.equals(Environment.MEDIA_SHARED)) {
            //tv.setText("External storage is SHARED");
        }
        else {
            //tv.setText("External storage is unavailable");
        }
        return false;
    }

    public void createFile(View v)throws IOException,JSONException{

        if(!checkExternalStorage() ){ return; }

        JSONArray data = getNewJSONData();

        String text = data.toString();
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(text.getBytes("UTF-8"));
        fos.close();
        //tv.setText("File written to disk:\n" + data.toString());

    }

    public void readFile() throws IOException , JSONException{
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream bis = new BufferedInputStream(fis);
        StringBuffer b = new StringBuffer();
        while(bis.available() != 0){
            char c = (char)bis.read();
            b.append(c);
        }
        bis.close();
        fis.close();

        JSONArray data = new JSONArray(b.toString());
        cusText = new String[data.length()];

        //StringBuffer cusBuffer = new StringBuffer();

        for(int i = 0; i < data.length(); i++ ){
            String cus = new String( data.getJSONObject(i).getString("name").getBytes("ISO-8859-1"),"UTF-8" );
            String tel =  new String( data.getJSONObject(i).getString("tel").getBytes("ISO-8859-1"),"UTF-8" );

            cusText[i]=cus+" : "+tel;
            //cusBuffer.append(cus+" :"+tel1 + "\n");
        }
        //StringBuilder finalString = new StringBuilder();
        //finalString.append(cusBuffer.toString()+"\n");
        //tv.setText(finalString);

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
