package com.example.kevin.watsoninnovation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter mAdapter;
    ConversationService myConversationService;
    ListView conversation;
    EditText userInput;
    ImageButton sendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        myConversationService =
                new ConversationService(
                        "2017-05-26",
                        getString(R.string.username_dr_watson_1),
                        getString(R.string.password_dr_watson_1)
                );
        conversation = (ListView)findViewById(R.id.messagesContainer);
        userInput = (EditText)findViewById(R.id.messageEdit);
        sendButton = findViewById(R.id.chatSendButton);

        List<String> initialList = new ArrayList<String>(); //load these
        mAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, initialList);
        conversation.setAdapter(mAdapter);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String inputText = userInput.getText().toString();
                mAdapter.add(
                        Html.fromHtml("<p><b>You:</b> " + inputText + "</p>")
                );

                // Optionally, clear edittext
                userInput.setText("");

                MessageRequest request = new MessageRequest.Builder()
                        .inputText(inputText)
                        .build();

                myConversationService
                        .message(getString(R.string.workspace_dr_watson_1), request)
                        .enqueue(new ServiceCallback<MessageResponse>() {
                            @Override
                            public void onResponse(MessageResponse response) {
                                final String outputText = response.getText().get(0);
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mAdapter.add(
                                                Html.fromHtml("<p><b>Bot:</b> " +
                                                        outputText + "</p>")
                                        );
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) {}
                        });
            }
        });

    }
}
