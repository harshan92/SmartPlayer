package com.example.smartplayer;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

public class SmartPlayerActivity extends AppCompatActivity {

    private RelativeLayout parentRelativeLayout;
    private SpeechRecognizer speechRecognizer;
    private Intent speechRecognizerIntent;
    private String keeper;

    private ImageView playPauseBtn, nextBtn, prevBtn;
    private TextView songNameTxt;

    private ImageView imageView;
    private RelativeLayout lowerRelativeLayout;

    private Button voiceEnableButton, langButton;
    private String mode="ON";



    private MediaPlayer mediaPlayer;
    private int position;
    private ArrayList<File> mySongs;
    private String mSongName;

    private String languagePref = "en_US";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smartplayer);

        checkVoiceCommandPermisstion();

        playPauseBtn=findViewById(R.id.playpause_btn);
        nextBtn=findViewById(R.id.next_btn);
        prevBtn=findViewById(R.id.prev_btn);
        voiceEnableButton=findViewById(R.id.voice_enable_btn);

        songNameTxt=findViewById(R.id.songName);
        imageView=findViewById(R.id.logo);
        lowerRelativeLayout=findViewById(R.id.lower);

        parentRelativeLayout=findViewById(R.id.parentRelativeLayout);
        speechRecognizer=SpeechRecognizer.createSpeechRecognizer(SmartPlayerActivity.this);
        speechRecognizerIntent=new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        langButton=findViewById(R.id.lang_btn);
        langButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(languagePref.equals("en_US")){
                    languagePref="si-LK";
                    langButton.setText("Language - Sinhala");
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
                }else{
                    languagePref="en_US";
                    langButton.setText("Language - English");
                    speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, languagePref);
                }
            }
        });


        setupPlayer();
        imageView.setBackgroundResource(R.drawable.logo);

        //start recognition event listener
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onReadyForSpeech(Bundle bundle) {

            }

            @Override
            public void onBeginningOfSpeech() {

            }

            @Override
            public void onRmsChanged(float v) {

            }

            @Override
            public void onBufferReceived(byte[] bytes) {

            }

            @Override
            public void onEndOfSpeech() {

            }

            @Override
            public void onError(int i) {

            }

            @Override
            public void onResults(Bundle bundle) {
                ArrayList<String> matchesFound=bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if(matchesFound!=null){
                    keeper=matchesFound.get(0);

                    if(mode.equals("ON")){
                        if(keeper.equals("pause the song") || keeper.equals("stop") || keeper.equals("stop song") || keeper.equals("නවත්වන්න") || keeper.equals("නවත්තන්න")){
                            playPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = "+keeper,Toast.LENGTH_LONG).show();
                        }else if(keeper.equals("play the song") || keeper.equals("play song") || keeper.equals("වාදනය කරන්න") ){
                            playPauseSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = "+keeper,Toast.LENGTH_LONG).show();
                        }else if(keeper.equals("play next song") || keeper.equals("next song") || keeper.equals("ඊළඟ එක") || keeper.equals("ඊළඟ")){
                            playNextSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = "+keeper,Toast.LENGTH_LONG).show();
                        }else if(keeper.equals("play previous song") || keeper.equals("previous song") || keeper.equals("කලින් එක")|| keeper.equals("කලින්")|| keeper.equals("කලින් එකට") ){
                            playPreviousSong();
                            Toast.makeText(SmartPlayerActivity.this, "Command = "+keeper,Toast.LENGTH_LONG).show();
                        }
                        Toast.makeText(SmartPlayerActivity.this, "Command = "+keeper,Toast.LENGTH_LONG).show();
                    }
                }

            }

            @Override
            public void onPartialResults(Bundle bundle) {

            }

            @Override
            public void onEvent(int i, Bundle bundle) {

            }
        });
        //end of recognize event listener

        parentRelativeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        speechRecognizer.startListening(speechRecognizerIntent);
                        keeper="";
                        break;
                    case MotionEvent.ACTION_UP:
                        speechRecognizer.stopListening();
                        keeper="";
                        break;
                }
                return false;
            }
        });

        voiceEnableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mode.equals("ON")){
                    mode="OFF";
                    voiceEnableButton.setText("Voice Enable Mode - OFF");
                    lowerRelativeLayout.setVisibility(view.VISIBLE);
                }else{
                    mode="ON";
                    voiceEnableButton.setText("Voice Enable Mode - ON");
                    lowerRelativeLayout.setVisibility(view.GONE);
                }
            }
        });

        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playPauseSong();
            }
        });
    }
//    end of onCreate

    private void setupPlayer(){
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
        }
        Intent intent=getIntent();
        Bundle bundle=intent.getExtras();
        mySongs=(ArrayList) bundle.getParcelableArrayList("song");
        mSongName=mySongs.get(position).getName();
        String songName=intent.getStringExtra("name");
Toast.makeText(SmartPlayerActivity.this,"Selected song is "+songName,Toast.LENGTH_LONG).show();
        songNameTxt.setText(songName);
        songNameTxt.setSelected(true);
        position=bundle.getInt("position",0);

        Uri uri=Uri.parse(mySongs.get(position).toString());
        mediaPlayer=MediaPlayer.create(SmartPlayerActivity.this, uri);
        mediaPlayer.start();
    }

    //give audio permissions
    private void checkVoiceCommandPermisstion(){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
            if(!(ContextCompat.checkSelfPermission(SmartPlayerActivity.this, Manifest.permission.RECORD_AUDIO)== PackageManager.PERMISSION_GRANTED)){
                Intent intent=new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("Package : "+getPackageName()));
                startActivity(intent);
                finish();
            }
        }
    }

    private void playPauseSong(){
        imageView.setImageResource(R.drawable.four);
        if (mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.play);
            mediaPlayer.pause();
        }else{
            playPauseBtn.setImageResource(R.drawable.pause);
            mediaPlayer.start();
            imageView.setImageResource(R.drawable.five);
        }
    }

    private void playNextSong(){
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position=((position+1)%mySongs.size());

        Uri uri=Uri.parse(mySongs.get(position).toString());
        mediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);

        mSongName=mySongs.get(position).getName();
        songNameTxt.setText(mSongName);

        mediaPlayer.start();

        imageView.setImageResource(R.drawable.three);

        if (mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.pause);
        }else{
            playPauseBtn.setImageResource(R.drawable.play);
            imageView.setImageResource(R.drawable.five);
        }
    }

    private void playPreviousSong(){
        mediaPlayer.pause();
        mediaPlayer.stop();
        mediaPlayer.release();

        position=((position-1)<0?(mySongs.size()-1) : (position-1));

        Uri uri=Uri.parse(mySongs.get(position).toString());
        mediaPlayer=MediaPlayer.create(SmartPlayerActivity.this,uri);

        mSongName=mySongs.get(position).getName();
        songNameTxt.setText(mSongName);

        mediaPlayer.start();

        imageView.setImageResource(R.drawable.three);

        if (mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.pause);
        }else{
            playPauseBtn.setImageResource(R.drawable.play);
            imageView.setImageResource(R.drawable.five);
        }
    }
}
