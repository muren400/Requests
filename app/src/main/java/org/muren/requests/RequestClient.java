package org.muren.requests;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.List;

public class RequestClient {

    public RequestClient(){
    }

    public void makeRequest(final Context context, List<String> urls,
                            final Response.Listener responseListener, final Response.ErrorListener errorListener, String progressMessage){
        if(urls == null || urls.size() < 1)
            return;

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        ProgressDialog progressDialog = null;

        if(progressMessage != null){
            progressDialog = ProgressDialog.show(context, "", progressMessage, false);
        }

        for (String url : urls){
            if(url == null || url.trim().length() < 1)
                continue;

            // Request a string response from the provided URL.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new RequestResponseListener(context, progressDialog, responseListener),
                    new RequestErrorListener(context, progressDialog, errorListener));

            // Add the request to the RequestQueue.
            queue.add(stringRequest);
        }
    }

    public class RequestResponseListener implements Response.Listener<String> {

        private ProgressDialog progressDialog;
        private Response.Listener<String> responseListener;
        private Context context;

        public RequestResponseListener(Context context, ProgressDialog progressDialog, Response.Listener<String> responseListener){
            this.progressDialog = progressDialog;
            this.responseListener = responseListener;
            this.context = context;
        }

        @Override
        public void onResponse(String response) {

            if(progressDialog != null) progressDialog.dismiss();

            if(responseListener != null) {
                responseListener.onResponse(response);

            } else{
                Toast.makeText(context, response, Toast.LENGTH_LONG).show();
            }
        }
    }

    public class RequestErrorListener implements Response.ErrorListener {

        private ProgressDialog progressDialog;
        private Response.ErrorListener errorListener;
        private Context context;

        public RequestErrorListener(Context context, ProgressDialog progressDialog, Response.ErrorListener errorListener){
            this.progressDialog = progressDialog;
            this.errorListener = errorListener;
            this.context = context;
        }

        @Override
        public void onErrorResponse(VolleyError error) {

            if(progressDialog != null) progressDialog.dismiss();

            if(errorListener != null) {
                errorListener.onErrorResponse(error);
            } else {
                Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }
    }
}
