package tecsup.example.proyectoaudiov3;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.io.FileInputStream;

public class MainActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener{
    //could be useful info https://medium.com/androiddevelopers/building-a-simple-audio-app-in-android-part-1-3-c14d1a66e0f1
    private MediaPlayer mediaplayer;
    private int playbackPosition = 0;
    TextView lblNombre;
    //Metodos del seeker
    private Handler mHandler = new Handler();
    SeekBar progresoMusica;
    TextView lblDuracion, lblPosicionActual;
    //Metodos del seekbar audio volume  https://stackoverflow.com/questions/10134338/using-seekbar-to-control-volume-in-android
    private SeekBar volumeSeekbar = null;
    private AudioManager audioManager = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //metodo del seekbar volume control
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
        initControls();


        lblNombre = findViewById(R.id.lblNombre);
        //Del metodo seeker
        progresoMusica = findViewById(R.id.progresoMusica);
        //metodo del seeker 1linea
        progresoMusica.setOnSeekBarChangeListener(this);
        lblDuracion = findViewById(R.id.lblDuracion);
        lblPosicionActual = findViewById(R.id.lblPosicionActual);

        Bundle reproducir = this.getIntent().getExtras();
        if(reproducir != null){
            String musica = getIntent().getExtras().getString("musica");
            String nombre = getIntent().getExtras().getString("nombre");
            try {
                playAudio(musica);
                lblNombre.setText(nombre);
                //adiciones
                progresoMusica.setProgress(0);
                progresoMusica.setMax(100);
                actualizarProgreso();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    @Override
    protected void onDestroy() {
        killMediaPlayer();
        super.onDestroy();
    }

    private void killMediaPlayer(){
        if(mediaplayer!=null){
            try{
                mediaplayer.release();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }



    public void playLocalAudio() throws Exception{
        killMediaPlayer();
        mediaplayer = MediaPlayer.create(this,R.raw.audio);
        mediaplayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaplayer.start();
    }


    public void doClic(View v){
        switch (v.getId()){
            case R.id.btnPlay:
                try{
                    if(mediaplayer==null) {
                        playLocalAudio();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
                break;
            case R.id.btnPause:
                if(mediaplayer!=null && mediaplayer.isPlaying()){
                    playbackPosition = mediaplayer.getCurrentPosition();
                    mediaplayer.pause();
                }
                break;
            case R.id.btnReset:
                if(mediaplayer!=null && !mediaplayer.isPlaying()){
                    mediaplayer.seekTo(playbackPosition);
                    mediaplayer.start();
                }
                break;
            case R.id.btnStop:
                if(mediaplayer!=null){
                    mediaplayer.stop();
                    playbackPosition = 0;
                    mediaplayer=null;
                }
                break;

        }
    }


    //LISTVIEW MUSIC

    public void playAudio(String url) throws Exception{
        killMediaPlayer();
        String filePath = url;
        File file = new File(filePath);
        FileInputStream inputStream = new FileInputStream(file);
        mediaplayer = new MediaPlayer();
        mediaplayer.setDataSource(inputStream.getFD());
        inputStream.close();
        mediaplayer.prepare();
        mediaplayer.start();
    }



    public void verLista(View v){
        Intent cargarLista = new Intent(this, musicas.class);
        startActivity(cargarLista);
    }

    //METODO DEL SEEKER ACTUALIZAR PROGRESO
    private Runnable hiloActualizarProgreso = new Runnable() {
        public void run() {
            try {
                long totalDuration = mediaplayer.getDuration();
                long currentDuration = mediaplayer.getCurrentPosition();

                // Displaying Total Duration time
                lblDuracion.setText("" + Utilidades.milliSecondsToTimer(totalDuration));
                // Displaying time completed playing
                lblPosicionActual.setText("" + Utilidades.milliSecondsToTimer(currentDuration));

                // Updating progress bar
                int progress = Utilidades.getProgressPercentage(currentDuration, totalDuration);
                progresoMusica.setProgress(progress);

                // Running this thread after 1000 milliseconds
                mHandler.postDelayed(this, 1000);
            }catch (Exception e){

            }
        }
    };
    //funcion del runnable
    public void actualizarProgreso() {
        mHandler.postDelayed(hiloActualizarProgreso, 1000);
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(hiloActualizarProgreso);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mHandler.removeCallbacks(hiloActualizarProgreso);
        int totalDuration = mediaplayer.getDuration();
        int currentPosition = Utilidades.progressToTimer(seekBar.getProgress(), totalDuration);

        // forward or backward to certain seconds
        mediaplayer.seekTo(currentPosition);

        // update timer progress again
        actualizarProgreso();
    }

    //FUNCION DE CONTROL DEL SEEKBAR VOLUMEN
    private void initControls()
    {
        try
        {
            volumeSeekbar = (SeekBar)findViewById(R.id.seekBar1);
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumeSeekbar.setMax(audioManager
                    .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumeSeekbar.setProgress(audioManager
                    .getStreamVolume(AudioManager.STREAM_MUSIC));


            volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
            {
                @Override
                public void onStopTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onStartTrackingTouch(SeekBar arg0)
                {
                }

                @Override
                public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
                {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                            progress, 0);
                }
            });
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
