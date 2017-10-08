package com.example.urban.googlemaps;

/**
 * Created by urban on 7. 10. 2017.
 */

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class PreviousRoutesActivity extends Activity {

    private ListView mList;
    CSVAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_routes);

        mList = (ListView) findViewById(R.id.myListView);

        mAdapter = new CSVAdapter(this, -1);

        mList.setAdapter(mAdapter);

        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Toast.makeText(view.getContext(), String.valueOf(mAdapter.getItem(i).getAccuracy()) + ", " +
                        String.valueOf(mAdapter.getItem(i).getSpeed()), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
