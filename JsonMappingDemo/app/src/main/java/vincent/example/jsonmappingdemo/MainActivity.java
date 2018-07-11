package vincent.example.jsonmappingdemo;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import vincent.example.jsonmappingdemo.model.Contact;
import vincent.example.jsonmappingdemo.model.Followers;
import vincent.example.jsonmappingdemo.model.Suggestions;
import vincent.example.jsonmappingdemo.tools.Utils;
import vincent.example.jsonmappingdemo.tools.json.JsonModel;

/**
 * Created by vincent on 11/1/2018.
 */

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        try {
//            String followersJson = Utils.loadTestJsonData(this, null, "twitter_followers_list.json");
//            Followers followers = JsonModel.parseObject(new JSONObject(followersJson), Followers.class, null);
//            Log.i(TAG, "followers: " + followers.users.get(0).name);
//            String suggestionJson = Utils.loadTestJsonData(this, null, "twitter_suggestions.json");
//            ArrayList<Suggestions> suggestions = JsonModel.parseArray(new JSONArray(suggestionJson), Suggestions.class, null);
//            Log.i(TAG, "Suggestion: " + suggestions.get(0).name);

            String customJson = Utils.loadTestJsonData(this, null, "contact_data.json");
            Contact contact = JsonModel.mapObject(new JSONObject(customJson), Contact.class, null);
            Log.i(TAG, "Contact : " + contact.list.friendList.get(0).name.value);

//            Followers followers = JsonModel.mapObject(new JSONObject(followersJson), Followers.class, null);
//            Log.i(TAG, "followers name: " + followers.users.get(0).name);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
