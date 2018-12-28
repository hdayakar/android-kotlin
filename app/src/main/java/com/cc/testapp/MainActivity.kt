package com.cc.testapp

import android.Manifest
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.media.MediaRecorder
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.widget.Button
import android.widget.TextView
import java.io.File
import java.io.IOException

class MainActivity : AppCompatActivity(),
    MediaPlayer.OnCompletionListener {
        lateinit var recorder: MediaRecorder
        lateinit var player: MediaPlayer
        lateinit var file: File
        lateinit var button1: Button
        lateinit var button2: Button
        lateinit var button3: Button
        lateinit var tv1: TextView

        override fun onCreate (savedInstanceState: Bundle?) {
            super.onCreate (savedInstanceState)
            setContentView (R.layout.activity_main)

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                    Array<String>(1){Manifest.permission.READ_PHONE_STATE},1);
            }

            tv1 = findViewById (R.id.tv1) as TextView
            button1 = findViewById (R.id.button1) as Button
            button2 = findViewById (R.id.button2) as Button
            button3 = findViewById (R.id.button3) as Button

            button1.setOnClickListener {
                recorder = MediaRecorder()
                recorder.setAudioSource(MediaRecorder.AudioSource.MIC)
                recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP)
                recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB)
                val path = File (Environment.getExternalStorageDirectory().getPath())
                try {
                    file = File.createTempFile ("temporary", ".3gp", path)
                } catch (e: IOException) {
                    throw IOException("Failed to create temp file." + e + ".");
                }

                recorder.setOutputFile(file.absolutePath)
                try {
                    recorder.prepare ()
                } catch (e: IOException) {
                    throw IOException("Failed to prepare recorder." + e + ".");
                }

                recorder.start()
                tv1.text = Environment.getExternalStorageDirectory().getAbsolutePath()
                button1.setEnabled(false)
                button2.setEnabled(true)
            }

            button2.setOnClickListener {
                recorder.stop ()
                recorder.release ()
                player = MediaPlayer ()
                player.setOnCompletionListener (this)
                try {
                    player.setDataSource(file.absolutePath)
                } catch (e: IOException) {
                    throw IOException("Data source set failed." + e + ".");
                }

                try {
                    player.prepare ()
                } catch (e: IOException) {
                }

                button1.setEnabled (true)
                button2.setEnabled (false)
                button3.setEnabled (true)
                tv1.text = "Ready to play"
            }

            button3.setOnClickListener {
                player.start();
                button1.setEnabled (false);
                button2.setEnabled (false);
                button3.setEnabled (false);
                tv1.setText ("Playing");
            }
        }

        override fun onCompletion (mp: MediaPlayer) {
            button1.setEnabled (true)
            button2.setEnabled (true)
            button3.setEnabled (true)
            tv1.setText ("Ready")
        }
}
