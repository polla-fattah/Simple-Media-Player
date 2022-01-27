package adb.polla.smp;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

/**
 * A bound Service for managing musics in the background. the managing includes play, pause, next
 * previous, stop actions on the provided list of musics.
 *
 * @author polla F Abduljabbar
 */
public class PlayingService extends Service {
    private final IBinder binder = new PlayBinder();

    /**
     * @param intent Intent Object
     * @return IBinder object
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * @param intent Intent
     * @return boolean indicates success or failure of the process of unbinding
     */
    @Override
    public boolean onUnbind(Intent intent) {
        if(MainActivity.mediaPlayer != null){
            MainActivity.mediaPlayer.release();
            MainActivity.mediaPlayer = null;
        }
        return super.onUnbind(intent);
    }

    /**
     * Plays the music of the current position
     */
    public void play() {
        if(MainActivity.mediaPlayer == null)
            MainActivity.mediaPlayer = new Player();

        MainActivity.setToPlay(false);

        try {
            MainActivity.mediaPlayer.reset();
            MainActivity.mediaPlayer.setDataSource(
                    MusicProvider.songs[MainActivity.currentPosition].path);

            MainActivity.mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(MainActivity.context, "Error occurred while retrieving musics",
                    Toast.LENGTH_LONG).show();
            Log.e("smp", "Error in {PlayingService extends Service} class,  error message:"
                    + e.getMessage());
        }
    }

    /**
     * Pauses the Music and changes image buttons
     */
    public void pause() {
        if(MainActivity.mediaPlayer != null)
            MainActivity.mediaPlayer.pause();

        MainActivity.setToPlay(true);
    }

    /**
     * Stops the music and releases MediaPlayer resource
     */
    public void stop() {
        if(MainActivity.mediaPlayer == null)
            return;

        MainActivity.mediaPlayer.stop();
        MainActivity.mediaPlayer.release();
        MainActivity.mediaPlayer = null;

        MainActivity.setToPlay(true);

    }

    /**
     * Advances to the next music in the list if it is the last returns to the beginning
     */
    public void next() {
        MainActivity.currentPosition = (MainActivity.currentPosition + 1) % MusicProvider.numberOfMusics;

        MainActivity.setToPlay(true);
        MainActivity.view = MainActivity.getViewById(MainActivity.currentPosition);

        play();
    }
    /**
     * Retrieves to the previous  music in the list if it is the first one it jumps to the end
     */
    public void prev() {
        MainActivity.currentPosition = MainActivity.currentPosition - 1;
        if(MainActivity.currentPosition < 0)
            MainActivity.currentPosition = MusicProvider.numberOfMusics - 1;

        MainActivity.setToPlay(true);
        MainActivity.view = MainActivity.getViewById(MainActivity.currentPosition);

        play();
    }

    /**
     * This class binds service with the caller wich is MainActivity class.
     */
    public class PlayBinder extends Binder {
        void play() {
            PlayingService.this.play();
        }
        void pause() {
            PlayingService.this.pause();
        }
        void stop() {
            PlayingService.this.stop();
        }
        void next() {
            PlayingService.this.next();
        }
        void prev() {
            PlayingService.this.prev();
        }
    }

}