package com.example.erzae.requests;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.AppCompatButton;

public class RequestTextView extends AppCompatButton {

    private RequestObject requestObject;

    public RequestTextView(Context context, RequestObject requestObject){
        super(context);

        this.requestObject = requestObject;

        setTextSize(20);
        setTextColor(Color.LTGRAY);
        setBackgroundColor(getResources().getColor(R.color.colorButton));
        setText(requestObject.getName());
    }

    public RequestObject getRequestObject() {
        return requestObject;
    }
}
