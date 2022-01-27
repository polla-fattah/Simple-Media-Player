package adb.polla.smp;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * This class is provides the ListView {@see adb.polla.smp.MainActivity.musicList} with customised
 * list elements each list element contains an image, title of the music and its duration.
 *
 * @author polla F Abduljabbar
 */
public class MusicAdapter extends BaseAdapter {
    private LayoutInflater layoutInflater;

    /**
     * constructor initializes a LayoutInflater
     */
    public MusicAdapter() {
        layoutInflater = (LayoutInflater) MainActivity.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    /**
     * @return number of musics
     */
    @Override
    public int getCount() {
        return MusicProvider.numberOfMusics;
    }

    /**
     * @param position int required position
     * @return Object of position's song
     */
    @Override
    public Object getItem(int position) {
        return MusicProvider.songs[position];
    }

    /**
     * @param position int required position
     * @return returns position back as index of array songs is the same index of items in ListView.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * Constructs and returns an Item for the ListView
     *
     * @param position int current position
     * @param convertView ViewGroup
     * @param parent View current view
     * @return returns a View of item at position
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        Info info;

        if (convertView == null) {
            view = layoutInflater.inflate(R.layout.music_layout, parent, false);

            // links music_layout to the Info to be filled with data later
            info = new Info();
            info.title = (TextView)view.findViewById(R.id.title);
            info.duration = (TextView)view.findViewById(R.id.duration);
            info.status = (ImageView)view.findViewById(R.id.status);

            view.setTag(info);
        } else {
            view = convertView;
            info = (Info)view.getTag();
        }
        //providing  data to the View items
        info.title.setText(MusicProvider.songs[position].title);
        info.duration.setText("Duration : " + getHumanReadableTime(
                Long.parseLong(MusicProvider.songs[position].duration)));
        info.status.setImageResource(R.drawable.play);

        return view;
    }

    private String getHumanReadableTime(long millisec){
        int seconds = (int) (millisec / 1000) % 60 ;
        int minutes = (int) ((millisec / (1000*60)) % 60);
        int hours   = (int) ((millisec / (1000*60*60)) % 24);
        String time = "";

        time += hours != 0 ? hours + " hur " : "";
        time += minutes != 0 ? minutes + " min " : "";
        time += seconds != 0 ? seconds + " sec" : "";
        time = time.equals("") ? " < 1 sec" : time;

        return time;
    }

    /**
     * This class holds View elements for an item of the ViewList elements are
     * status, title, duration.
     */
    public class Info {
        public ImageView status;
        public TextView title, duration;
    }
}



