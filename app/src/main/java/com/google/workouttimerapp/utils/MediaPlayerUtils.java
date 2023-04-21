package com.google.workouttimerapp.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;

/**
 * 声音播放
 */
public class MediaPlayerUtils {

    private static MediaPlayer mPlayer;
    private static boolean isPause = false;
    private static volatile boolean isPlaying = false;

    public static boolean isPlaying() {
        return isPlaying;
    }

    /**
     * playSound 播放声音  例如 MediaPlayerUtil.playSound(this, R.raw.verify_error_passenger);//无效票
     * @param context 上下文
     */
    public static void playSound(Context context, int rawResId) {
        playSound(context, rawResId, null);
    }

    public static void playSound(Context context, String filePath) {
        playSound(context, filePath, null);
    }

    public static void playSound(Context context,final String filePath,final OnCompletionListener listener){
        try {
            release();
            Uri uri = Uri.parse(filePath);

            mPlayer = new MediaPlayer();
            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setDataSource(context,uri);
            mPlayer.setOnCompletionListener(mediaPlayer -> {
                release();
                isPlaying = false;
                if (listener != null) {
                    try {
                        listener.onCompletion(mediaPlayer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mPlayer.setOnErrorListener((mp, what, extra) -> {
                mPlayer.reset();
                isPlaying = false;
                if (listener != null) {
                    try {
                        listener.onCompletion(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });

            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;

            isPause = false;
        } catch (Exception e) {
            if (listener != null) {
                try {
                    listener.onCompletion(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void playSound(Context context, final int rawResId, final OnCompletionListener listener) {
        try {
            release();

            mPlayer = MediaPlayer.create(context, rawResId);

            mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mPlayer.setOnCompletionListener(mediaPlayer -> {
                release();
                isPlaying = false;
                if (listener != null) {
                    try {
                        listener.onCompletion(mediaPlayer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });
            mPlayer.setOnErrorListener((mp, what, extra) -> {
                mPlayer.reset();
                isPlaying = false;
                if (listener != null) {
                    try {
                        listener.onCompletion(null);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                return false;
            });

//            mPlayer.prepare();
            mPlayer.start();
            isPlaying = true;

            isPause = false;
        } catch (Exception e) {
            if (listener != null) {
                try {
                    listener.onCompletion(null);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public static void pause() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPause = true;
        }
    }

    // 继续
    public static void resume() {
        if (mPlayer != null && isPause) {
            mPlayer.start();
            isPause = false;
        }
    }

    public static void release() {
        if (mPlayer != null) {
            try {
                mPlayer.release();
            } catch (Exception ignored) {

            }
            mPlayer = null;
        }

        isPlaying = false;
    }
}
