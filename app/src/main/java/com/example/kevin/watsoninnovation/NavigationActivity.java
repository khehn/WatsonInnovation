package com.example.kevin.watsoninnovation;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
import com.ibm.watson.developer_cloud.conversation.v1.ConversationService;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageRequest;
import com.ibm.watson.developer_cloud.conversation.v1.model.MessageResponse;
import com.ibm.watson.developer_cloud.http.ServiceCallback;
import com.ibm.watson.developer_cloud.visual_recognition.v3.VisualRecognition;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ClassifyImagesOptions;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.ImageClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassification;
import com.ibm.watson.developer_cloud.visual_recognition.v3.model.VisualClassifier;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents the main activity of this app.
 * Incorporates a navigationdrawer to navigate between the different functionalities.
 * These functionalities are realized as fragments.
 */
public class NavigationActivity extends AppCompatActivity {

    //Declare visual elements
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;

    // Generally needed variables
    private CharSequence mDrawerTitle;
    private CharSequence mTitle;
    private String[] mNavElements;
    private File f;

    //Variables needed for IBM Watson
    private VisualRecognition vrClient;
    private CameraHelper helper;


    /**
     * Executed when it Activity is created.
     * Sets up the navigation drawer.
     * Additionally it initializes stuff needed for IBM Watson methods
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        mTitle = mDrawerTitle = getTitle();
        mNavElements = getResources().getStringArray(R.array.navigation_elements);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);


        // set up the drawer's list view with items and click listener
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, mNavElements));
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());


        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.drawable.ic_drawer,  /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle(mDrawerTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
        mDrawerLayout.setDrawerListener(mDrawerToggle);

        if (savedInstanceState == null) {
            selectItem(0);
        }

        // Initialize Visual Recognition client
        vrClient = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.api_key_vis_rec)
        );

        // Initialize camera helper
        helper = new CameraHelper(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // The action bar home/up action should open or close the drawer.
        // ActionBarDrawerToggle will take care of this.
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle action buttons
        switch (item.getItemId()) {
            case R.id.action_websearch:
                // create intent to perform web search for this planet
                Intent intent = new Intent(Intent.ACTION_WEB_SEARCH);
                intent.putExtra(SearchManager.QUERY, getSupportActionBar().getTitle());
                // catch event that there's no activity to handle intent
                if (intent.resolveActivity(getPackageManager()) != null) {
                    startActivity(intent);
                } else {
                    Toast.makeText(this, R.string.app_not_available, Toast.LENGTH_LONG).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void takePicture(View view) {
        helper.dispatchTakePictureIntent();
    }



    /* The click listner for ListView in the navigation drawer */
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItem(position);
        }
    }

    private void selectItem(int position) {
        // update the main content by replacing fragments
        Fragment fragment;
        switch (position) {
            case 0:
                fragment = new ChatBotFragment();
                break;
            case 1:
                fragment = new VisualRecognitionFragment();
                break;
            default:
                fragment = new ChatBotFragment();
                break;
        }

        Bundle args = new Bundle();
        args.putInt(ChatBotFragment.ARG_NAV_NUM, position);
        fragment.setArguments(args);

        FragmentManager fragmentManager = getFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();

        // update selected item and title, then close the drawer
        mDrawerList.setItemChecked(position, true);
        setTitle(mNavElements[position]);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getSupportActionBar().setTitle(mTitle);
    }

    /**
     * When using the ActionBarDrawerToggle, you must call it during
     * onPostCreate() and onConfigurationChanged()...
     */

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggls
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    /**
     * Callback method for when the user has taken a picture using the camera
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode,
                                    int resultCode,
                                    Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {
            /*
            Is is necessary to resize the picture, since otherwise the pictures
            take to long to send and Watson gives an error message
             */
            final Bitmap photo = helper.getBitmap(resultCode);
            final File photoFile = helper.getFile(resultCode);

            Log.d("File",photoFile.toString());
            Bitmap b = getResizedBitmap(photo,1000,1000);

            ImageView preview = findViewById(R.id.preview);
            preview.setImageBitmap(b);

            f = new File(getApplicationContext().getCacheDir(), "temp.jpg");
            try {
                f.createNewFile();
                //Convert bitmap to byte array
                Bitmap bitmap = b;
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                b.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                byte[] bitmapdata = bos.toByteArray();

                //write the bytes in file
                FileOutputStream fos = new FileOutputStream(f);
                fos.write(bitmapdata);
                fos.flush();
                fos.close();
            } catch (IOException e) {
                Log.d("Error", "Error");
            }
            Log.d("File",f.toString());


            //Call the Watson API
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    VisualClassification response =
                            null;
                    response = vrClient.classify(
                            new ClassifyImagesOptions.Builder()
                                    .images(f)
                                    .build()
                    ).execute();

                    //Get the results
                    ImageClassification classification =
                            response.getImages().get(0);

                    VisualClassifier classifier =
                            classification.getClassifiers().get(0);
                    final StringBuffer output = new StringBuffer();
                    for(VisualClassifier.VisualClass object: classifier.getClasses()) {
                        if(object.getScore() > 0.4f)
                            output.append("<")
                                    .append(object.getName())
                                    .append("> \n");
                    }
                    //Update UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView detectedObjects =
                                    findViewById(R.id.detected_objects);
                            detectedObjects.setText(output);
                        }
                    });

                }

            });
        }
    }

    /**
     * Method used to resize a bitmap
     * @param bm
     * @param newWidth
     * @param newHeight
     * @return
     */
    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    /**
     * Fragment for the Chatbot
     */
    public static class ChatBotFragment extends Fragment {
        public static final String ARG_NAV_NUM = "element_number";
        private ArrayAdapter mAdapter;
        ConversationService myConversationService;
        ListView conversation;
        EditText userInput;
        ImageButton sendButton;


        public ChatBotFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Get the number that corresponds to the entry in the menu the user has selected.
            int i = getArguments().getInt(ARG_NAV_NUM);
            View rootView = inflater.inflate(R.layout.fragment_navigation_chatbot, container, false);

            //Get the string representing the entry in the menu.
            String navElement = getResources().getStringArray(R.array.navigation_elements)[i];
            //Set the string as title of the activity.
            getActivity().setTitle(navElement);


            myConversationService =
                    new ConversationService(
                            "2017-05-26",
                            getString(R.string.username_dr_watson_1),
                            getString(R.string.password_dr_watson_1)
                    );
            conversation = (ListView) rootView.findViewById(R.id.messagesContainer);
            userInput = (EditText) rootView.findViewById(R.id.messageEdit);
            sendButton = rootView.findViewById(R.id.chatSendButton);

            List<String> initialList = new ArrayList<String>(); //load these
            mAdapter = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, initialList);
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
                                    getActivity().runOnUiThread(new Runnable() {
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
                                public void onFailure(Exception e) {
                                }
                            });
                }
            });


            return rootView;
        }
    }

    public static class VisualRecognitionFragment extends Fragment {
        public static final String ARG_NAV_NUM = "element_number";

        public VisualRecognitionFragment() {
            // Empty constructor required for fragment subclasses
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            // Get the number that corresponds to the entry in the menu the user has selected.
            int i = getArguments().getInt(ARG_NAV_NUM);
            View rootView = inflater.inflate(R.layout.fragment_navigation_visual_rec, container, false);

            //Get the string representing the entry in the menu.
            String navElement = getResources().getStringArray(R.array.navigation_elements)[i];
            //Set the string as title of the activity.
            getActivity().setTitle(navElement);




            return rootView;
        }




    }
}

