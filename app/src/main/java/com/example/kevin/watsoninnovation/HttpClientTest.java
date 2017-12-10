package com.example.kevin.watsoninnovation;

import java.io.*;
import java.net.MalformedURLException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClientTest {
    public static void main(String[] args) throws IOException {

        // NOTE: you must manually construct wml_credentials hash map below
        // using information retrieved from your IBM Cloud Watson Machine Learning Service instance
        Map<String, String> wml_credentials = new HashMap<String, String>()
        {{
            put("url", "https://ibm-watson-ml.mybluemix.net");
            put("username", "87491c66-acef-40cf-ab3a-4255bb7bc3ef");
            put("password", "68727536-fb7f-4fb5-badd-92fbec67b5db");
        }};
        String wml_auth_header = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            wml_auth_header = "Basic " + Base64.getEncoder().encodeToString((wml_credentials.get("username") + ":" + wml_credentials.get("password")).getBytes(StandardCharsets.UTF_8));
        }
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
            tokenConnection.setDoOutput(true);
            tokenConnection.setRequestMethod("GET");
            tokenConnection.setRequestProperty("Authorization", wml_auth_header);
            tokenBuffer = new BufferedReader(new InputStreamReader(tokenConnection.getInputStream()));
            StringBuffer jsonString = new StringBuffer();
            String line;
            while ((line = tokenBuffer.readLine()) != null) {
                jsonString.append(line);
            }
            // Scoring request
            URL scoringUrl = new URL("https://ibm-watson-ml.mybluemix.net/v3/wml_instances/11bafd0c-30f3-42d9-8287-bd5e73280dc9/published_models/f7c5c4a4-9c78-4326-b612-1c8c7adf3266/deployments/9acf7528-7269-49b5-a18d-9baddc8ae104/online");
            String wml_token = "Bearer " +
                    jsonString.toString()
                            .replace("\"","").replace("}", "").split(":")[1];
            scoringConnection = (HttpURLConnection) scoringUrl.openConnection();
            scoringConnection.setDoInput(true);
            scoringConnection.setDoOutput(true);
            scoringConnection.setRequestMethod("POST");
            scoringConnection.setRequestProperty("Accept", "application/json");
            scoringConnection.setRequestProperty("Authorization", wml_token);
            scoringConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            OutputStreamWriter writer = new OutputStreamWriter(scoringConnection.getOutputStream(), "UTF-8");

            // NOTE: manually define and pass the array(s) of values to be scored in cthe next line
            String payload = "{\"fields\": [\"user_id\", \"history\", \"art\", \"calm\", \"action\", \"mystery\", \"adventure\", \"young\", \"group\", \"couple\", \"family\", \"quest_id\"], \"values\": [[0,1,0,0,1,1,1,1,0,0,1002], [0,1,0,0,1,1,1,1,0,0,1003]]}";
            writer.write(payload);
            writer.close();

            scoringBuffer = new BufferedReader(new InputStreamReader(scoringConnection.getInputStream()));
            StringBuffer jsonStringScoring = new StringBuffer();
            String lineScoring;
            while ((lineScoring = scoringBuffer.readLine()) != null) {
                jsonStringScoring.append(lineScoring);
            }
            System.out.println(jsonStringScoring);
        } catch (IOException e) {
            System.out.println("The URL is not valid.");
            System.out.println(e.getMessage());
        }
        finally {
            if (tokenConnection != null) {
                tokenConnection.disconnect();
            }
            if (tokenBuffer != null) {
                tokenBuffer.close();
            }
            if (scoringConnection != null) {
                scoringConnection.disconnect();
            }
            if (scoringBuffer != null) {
                scoringBuffer.close();
            }
        }
    }
}
