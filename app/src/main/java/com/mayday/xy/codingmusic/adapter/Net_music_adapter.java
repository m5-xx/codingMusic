package com.mayday.xy.codingmusic.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.Mp3Net;

import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/17.
 */

public class Net_music_adapter extends BaseAdapter{
    private Context context;
    private ArrayList<Mp3Net> netMp3Info= new ArrayList<>();
    ViewHoulder vh;

    public Net_music_adapter(Context context, ArrayList<Mp3Net> netMp3Info) {
        this.context = context;
        this.netMp3Info = netMp3Info;
    }
    @Override
    public int getCount() {
        return netMp3Info.size();
    }

    @Override
    public Object getItem(int i) {
        return netMp3Info.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.net_music_list_adapter, null);
            vh = new ViewHoulder();
            vh.textView_titles = (TextView) view.findViewById(R.id.net_songname);
            vh.textView_artist = (TextView) view.findViewById(R.id.net_singer);
            vh.net_image_id = (ImageView) view.findViewById(R.id.net_image_id);
            vh.net_more= (ImageView) view.findViewById(R.id.net_more);

            view.setTag(vh);
        }
        vh = (ViewHoulder) view.getTag();
        Mp3Net mp3Net= netMp3Info.get(i);
        vh.textView_titles.setText(mp3Net.getTitle());
        vh.textView_artist.setText(mp3Net.getArtist());

//        Bitmap bitmap = MediaUtils.getArtwork(context, mp3Info.getId(), mp3Info.getAlbumId(), true);
//        vh.image_id.setImageBitmap(bitmap);

        return view;
    }

    //列表项布局(每一个Item) item_music_list
    static class ViewHoulder {
        ImageView net_image_id;
        TextView textView_titles;
        TextView textView_artist;
        ImageView net_more;
    }

}
