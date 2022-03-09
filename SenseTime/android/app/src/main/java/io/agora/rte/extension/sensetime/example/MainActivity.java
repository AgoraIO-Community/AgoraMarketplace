package io.agora.rte.extension.sensetime.example;

import android.Manifest;
import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;

import com.sensetime.stmobile.STMobileEffectNative;
import com.sensetime.stmobile.STMobileHumanActionNative;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rte.extension.sensetime.ExtensionManager;

public class MainActivity
        extends AppCompatActivity implements IMediaExtensionObserver {

    private static final String TAG = "MainActivity";

    private RtcEngine mRtcEngine;

    private Button button;
    private Button buttonInit;
    private Button buttonComposers;
    private Button buttonStickers;
    private final ObservableBoolean enableExtension =
            new ObservableBoolean(false);
    private final AtomicBoolean bundleLoaded = new AtomicBoolean(false);
    private final Handler handler = new Handler(Looper.getMainLooper());

    private int preSelectedBeauty = -1;

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
                                if (buttonInit.isEnabled()) {
                                    buttonComposers.setEnabled(false);
                                    buttonStickers.setEnabled(false);
                                } else {
                                    buttonComposers.setEnabled(true);
                                    buttonStickers.setEnabled(true);
                                }
                            } else {
                                button.setText(R.string.enable_extension);
                                buttonComposers.setEnabled(false);
                                buttonStickers.setEnabled(false);
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
        buttonInit = findViewById(R.id.button_init);
        buttonInit.setOnClickListener(view -> {
            initExtension();
            view.setEnabled(false);

            buttonComposers.setEnabled(true);
            buttonStickers.setEnabled(true);
        });
        buttonComposers = findViewById(R.id.button_composers);

        buttonComposers.setOnClickListener(view -> choiceComposer());
        buttonStickers = findViewById(R.id.button_stickers);
        buttonStickers
                .setOnClickListener(view -> choiceSticker());
    }

    private void initExtension() {
        File licensePath = new File(
                getExternalFilesDir(null),
                "license/" + io.agora.rte.extension.sensetime.example.Constants.mLicenseName);
        // Check license
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("license_path", licensePath.getPath());
            setExtensionProperty("st_mobile_check_activecode", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        try {
            File modelsPath = new File(
                    getExternalFilesDir(null),
                    "models/M_SenseME_Face_Extra_Advanced_6.0.13.model");
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("model_path", modelsPath.getPath());
            jsonObject.put("config", STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE);
            setExtensionProperty("st_mobile_human_action_create", jsonObject.toString());
        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }

        setExtensionProperty("st_mobile_effect_create_handle", "{}");
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("SenseTime", "Effect", key, property);
    }

    private void choiceComposer() {
        ArrayList<EffectType> beautyList = new ArrayList<>(Arrays.asList(
                EffectType.TYPE_BASIC_1,
                EffectType.TYPE_BASIC_2,
                EffectType.TYPE_BASIC_3,
                EffectType.TYPE_BASIC_4,
                EffectType.TYPE_BASIC_5,
                EffectType.TYPE_BASIC_6,
                EffectType.TYPE_MX_1,
                EffectType.TYPE_MX_HIGH_THIN_FACE,
                EffectType.TYPE_MX_2,
                EffectType.TYPE_MX_3,
                EffectType.TYPE_MX_4,
                EffectType.TYPE_MX_5,
                EffectType.TYPE_HIGH_BACK,
                EffectType.TYPE_HIGH_1,
                EffectType.TYPE_HIGH_2,
                EffectType.TYPE_HIGH_3,
                EffectType.TYPE_HIGH_4,
                EffectType.TYPE_THINNER_HEAD_1,
                EffectType.TYPE_THINNER_HEAD_2,
                EffectType.TYPE_WZH_2,
                EffectType.TYPE_WZH_3,
                EffectType.TYPE_WZH_4,
                EffectType.TYPE_WZH_JAW,
                EffectType.TYPE_WZH_5,
                EffectType.TYPE_WZH_6,
                EffectType.TYPE_WZH_7,
                EffectType.TYPE_WZH_8,
                EffectType.TYPE_WZH_9,
                EffectType.TYPE_WZH_10,
                EffectType.TYPE_WZH_11,
                EffectType.TYPE_WZH_12,
                EffectType.TYPE_WZH_13,
                EffectType.TYPE_WZH_14,
                EffectType.TYPE_WZH_15,
                EffectType.TYPE_WZH_16,
                EffectType.TYPE_WZH_17,
                EffectType.TYPE_WZH_18,
                EffectType.TYPE_WZH_19
        ));

        ArrayList<String> beautyNames = new ArrayList<>();
        for (EffectType type : beautyList) {
            beautyNames.add(type.getDesc());
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] items = beautyNames.toArray(new String[0]);
        builder.setTitle(R.string.set_sticker)
                .setSingleChoiceItems(items, -1,
                        (dialogInterface, i) -> {

                            // Clear previous beauty
                            if (preSelectedBeauty != -1) {
                                // st_mobile_effect_set_beauty_strength
                                try {
                                    JSONObject jsonObject = new JSONObject();
                                    jsonObject.put("param", preSelectedBeauty);
                                    jsonObject.put("val", Double.valueOf(0.0).doubleValue());
                                    setExtensionProperty("st_mobile_effect_set_beauty_strength", jsonObject.toString());
                                } catch (JSONException e) {
                                    Log.e(TAG, e.toString());
                                }
                            }

                            EffectType beauty = beautyList.get(i);
                            int param = beauty.getCode();

                            // st_mobile_effect_set_beauty_strength
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("param", param);
                                jsonObject.put("val", Double.valueOf(0.8).doubleValue());
                                setExtensionProperty("st_mobile_effect_set_beauty_strength", jsonObject.toString());
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                            }

                            preSelectedBeauty = param;

                            dialogInterface.dismiss();
                        })
                .setNegativeButton("Cancel",
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    private void choiceSticker() {
        final EnumMap<EffectType, List<MakeupItem>> makeupList =
                FileUtils.getMakeupListsLocal2(getApplicationContext());
        ArrayList<String> itemList = new ArrayList<>();

        for (Map.Entry<EffectType, List<MakeupItem>> entry : makeupList.entrySet()) {
            for (MakeupItem item : entry.getValue()) {
                itemList.add(item.path);
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String[] items = itemList.toArray(new String[0]);
        builder.setTitle(R.string.set_sticker)
                .setSingleChoiceItems(items, -1,
                        (dialogInterface, i) -> {
                            int param = -1;
                            String path = "";
                            for (Map.Entry<EffectType, List<MakeupItem>> entry : makeupList.entrySet()) {
                                for (MakeupItem item : entry.getValue()) {
                                    if (items[i].equals(item.path)) {
                                        param = entry.getKey().getCode();
                                        path = item.path;
                                        break;
                                    }
                                }
                            }

                            // st_mobile_effect_set_beauty
                            try {
                                JSONObject jsonObject = new JSONObject();
                                jsonObject.put("param", param);
                                jsonObject.put("path", path);
                                setExtensionProperty("st_mobile_effect_set_beauty", jsonObject.toString());
                            } catch (JSONException e) {
                                Log.e(TAG, e.toString());
                            }

                            dialogInterface.dismiss();
                        })
                .setNegativeButton("Cancel",
                        (dialogInterface, i) -> dialogInterface.dismiss())
                .create()
                .show();
    }

    private void initBundle() {
        ProgressDialog dialog =
                ProgressDialog.show(this, "", "Loading bundle. Please wait...", true);
        new Thread() {
            @Override
            public void run() {
                FileUtils.copyStickerFiles(getApplicationContext(), "license");
                FileUtils.copyFileIfNeed(getApplicationContext(), io.agora.rte.extension.sensetime.example.Constants.mLicenseName, "license");

                FileUtils.copyStickerFiles(getApplicationContext(), "license");
                FileUtils.copyStickerFiles(getApplicationContext(), "newEngine");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_eyeshadow");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_brow");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_blush");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_highlight");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_lip");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_eyeliner");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_eyelash");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_eyeball");
                FileUtils.copyStickerFiles(getApplicationContext(), "makeup_hairdye");
                FileUtils.copyStickerFiles(getApplicationContext(), "style_nature");
                FileUtils.copyStickerFiles(getApplicationContext(), "style_lightly");
                FileUtils.copyStickerFiles(getApplicationContext(), "style_fashion");
                FileUtils.copyStickerFiles(getApplicationContext(), "tryon_lip");
                FileUtils.copyStickerFiles(getApplicationContext(), "tryon_hair");

                FileUtils.copyModelsFiles(getApplicationContext(), "models");

                bundleLoaded.set(true);
                handler.post(dialog::dismiss);
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
        config.mAppId = io.agora.rte.extension.sensetime.example.Constants.mAppId;
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
        mRtcEngine.enableExtension("SenseTime", "Effect", enabled);
    }

    @Override
    public void onEvent(String s, String s1, String s2, String s3) {
    }

    @Override
    public void onStarted(String s, String s1) {

    }

    @Override
    public void onStopped(String s, String s1) {

    }

    @Override
    public void onError(String s, String s1, int i, String s2) {

    }
}
