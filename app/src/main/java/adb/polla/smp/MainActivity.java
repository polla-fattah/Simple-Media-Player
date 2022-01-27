package adb.polla.smp;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.Toast;

/**
 * This class crates the user interface and binds all application functionality together.
 * @author polla F Abduljabbar
 */
public class MainActivity extends AppCompatActivity {
    public static ListView musicList = null; //to display list of available musics in the SD
    public static int currentPosition = 0; // current music index
    public static MediaPlayer mediaPlayer = null;
    public static View view = null; // current running music view
    public static Context context= null; // this class's context to be used by other classes
    public static ImageButton playPauseBtn = null;
    public static PlayingService.PlayBinder playService = null;

    private Notification notification;
    private NotificationManager notificationManager;
    private static final int NOTIFICATION_ID = 0;
    //handles bound service connect and disconnect
    private ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            playService = (PlayingService.PlayBinder) service;
        }
        @Override
        public void onServiceDisconnected(ComponentName name) {
            playService = null;
        }
    };
    /**
     * Creates interfaces and initializes data
     * @param savedInstanceState Bundle instance
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = getApplicationContext();

        MusicProvider.InitialiseSongs(); // Fetches list of musics form SD

        playPauseBtn = (ImageButton)findViewById(R.id.playPause_btn);

        // construct music list interface
        musicList = (ListView) findViewById(R.id.listView1);
        musicList.setAdapter(new MusicAdapter());
        musicList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView parent, View v, int position, long id) {
                MainActivity.setToPlay(true);
                MainActivity.currentPosition = position;
                MainActivity.view = v;
                playService.play();
            }
        });

        //Binds and starts a PlayingService instance
        this.bindService(new Intent(this, PlayingService.class),
                serviceConnection, Context.BIND_AUTO_CREATE);

        // initialize and register broadcast receiver which handles calls and music noise
        MusicBroadcastListener receiver = new MusicBroadcastListener();
        IntentFilter intentFilter = new IntentFilter("android.media.AUDIO_BECOMING_NOISY");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, intentFilter);

        //initialize notification and it's manager
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0);
        notification = new NotificationCompat.Builder(this)
                .setTicker(("Simple Music Player"))
                .setSmallIcon(R.drawable.play)
                .setContentTitle("Simple Music Player ")
                .setContentText("   Is Playing Now ...")
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true)
                .build();
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    /**
     * handles user click action on the button prev
     */
    public void onClickPrev(View view){
        playService.prev();
    }

    /**
     * handles user click action on the button play
     */
    public void onClickPlay(View view){
        if(MainActivity.mediaPlayer == null)
            MainActivity.mediaPlayer = new Player();
        if(MainActivity.mediaPlayer.isPlaying()){
            MainActivity.mediaPlayer.pause();
            MainActivity.setToPlay(true);
        }
        else{
            MainActivity.setToPlay(false);
            MainActivity.mediaPlayer.start();
        }
    }

    /**
     * handles user click action on the button stop
     */
    public void onClickStop(View view){
        MainActivity.setToPlay(true);
        playService.stop();
    }

    /**
     * handles user click action on the button next
     */
    public void onClickNext(View view){
        playService.next();
    }

    /**
     * handles user click action on the button cross
     */
    public void onClickCross(View view){
        moveTaskToBack(true);
    }

    /**
     * This method returns item in the requred position if the position is out of
     * boundary it returns null
     *
     * @param position int required position
     * @return View and item of ListView
     */
    public static View getViewById(int position){
        final int firs = MainActivity.musicList.getFirstVisiblePosition();
        final int last = firs + MainActivity.musicList.getChildCount() - 1;

        if (position < firs || position > last ) {
            Toast.makeText(MainActivity.context, "Error occurred while retrieving musics",
                    Toast.LENGTH_LONG).show();
            Log.e("smp", "Error in {PlayingService extends Service} class,  error in position: "
                   + position );
            return null;
        }
        else {
            final int childIndex = position - firs; // calculate correct position relative to the start
            return MainActivity.musicList.getChildAt(childIndex);
        }
    }

    /**
     * This method changes
     * @param play boolean to choose between play and pause
     */
    public static void setToPlay(boolean play){
        if(MainActivity.view != null){
            if(play) {
                ((MusicAdapter.Info) MainActivity.view.getTag()).status.setImageResource(R.drawable.play);
                MainActivity.playPauseBtn.setImageResource(R.drawable.play);
            }
            else {
                ((MusicAdapter.Info) MainActivity.view.getTag()).status.setImageResource(R.drawable.pause);
                MainActivity.playPauseBtn.setImageResource(R.drawable.pause);
            }
        }
    }
    //returns all resources, set them to null and calls garbage collector
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(serviceConnection!=null) {
            unbindService(serviceConnection);
            serviceConnection = null;
        }
        if(mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        System.gc();
    }

    //hides notification
    @Override
    protected void onResume() {
        super.onResume();
        notificationManager.cancel(MainActivity.NOTIFICATION_ID);
    }

    // if mediaPlayer is stopped, this function release it and sets it to null
    // while if mediaPlayer is playing the it crates a notification before the app goes to the background
    @Override
    protected void onStop() {
        super.onStop();
        if(mediaPlayer != null) {
            if (mediaPlayer.isPlaying()) {
                notificationManager.notify(MainActivity.NOTIFICATION_ID, notification);
            }
            else {
                mediaPlayer.release();
                mediaPlayer = null;
            }
        }
    }
}

