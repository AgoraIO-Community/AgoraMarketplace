package io.agora.rte.extension.bytedance.example;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rte.extension.bytedance.ExtensionManager;

public class MainActivity
        extends AppCompatActivity implements IMediaExtensionObserver {

    private static final String TAG = "MainActivity";

    private RtcEngine mRtcEngine;

    private Button button;
    private final ObservableBoolean enableExtension =
            new ObservableBoolean(false);

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Handler mWorkHandler;
    private Runnable mWorkHandlerRunnable;
    private HandlerThread mHandlerThread;

    // Only set some resources for demo purpose
    private static final Map<String, String> mSticker = new HashMap<String, String>() {{
        put("zhutouzhuer", "byte_dance/StickerResource.bundle/stickers/zhutouzhuer");
        put("zhaocaimao", "byte_dance/StickerResource.bundle/stickers/zhaocaimao");
        put("zisemeihuo", "byte_dance/StickerResource.bundle/stickers/zisemeihuo");
        put("zhuluojimaoxian", "byte_dance/StickerResource.bundle/stickers/zhuluojimaoxian");
        put("zhangshangyouxiji", "byte_dance/StickerResource.bundle/stickers/zhangshangyouxiji");
    }};

    private static final Map<String, String> mComposer = new HashMap<String, String>() {{
        put("hanxi", "byte_dance/ComposeMakeup.bundle/ComposeMakeup/style_makeup/hanxi");
        put("yuanqi", "byte_dance/ComposeMakeup.bundle/ComposeMakeup/style_makeup/yuanqi");
        put("tianmei", "byte_dance/ComposeMakeup.bundle/ComposeMakeup/style_makeup/tianmei");
        put("baicha", "byte_dance/ComposeMakeup.bundle/ComposeMakeup/style_makeup/baicha");
        put("qise", "byte_dance/ComposeMakeup.bundle/ComposeMakeup/style_makeup/qise");
    }};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        initUI();
        initData();
        initPermission();
    }

    private void initData() {
        mHandlerThread = new HandlerThread("LoadBundleHandlerThread");
        mHandlerThread.start();
        mWorkHandler = new Handler(mHandlerThread.getLooper());

        enableExtension.addOnPropertyChangedCallback(
                new Observable.OnPropertyChangedCallback() {
                    @Override
                    public void onPropertyChanged(Observable sender, int propertyId) {
                        if (sender instanceof ObservableBoolean) {
                            boolean enabled = ((ObservableBoolean) sender).get();
                            enableExtension(enabled);
                            if (enabled) {
                                button.setText(R.string.disable_extension);
                            } else {
                                button.setText(R.string.enable_extension);
                            }
                        }
                    }
                });
    }

    private void initUI() {
        button = findViewById(R.id.button_enable);
        button.setOnClickListener(
                view -> enableExtension.set(!enableExtension.get()));
        findViewById(R.id.button_init).setOnClickListener(view -> initExtension());
        findViewById(R.id.button_composers)
                .setOnClickListener(view -> choiceComposer());
        findViewById(R.id.button_stickers)
                .setOnClickListener(view -> choiceSticker());
    }

    private void initExtension() {
        String[] resources = new String[] {"byte_dance/LicenseBag.bundle", "byte_dance/ModelResource.bundle"};
        loadBundle(resources, () -> {
            File destFile = getExternalFilesDir(null);

            File licensePath = new File(
                    destFile,
                    "byte_dance/LicenseBag.bundle/" + io.agora.rte.extension.bytedance.example.Constants.mLicenseName);
            // Check license
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("licensePath", licensePath.getPath());
                setExtensionProperty("bef_effect_ai_check_license", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            File strModelDir = new File(destFile, "byte_dance/ModelResource.bundle");
            // Init
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("strModelDir", strModelDir.getPath());
                jsonObject.put("deviceName", "");
                setExtensionProperty("bef_effect_ai_init", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            // Enable composer and sticker
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("mode", 1);
                jsonObject.put("orderType", 0);
                setExtensionProperty("bef_effect_ai_composer_set_mode", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        });
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("ByteDance", "Effect", key, property);
    }

    private void choiceComposer() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = mComposer.keySet().toArray(new String[0]);

        List<String> nodePaths = new ArrayList<>();
        builder.setTitle(R.string.set_composer)
                .setMultiChoiceItems(items, null,
                        (dialogInterface, i, b) -> {
                            String path = mComposer.get(items[i]);
                            if (b) {
                                nodePaths.add(path);
                            } else {
                                nodePaths.remove(path);
                            }
                        })
                .setNegativeButton("Cancel",
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .setPositiveButton("Confirm",
                        (dialogInterface, i) -> {

                            dialogInterface.dismiss();

                            loadBundle(nodePaths.toArray(new String[0]), () -> {
                                // Composer set nodes
                                JSONArray jsonArray = new JSONArray();

                                for (String nodePath : nodePaths) {
                                    String path = getExternalFilesDir(null) + "/" + nodePath;
                                    jsonArray.put(path);
                                }

                                setExtensionProperty("bef_effect_ai_composer_set_nodes", jsonArray.toString());
                            });
                        })
                .create()
                .show();
    }

    private void choiceSticker() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        String[] items = mSticker.keySet().toArray(new String[0]);

        builder.setTitle(R.string.set_sticker)
                .setSingleChoiceItems(items, -1,
                        (dialogInterface, i) -> {
                            dialogInterface.dismiss();

                            loadBundle(new String[]{mSticker.get(items[i])}, () -> {
                                // Set effect
                                try {
                                    final String path = getExternalFilesDir(null) + "/" + mSticker.get(items[i]);
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("strPath", path);
                                    setExtensionProperty("bef_effect_ai_set_effect", jsonObject.toString());
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            });
                        })
                .setNegativeButton("Cancel",
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO},
                    0);
        } else {
            initRtcEngine();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 0) {
            if (Arrays.equals(grantResults, new int[]{0, 0})) {
                initRtcEngine();
            }
        }
    }

    private void initRtcEngine() {
        RtcEngineConfig config = new RtcEngineConfig();
        config.mContext = getApplicationContext();
        config.mAppId = io.agora.rte.extension.bytedance.example.Constants.mAppId;
        config.mEventHandler = new IRtcEngineEventHandler() {
            @Override
            public void onWarning(int warn) {
                Log.w(TAG, String.format("onWarning %d", warn));
            }

            @Override
            public void onError(int err) {
                Log.e(TAG, String.format("onError %d", err));
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                Log.i(TAG, String.format("onJoinChannelSuccess %s %d %d", channel, uid, elapsed));
            }
        };
        try {
            mRtcEngine = RtcEngine.create(config);
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        if (mRtcEngine == null) {
            return;
        }
        enableExtension.set(true);
        mRtcEngine.enableVideo();
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.startPreview();
        VideoCanvas canvas = new VideoCanvas(findViewById(R.id.surfaceView));
        mRtcEngine.setupLocalVideo(canvas);
    }

    private void enableExtension(boolean enabled) {
        ExtensionManager.getInstance(mRtcEngine).initialize(this);
        mRtcEngine.enableExtension("ByteDance", "Effect", enabled);
    }

    @Override
    public void onEvent(String s, String s1, String s2, String s3) {
    }

    private void loadBundle(String[] assetNames, Runnable onCompleted) {
        ProgressDialog dialog =
                ProgressDialog.show(this, "", "Loading bundle. Please wait...", true);
        mWorkHandler.removeCallbacks(mWorkHandlerRunnable);

        mWorkHandlerRunnable = () -> {
            try {
                for (String assetName : assetNames) {
                    ResourceUtils.copyFileOrDir(
                            MainActivity.this.getAssets(),
                            assetName,
                            getExternalFilesDir(null).getAbsolutePath());
                }

                handler.post(dialog::dismiss);
                onCompleted.run();
            } catch (IOException e) {
                Log.e(TAG, e.toString());
            }
        };
        mWorkHandler.post(mWorkHandlerRunnable);
    }

    @Override
    protected void onDestroy() {
        mWorkHandler.removeCallbacks(mWorkHandlerRunnable);
        super.onDestroy();
    }
}
