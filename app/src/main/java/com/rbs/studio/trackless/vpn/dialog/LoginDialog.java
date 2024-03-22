package com.rbs.studio.trackless.vpn.dialog;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.rbs.studio.trackless.vpn.BuildConfig;
import com.rbs.studio.trackless.vpn.R;
import com.rbs.studio.trackless.vpn.utils.SessionManager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LoginDialog extends DialogFragment {

    @BindView(R.id.host_url_ed)
    EditText hostUrlEditText;

    @BindView(R.id.carrier_id_ed)
    EditText carrierIdEditText;
    SessionManager sessionManager;
    private LoginConfirmationInterface loginConfirmationInterface;

    public LoginDialog() {
    }

    public static LoginDialog newInstance() {
        LoginDialog frag = new LoginDialog();
        Bundle args = new Bundle();
        frag.setArguments(args);
        return frag;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        return dialog;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_login, container);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        if(getActivity() != null){
        sessionManager = new SessionManager(getActivity());
        SharedPreferences prefs = getPrefs();

        hostUrlEditText.setText(prefs.getString(BuildConfig.STORED_HOST_URL_KEY, BuildConfig.BASE_HOST));
        carrierIdEditText.setText(prefs.getString(BuildConfig.STORED_CARRIER_ID_KEY, BuildConfig.BASE_CARRIER_ID));

        hostUrlEditText.requestFocus();
        getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        }
    }

    public SharedPreferences getPrefs() {
        return getActivity().getSharedPreferences(BuildConfig.SHARED_PREFS, Context.MODE_PRIVATE);
    }

    @Override
    public void onAttach(Context ctx) {
        super.onAttach(ctx);
        if (ctx instanceof LoginConfirmationInterface) {
            loginConfirmationInterface = (LoginConfirmationInterface) ctx;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginConfirmationInterface = null;
    }

    @OnClick(R.id.login_btn)
    void onLoginBtnClick(View v) {
        String hostUrl = hostUrlEditText.getText().toString();
        if (hostUrl.equals("")) hostUrl = BuildConfig.BASE_HOST;
        String carrierId = carrierIdEditText.getText().toString();
        if (carrierId.equals("")) carrierId = BuildConfig.BASE_CARRIER_ID;
        loginConfirmationInterface.setLoginParams(hostUrl, carrierId);
        loginConfirmationInterface.loginUser();
        dismiss();
    }

    public interface LoginConfirmationInterface {
        void setLoginParams(String hostUrl, String carrierId);

        void loginUser();
    }
}
