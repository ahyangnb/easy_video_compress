package com.video.compress.video_compress;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.nfc.Tag;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.video.compress.video_compress.plugin.VideoCompress;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.logging.StreamHandler;

import androidx.annotation.NonNull;
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
 * VideoCompressPlugin
 */
public class VideoCompressPlugin implements FlutterPlugin, MethodCallHandler, ActivityAware {

    String TAG = "VideoCompress";
    static Context ctx;
    private static FlutterState flutterState;

    private QueuingEventSink eventSink = new QueuingEventSink();

    public VideoCompressPlugin() {
    }

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
        final MethodChannel channel = new MethodChannel(flutterPluginBinding.getFlutterEngine().getDartExecutor(), "video_compress");
        channel.setMethodCallHandler(new VideoCompressPlugin());
        flutterState =
                new FlutterState(
                        flutterPluginBinding.getBinaryMessenger());
    }

    public static void registerWith(Registrar registrar) {
        final MethodChannel channel = new MethodChannel(registrar.messenger(), "video_compress");
        channel.setMethodCallHandler(new VideoCompressPlugin());
    }

    @Override
    public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {

        final EventChannel eventChannel =
                new EventChannel(flutterState.binaryMessenger, "video_compress_event");
        eventChannel.setStreamHandler(new EventChannel.StreamHandler() {
            @Override
            public void onListen(Object arguments, EventChannel.EventSink events) {
                eventSink.setDelegate(events);
            }

            @Override
            public void onCancel(Object arguments) {
                eventSink.setDelegate(null);
            }
        });

        if (call.method.equals("getPlatformVersion")) {
            eventSink.success("getPlatformVersion");
            result.success("Android " + android.os.Build.VERSION.RELEASE);
        } else if (call.method.equals("videoCompress")) {
            String path = call.argument("path");

            String destPath;
            String outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
            destPath = outputDir + File.separator + "out_VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";

            VideoCompress.compressVideoLow(path, destPath, new VideoCompress.CompressListener() {
                @Override
                public void onStart() {
                    eventSink.success("start");

//                tv_indicator.setText("Compressing..." + "\n"
//                        + "Start at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
//                pb_compress.setVisibility(View.VISIBLE);
//                startTime = System.currentTimeMillis();
//                Util.writeFile(ctx, "Start at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()) + "\n");
                }

                @Override
                public void onSuccess() {
                    eventSink.success("onSuccess");

//                String previous = tv_indicator.getText().toString();
//                tv_indicator.setText(previous + "\n"
//                        + "Compress Success!" + "\n"
//                        + "End at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
//                pb_compress.setVisibility(View.INVISIBLE);
//                endTime = System.currentTimeMillis();
//                Util.writeFile(ctx, "End at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()) + "\n");
//                Util.writeFile(ctx, "Total: " + ((endTime - startTime) / 1000) + "s" + "\n");
//                Util.writeFile(ctx);
                }

                @Override
                public void onFail() {
                    eventSink.error("error", "Compress ", "Failed!");
//                tv_indicator.setText("Compress Failed!");
//                pb_compress.setVisibility(View.INVISIBLE);
//                endTime = System.currentTimeMillis();
//                Util.writeFile(ctx, "Failed Compress!!!" + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
                }

                @Override
                public void onProgress(float percent) {
                    eventSink.success(percent);
                    Log.d(TAG, "压缩进度：" + percent + "%");
//                tv_progress.setText(String.valueOf(percent) + "%");
                }
            });
            result.success("视频处理中");
        } else {
            result.notImplemented();
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

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
        ctx = binding.getApplicationContext();
    }

    @Override
    public void onAttachedToActivity(ActivityPluginBinding binding) {
        ctx = binding.getActivity();
    }

    @Override
    public void onDetachedFromActivityForConfigChanges() {

    }

    @Override
    public void onReattachedToActivityForConfigChanges(ActivityPluginBinding binding) {
        ctx = binding.getActivity();
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

}
