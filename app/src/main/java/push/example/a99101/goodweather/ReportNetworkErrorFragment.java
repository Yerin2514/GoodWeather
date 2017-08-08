package push.example.a99101.goodweather;


import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import lpadron.me.weatherly.R;

public class ReportNetworkErrorFragment extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Context context = getActivity();
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(context.getString(R.string.network_error_title))
                .setMessage(context.getString(R.string.network_error_message))
                .setPositiveButton("Ok", null);

        AlertDialog dialog = builder.create();
        return dialog;
    }

    public void show(FragmentManager fragmentManager, String s) {
    }
}
