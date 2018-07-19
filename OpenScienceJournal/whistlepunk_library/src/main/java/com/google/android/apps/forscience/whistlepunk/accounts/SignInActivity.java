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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.apps.forscience.whistlepunk.R;

/** Activity that tells the user to explore their world. */
public class SignInActivity extends AppCompatActivity {
  private static final String TAG = "SignInActivity";
  private static final String FRAGMENT_TAG = "SignIn";

  static void launch(Context context) {
    Intent intent = new Intent(context, SignInActivity.class);
    context.startActivity(intent);
  }

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    setContentView(R.layout.activity_sign_in);

    if (getSupportFragmentManager().findFragmentByTag(FRAGMENT_TAG) == null) {
      Fragment fragment = new SignInFragment();
      getSupportFragmentManager()
          .beginTransaction()
          .add(R.id.container, fragment, FRAGMENT_TAG)
          .commit();
    }
  }
}