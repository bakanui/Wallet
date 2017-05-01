package com.vct.wallet.ui.expenses;

import android.os.Bundle;

import com.vct.wallet.R;
import com.vct.wallet.ui.BaseActivity;

public class ExpenseDetailActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_base);
        String expenseId = getIntent().getStringExtra(ExpenseDetailFragment.EXPENSE_ID_KEY);
        replaceFragment(ExpenseDetailFragment.newInstance(expenseId), false);
    }

}
