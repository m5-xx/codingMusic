package com.mayday.xy.codingmusic.MainActivitys;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import com.mayday.xy.codingmusic.R;
import com.mayday.xy.codingmusic.Utils.Mp3Net;

/**
 * Created by xy-pc on 2016/11/23.
 */

public class DownloadDialogFragment extends DialogFragment {
    private Mp3Net searchResult;
    private MainActivity mainActivity;
    private String []item;

    public static DownloadDialogFragment newInstance(Mp3Net searchResult){
        DownloadDialogFragment downloadDialogFragment=new DownloadDialogFragment();
        downloadDialogFragment.searchResult=searchResult;

        return downloadDialogFragment;
    }

    @Override
    public void onAttach(Context context) {
        mainActivity= (MainActivity) getActivity();
        item=new String []{getString(R.string.download),getString(R.string.cancle)};
        super.onAttach(context);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(mainActivity);
        builder.setCancelable(true);//点击back可以退出Dialog
        builder.setItems(item, new DialogInterface.OnClickListener(){

            @Override
            public void onClick(DialogInterface dialogInterface, int i) { //i表示我们点击是哪一个按钮
                switch (i){
                    case 0: //0表示下载，因为我们的item下标为0的时候是表示下载
                        DownloadUtils.getInstance().setListener(new DownloadUtils.DownMusic() {
                            @Override
                            public void onDownload(String mp3Url) { //传入url下载地址

                            }

                            @Override
                            public void onError(String error) {

                            }
                        });

                        break;
                    case 1:
                        dialogInterface.dismiss();//点击取消就直接退出Dialog
                        break;
                }
            }
        });
//        return super.onCreateDialog(savedInstanceState);
        return builder.create();
    }
}
