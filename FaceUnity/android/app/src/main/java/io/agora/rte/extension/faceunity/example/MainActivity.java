package io.agora.rte.extension.faceunity.example;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rte.extension.faceunity.ExtensionManager;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity
        extends AppCompatActivity implements IMediaExtensionObserver {

    private static final String TAG = "MainActivity";

    private RtcEngine mRtcEngine;

    private Button button;
    private final ObservableBoolean enableExtension =
            new ObservableBoolean(false);
    private final AtomicBoolean bundleLoaded = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initUI();
        initData();
        initPermission();
    }

    private void initData() {
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
        initBundle();
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
        // Setup
        try {
            JSONObject jsonObject = new JSONObject();
            JSONArray jsonArray = new JSONArray();
            for (byte it : authpack.A()) {
                jsonArray.put(it);
            }
            jsonObject.put("authdata", jsonArray);
            setExtensionProperty("fuSetup", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        // Load AI model
        File modelDir = new File(getExternalFilesDir("assets"),
                "face_unity/model/ai_face_processor.bundle");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 10);
            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("FaceUnity", "Effect", key, property);
    }

    private void choiceComposer() {
        File composerDir = new File(getExternalFilesDir("assets"),
                "face_unity/graphics/face_beautification.bundle");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", composerDir.getAbsolutePath());
            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void choiceSticker() {
        File stickerDir =
                new File(getExternalFilesDir("assets"),
                        "face_unity/items/ItemSticker/CatSparks.bundle");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", stickerDir.getAbsolutePath());
            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void initBundle() {
        ProgressDialog dialog =
                ProgressDialog.show(this, "", "Loading bundle. Please wait...", true);
        new Thread() {
            @Override
            public void run() {
                String assetsName = "face_unity";
                File destFile = getExternalFilesDir("assets");
                try {
                    ResourceUtils.initResources(getAssets(), assetsName,
                            destFile.getAbsolutePath());
                    bundleLoaded.set(true);
                    handler.post(dialog::dismiss);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void initPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA,
                            Manifest.permission.RECORD_AUDIO},
                    0);
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
        config.mAppId = io.agora.rte.extension.faceunity.example.Constants.mAppId;
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
        ExtensionManager.getInstance(mRtcEngine).initialize();
        mRtcEngine.enableExtension("FaceUnity", "Effect", enabled);
    }

    @Override
    public void onEvent(String s, String s1, String s2, String s3) {
    }
}
