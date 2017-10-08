package com.example.urban.googlemaps;

import android.app.IntentService;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Created by urban on 7. 10. 2017.
 */

public class FetchAddressIntentService extends IntentService {

    // Result Receiver object
    protected ResultReceiver mReceiver;

    //Constructor
    public FetchAddressIntentService(){
        super("fetch-address-intent-service"); //give name to worker thread
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        String errorMessage = "";
        // get receiver
        mReceiver = intent.getParcelableExtra(Constants.RECEIVER);
        // location (lat, lng)
        Location location = intent.getParcelableExtra(Constants.LOCATION_DATA_EXTRA);
        // create geocoder
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        // Address found using the Geocoder.
        List<Address> addresses = null;
        try {
            // fetch only single address
            addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
        } catch (IOException ioException) {
            // network or other I/O problems
            errorMessage = "Service is not available (IOException)";
        } catch (IllegalArgumentException illegalArgumentException) {
            // invalid latitude or longitude values.
            errorMessage = "Invalid latitude and longitude values.";
        }
        // handle case where no address was found.
        if (addresses == null || addresses.size()  == 0) {
            if (errorMessage.isEmpty()) errorMessage = "No address found.";
            deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
        } else {
            Address address = addresses.get(0);
            ArrayList<String> addressFragments = new ArrayList<String>();
            // Fetch the address lines
            for(int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                addressFragments.add(address.getAddressLine(i));
            }
            deliverResultToReceiver(Constants.SUCCESS_RESULT,
                    TextUtils.join(System.getProperty("line.separator"), addressFragments));
        }
    }

    // Return the address to the requestor -> Activity
    private void deliverResultToReceiver(int resultCode, String message) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.RESULT_DATA_KEY, message);
        mReceiver.send(resultCode, bundle);
    }

}
