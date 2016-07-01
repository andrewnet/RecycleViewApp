package com.example.andreww.recycleviewapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private List<String> myDataset = new ArrayList<String>();
    private Storage myStorage = new FileStorage();
    private static final String DEBUG_TAG = "DEBUG_TAG";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        Button btnSave = (Button) findViewById(R.id.buttonSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        Button btnLoad = (Button) findViewById(R.id.buttonLoad);
        btnLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                load();
            }
        });

        Button btnClear = (Button) findViewById(R.id.buttonClear);
        btnClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clear();
            }
        });
//

        mRecyclerView = (RecyclerView) findViewById(R.id.my_recycler_view);
//
//        // use this setting to improve performance if you know that changes
//        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);
//
//        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
//
//        // specify an adapter (see also next example)
        mAdapter = new MyAdapter(myDataset, this);
        mRecyclerView.setAdapter(mAdapter);



//        mRecyclerView.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                Log.d(DEBUG_TAG, "on touch event: " + event);
//                return true;
//            }
//        });


        final TextView tv = (TextView) findViewById(R.id.editText);
        tv.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                Log.e("DD", "onEditorAction: " + actionId + ",  " + event);
                myDataset.add(tv.getText().toString());
                return false;
            }
        });







        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
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

    private void save() {
        myStorage.save();
    }


    private void load() {
        myStorage.load();
        mAdapter.notifyDataSetChanged();
    }

    private void clear() {
        myDataset.clear();
        mAdapter.notifyDataSetChanged();
    }

    abstract class Storage{
        public abstract void save();
        public abstract void load();
    }

    class SharedPreferencesStorage extends Storage {

        @Override
        public void save() {
            SharedPreferences sharedPref = getSharedPreferences(
                    "mytest.file", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt("size", myDataset.size());
            for (int i = 0; i < myDataset.size(); i++) {
                editor.putString("item" + i, myDataset.get(i));
            }
            editor.commit();
        }

        @Override
        public void load() {
            SharedPreferences sharedPref = getSharedPreferences(
                    "mytest.file", Context.MODE_PRIVATE);
            myDataset.clear();
            int size = sharedPref.getInt("size", 0);
            if (size > 0) {
                for (int i = 0; i < size; i++) {
                    myDataset.add(sharedPref.getString("item" + i, "MISSING"));
                }
            }
            mAdapter.notifyDataSetChanged();
        }
    }

    class SharedPreferencesStorage2 extends Storage {

        @Override
        public void save() {
            SharedPreferences sharedPref = getSharedPreferences(
                    "mytest.file", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            Set<String> sets = new HashSet<String>();
            sets.addAll(myDataset);
            editor.putStringSet("items", sets);

            editor.commit();
        }

        @Override
        public void load() {
            SharedPreferences sharedPref = getSharedPreferences(
                    "mytest.file", Context.MODE_PRIVATE);

            Set<String> sets = sharedPref.getStringSet("items", new HashSet<String>());
            myDataset.clear();
            myDataset.addAll(sets);
        }
    }

    class FileStorage extends Storage {

        @Override
        public void save() {
            String filename = "myfile";
            String string = "Hello world!";
            BufferedWriter bw = null;
            FileOutputStream outputStream = null;
            try {
                outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
                bw = new BufferedWriter(new OutputStreamWriter(outputStream, "UTF-8"));
                bw.write(myDataset.size());
                for (int i = 0; i < myDataset.size(); i++) {
                    bw.write("\r\n");
                    bw.write(myDataset.get(i));
                }
                bw.close();
                bw = null;
                outputStream.close();
                outputStream = null;
            } catch (Exception e) {
                Log.e("tag", "failed to save", e);
            } finally {
                if (bw != null) {
                    try {
                        bw.close();
                    } catch (Exception e) {
                        Log.e("tag", "failed to close bw", e);
                    }
                }
                if (outputStream != null) {
                    try {
                        outputStream.close();
                    } catch (Exception e) {
                        Log.e("tag", "failed to close ouputstream", e);
                    }
                }
            }
        }

        @Override
        public void load() {
            String filename = "myfile";
            String string = "Hello world!";
            LineNumberReader lnr = null;
            FileInputStream inputStream = null;
            try {
                inputStream = openFileInput(filename);
                lnr = new LineNumberReader(new InputStreamReader(inputStream, "UTF-8"));
                myDataset.clear();
                String line = lnr.readLine();
                if (line != null) {
                    line = lnr.readLine();
                    while (line != null) {
                        myDataset.add(line);
                        line = lnr.readLine();
                    }
                }
                lnr.close();
                lnr = null;
                inputStream.close();
                inputStream = null;
            } catch (Exception e) {
                Log.e("tag", "failed to load", e);
            } finally {
                if (lnr != null) {
                    try {
                        lnr.close();
                    } catch (Exception e) {
                        Log.e("tag", "failed to close lnr", e);
                    }
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        Log.e("tag", "failed to close inputStream", e);
                    }
                }
            }
        }
    }
}
