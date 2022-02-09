package org.muren.requests;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * MainActivityv
 */
public class MainActivity extends AppCompatActivity {

    public static final String ACTION_START = "org.muren.requests.actionStart";

    public static final int MAX_REQUESTS = 10;

    // Main layout
    private LinearLayout linearRoot = null;

    private RequestClient client;
    private List<RequestObject> requestObjects;
    private LinkedList<RequestTextView> requestTextViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linearRoot = findViewById(R.id.linearRoot);

        client = new RequestClient();

        // Read configured request from preferences
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        requestObjects = new ArrayList<>();
        readPreferences(sharedPref, requestObjects);

        updateList();

        // Start Notifaction Service
        Intent backgroundService = new Intent(getApplicationContext(), NotificationService.class);
        backgroundService.setAction(ACTION_START);
        startService(backgroundService);
    }

    /**
     * Updates the GUI
     */
    public void updateList(){
        if(requestTextViewList == null) requestTextViewList = new LinkedList<>();

        // Cleanup old TextViews
        for(RequestTextView textView : requestTextViewList){
            linearRoot.removeView(textView);
        }
        requestTextViewList.clear();

        // Create new Textviews
        int index = 0;
        for(RequestObject requestObject : requestObjects){
            RequestTextView textView = new RequestTextView(linearRoot.getContext(), requestObject);

            final int currentIndex = index++;

            textView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    editRequest(view);
                    return false;
                }
            });

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    RequestObject requestObject = requestObjects.get(currentIndex);
                    client.makeRequest(getApplicationContext(), requestObject.getUrls(), null, null, null);
                }
            });

            linearRoot.addView(textView);
            requestTextViewList.add(textView);
        }
    }

    /**
     * opens a dialog to edit the request object
     * @param view
     */
    public void editRequest(View view) {
        if(view instanceof RequestTextView == false)
            return;

        RequestTextView requestTextView = (RequestTextView) view;

        LinkedList<Pair<String, String>> args = new LinkedList<>();
        args.add(new Pair<>("action", "get_bulbs"));

        EditSocketsDialogFragment dialog = new EditSocketsDialogFragment();
        dialog.setMainActivity(MainActivity.this);
        dialog.setRequestTextView(requestTextView);
        dialog.show(getSupportFragmentManager(), "test");
    }

    /**
     * EditSocketsDialogFragment
     */
    public static class EditSocketsDialogFragment extends DialogFragment {
        private MainActivity mainActivity;
        private RequestTextView requestTextView;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final RequestObject requestObject = requestTextView.getRequestObject();
            final EditText inputName = new EditText(getContext());
            inputName.setText(requestObject.getName());
            inputName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

            // Builds a list of EditText objects for the URLs
            final List<EditText> inputUrls = new ArrayList<>();
            List<String> urls = requestObject.getUrls();
            for(int i=0; i<RequestObject.MAX_URLS; i++){
                final EditText inputUrl = new EditText(getContext());

                String url = urls.get(i);
                if(url == null)
                    url = "";

                inputUrl.setText(url);
                inputUrl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

                inputUrls.add(inputUrl);
            }

            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
            linearLayout.setWeightSum(2);

            linearLayout.addView(inputName);

            for (EditText inputUrl : inputUrls) {
                linearLayout.addView(inputUrl);
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setView(linearLayout);
            // Set the dialog title
            builder.setTitle("Edit Requests")
                    // Set the action buttons
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {

                            List<String> urls = new ArrayList<>();

                            for(EditText inputUrl : inputUrls){
                                urls.add(inputUrl.getText().toString());
                            }

                            requestObject.setName(inputName.getText().toString());
                            requestObject.setUrls(urls);
                            mainActivity.updateList();
                            mainActivity.writePreferences();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                        }
                    });

            return builder.create();
        }

        public void setRequestTextView(RequestTextView requestTextView) {
            this.requestTextView = requestTextView;
        }

        public void setMainActivity(MainActivity mainActivity) {
            this.mainActivity = mainActivity;
        }
    }

    /**
     * Writes the configured Information back to the preferences
     */
    private void writePreferences(){
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sharedPref.edit();
        for(int i=0; i<MAX_REQUESTS; i++){
            RequestObject requestObject = requestObjects.get(i);

            if(requestObject == null)
                return;
            if(requestObject.getName() == null)
                return;
            if(requestObject.getUrls() == null)
                return;

            editor.putString("request_key" + i, requestObject.getName());

            int iUrl = 0;
            for(String url : requestObject.getUrls()){
                editor.putString("request_value" + i + "_" + iUrl, url);
                iUrl++;
            }
        }
        editor.commit();
    }

    /**
     * Reads the configured Information from the Preferences
     * @param sharedPref
     * @param requestObjects
     */
    public static void readPreferences(SharedPreferences sharedPref, List<RequestObject> requestObjects) {
        for(int i=0; i<MAX_REQUESTS; i++){
            String key = sharedPref.getString("request_key" + i, "EMPTY");

            List<String> urls = new ArrayList<>();

            for(int iUrl = 0; iUrl<RequestObject.MAX_URLS; iUrl++){
                String value = sharedPref.getString("request_value" + i + "_" + iUrl, "");
                urls.add(value);
            }
            requestObjects.add(new RequestObject(key, urls));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
