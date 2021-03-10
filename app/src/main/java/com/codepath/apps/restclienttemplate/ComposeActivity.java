package com.codepath.apps.restclienttemplate;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.codepath.apps.restclienttemplate.models.Tweet;
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler;

import org.json.JSONException;
import org.parceler.Parcels;

import okhttp3.Headers;

public class ComposeActivity extends AppCompatActivity {
    public static final String tag = "ComposeActivity";
    public static final int max_tweet_length = 280;

    EditText etCompose;
    Button btnTweet;
    TextView tweetCount;


    TwitterClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        client = TwitterApp.getRestClient(this);

        etCompose = findViewById(R.id.etCompose);
        btnTweet = findViewById(R.id.btnTweet);
        tweetCount = findViewById(R.id.tweetCount);

        etCompose.setFilters(new InputFilter[] {new InputFilter.LengthFilter(max_tweet_length)});
        tweetCount.setText(0 + " of " + max_tweet_length);
        etCompose.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                tweetCount.setText(charSequence.length() + " of " + max_tweet_length);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        btnTweet.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                final String tweetContent = etCompose.getText().toString();
                if(tweetContent.isEmpty()){
                    Toast.makeText(ComposeActivity.this, "Tweet cannot be empty", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(tweetContent.length() > max_tweet_length){
                    Toast.makeText(ComposeActivity.this, "Tweet cannot be longer than 140 characters", Toast.LENGTH_SHORT).show();
                    return;
                }
                client.publishTweet(tweetContent, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Headers headers, JSON json) {
                        Log.i(tag, "Successful to publish tweet");
                        try{
                            Log.i(tag, "Published tweet says: " + tweetContent);
                            Tweet tweet = Tweet.fromJson(json.jsonObject);
                            Intent intent = new Intent();
                            intent.putExtra("tweet", Parcels.wrap(tweet));
                            setResult(RESULT_OK, intent);
                            finish();
                        }catch(JSONException e){
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailure(int statusCode, Headers headers, String response, Throwable throwable) {
                        Log.e(tag, "Failure to publish tweet", throwable);
                    }
                });
            }
        });
    }
}