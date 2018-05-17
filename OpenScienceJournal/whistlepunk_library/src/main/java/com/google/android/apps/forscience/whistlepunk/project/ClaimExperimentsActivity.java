/*
 *  Copyright 2018 Google Inc. All Rights Reserved.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.google.android.apps.forscience.whistlepunk.project;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.apps.forscience.whistlepunk.R;
import com.google.android.apps.forscience.whistlepunk.WhistlePunkApplication;
import com.google.android.apps.forscience.whistlepunk.accounts.AppAccount;

/**
 * Activity that allows a signed-in user to claim experiments that were created before signed-in
 * accounts were supported.
 */
public class ClaimExperimentsActivity extends AppCompatActivity {
  private static final String FRAGMENT_TAG = "ClaimExperiments";
  private static final String ARG_ACCOUNT_KEY = "accountKey";
  private static final String ARG_USE_PANES = "usePanes";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_claim_experiments);

    AppAccount claimingAccount =
        WhistlePunkApplication.getAccount(this, getIntent(), ARG_ACCOUNT_KEY);

    ActionBar actionBar = getSupportActionBar();
    actionBar.setHomeAsUpIndicator(R.drawable.ic_close_white_24dp);
    actionBar.setHomeActionContentDescription(android.R.string.cancel);
    actionBar.setTitle(getString(R.string.title_activity_claim_experiments));
    actionBar.setSubtitle(claimingAccount.getAccountName());

    if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
      Fragment fragment =
          ExperimentListFragment.newInstanceForClaimExperimentsMode(
              this, claimingAccount, getIntent().getBooleanExtra(ARG_USE_PANES, true));
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container, fragment, FRAGMENT_TAG)
          .commit();
    }
  }

  @Override
  public void onActivityResult(int requestCode, int resultCode, Intent data) {
    Fragment fragment = getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG);
    if (fragment != null) {
      fragment.onActivityResult(requestCode, resultCode, data);
    }
  }

  static void launch(Context context, AppAccount appAccount, boolean usePanes) {
    Intent intent = new Intent(context, ClaimExperimentsActivity.class);
    intent.putExtra(ARG_ACCOUNT_KEY, appAccount.getAccountKey());
    intent.putExtra(ARG_USE_PANES, usePanes);
    context.startActivity(intent);
  }
}