package com.example.myapplication;

import static com.example.myapplication.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.media.MediaMetadataRetriever;
import android.os.Bundle;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.IOException;
import java.util.ArrayList;

public class AlbumDetails extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageView albumPhoto;
    String albumName;
    ArrayList<MusicFiles> albumSongs=new ArrayList<>();
    AlbumDetailsAdapter albumDetailsAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {// lỗi hiện thị ảnh và album
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_details);
        recyclerView=findViewById(R.id.recycleView);
        albumPhoto=findViewById(R.id.albumphoto);
        albumName=getIntent().getStringExtra("albumName");
        int j=0;
        for (int i =0; i <musicFiles.size(); i++)
        {
            if (albumName.equals(musicFiles.get(i).getAlbum()));
            {
                albumSongs.add(j,musicFiles.get(i));
                j++;
            }
        }
        byte[] image= getAlbumArt(albumSongs.get(0).getPath());// còn lỗi hiện thị ảnh
        if (image !=null){
            Glide.with(this)
                    .load(image)
                    .into(albumPhoto);
        }
        else
        {
            Glide.with(this)
                    .load(R.drawable.vitcon)
                    .into(albumPhoto);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!(albumSongs.size()<1)){
            albumDetailsAdapter=new AlbumDetailsAdapter(this,albumSongs);
            recyclerView.setAdapter(albumDetailsAdapter);
            recyclerView.setLayoutManager(new LinearLayoutManager(this,RecyclerView.VERTICAL, false));

        }
    }

    private byte[] getAlbumArt(String uri)  {
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri);
        byte[] art = retriever.getEmbeddedPicture();
        try {
            retriever.release();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return art;
    }
}