package org.muren.requests;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * EditSocketsDialogFragment
 */
public class EditSocketsDialogFragment extends DialogFragment {
    private MainActivity mainActivity;
    private RequestTextView requestTextView;

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final RequestObject requestObject = requestTextView.getRequestObject();
        final EditText inputName = new EditText(getContext());
        inputName.setText(requestObject.getName());
        inputName.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1f));

        // Builds a list of EditText objects for the URLs
        final List<EditText> inputUrls = new ArrayList<>();
        List<String> urls = requestObject.getUrls();
        for (int i = 0; i < RequestObject.MAX_URLS; i++) {
            final EditText inputUrl = new EditText(getContext());

            String url = urls.get(i);
            if (url == null)
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

                        for (EditText inputUrl : inputUrls) {
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
