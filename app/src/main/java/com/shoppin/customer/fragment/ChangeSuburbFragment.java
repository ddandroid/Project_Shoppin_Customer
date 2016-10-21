package com.shoppin.customer.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.shoppin.customer.R;
import com.shoppin.customer.activity.NavigationDrawerActivity;
import com.shoppin.customer.database.DBAdapter;
import com.shoppin.customer.database.IDatabase;
import com.shoppin.customer.model.Suburb;
import com.shoppin.customer.network.DataRequest;
import com.shoppin.customer.network.IWebService;
import com.shoppin.customer.utils.Utils;

import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.ButterKnife;

/**
 * Created by ubuntu on 24/8/16.
 */

public class ChangeSuburbFragment extends DialogFragment {

    public static final String TAG = ChangeSuburbFragment.class.getSimpleName();

    private ArrayList<Suburb> suburbArrayList;
    private ArrayAdapter<Suburb> suburbArrayAdapter;
    private Suburb selectedSuburb;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View layoutView = inflater.inflate(R.layout.fragment_change_suburb, container, false);

        ButterKnife.bind(this, layoutView);

        suburbArrayList = new ArrayList<>();
        suburbArrayAdapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_dropdown_item_1line, suburbArrayList);
        getSuburbs();

        return layoutView;
    }

    private void getSuburbs() {
        DataRequest getSuburbsDataRequest = new DataRequest(getActivity());
        getSuburbsDataRequest.execute(IWebService.GET_SUBURB, null, new DataRequest.CallBack() {
            public void onPreExecute() {
            }

            public void onPostExecute(String response) {
                try {
                    if (!DataRequest.hasError(getActivity(), response, true)) {
                        Gson gson = new Gson();
                        JSONObject dataJObject = DataRequest.getJObjWebdata(response);

                        ArrayList<Suburb> tmpSuburbArrayList = gson.fromJson(
                                dataJObject.getJSONArray(IWebService.KEY_RES_SUBURB_LIST).toString(),
                                new TypeToken<ArrayList<Suburb>>() {
                                }.getType());
                        if (tmpSuburbArrayList != null) {
                            Log.e(TAG, "size = " + tmpSuburbArrayList.size());
                            suburbArrayList.addAll(tmpSuburbArrayList);
                            suburbArrayAdapter.notifyDataSetChanged();
                            showAlertGuestLogin();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void showAlertGuestLogin() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_guest_suburb, null);
        dialogBuilder.setView(dialogView);
        dialogBuilder.setCancelable(false);
        final AlertDialog alertDialog = dialogBuilder.create();


        ((TextView) dialogView.findViewById(R.id.txtDilalogTitle))
                .setText(getString(R.string.dialog_title_change_suburb));

        final AutoCompleteTextView atxSuburbDialog = (AutoCompleteTextView) dialogView.findViewById(R.id.atxSuburbDialog);
        atxSuburbDialog.setAdapter(suburbArrayAdapter);

        dialogView.findViewById(R.id.txtCancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                dismiss();
            }
        });

        dialogView.findViewById(R.id.txtGo).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                atxSuburbDialog.setError(null);
                selectedSuburb = Utils.getSelectedSuburb(suburbArrayList, atxSuburbDialog.getText().toString());
                if (selectedSuburb == null) {
                    atxSuburbDialog.setError(getString(R.string.error_valid_suburb));
                    atxSuburbDialog.requestFocus();
                } else {
                    DBAdapter.insertUpdateMap(getActivity(), IDatabase.IMap.SUBURB_ID,
                            selectedSuburb.suburb_id);
                    DBAdapter.insertUpdateMap(getActivity(), IDatabase.IMap.SUBURB_NAME,
                            selectedSuburb.suburb_name);

                    alertDialog.dismiss();
                    dismiss();
                    NavigationDrawerActivity navigationDrawerActivity = (NavigationDrawerActivity) getActivity();
                    if (navigationDrawerActivity != null) {
                        navigationDrawerActivity.switchContent(new HomeFragment(), true);
                    }
                }
            }
        });

        alertDialog.getWindow().setBackgroundDrawableResource(R.color.transparent);
        alertDialog.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        alertDialog.setCancelable(false);
        alertDialog.show();
    }
}
