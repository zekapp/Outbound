package com.outbound.loginSignUp;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.outbound.R;
import com.outbound.util.ConnectionDetector;
import com.outbound.view.BaseActivity;
import com.outbound.view.BaseFragment;
import com.outbound.view.DispatchActivity;
import com.parse.FindCallback;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;


public class LoginWithEmail extends Activity {

    private EditText mEmailView;
    private EditText mPasswordView;
    private Boolean isInternetPresent = false;
    private ConnectionDetector cd;

    private Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_with_email);


        cd = new ConnectionDetector(getApplicationContext());

        setUpActionbar();

        setUpEditTexts();
        setUpLogin();

    }

    private void progressDialogShow(){
        LoginWithEmail.this.progressDialog = ProgressDialog.show(
                LoginWithEmail.this, "", "Logging in...", true);
    }
    private void progressDialogDismiss(){
        LoginWithEmail.this.progressDialog.dismiss();
    }

    private void setUpEditTexts() {
        mEmailView = (EditText)findViewById(R.id.email);
        mPasswordView = (EditText)findViewById(R.id.password);
    }

    private void setUpLogin() {
        findViewById(R.id.login).
                setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        // get Internet status
                        isInternetPresent = cd.isConnectingToInternet();
                        // check for Internet status
                        if (isInternetPresent) {
                            // Internet Connection is Present
                            // make HTTP requests
                            attemptLogin();
                        } else {
                            // Internet connection is not present
                            // Ask user to connect to Internet
                            showAlertDialog(LoginWithEmail.this, "No Internet Connection",
                                    "You don't have internet connection.", false);
                        }

                    }
                });
    }

    private void attemptLogin() {
        String password = mPasswordView.getText().toString();
        String email = mEmailView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        }else{
            logIn(email, password);
        }
    }

    private void logIn(String email, final String password) {
        progressDialogShow();

        ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("email", email);
        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> parseObjects, ParseException e) {
                if(parseObjects.size() > 0){
                    ParseUser.logInInBackground(parseObjects.get(0).getUsername(), password, new LogInCallback() {
                        @Override
                        public void done(ParseUser user, ParseException e) {
                            if(e == null)
                                loginSuccessful();
                            else
                                loginUnSuccessful();
                        }
                    });
                }else
                    loginUnSuccessful();

            }
        });
    }
    protected void loginSuccessful() {
        progressDialogDismiss();
        Intent in =  new Intent(LoginWithEmail.this, DispatchActivity.class);
        startActivity(in);
    }
    protected void loginUnSuccessful() {
        progressDialogDismiss();
        Toast.makeText(getApplicationContext(), "", Toast.LENGTH_SHORT).show();
        showAlertDialog(LoginWithEmail.this,"Login", "Username or Password is invalid.", false);
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message, Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }
    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private void setUpActionbar() {
        ActionBar actionBar = getActionBar();
        if(actionBar != null){
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setTitle("Log In");
        }
    }
}
