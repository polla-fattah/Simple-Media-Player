package adb.polla.smp;

import android.database.Cursor;
import android.provider.MediaStore;

/**
 * This class uses system content provider to retrieves all musics which has been saved on the SD
 * and saves them in an array of Songs which is static array can be used by other classes.
 *
 * @author polla F Abduljabbar
 */
public class MusicProvider {
    public static Song[] songs; // The songs list
    public static int numberOfMusics; //number of songs

    public static void InitialiseSongs(){
        // query songs from external SD content provider for path of data, title, duration of music
        // and sorts them alphabetically
        Cursor musicCursor = MainActivity.context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                new String[]{MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.DURATION}
                , null, null, "LOWER(" + MediaStore.Audio.Media.TITLE + ") ASC");

        if( musicCursor != null)
            MusicProvider.numberOfMusics = musicCursor.getCount();
        else{
            MusicProvider.numberOfMusics = 0;
            MusicProvider.songs = new Song[0];
            return;
        }

        MusicProvider.songs = new Song[MusicProvider.numberOfMusics];

        for(int i = 0; musicCursor.moveToNext(); i++){
            MusicProvider.songs[i] = new Song(musicCursor.getString(0),
                    musicCursor.getString(1), musicCursor.getString(2));
        }
        musicCursor.close();
    }

    /**
     * This class holds information of a song
     * the information is path, title and duration
     */
    public static class Song {
        public String path, title, duration;
        public Song(String p, String t, String d){
            path = p; title = t; duration = d;
        }
    }
}
