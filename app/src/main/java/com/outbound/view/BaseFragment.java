package com.outbound.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Toast;

import com.outbound.R;
import com.outbound.ui.util.SwipeRefreshLayout;
import com.outbound.util.CountryCodes;
import com.outbound.util.FindAddressCallback;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Locale;

import static com.outbound.util.LogUtils.*;


/**
 * Created by zeki on 15/09/2014.
 */
public abstract class BaseFragment extends Fragment {
    private static final String TAG = makeLogTag(BaseFragment.class);
    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private Dialog progressDialog;

    protected BaseFragmentCallbacks mCallbacks;

    protected int mBaseActivityFrameLayoutId;

    private GetAddressTask mDownloadAddressTask;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (BaseFragmentCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    /**
     * Callbacks interface that the activity using this fragment must implement.
     */
    public interface BaseFragmentCallbacks{
        // Get the current location (may return null)
        Location getLocation();

        //LogOut requested
        void logOut();

        //
        void deployFragment(final int fragmentItemId, Object param1, Object param2);

        // register the view which is disappear when list view scrolling
        void registerHideableHeaderView(View view);

        // whic listview will be lissened
        void enableActionBarAutoHide(final ListView listView);

        // after unvisible of the up views swipelayout top location
        void registerSwipeRefreshProgressBarAsTop(SwipeRefreshLayout swipeRefreshLayout, int progressBarTopWhenActionBarShown);

        // similar action like back button
        void backIconClicked();

        //hide the tabbar
        void hideTabbar();
    }

    protected void setUp( Object param1, Object param2){

    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     *
     * @param baseActivityFrameLayoutId  The android:id containing this fragment's UI.
     */
    protected void setUpBaseLayout(int baseActivityFrameLayoutId){
        mBaseActivityFrameLayoutId = baseActivityFrameLayoutId;
    }

    protected void showAlertDialog(String title, String message, Boolean status) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
//        alertDialog.setIcon(R.drawable.fail);

        // Setting OK Button
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    protected void stopProgress() {
        BaseFragment.this.progressDialog.dismiss();
    }

    protected void startProgress(String message) {
        BaseFragment.this.progressDialog = ProgressDialog.show(
                getActivity(), "", message, true);
    }


    protected void showToastMessage(String message){
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    protected void getAddress(final FindAddressCallback<Address> callback){
        LOGD(TAG,"getAddress called:");
        // In Gingerbread and later, use Geocoder.isPresent() to see if a geocoder is available.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD && !Geocoder.isPresent()) {
            // No geocoder is present. Issue an error message
            Toast.makeText(getActivity(), R.string.no_geocoder_available, Toast.LENGTH_LONG).show();
            return;
        }

        Location loc = mCallbacks.getLocation();

        mDownloadAddressTask = new GetAddressTask(getActivity(),callback);
        mDownloadAddressTask.execute(loc);


//        (new GetAddressTask(getActivity(),callback)).execute(loc);
    }

    private class GetAddressTask extends AsyncTask<Location, Void, Address> {
        Context localContext;
        FindAddressCallback<Address> callback;
        public GetAddressTask(Context context,FindAddressCallback<Address> cb ) {
            // Required by the semantics of AsyncTask
            super();
            localContext = context;
            callback = cb;
        }
        @Override
        protected Address doInBackground(Location... params){
            Geocoder geocoder = new Geocoder(localContext, Locale.getDefault());
            Location location = params[0];
            List<Address> addresses = null;
//            Address addresstemo = null;

            if(location == null) {
                LOGD(TAG, "location is NULL");
                return (Address)null;
            }

            try {
                LOGD(TAG, "geocoder.getFromLocation called");
                    addresses = geocoder.getFromLocation(
                            location.getLatitude(),
                            location.getLongitude(), 1);
                LOGD(TAG, "geocoder.getFromLocation getted");
            } catch (IOException e) {
                LOGD(TAG, "IO Exception in Geocoder.getFromLocation() " + e.getMessage());
                e.printStackTrace();
                return null;
            }

            if (addresses != null && addresses.size() > 0){
                Address address = addresses.get(0);
                LOGD(TAG,"city: " + address.getLocality() +
                        " country: " + address.getCountryName() +
                        " countryCode: " + address.getCountryCode());
                return address;
            }
            else{
                LOGD(TAG, "Address doInBackground adrss is NULL");
                return null;
            }
        }

        @Override
        protected void onPostExecute(Address address) {
            mDownloadAddressTask = null;
            LOGD(TAG, "GetAddressTask onPostExecuted");
            if (address != null){
                callback.done(address.getCountryName(),
                        address.getLocality(),
                        address.getCountryCode(),
                        null);
            }else
                callback.done("",
                        "",
                        "",new Exception("Location is Null"));
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            mDownloadAddressTask = null;
        }
    }
}
