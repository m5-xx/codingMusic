package com.mayday.xy.codingmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.MediaUtils;
import com.mayday.xy.codingmusic.Utils.Mp3Info;

import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/3.
 */
public class My_music_adapter extends BaseAdapter {

    private Context context;
    private ArrayList<Mp3Info> mp3Infos;
    private ViewHoulder vh;

    public My_music_adapter(Context context, ArrayList<Mp3Info> mp3Infos) {
        this.context = context;
        this.mp3Infos = mp3Infos;
    }

    public void setMp3Infos(ArrayList<Mp3Info> mp3Infos) {
        this.mp3Infos = mp3Infos;
    }

    @Override
    public int getCount() {
        return mp3Infos.size();
    }

    @Override
    public Object getItem(int i) {
        return mp3Infos.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_music_list, null);
            vh = new ViewHoulder();
            vh.textView_titles = (TextView) view.findViewById(R.id.textView1);
            vh.textView_artist = (TextView) view.findViewById(R.id.textView2);
            vh.textView_duration = (TextView) view.findViewById(R.id.textView3);
            vh.image_id = (ImageView) view.findViewById(R.id.image_id);
            view.setTag(vh);
        }
        vh = (ViewHoulder) view.getTag();
        Mp3Info mp3Info = mp3Infos.get(i);
        vh.textView_titles.setText(mp3Info.getTitle());
        vh.textView_artist.setText(mp3Info.getArtist());
        vh.textView_duration.setText(MediaUtils.formatTime(mp3Info.getDuration()));

//        Bitmap bitmap = MediaUtils.getArtwork(context, mp3Info.getId(), mp3Info.getAlbumId(), true);
//        vh.image_id.setImageBitmap(bitmap);

        return view;
    }

    //列表项布局(每一个Item) item_music_list
    static class ViewHoulder {
        ImageView image_id;
        TextView textView_titles;
        TextView textView_artist;
        TextView textView_duration;
    }

}
