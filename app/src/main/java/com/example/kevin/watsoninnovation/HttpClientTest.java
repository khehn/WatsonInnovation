package com.example.kevin.watsoninnovation;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.*;
import java.net.URLEncoder;
import android.util.Base64;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;


public class HttpClientTest extends AsyncTask<Void, Integer, Boolean> {

    Context mContext;
    String questID;
    public HttpClientTest(Context context, String questID) {
        this.mContext = context;
        this.questID = questID;
    }

    @Override
    protected Boolean doInBackground(Void... voids) {
            // NOTE: you must manually construct wml_credentials hash map below
            // using information retrieved from your IBM Cloud Watson Machine Learning Service instance
            String output = "";
        MyApplication x = (MyApplication)mContext.getApplicationContext();
        int action = x.getAction();
        int adventure = x.getAdventure();
        int calm = x.getCalm();
        int family = x.getFamily();
        int young = x.getYoung();
        int history = x.getHistory();
        int art = x.getArt();
        int group = x.getGroup();
        int mystery = x.getMystery();
        int couple = x.getCouple();

            Map<String, String> wml_credentials = new HashMap<String, String>()
            {{
                put("url", "https://ibm-watson-ml.mybluemix.net");
                put("username", "87491c66-acef-40cf-ab3a-4255bb7bc3ef");
                put("password", "68727536-fb7f-4fb5-badd-92fbec67b5db");
            }};
            String wml_auth_header = null;
            wml_auth_header = "Basic " + Base64.encodeToString((wml_credentials.get("username") + ":" + wml_credentials.get("password")).getBytes(StandardCharsets.UTF_8),Base64.NO_WRAP);

            //wml_auth_header = Base64.encodeToString(((wml_credentials.get("username") + ":" + wml_credentials.get("password")).getBytes(StandardCharsets.UTF_8)),0);
            //wml_auth_header = "Basic "+URLEncoder.encode((wml_credentials.get("username") + ":" + wml_credentials.get("password")).getBytes(StandardCharsets.UTF_8).toString(),"utf-8");

        Log.w("wml_auth_header", wml_auth_header);


            String wml_url = wml_credentials.get("url") + "/v3/identity/token";
            HttpURLConnection tokenConnection = null;
            HttpURLConnection scoringConnection = null;
            BufferedReader tokenBuffer = null;
            BufferedReader scoringBuffer = null;
            try {
                // Getting WML token
                URL tokenUrl = new URL(wml_url);
                tokenConnection = (HttpURLConnection) tokenUrl.openConnection();
                tokenConnection.setDoInput(true);
                tokenConnection.setDoOutput(false);
                tokenConnection.setRequestMethod("GET");
                tokenConnection.setRequestProperty("Authorization", wml_auth_header);


                tokenBuffer = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
                StringBuffer jsonString = new StringBuffer();
                String line;
                while ((line = tokenBuffer.readLine()) != null) {
                    jsonString.append(line);
                }
                Log.d("HTTP",jsonString.toString());
                // Scoring request
                URL scoringUrl = new URL("https://ibm-watson-ml.mybluemix.net/v3/wml_instances/11bafd0c-30f3-42d9-8287-bd5e73280dc9/published_models/dc9ec35b-d5f0-4a3c-beb0-53cbaaf65903/deployments/2b2e5130-836e-473e-8cbb-07a638cb5f9c/online");
                String wml_token = "Bearer " +
                        jsonString.toString()
                                .replace("\"","").replace("}", "").split(":")[1];
                scoringConnection = (HttpURLConnection) scoringUrl.openConnection();
                scoringConnection.setDoInput(true);
                scoringConnection.setDoOutput(true);
                Log.d("HTTP",wml_token);
                scoringConnection.setRequestMethod("POST");
                scoringConnection.setRequestProperty("Accept", "application/json");
                scoringConnection.setRequestProperty("Authorization", wml_token);
                scoringConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

                OutputStreamWriter writer = new OutputStreamWriter(scoringConnection.getOutputStream(), "UTF-8");


                // NOTE: manually define and pass the array(s) of values to be scored in cthe next line
                //String payload = "{\"fields\": [\"user_id\", \"history\", \"art\", \"calm\", \"action\", \"mystery\", \"adventure\", \"young\", \"group\", \"couple\", \"family\", \"quest_id\"], \"values\": [[0,1,0,0,1,1,1,1,0,0,1003]]}";
                String payload = "{\"fields\": [\"user_id\", \"history\", \"art\", \"calm\", \"action\", \"mystery\", " +
                        "\"adventure\", \"young\", \"group\", \"couple\", \"family\", \"quest_id\"], \"values\": " +
                        "[[\"5a2d1089bccc7ec0d3475453\","+history+","+art+","+calm+","+action+","+mystery+","+adventure+","+young+","+group+"" +
                        ","+couple+","+family+","+questID+"]]}";
                writer.write(payload);
                writer.flush();
                writer.close();

                int statusCode = scoringConnection.getResponseCode();

                Log.d("Response Code",statusCode+" "+scoringConnection.getResponseMessage());

                scoringBuffer = new BufferedReader(new InputStreamReader(scoringConnection.getInputStream()));

                StringBuffer jsonStringScoring = new StringBuffer();
                String lineScoring;
                while ((lineScoring = scoringBuffer.readLine()) != null) {
                    jsonStringScoring.append(lineScoring);
                }
                output = jsonStringScoring.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                if (tokenConnection != null) {
                    tokenConnection.disconnect();
                }
                if (tokenBuffer != null) {
                    try {
                        tokenBuffer.close();
                    } catch (IOException e) {

                    }
                }
                if (scoringConnection != null) {
                    scoringConnection.disconnect();
                }
                if (scoringBuffer != null) {
                    try {
                        scoringBuffer.close();
                    } catch (IOException e) {

                    }
                }
            }
        Log.d("HTTP",output);
        double feedbackZero = 0;
        double feebackOne = 0;
        try {
            JSONObject jObject = new JSONObject(output);
            JSONArray jArray = jObject.getJSONArray("values");
            JSONArray answerArray = (JSONArray) jArray.get(0);
            JSONArray scoreArray = (JSONArray) answerArray.get(14);
            feedbackZero = scoreArray.getDouble(0);
            feebackOne = scoreArray.getDouble(1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d("HTTP", "Zero: "+feedbackZero+"  One; "+feebackOne);

        Map<String, Boolean> quests = x.getQuests();
        if(quests==null)
            quests = new HashMap<String,Boolean>();
        quests.put(questID,(feebackOne>feedbackZero));
        x.setQuests(quests);

        if(feebackOne>0.6)
            return true;
        else
            return false;
        }


}
