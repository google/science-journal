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

package com.google.android.apps.forscience.whistlepunk.accounts;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.FragmentActivity;
import com.google.android.apps.forscience.whistlepunk.ActivityWithNavigationView;
import com.google.android.apps.forscience.whistlepunk.R;

/** An accounts provider which supports a user with no signed-in account. */
public final class NonSignedInAccountsProvider extends AbstractAccountsProvider {
  public NonSignedInAccountsProvider(Context context) {
    super(context);
    setCurrentAccount(NonSignedInAccount.getInstance(context));
  }

  @Override
  public boolean supportSignedInAccount() {
    return false;
  }

  @Override
  public boolean requireSignedInAccount() {
    return false;
  }

  @Override
  public int getAccountCount() {
    return 0;
  }

  @Override
  public void setContentView(ActivityWithNavigationView activity) {
    activity.setContentView(R.layout.activity_main);
  }

  @Override
  public void onStop(ActivityWithNavigationView activity) {}

  @Override
  public void showAddAccountDialog(Activity activity) {
    throw new IllegalStateException("Accounts not supported");
  }

  @Override
  public void showAccountSwitcherDialog(FragmentActivity fragmentActivity) {
    throw new IllegalStateException("Accounts not supported");
  }
}