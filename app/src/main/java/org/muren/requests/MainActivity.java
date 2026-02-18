package org.muren.requests;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * MainActivityv
 */
public class MainActivity extends AppCompatActivity {

    public static final String ACTION_START = "org.muren.requests.actionStart";

    // Main layout
    private LinearLayout scrollLinearLayout = null;

    private RequestClient client;
    private List<RequestObject> requestObjects;
    private LinkedList<RequestTextView> requestTextViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        scrollLinearLayout = findViewById(R.id.scrollLinearLayout);

        client = new RequestClient();

        // Read configured request from preferences
        requestObjects = readPreferences(this);

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
            scrollLinearLayout.removeView(textView);
        }
        requestTextViewList.clear();

        addRequestButtons();
    }

    private void addRequestButtons() {
        for(RequestObject requestObject : requestObjects){
            addRequestButton(requestObject);
        }
    }

    private void addRequestButton(RequestObject requestObject) {
        RequestTextView textView = new RequestTextView(scrollLinearLayout.getContext(), requestObject);

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
                client.makeRequest(getApplicationContext(), requestObject.getUrls(), null, null, null);
            }
        });

        scrollLinearLayout.addView(textView);
        requestTextViewList.add(textView);
    }

    /**
     * opens a dialog to edit the request object
     * @param view
     */
    public void editRequest(View view) {
        if(!(view instanceof RequestTextView)) {
            return;
        }

        RequestTextView requestTextView = (RequestTextView) view;

        LinkedList<Pair<String, String>> args = new LinkedList<>();
        args.add(new Pair<>("action", "get_bulbs"));

        EditSocketsDialogFragment dialog = new EditSocketsDialogFragment();
        dialog.setMainActivity(MainActivity.this);
        dialog.setRequestTextView(requestTextView);
        dialog.show(getSupportFragmentManager(), "test");
    }

    public void addRequest(View view) {
        List<String> urls = new ArrayList<>();

        for(int i=0; i<RequestObject.MAX_URLS; i++){
            urls.add("");
        }

        RequestObject newRequest = new RequestObject("EMPTY", urls);
        requestObjects.add(newRequest);
        addRequestButton(newRequest);
    }

    public void writePreferences(){
        new RequestsConfig().writeConfig(requestObjects, this);
    }

    public static List<RequestObject> readPreferences(Context context) {
        return new RequestsConfig().readConfig(context);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
