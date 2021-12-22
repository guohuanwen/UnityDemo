package com.DefaultCompany.NewUnity;

import com.unity3d.player.*;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class UnityPlayerActivity extends Activity {
    private String TAG = "UnityPlayerActivity";
    protected static UnityPlayer mUnityPlayer; // don't change the name of this variable; referenced from native code

    // Override this in your custom UnityPlayerActivity to tweak the command line arguments passed to the Unity Android Player
    // The command line arguments are passed as a string, separated by spaces
    // UnityPlayerActivity calls this from 'onCreate'
    // Supported: -force-gles20, -force-gles30, -force-gles31, -force-gles31aep, -force-gles32, -force-gles, -force-vulkan
    // See https://docs.unity3d.com/Manual/CommandLineArguments.html
    // @param cmdLine the current command line arguments, may be null
    // @return the modified command line string or null
    protected String updateUnityCommandLineArguments(String cmdLine) {
        return cmdLine;
    }

    // Setup activity layout
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);

        String cmdLine = updateUnityCommandLineArguments(getIntent().getStringExtra("unity"));
        getIntent().putExtra("unity", cmdLine);

        if (mUnityPlayer == null) {
            mUnityPlayer = new UnityPlayer(WPApplication.getApplication());
        } else {
            if (mUnityPlayer.getParent() != null) {
                ViewGroup viewGroup = (ViewGroup) mUnityPlayer.getParent();
                viewGroup.removeView(mUnityPlayer);
            }
        }

        try {
            Class<?> unityClass = Class.forName("com.unity3d.player.UnityPlayer");
            final Field contextField = unityClass.getDeclaredField("t");
            contextField.setAccessible(true);
            contextField.set(mUnityPlayer, this);
//
//            //this.u = this.c();
//            final Field surfaceField = unityClass.getDeclaredField("u");
//            surfaceField.setAccessible(true);
//            Method getSurfaceMethod = unityClass.getDeclaredMethod("c");
//            getSurfaceMethod.setAccessible(true);
//            Object surface = getSurfaceMethod.invoke(mUnityPlayer);
//            surfaceField.set(mUnityPlayer, surface);
//
//            //this.u.setContentDescription(a(var1));
//            Method getStringMethod = unityClass.getDeclaredMethod("a", Context.class);
//            getStringMethod.setAccessible(true);
//            String unityContext = (String) getStringMethod.invoke(mUnityPlayer, this);
//            SurfaceView surfaceView = (SurfaceView)surfaceField.get(mUnityPlayer);
//            surfaceView.setContentDescription(unityContext);
//
//            //this.addView(this.u);
//            mUnityPlayer.removeAllViews();
//            mUnityPlayer.addView((View) surfaceField.get(mUnityPlayer));
        } catch (Exception e) {
            e.printStackTrace();
        }

        setContentView(mUnityPlayer);
        mUnityPlayer.requestFocus();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        // To support deep linking, we need to make sure that the client can get access to
        // the last sent intent. The clients access this through a JNI api that allows them
        // to get the intent set on launch. To update that after launch we have to manually
        // replace the intent with the one caught here.
        setIntent(intent);
        mUnityPlayer.newIntent(intent);
    }

    @Override
    public void finish() {
        try {
            Class<?> unityClass = Class.forName("com.unity3d.player.UnityPlayer");
            final Field contextField = unityClass.getDeclaredField("t");
            contextField.setAccessible(true);
            contextField.set(mUnityPlayer, WPApplication.getApplication());
        }catch (Exception e) {
            e.printStackTrace();
        }
        super.finish();
    }

    // Quit Unity
    @Override
    protected void onDestroy() {
//        mUnityPlayer.destroy();
        super.onDestroy();
    }

    // Pause Unity
    @Override
    protected void onPause() {
        super.onPause();
        mUnityPlayer.pause();
    }

    // Resume Unity
    @Override
    protected void onResume() {
        super.onResume();
        mUnityPlayer.resume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mUnityPlayer.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mUnityPlayer.stop();
    }

    // Low Memory Unity
    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mUnityPlayer.lowMemory();
    }

    // Trim Memory Unity
    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        if (level == TRIM_MEMORY_RUNNING_CRITICAL) {
            mUnityPlayer.lowMemory();
        }
    }

    // This ensures the layout will be correct.
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mUnityPlayer.configurationChanged(newConfig);
    }

    // Notify Unity of the focus change.
    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        mUnityPlayer.windowFocusChanged(hasFocus);
    }

    // For some reason the multiple keyevent type is not supported by the ndk.
    // Force event injection by overriding dispatchKeyEvent().
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.i("TAG", "dispatchKeyEvent: " + event.getKeyCode());
        return super.dispatchKeyEvent(event);
    }

    // Pass any events not handled by (unfocused) views straight to UnityPlayer
    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i(TAG, "onKeyDown: "  + keyCode);
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return mUnityPlayer.injectEvent(event);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }

    /*API12*/
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mUnityPlayer.injectEvent(event);
    }
}
