package com.example.kevin.watsoninnovation;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.ibm.watson.developer_cloud.android.library.camera.CameraHelper;
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

public class PhotoChallengeActivity extends AppCompatActivity {

    //Variables needed for IBM Watson
    private VisualRecognition vrClient;
    private CameraHelper helper;
    //Firebase objects
    private FirebaseAnalytics mFirebaseAnalytics;
    Button btn_take_a_picture_photo_challenge;
    Button btn_abort_photo_challenge;
    File f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_challenge);

        Log.w("Quest ID", ((MyApplication) getApplication()).getRunningQuest());
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);
        btn_take_a_picture_photo_challenge = findViewById(R.id.btn_take_a_picture_photo_challenge);
        btn_abort_photo_challenge = findViewById(R.id.btn_abort_photo_challenge);
        // Initialize Visual Recognition client
        vrClient = new VisualRecognition(
                VisualRecognition.VERSION_DATE_2016_05_20,
                getString(R.string.api_key_vis_rec)
        );
        // Initialize camera helper
        helper = new CameraHelper(this);
        btn_take_a_picture_photo_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.w("Photo","Test");
                takePicture(view);
            }
        });

        btn_abort_photo_challenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DBQuest questToStart = ((MyApplication) getApplication()).getDbQuestMap().get(((MyApplication) getApplication()).getCurrentQuestKey());

                Bundle params = new Bundle();
                params.putString("quest_name", questToStart.getTitle());
                mFirebaseAnalytics.logEvent("quest_aborted", params);
                ((MyApplication) getApplication()).setQuestRunning(false);
                finish();
            }
        });

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
        Log.w("Test", "TestOnActivityResult");
        if(data==null)
            return;
        if(requestCode == CameraHelper.REQUEST_IMAGE_CAPTURE) {

            final Bitmap photo = helper.getBitmap(resultCode);
            final File photoFile = helper.getFile(resultCode);

            if(photo==null || photoFile==null)
                return;
            //Call the Watson API
            AsyncTask.execute(new Runnable() {
                @Override
                public void run() {
                    /*
            Is is necessary to resize the picture, since otherwise the pictures
            take to long to send and Watson gives an error message
             */


                    Log.w("File",photoFile.toString());
                    Bitmap b = getResizedBitmap(photo,1000,1000);


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



                    VisualClassification response =
                            null;
                    response = vrClient.classify(
                            new ClassifyImagesOptions.Builder()
                                    .images(f)
                                    .classifierIds("Try_586142397")
                                    .build()

                    ).execute();

                    //Get the results
                    ImageClassification classification =
                            response.getImages().get(0);


                    if(classification.getClassifiers().size()==0){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView detectedObjects =
                                        findViewById(R.id.text_view_results_content_photo_challenge);
                                detectedObjects.setText("No Results");
                            }
                        });
                        return;
                    }
                    VisualClassifier classifier =
                            classification.getClassifiers().get(0);
                    final StringBuffer output = new StringBuffer();
                    double maxScore = 0;
                    String currentMax = "NO RESULT";
                    for(VisualClassifier.VisualClass object: classifier.getClasses()) {
                        if(object.getScore() > 0.6f &&  object.getScore()>maxScore)
                            currentMax = object.getName();
                    }
                    output.append(currentMax);
                    //Update UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            TextView detectedObjects =
                                    findViewById(R.id.text_view_results_content_photo_challenge);
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


    public void takePicture(View view) {
        helper.dispatchTakePictureIntent();
    }
}
