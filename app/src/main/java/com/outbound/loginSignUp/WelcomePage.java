package com.outbound.loginSignUp;


import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.model.GraphUser;
import com.nvanbenschoten.motion.ParallaxImageView;
import com.outbound.R;
import com.outbound.view.DispatchActivity;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;
import java.util.List;

import static com.outbound.util.LogUtils.*;


public class WelcomePage extends Activity {

    private static final String TAG = makeLogTag(WelcomePage.class);
    private Dialog progressDialog;
    private ParallaxImageView mBackground;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_activiy);

        TextView tos = (TextView) findViewById(R.id.tos);
        tos.setMovementMethod(LinkMovementMethod.getInstance());

        mBackground = (ParallaxImageView)findViewById(R.id.background);

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
                WelcomePage.this.progressDialog = ProgressDialog.show(
                        WelcomePage.this, "", "Logging in...", true);

                List<String> permissions = Arrays.asList("email", "public_profile", "user_friends", "user_about_me",
                        "user_relationships", "user_birthday", "user_location");
                ParseFacebookUtils.logIn(permissions, WelcomePage.this, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException e) {
                        if(user == null){
                            LOGD(TAG,"error: " + e.getMessage());
                            return;
                        }

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

    @Override
    protected void onResume() {
        super.onResume();

        mBackground.registerSensorManager();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mBackground.unregisterSensorManager();
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
                                generateUser(user);
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

    private void generateUser(GraphUser facebookUser) {

        ParseUser parseUser = ParseUser.getCurrentUser();
        try {
            // Populate the JSON object
            parseUser.put("facebookID", facebookUser.getId());
            parseUser.put("username", facebookUser.getName());
            if (facebookUser.getProperty("gender") != null)
                parseUser.put("gender", facebookUser.getProperty("gender"));

            if (facebookUser.getBirthday() != null)
                parseUser.put("age", facebookUser.getBirthday());

            String email = facebookUser.asMap().get("email").toString();

            if(email != null)
                parseUser.put("email",email);

            getProfilePicture(facebookUser);
            getCoverPicture(facebookUser);
        } catch (Exception e) {
            LOGD(TAG, "Error: " + e.getMessage());
        }

        ParseUser.getCurrentUser().saveInBackground();

        finishActivity();
    }

    private void getCoverPicture(GraphUser facebookUser) {
        String url = "https://graph.facebook.com/" + facebookUser.getId() + "?fields=cover&access_token=" + ParseFacebookUtils.getSession().getAccessToken();

        URL finalCoverPhotoUrl = null;

        try {
            finalCoverPhotoUrl = new URL(getCoverPhotoUrl(url));
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Bitmap dp = BitmapFactory.decodeStream(finalCoverPhotoUrl.openConnection().getInputStream());
            LOGD(TAG, "cover image retrieved from facebook");
            // Save the cover profile info in a user property
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(dp!=null){
                ParseFile saveImageFile= new ParseFile("coverPicture.jpg",compressAndConvertImageToByteFrom(dp));
                currentUser.put("backGroundImage",saveImageFile);
            }
        } catch (IOException e) {
            e.printStackTrace();
            LOGD(TAG, "getProfilePicture Exception: " + e.getMessage());
        }

    }

    private String getCoverPhotoUrl(String url) {
        String finalCoverPhoto = null;

        try{
            HttpClient hc = new DefaultHttpClient();
            HttpGet get = new HttpGet(url);
            HttpResponse rp = hc.execute(get);

            if (rp.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(rp.getEntity());

                JSONObject JODetails = new JSONObject(result);

                if (JODetails.has("cover")) {
                    String getInitialCover = JODetails.getString("cover");

                    if (getInitialCover.equals("null")) {
                        finalCoverPhoto = null;
                    } else {
                        JSONObject JOCover = JODetails.optJSONObject("cover");

                        if (JOCover.has("source"))  {
                            finalCoverPhoto = JOCover.getString("source");
                        } else {
                            finalCoverPhoto = null;
                        }
                    }
                } else {
                    finalCoverPhoto = null;
                }
            }
        }catch (IOException e){
            LOGD(TAG,"getCoverPhotoUrl " + e.getMessage());
        } catch (JSONException e) {
            e.printStackTrace();
            LOGD(TAG,"getCoverPhotoUrl JSONException" + e.getMessage());
        }

        return finalCoverPhoto;
    }

    private byte[] compressAndConvertImageToByteFrom(Bitmap image) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.PNG, 100, stream);
        return stream.toByteArray();
    }
    private void getProfilePicture(final GraphUser facebookUser) {

        URL img_value = null;
        try {
            img_value = new URL("https://graph.facebook.com/"+facebookUser.getId()+"/picture?type=small");
        } catch (MalformedURLException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGD(TAG,"getProfilePicture Exception img: " + e.getMessage());
        }
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            Bitmap dp = null;
            if (img_value != null) {
                dp = BitmapFactory.decodeStream(img_value.openConnection().getInputStream());
            }
            LOGD(TAG, "image retrieved from facebook");
            // Save the user profile info in a user property
            ParseUser currentUser = ParseUser.getCurrentUser();
            if(dp!=null){
                ParseFile saveImageFile= new ParseFile("profilePicture.jpg",compressAndConvertImageToByteFrom(dp));
                currentUser.put("profileImage",saveImageFile);
            }
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            LOGD(TAG,"getProfilePicture Exception: " + e.getMessage());
        }

/*
        URL imageURL = null;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap image = null;
        try {
            imageURL = new URL("https://graph.facebook.com/" + facebookUser.getId() + "/picture?type=large");
            image = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
            image.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();

            if(byteArray != null){
                ParseFile file = new ParseFile("img1.jpg",byteArray);
                ParseUser.getCurrentUser().put("profileImage", file);
            }
        } catch (Exception e) {
            LOGD(TAG, "getProfilePicture Exception :" + e.getMessage());
        }
*/
        /*
        if(facebookUser != null)
        {
            AsyncTask<Void, Void, Bitmap> t = new AsyncTask<Void, Void, Bitmap>(){
                protected Bitmap doInBackground(Void... p) {
                    Bitmap bm = null;
                    try {
                        URL aURL = new URL("http://graph.facebook.com/"+facebookUser.getId()+"/picture?type=large");
                        URLConnection conn = aURL.openConnection();
                        conn.setUseCaches(true);
                        conn.connect();
                        InputStream is = conn.getInputStream();
                        BufferedInputStream bis = new BufferedInputStream(is);
                        bm = BitmapFactory.decodeStream(bis);
                        bis.close();
                        is.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    return bm;
                }

                protected void onPostExecute(Bitmap image){
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    image.compress(Bitmap.CompressFormat.PNG, 100, stream);
                    byte[] byteArray = stream.toByteArray();

                    if(byteArray != null){
                        ParseFile file = new ParseFile("img1.jpg",byteArray);
                        ParseUser.getCurrentUser().put("profileImage", file);
//                        file.saveInBackground();
                    }


                    ParseUser.getCurrentUser().saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            WelcomePage.this.progressDialog.dismiss();
                        }
                    });
//                    photoImageView.setBackgroundDrawable(drawable);

                }
            };
            t.execute();
        }
        */
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
