package com.mayday.xy.codingmusic.MainActivitys;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.mayday.xy.codingmusic.Constant.ConstantNombel;
import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.Mp3Net;
import com.mayday.xy.codingmusic.adapter.Net_music_adapter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by xy-pc on 2016/11/3.
 */
public class Net_music_fragment extends Fragment implements View.OnClickListener,AdapterView.OnItemClickListener{
    private RelativeLayout btn_select;  //点击进入搜索状态(进入后隐藏)
    private RelativeLayout select_view; //(上一个动作完成后，显示该UI)
    private LinearLayout login_Id;//加载圈
    private ListView net_listView;

    private EditText edit_text;    //输入框里的内容
    private Button button;//搜索按钮
    private static Net_music_fragment net_music_fragment;
    private MyApplication app=new MyApplication();
    private LoadNetDateTask loadNetDateTask;


    //给其一个集合用于保存加载下来的音乐
    private ArrayList<Mp3Net> serachResults;

    //提供方法供外界调用
    public static Net_music_fragment newInstance() {
        net_music_fragment=new Net_music_fragment();
        return net_music_fragment;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.net_music_list_layout, null);
        btn_select= (RelativeLayout) view.findViewById(R.id.btn_select);
        select_view= (RelativeLayout) view.findViewById(R.id.select_view);
        login_Id= (LinearLayout) view.findViewById(R.id.login_Id);
        net_listView= (ListView) view.findViewById(R.id.net_listView);
        edit_text= (EditText) view.findViewById(R.id.edit_text);
        button= (Button) view.findViewById(R.id.Button);
        loadNetDateTask=new LoadNetDateTask();
        serachResults=new ArrayList<>();
//        fragmentManager=getFragmentManager();
        loadData();

        net_listView.setOnItemClickListener(this);
        btn_select.setOnClickListener(this);
        select_view.setOnClickListener(this);


        return view;
    }

    private void loadData() {
        if(login_Id.getVisibility()==View.GONE){
            login_Id.setVisibility(View.VISIBLE);
        }
        //加载网络数据，需要进行一个耗时的操作,使用AsyncTask来进行处理
        //参数传入百度音乐的url地址
        loadNetDateTask.execute(ConstantNombel.XIAMI+ConstantNombel.XIAMMUSIC);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_select:
                btn_select.setVisibility(View.GONE);
                select_view.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
        if(position>=serachResults.size()||position<0){
            return;
        }
        showDownloadDialog(position);
    }

    private void showDownloadDialog(int position) {
        //下载功能
        DownloadDialogFragment downloadDialogFragment = DownloadDialogFragment.newInstance(serachResults.get(position));
        //
        downloadDialogFragment.show(getFragmentManager(),"download");//getFragmentManager()中的DownloadDialogFragment必须导入v4包
    }

    class LoadNetDateTask extends AsyncTask<String,Void,Integer>{//参数，进度，返回值类型
        @Override
        protected void onPreExecute() {
            login_Id.setVisibility(View.VISIBLE);
            net_listView.setVisibility(View.GONE);
//            serachResults.clear();
            super.onPreExecute();
        }
        @Override
        protected Integer doInBackground(String... Params) {
            //得到该HTML的内容信息
            String url=Params[0];
            //请求网页返回HTML代码
            try {
                Document doc = Jsoup.connect(url).userAgent(ConstantNombel.USER_AGENT).timeout(6 * 1000).get();
                //doc则为返回的指定网页的源码
                //从网页上解析出所以歌曲
//                System.out.println(doc);
//                Elements songTitles = doc.select("span.song-title");
//                Elements artists = doc.select("span.author_list");
                Elements songTitles = doc.select("td.song_name");
                Elements artists = doc.select("td.song_artist");

                for(int i=0;i<songTitles.size();i++){
                    Mp3Net searchResult=new Mp3Net();
                    //解析出</a>标签
                    Elements urls = songTitles.get(i).getElementsByTag("a");
                    //解析出歌曲的url地址
                    searchResult.setUrl(urls.get(0).attr("href"));
//                    System.out.println(urls.get(0).attr("href"));
                    //解析出歌曲的名字
                    searchResult.setTitle(urls.get(0).text());
                    System.out.println(searchResult.getTitle());

                    Elements artistElements = artists.get(i).getElementsByTag("a");
                    searchResult.setArtist(artistElements.get(0).text());

                    searchResult.setAlbum("热歌榜");
                    serachResults.add(searchResult);
                }
//                System.out.println(serachResults);
            } catch (IOException e) {
                e.printStackTrace();
                return -1;
            }
            return 1;
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Integer integer) {
            if(integer==1){
                login_Id.setVisibility(View.GONE);
                net_listView.setVisibility(View.VISIBLE);
                net_listView.setAdapter(new Net_music_adapter(app.getContext(),serachResults));
            }else{
                Toast.makeText(app.getContext(),"加载失败,请连接互联网",Toast.LENGTH_SHORT).show();
                login_Id.setVisibility(View.GONE);
            }
            super.onPostExecute(integer);
        }
    }
}
