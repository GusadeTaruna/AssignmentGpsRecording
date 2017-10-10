package com.example.urban.googlemaps;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import android.content.Context;
import android.os.Environment;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;



/*
 * Very basic Custom Adapter that takes state name,capital pairs out of a csv
 * file from the assets and uses those values to build a List of State objects.
 * Overrides the default getView() method to return a TextView with the state name.
 *
 * ArrayAdapter - a type of Adapter that works a lot like ArrayList.
 */
public class CSVAdapter extends ArrayAdapter<GpsData>{
    Context ctx;
    File file;

    //We must accept the textViewResourceId parameter, but it will be unused
    //for the purposes of this example.
    public CSVAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);

        //Store a reference to the Context so we can use it to load a file from Assets.
        this.ctx = context;

        //Load the data.
        loadArrayFromFile();
    }



    /*
     * getView() is the method responsible for building a View out of a some data that represents
     * one row within the ListView. For this example our row will be a single TextView that
     * gets populated with the state name.
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(final int pos, View convertView, final ViewGroup parent){
		/*
		 * Using convertView is important. The system will pass back Views that have been
		 * created but scrolled off of the top (or bottom) of the screen, and thus are no
		 * longer being shown on the screen. Since they are unused, we can "recycle" them
		 * instead of creating a new View object for every row, which would be wasteful,
		 * and lead to poor performance. The diference may not be noticeable in this
		 * small example. But with larger more complex projects it will make a significant
		 * improvement by recycling Views rather than creating new ones for each row.
		 */
        TextView mView = (TextView)convertView;
        //If convertView was null then we have to create a new TextView.
        //If it was not null then we'll re-use it by setting the appropriate
        //text String to it.
        if(null == mView){
            mView = new TextView(parent.getContext());
            mView.setTextSize(28);
        }

        //Set the state name as the text.
        mView.setText(String.valueOf(getItem(pos).getLatitude())+" "+String.valueOf(getItem(pos).getLongitude()));

        //We could handle the row clicks from here. But instead
        //we'll use the ListView.OnItemClickListener from inside
        //of MainActivity, which provides some benefits over doing it here.

		/*mView.setOnClickListener(new OnClickListener(){
			public void onClick(View v){
				Toast.makeText(parent.getContext(), getItem(pos).getCapital(), Toast.LENGTH_SHORT).show();
			}
		});*/

        return mView;
    }

    /*
     * Helper method that loads the data from the gpsData.csv and builds
     * each csv row into a gpsObject object which then gets added to the Adapter.
     */
    private void loadArrayFromFile(){
        try {
            File sdcard = Environment.getExternalStorageDirectory();

            //Get the list of file
            File dirFileObj = new File(sdcard,"/gpsApp/");
            String[] files = dirFileObj.list();
            long max = 0;
            int maxIndex = 0;
            if(files != null) {
                for (int i = 0; i < files.length; i++) {
                    String name = files[i];
                    String com = name.replace(".csv","");
                    //find the newest file
                    if (Long.parseLong(com) > max) {
                        maxIndex = i;
                        max = Long.parseLong(com);
                    }
                }
                String fileName = sdcard + "/gpsApp/" + files[maxIndex];
                file = new File(fileName);
            }
            // Get input stream and Buffered Reader for our data file.
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            //Read each line
            while ((line = reader.readLine()) != null) {

                //Split to separate the variables
                String[] RowData = line.split(",");

                //Create a GpsObject object for this row's data.
                GpsData cur = new GpsData();
                cur.setLatitude(Double.parseDouble(RowData[0]));
                cur.setLongitude(Double.parseDouble(RowData[1]));
                cur.setAccuracy(Double.parseDouble(RowData[2]));
                cur.setSpeed(Double.parseDouble(RowData[3]));

                //Add the GpsObject object to the ArrayList (in this case we are the ArrayList).
                this.add(cur);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}