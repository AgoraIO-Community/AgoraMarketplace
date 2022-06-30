package io.agora.rte.extension.faceunity.example;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
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
    private Button btnComposers;
    private Button btnSticker;
    private TextView tvAITracking;
    private Button enableAITracking;
    private AppCompatSeekBar colorLevelSeekBar;
    private AppCompatSeekBar filterLevelSeekBar;
    private final ObservableBoolean enableExtension =
            new ObservableBoolean(false);
    private final AtomicBoolean bundleLoaded = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int faces = 0;
    private int hands = 0;
    private int people = 0;

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
        tvAITracking = findViewById(R.id.tv_ai_tracking);
        enableAITracking = findViewById(R.id.button_enable_ai);
        button.setOnClickListener(
                view -> enableExtension.set(!enableExtension.get()));

        btnComposers = findViewById(R.id.button_composers);
        btnComposers.setOnClickListener(view -> choiceComposer());

        btnSticker = findViewById(R.id.button_stickers);
        btnSticker.setOnClickListener(view -> choiceSticker());
        enableAITracking.setOnClickListener(v -> {
            boolean isEnableAITracking = enableAITracking.getTag() != null && (boolean) enableAITracking.getTag();
            isEnableAITracking = !isEnableAITracking;
            if (isEnableAITracking) {
                enableAITracking();
            } else {
                disableAITracking();
            }

            enableAITracking.setTag(isEnableAITracking);
        });

        colorLevelSeekBar = findViewById(R.id.color_level);
        filterLevelSeekBar = findViewById(R.id.filter_level);

        btnComposers.setEnabled(false);
        btnSticker.setEnabled(false);
        enableAITracking.setEnabled(false);
        colorLevelSeekBar.setEnabled(false);
        filterLevelSeekBar.setEnabled(false);

        colorLevelSeekBar.setMax(20);
        colorLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    File composerDir = new File(getExternalFilesDir("assets"),
                            "Resource/graphics/face_beautification.bundle");

                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("obj_handle", composerDir);
                        jsonObject.put("name", "filter_name");
                        jsonObject.put("value", "ziran2");
                        setExtensionProperty("fuItemSetParam", jsonObject.toString());
                    }

                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("obj_handle", composerDir);
                        jsonObject.put("name", "color_level");
                        jsonObject.put("value", progress / 10.0);
                        setExtensionProperty("fuItemSetParam", jsonObject.toString());
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });


        filterLevelSeekBar.setMax(10);
        filterLevelSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                try {
                    File composerDir = new File(getExternalFilesDir("assets"),
                            "Resource/graphics/face_beautification.bundle");

                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("obj_handle", composerDir);
                        jsonObject.put("name", "filter_name");
                        jsonObject.put("value", "ziran2");
                        setExtensionProperty("fuItemSetParam", jsonObject.toString());
                    }

                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("obj_handle", composerDir);
                        jsonObject.put("name", "filter_level");
                        jsonObject.put("value", progress / 10.0);
                        setExtensionProperty("fuItemSetParam", jsonObject.toString());
                    }
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
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
    }

    private void disableAITracking() {
        faces = 0;
        hands = 0;
        people = 0;
        tvAITracking.setVisibility(View.GONE);
        enableAITracking.setText("enableAITracking");
        updateAITrackingResult(0, 0, 0);

        try {
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enable", false);
                setExtensionProperty("fuIsTracking", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enable", false);
                setExtensionProperty("fuHumanProcessorGetNumResults", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enable", false);
                setExtensionProperty("fuHandDetectorGetResultNumHands", jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void loadAIModels() {
        try {
            // Load AI model
            File modelDir = new File(getExternalFilesDir("assets"),
                    "Resource/model/ai_face_processor.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 10);

            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            // Load AI model
            File modelDir = new File(getExternalFilesDir("assets"),
                    "Resource/model/ai_hand_processor.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 3);
            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            // Load AI model
            File modelDir = new File(getExternalFilesDir("assets"),
                    "Resource/model/ai_human_processor_pc.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 19);
            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            File modelDir = new File(getExternalFilesDir("assets"),
                    "Resource/graphics/aitype.bundle");

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("data", modelDir.getAbsolutePath());
                setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("obj_handle", modelDir);
                jsonObject.put("name", "aitype");
                jsonObject.put("value", 1 << 10 | 1 << 21 | 1 << 3);
                setExtensionProperty("fuItemSetParam", jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void enableAITracking() {
        tvAITracking.setVisibility(View.VISIBLE);
        enableAITracking.setText("disableAITracking");

        try {
            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("n", 5);
                setExtensionProperty("fuSetMaxFaces", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enable", true);
                setExtensionProperty("fuIsTracking", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enable", true);
                setExtensionProperty("fuHumanProcessorGetNumResults", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("max_humans", 5);
                setExtensionProperty("fuHumanProcessorSetMaxHumans", jsonObject.toString());
            }

            {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("enable", true);
                setExtensionProperty("fuHandDetectorGetResultNumHands", jsonObject.toString());
            }
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void updateAITrackingResult(int faces, int hands, int people) {
        runOnUiThread(() -> {
            String result = "faces: " + faces + "\nhands: " + hands + "\npeople: " + people;
            tvAITracking.setText(result);
        });
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("FaceUnity", "Effect", key, property);
    }

    private void choiceComposer() {
        File composerDir = new File(getExternalFilesDir("assets"),
                "Resource/graphics/face_beautification.bundle");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", composerDir.getAbsolutePath());
            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());

            btnComposers.setEnabled(false);
            colorLevelSeekBar.setEnabled(true);
            filterLevelSeekBar.setEnabled(true);
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
    }

    private void choiceSticker() {
        File stickerDir =
                new File(getExternalFilesDir("assets"),
                        "Resource/items/ItemSticker/CatSparks.bundle");
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", stickerDir.getAbsolutePath());
            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());

            btnSticker.setEnabled(false);
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
                String assetsName = "Resource";
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
        config.mAppId = Config.mAppId;
        config.mExtensionObserver = this;
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
    }

    private void enableExtension(boolean enabled) {
        ExtensionManager.getInstance(mRtcEngine).initialize();
        mRtcEngine.enableExtension("FaceUnity", "Effect", enabled);
    }

    @Override
    public void onEvent(String vendor, String extension, String key, String value) {
        Log.d(TAG, "onEvent vendor: " + vendor + "  extension: " + extension + "  key: " + key + "  value: " + value);

        try {
            JSONObject jsonObject = new JSONObject(value);
            if ("fuIsTracking".equals(key)) {
                faces = jsonObject.getInt("faces");
            } else if ("fuHandDetectorGetResultNumHands".equals(key)) {
                hands = jsonObject.getInt("hands");
            } else if ("fuHumanProcessorGetNumResults".equals(key)) {
                people = jsonObject.getInt("people");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateAITrackingResult(faces, hands, people);
    }

    @Override
    public void onStarted(String s, String s1) {
        initExtension();
        loadAIModels();
        runOnUiThread(() -> {
            btnSticker.setEnabled(true);
            btnComposers.setEnabled(true);
            enableAITracking.setEnabled(true);
            VideoCanvas canvas = new VideoCanvas(findViewById(R.id.surfaceView));
            mRtcEngine.setupLocalVideo(canvas);
        });
    }

    @Override
    public void onStopped(String s, String s1) {
        Log.e(TAG, "onStopped: " + s + ", s1: " + s1);
    }

    @Override
    public void onError(String s, String s1, int i, String s2) {

    }
}
