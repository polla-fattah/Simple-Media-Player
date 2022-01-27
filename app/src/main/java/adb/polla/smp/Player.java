package adb.polla.smp;

import android.media.MediaPlayer;
import android.os.PowerManager;
import android.util.Log;
import android.widget.Toast;

/**
 * This class gives a ready implementation for MediaPlayer with registered handlers prepared
 * and completed events as well as music error handler.
 *
 * @author polla F Abduljabbar
 *
 */
public class Player extends MediaPlayer implements MediaPlayer.OnCompletionListener,
        MediaPlayer.OnErrorListener, MediaPlayer.OnPreparedListener{

    public Player(){
        // register all three handlers
        this.setOnCompletionListener(this);
        this.setOnErrorListener(this);
        this.setOnPreparedListener(this);

        // Make sure that the device's CPU will not go to sleep
        setWakeMode(MainActivity.context.getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
    }

    /**
     * When a music is completed this method will be triggered which advances to the next
     * music and plays it. if it is last music it will return to the first one.
     *
     * @param mediaPlayer a MediaPlayer object
     */
    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        MainActivity.currentPosition = (MainActivity.currentPosition + 1) % MusicProvider.numberOfMusics;

        if(MainActivity.view != null)
            ((MusicAdapter.Info)MainActivity.view.getTag()).status.setImageResource(R.drawable.play);
        MainActivity.view = MainActivity.getViewById(MainActivity.currentPosition);

        MainActivity.playService.play();
    }

    /**
     * This method shows a message and registers a log which music error occurs
     *
     * @param mediaPlayer a MediaPlayer object
     * @param what int error code
     * @param extra int
     * @return boolean about error handled
     */
    @Override
    public boolean onError(MediaPlayer mediaPlayer, int what, int extra) {
        Toast.makeText(MainActivity.context, "Error occurred while retrieving musics", Toast.LENGTH_LONG).show();
        Log.e("smp", "Error in {Player extends MediaPlayer} class what:"  + what + ", extra: " + extra);

        return false;
    }

    /**
     * When prepareAsync() is called and the music has been prepared this method will be invoked
     * to start the music.
     *
     * @param mediaPlayer a MediaPlayer object
     */
    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        MainActivity.mediaPlayer.start();
    }
}
