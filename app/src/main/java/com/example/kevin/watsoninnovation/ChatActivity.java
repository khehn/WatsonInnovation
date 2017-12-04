package com.example.kevin.watsoninnovation;


import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;


import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.mindorks.placeholderview.annotations.View;
import com.stfalcon.chatkit.commons.ImageLoader;
import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.commons.models.IUser;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


public class ChatActivity extends AppCompatActivity{
    Message message;
    private MessagesList messagesList;
    String senderId = "0";
    MessageInput inputView;
    MessagesListAdapter<Message> adapter;
    public static final String ARG_NAV_NUM = "element_number";
    private ArrayAdapter mAdapter;
    ConversationService myConversationService;
    ListView conversation;
    EditText userInput;
    ImageButton sendButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTitle("Ask Watson");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat2);

        this.messagesList = findViewById(R.id.messagesList);
        inputView = findViewById(R.id.input);

        myConversationService =
                new ConversationService(
                        "2017-05-26",
                        getString(R.string.username_dr_watson_1),
                        getString(R.string.password_dr_watson_1)
                );

        adapter = new MessagesListAdapter<>(senderId, null);
        messagesList.setAdapter(adapter);
        Author authorBot = new Author("1","Watson", "");
        message = new Message("0","Hello, how can I help you?",authorBot, Calendar.getInstance().getTime());
        adapter.addToStart(message, true);






        inputView.setInputListener(new MessageInput.InputListener() {
            @Override
            public boolean onSubmit(CharSequence input) {
                //validate and send message
                Author author = new Author("0","Kevin", "");
                message = new Message("1",input.toString(),author,Calendar.getInstance().getTime());
                adapter.addToStart(message, true);

                MessageRequest request = new MessageRequest.Builder()
                        .inputText(input.toString())
                        .build();

                myConversationService
                        .message(getString(R.string.workspace_dr_watson_1), request)
                        .enqueue(new ServiceCallback<MessageResponse>() {
                            @Override
                            public void onResponse(MessageResponse response) {
                                final String outputText = response.getText().get(0);
                                ChatActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Author author = new Author("1","Watson", "");
                                        String output = Html.fromHtml("" +
                                                outputText + "").toString();
                                        message = new Message("1",output,author,Calendar.getInstance().getTime());
                                        adapter.addToStart(message, true);
                                    }
                                });
                            }

                            @Override
                            public void onFailure(Exception e) {
                            }
                        });




                return true;
            }
        });

    }



}
class Message implements IMessage {

   String id;
   String text;
   Author author;
   Date createdAt;

    public Message(String id, String text, Author author, Date createdAt) {
        this.id = id;
        this.text = text;
        this.author = author;
        this.createdAt = createdAt;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getText() {
        return text;
    }

    @Override
    public Author getUser() {
        return author;
    }

    @Override
    public Date getCreatedAt() {
        return createdAt;
    }
}
class Author implements IUser {

   String id;
   String name;
   String avatar;

    public Author(String id, String name, String avatar) {
        this.id = id;
        this.name = name;
        this.avatar = avatar;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public String getAvatar() {
        return avatar;
    }
}