package org.muren.requests;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
//import android.support.v7.widget.AppCompatButton;

import androidx.appcompat.widget.AppCompatButton;

public class RequestTextView extends AppCompatButton {
    private MainActivity mainActivity;
    private RequestObject requestObject;

    public RequestTextView(Context context){
        super(context);
    }

    public RequestTextView(Context context, MainActivity mainActivity, RequestObject requestObject){
        this(context);

        this.requestObject = requestObject;
        this.mainActivity = mainActivity;

        setTextSize(20);
        setTextColor(Color.LTGRAY);
        setBackgroundColor(getResources().getColor(R.color.colorButton, null));
        setText(requestObject.getName());
        initClickListeners();
    }

    public RequestObject getRequestObject() {
        return requestObject;
    }

    public void setRequestObject(RequestObject requestObject) {
        this.requestObject = requestObject;
        setText(requestObject.getName());
    }

    private void initClickListeners() {
        setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                new EditSocketsDialogFragment(view, mainActivity);
                return false;
            }
        });

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mainActivity.getClient().makeRequest(mainActivity.getApplicationContext(), requestObject.getUrls(), null, null, null);
            }
        });
    }
}
