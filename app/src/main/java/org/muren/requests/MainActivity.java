package org.muren.requests;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * MainActivityv
 */
public class MainActivity extends AppCompatActivity {

    public static final String ACTION_START = "org.muren.requests.actionStart";

    private RecyclerView recyclerView = null;
    private RequestClient client;
    private List<RequestObject> requestObjects;
    private LinkedList<RequestTextView> requestTextViewList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestObjects = readPreferences(this);
        initRequestClient();
        initRecyclerView();
        startNotificationService();
    }

    private void initRequestClient()  {
        client = new RequestClient();
    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.scrollRecyclerView);
        var requestListAdapter = new RequestListAdapter(requestObjects, this);
        recyclerView.setAdapter(requestListAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        var itemTouchHelper = new RequestItemTouchHelper(requestObjects, requestListAdapter, this);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private void startNotificationService() {
        Intent backgroundService = new Intent(getApplicationContext(), NotificationService.class);
        backgroundService.setAction(ACTION_START);
        startService(backgroundService);
    }

    public void addRequest(View view) {
        List<String> urls = new ArrayList<>();

        for(int i=0; i<RequestObject.MAX_URLS; i++){
            urls.add("");
        }

        RequestObject newRequest = new RequestObject("EMPTY", urls);
        requestObjects.add(newRequest);
        recyclerView.getAdapter().notifyItemInserted(requestObjects.size() - 1);
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

    public RequestClient getClient() {
        return client;
    }
}
