package com.example.smartplayer;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private String[] itemsAll;
    private ListView songListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        songListView=findViewById(R.id.songsList);
        appExternalStoragePermissionCheck();


    }

    private void appExternalStoragePermissionCheck(){
        Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE).withListener(new PermissionListener() {
            @Override public void onPermissionGranted(PermissionGrantedResponse response) {
                Log.d("TAG","Permission Granted!");
                displayAudioSongsName();
            }
            @Override public void onPermissionDenied(PermissionDeniedResponse response) {

            }
            @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
                token.continuePermissionRequest();
            }
        }).check();
    }

    private ArrayList<File> readOnlyAudioFiles(File file){
        Log.d("TAG","Files Reading...");
        ArrayList<File> filesList=new ArrayList<>();

        File[] allFiles=file.listFiles();

        for (File aFile:allFiles){
            if(aFile.isDirectory() && !aFile.isHidden()){
                filesList.addAll(readOnlyAudioFiles(aFile));
            }else {
                if (aFile.getName().endsWith(".mp3") || aFile.getName().endsWith(".aac") || aFile.getName().endsWith(".wav") || aFile.getName().endsWith(".wma")){
                    filesList.add(aFile);
                }
            }
        }

        return filesList;
    }

    private void displayAudioSongsName(){
        Log.d("TAG","Display Reading files...");
        final ArrayList<File> audioSongs=readOnlyAudioFiles(Environment.getExternalStorageDirectory());

        itemsAll=new String[audioSongs.size()];
        for (int scount=0;scount<audioSongs.size();scount++){
            Log.d("TAG",audioSongs.get(scount).getName());
            itemsAll[scount]=audioSongs.get(scount).getName();
        }
        Log.d("TAG","Check items all : "+itemsAll[0]);
        ArrayAdapter<String> arrayAdapter=new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, itemsAll);
        songListView.setAdapter(arrayAdapter);
    }
}
