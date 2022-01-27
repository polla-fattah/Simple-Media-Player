package adb.polla.smp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

/**
 * This class listens to the broadcasts of audio becoming noise and  phone calls states.
 * For the noise when the user pulls the headphone out this broadcast listener pauses the music,
 * and for hte phone call it pauses the music at the start of phone ringing and conversation then
 * restarts it at the end of phone call.
 *
 * @author polla F Abduljabbar
 */
public class MusicBroadcastListener extends BroadcastReceiver {
    private static boolean wasPlaying = false; //saves the state of music play or not at the call time
    private static int oldState; // saves the last state of the call as it might trigger same state multiple times

     /* Implementing his class is important to differentiate between starting the call or ending it
         as the application pauses the music when the hon call rings and restarts it when the phone
         call finishes.
     */
    private static PhoneStateListener phoneStateListener = new PhoneStateListener() {
        public void onCallStateChanged(int state, String incomingNumber) {
            if (oldState == state)
                return;
            oldState = state;

            if (state == 1) {// phone rings
                wasPlaying = MainActivity.mediaPlayer.isPlaying();
                if(wasPlaying)
                    MainActivity.playService.pause();
            }
            if(state == 0 && wasPlaying){ // call ends
                MainActivity.playService.play();
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(android.media.AudioManager.ACTION_AUDIO_BECOMING_NOISY)) {
            //when user pulls the headphone
            MainActivity.playService.pause();
        }
        else {
            // Register listener for LISTEN_CALL_STATE
            ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                    .listen(phoneStateListener, PhoneStateListener.LISTEN_CALL_STATE);
        }
    }
}
