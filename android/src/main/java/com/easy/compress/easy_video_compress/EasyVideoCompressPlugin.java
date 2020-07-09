package com.easy.compress.easy_video_compress;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Environment;
import android.util.Log;


import com.easy.compress.easy_video_compress.plugin.VideoCompress;

import java.io.File;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.EventChannel;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * EasyVideoCompressPlugin
 */
public class EasyVideoCompressPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    String TAG = "EasyVideoCompress";
    static Context ctx;
    static Activity activity;
    //    private static EventChannel.EventSink eventSink;
    private static FlutterState flutterState;

    private QueuingEventSink eventSinkOK = new QueuingEventSink();

    public EasyVideoCompressPlugin() {
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "easy_video_compress");
        channel.setMethodCallHandler(new EasyVideoCompressPlugin());
        flutterState =
                new FlutterState(
                        flutterPluginBinding.getBinaryMessenger());
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "easy_video_compress");
        channel.setMethodCallHandler(new EasyVideoCompressPlugin());

    }

    private String[] mPermissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        final EventChannel eventChannel =
                new EventChannel(flutterState.binaryMessenger, "easy_video_compress_event");
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                eventSinkOK.setDelegate(events);
            }

            @Override
            public void onCancel(Object arguments) {
                eventSinkOK.setDelegate(null);
            }
        });

        if (call.method.equals("getPlatformVersion")) {
            eventSinkOK.success("getPlatformVersion");
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("getPath")) {

            String destPath;
            String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            destPath = outputDir + File.separator + "out_VID_";

            result.success(destPath);//+ new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4"
        } else if (call.method.equals("easyVideoCompress")) {
            if (lacksPermissions(ctx, mPermissions)) {
                result.error(TAG, "权限问题", "权限未开启");
                return;
            }
            String path = call.argument("path");
            String toPath = call.argument("toPath");
            Log.d(TAG, "android接受的完整路径::" + toPath);
            result.success(handleVideo(path, toPath));
        } else {
            result.notImplemented();
        }
    }

    private boolean handleVideo(String path, String toPath) {
        VideoCompress.compressVideoLow(path, toPath, new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
                eventSinkOK.success("start");
            }

            @Override
            public void onSuccess() {
                eventSinkOK.success("onSuccess");
            }

            @Override
            public void onFail() {
                eventSinkOK.error("error", "Compress ", "Failed!");
            }

            @Override
            public void onProgress(float percent) {
                eventSinkOK.success(percent);
            }
        });
        return true;
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        ctx = binding.getApplicationContext();
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        activity = binding.getActivity();
        ctx = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        ctx = binding.getActivity();
        activity = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivity() {

    }

    private static final class FlutterState {
        private final BinaryMessenger binaryMessenger;

        FlutterState(
                BinaryMessenger messenger) {
            this.binaryMessenger = messenger;
        }
    }


    private Locale getLocale() {
        Configuration config = ctx.getApplicationContext().getResources().getConfiguration();
        Locale sysLocale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }

        return sysLocale;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }

    /**
     * 判断是否缺少权限
     */
    private static boolean lacksPermission(Context mContexts, String permission) {
        return ContextCompat.checkSelfPermission(mContexts, permission) ==
                PackageManager.PERMISSION_DENIED;
    }

    /**
     * 判断权限集合
     * permissions 权限数组
     * return true-表示没有改权限  false-表示权限已开启
     */
    private boolean lacksPermissions(Context mContexts, String[] mPermissions) {
        for (String permission : mPermissions) {
            if (lacksPermission(mContexts, permission)) {
                ActivityCompat.requestPermissions(activity, new String[]{permission}, 1);
                Log.e("TAG", "-------没有开启权限");
                return true;
            }
        }
        Log.e("TAG", "-------权限已开启");
        return false;
    }

}