package com.example.myapplication;

import static com.example.myapplication.AlbumDetailsAdapter.albumFiles;
import static com.example.myapplication.MainActivity.musicFiles;
import static com.example.myapplication.MainActivity.repeatBoolean;
import static com.example.myapplication.MainActivity.shuffleBoolean;
import static com.example.myapplication.MusicAdapter.mFiles;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.palette.graphics.Palette;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name, artist_name, duration_played, duration_total;
    ImageView cover_art, nextBtn, prevBtn, backBtn, shuffleBtn, repeatBtn;
    FloatingActionButton playPauseBtn;
    SeekBar seekBar;
    int position;
    static ArrayList<MusicFiles> listSongs = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;
    private Handler handler = new Handler();
    private Thread playThread, prevThread, nextThread;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod();
        song_name.setText(listSongs.get(position).getTitle());
        artist_name.setText(listSongs.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 1000);
            }
        });
        shuffleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (shuffleBoolean){
                    shuffleBoolean=false;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
                }
                else {
                    shuffleBoolean=true;
                    shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
                }
            }
        });
        repeatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatBoolean)
                {
                    repeatBoolean = false;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_off);
                }
                else {
                    repeatBoolean = true;
                    repeatBtn.setImageResource(R.drawable.ic_repeat_on);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();
        super.onResume();
    }
    //chức năng lùi bài hát
    private void prevThreadBtn() {
        prevThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }
    //xử lí prevBtnClicked
    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position=getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                //nếu position < 0 thì sẽ chuyển về bài hát cuối cùng / còn không thì lùi bài bình thường
                position = ((position - 1 ) < 0  ? (listSongs.size() - 1) : (position - 1 ));
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            //tạo một đối tương bài hát mới
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            // lấy thông tin bài hát bằng uri
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            //phát bài hát vừa được chuyển
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position=getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                //nếu position < 0 thì sẽ chuyển về bài hát cuối cùng / còn không thì lùi bài bình thường
                position = ((position - 1 ) < 0  ? (listSongs.size() - 1) : (position - 1 ));
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }
    //chức năng next
    private void nextThreadBtn() {
        nextThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        nextBtnBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }
    //xử lí nextBtnBtnClicked()
    private void nextBtnBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position=getRandom(listSongs.size() - 1);
            }
             else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }

            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else {
            mediaPlayer.stop();
            mediaPlayer.release();
            if (shuffleBoolean && !repeatBoolean){
                position=getRandom(listSongs.size() - 1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % listSongs.size());
            }
            uri = Uri.parse(listSongs.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(listSongs.get(position).getTitle());
            artist_name.setText(listSongs.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
            playPauseBtn.setBackgroundResource(R.drawable.ic_play);
        }
    }

    private int getRandom(int i) {
        Random random=new Random();
        return random.nextInt(i+1);
    }

    //chức năng play, pause
    private void playThreadBtn() {
        playThread = new Thread()
        {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        playPauseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }
    //xử lí playPauseBtnClicked
    private void playPauseBtnClicked() {
        if(mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.ic_play);
            mediaPlayer.pause();
            //set thanh seekbar = thời lượng bài nhạc
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        //lấy thời lượng của bài nhạc và đổi qua giây
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        //chuyển thanh seekbar đến vị trí được click
                        seekBar.setProgress(mCurrentPosition);
                    }
                    //tự động cập nhật thời gian khi click trên thanh seekbar
                    handler.postDelayed(this, 1000);
                }
            });
        }
        else {
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 1000);
                }
            });
        }
    }

    private String formattedTime(int mCurrentPosition) {
        String totalout = "";
        String totalNew = "";
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalNew = minutes + ":" + "0" + seconds;
        if(seconds.length() == 1){
            return totalNew;
        }
        else {
            return totalout;
        }
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender= getIntent().getStringExtra("sender");
        if (sender!=null && sender.equals("albumDetails")){

            listSongs = albumFiles;
        }else   {
        listSongs = mFiles;
        }
        if(listSongs != null){
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(listSongs.get(position).getPath());
        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else {
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        //set thanh seekbar = thời lượng bài nhạc
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        // lấy thông tin bài hát bằng uri
        metaData(uri);
    }

    private void initView() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayer);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cover_art);
        nextBtn = findViewById(R.id.id_next);
        prevBtn = findViewById(R.id.id_prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.id_shuffle);
        repeatBtn = findViewById(R.id.id_repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
    }
    private void metaData (Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();//lấy thông tin một file nhạc
        retriever.setDataSource(uri.toString());//lấy đường dẫn
        int durationTotal = Integer.parseInt(listSongs.get(position).getDuration()) / 1000; //lấy thời lượng của bài nhạc / 1000 để đổi sang giây
        duration_total.setText(formattedTime(durationTotal));//định dạng thời lương
        //lấy hình ảnh của file nhạc
        byte[] art = retriever.getEmbeddedPicture();
        Bitmap bitmap ;
        if(art != null){
            /*Glide.with(this)
                    .asBitmap()
                    .load(art)
                    .into(cover_art);*/

            // Giải mã mảng byte `art` thành một đối tượng Bitmap bằng cách sử dụng lớp BitmapFactory
            bitmap= BitmapFactory.decodeByteArray(art,0,art.length);
            ImageAnimation(this, cover_art, bitmap);
            // Tạo bảng màu một cách bất đồng bộ từ bitmap bằng cách sử dụng lớp Palette
            Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(@Nullable Palette palette) {
                    // Trích xuất bảng màu chính từ bảng màu
                    Palette.Swatch swatch=palette.getDominantSwatch();
                    // Kiểm tra xem có bảng màu chính được tìm thấy hay không
                    if (swatch!=null){
                        // Tìm các view cần được cập nhật
                        ImageView gredient =findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        // Thiết lập gradient nền cho view gradient
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),0x00000000});
                        gredient.setBackground(gradientDrawable);
                        // Thiết lập gradient nền cho view main container
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{swatch.getRgb(),swatch.getRgb()});
                        mContainer.setBackground(gradientDrawableBg);
                        // Thiết lập màu chữ cho các view song name và artist name
                        song_name.setTextColor(swatch.getTitleTextColor());
                        artist_name.setTextColor(swatch.getBodyTextColor());
                    }
                    else {
                        // Tìm các view cần được cập nhật
                        ImageView gredient =findViewById(R.id.imageViewGredient);
                        RelativeLayout mContainer = findViewById(R.id.mContainer);
                        // Thiết lập gradient nền mặc định cho view gradient
                        gredient.setBackgroundResource(R.drawable.gredient_bg);
                        GradientDrawable gradientDrawable=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0x00000000});
                        gredient.setBackground(gradientDrawable);
                        // Thiết lập gradient nền mặc định cho view main container
                        mContainer.setBackgroundResource(R.drawable.main_bg);
                        GradientDrawable gradientDrawableBg=new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP,
                                new int[]{0xff000000,0xff000000});
                        mContainer.setBackground(gradientDrawableBg);
                        // Thiết lập màu chữ mặc định cho các view song name và artist name
                        song_name.setTextColor(Color.WHITE);
                        artist_name.setTextColor(Color.DKGRAY);
                    }
                }
            });
        }
        else {
            Glide.with(this)
                    .asBitmap()
                    .load(R.drawable.unlike)
                    .into(cover_art);
            ImageView gredient =findViewById(R.id.imageViewGredient);
            RelativeLayout mContainer = findViewById(R.id.mContainer);
            gredient.setBackgroundResource(R.drawable.gredient_bg);
            mContainer.setBackgroundResource(R.drawable.main_bg);
            song_name.setTextColor(Color.WHITE);
            artist_name.setTextColor(Color.DKGRAY);
        }
    }
    //tạo hiệu ứng chuyển đổi ảnh giữa hai Bitmaps trong một ImageView bằng cách sử dụng Animation và thư viện Glide.
    public void ImageAnimation(Context context,ImageView imageView, Bitmap bitmap)
    {
        Animation animOut= AnimationUtils.loadAnimation(context, android.R.anim.fade_out);
        Animation animIn=AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        animOut.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                Glide.with(context).load(bitmap).into(imageView);
                animIn.setAnimationListener(new Animation.AnimationListener() {
                    @Override
                    public void onAnimationStart(Animation animation) {

                    }
                    @Override
                    public void onAnimationEnd(Animation animation) {
                    }
                    @Override
                    public void onAnimationRepeat(Animation animation) {
                    }
                });
                imageView.startAnimation(animIn);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        imageView.startAnimation(animOut);

    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnBtnClicked();
        if (mediaPlayer!=null)
        {
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}