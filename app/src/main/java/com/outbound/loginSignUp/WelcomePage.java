package com.outbound.loginSignUp;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.outbound.R;
import com.outbound.view.DispatchActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;


public class WelcomePage extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activiy);

        TextView tos = (TextView) findViewById(R.id.tos);
        tos.setMovementMethod(LinkMovementMethod.getInstance());

        //Sign up with email
        ImageButton signUpEmail = (ImageButton) findViewById(R.id.imageSignUp);
        signUpEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, SignUpWithEmail.class);
                startActivity(intent);
            }
        });

        //Sign up via Facebook
        ImageButton fbLoginImg = (ImageButton)findViewById(R.id.imageFacebookLogin);
        fbLoginImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logIn(WelcomePage.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if (user.isNew()) {
                            // set favorites as null, or mark it as empty somehow
                            makeMeRequest();
                        } else {
                            finishActivity();
                        }
                    }
                });
            }
        });

        //Login
        ImageButton loginView = (ImageButton)findViewById(R.id.imageEmaiLogin);
        loginView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WelcomePage.this, LoginWithEmail.class);
                startActivity(intent);
            }
        });
    }

    private void makeMeRequest() {
        Session session = ParseFacebookUtils.getSession();
        if (session != null && session.isOpened()) {
            Request request = Request.newMeRequest(
                    ParseFacebookUtils.getSession(),
                    new Request.GraphUserCallback() {
                        @Override
                        public void onCompleted(GraphUser user,
                                                Response response) {
                            if (user != null) {
                                ParseUser.getCurrentUser().put("firstName",
                                        user.getFirstName());
                                ParseUser.getCurrentUser().saveInBackground();
                                finishActivity();
                            } else if (response.getError() != null) {
                                if ((response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_RETRY)
                                        || (response.getError().getCategory() == FacebookRequestError.Category.AUTHENTICATION_REOPEN_SESSION)) {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.session_invalid_error,
                                            Toast.LENGTH_LONG).show();

                                } else {
                                    Toast.makeText(getApplicationContext(),
                                            R.string.logn_generic_error,
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        }
                    }
            );
            request.executeAsync();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        ParseFacebookUtils.finishAuthentication(requestCode, resultCode, data);
    }


    private void finishActivity() {
        // Start an intent for the dispatch activity
        Intent intent = new Intent(WelcomePage.this, DispatchActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK
                | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
}
