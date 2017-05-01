package com.vct.wallet.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;

import com.vct.wallet.Wallet;
import com.vct.wallet.R;
import com.vct.wallet.ui.BaseActivity;
import com.vct.wallet.ui.MainActivity;

public class LoginActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        boolean logged = PreferenceManager.getDefaultSharedPreferences(Wallet.getContext()).getBoolean(getString(R.string.already_accepted_user_key), false);
        if (logged) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }

}
