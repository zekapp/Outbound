/**
 * Copyright 2014 Facebook, Inc.
 *
 * You are hereby granted a non-exclusive, worldwide, royalty-free license to
 * use, copy, modify, and distribute this software in source code or binary
 * form for use in connection with the web services and APIs provided by
 * Facebook.
 *
 * As with any software that integrates with the Facebook platform, your use
 * of this software is subject to the Facebook Developer Principles and
 * Policies [http://developers.facebook.com/policy/]. This copyright notice
 * shall be included in all copies or substantial portions of the software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 *
 */

package com.outbound.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.outbound.loginSignUp.WelcomePage;
import com.parse.ParseUser;

import static com.outbound.util.LogUtils.*;


public class DispatchActivity extends Activity {
    private static final String TAG = makeLogTag(DispatchActivity.class);

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		// Check if there is current user info
		if (ParseUser.getCurrentUser() != null) {
			// Start an intent for the logged in activity

			LOGD(TAG, "onCreate, got user,  "
					+ ParseUser.getCurrentUser().getUsername());
			startActivity(new Intent(this, ProfileFragment.class));
		} else {
			// Start and intent for the logged out activity
			LOGD(TAG, "onCreate, no user");
			startActivity(new Intent(this, WelcomePage.class));
		}
	}

}
