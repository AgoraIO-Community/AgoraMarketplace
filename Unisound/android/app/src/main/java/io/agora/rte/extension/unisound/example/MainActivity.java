package io.agora.rte.extension.unisound.example;

import static io.agora.rte.extension.unisound.example.Config.mAppKey;
import static io.agora.rte.extension.unisound.example.Config.mAppSecret;
import static io.agora.rte.extension.unisound.example.Config.mEvalAppKey;

import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;


import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;

public class MainActivity
        extends AppCompatActivity implements IMediaExtensionObserver {

    static { System.loadLibrary("AgoraUnisoundExtension"); }

    private static final String TAG = "MainActivity";

    private RtcEngine mRtcEngine;

    private Button button;
    private TextView mAsrResultTv;
    private Button buttonInit;
    private Button buttonComposers;
    private Button buttonStickers;
    private final ObservableBoolean enableExtension =
            new ObservableBoolean(false);

    private int mUid = 0;

    private String resultStr = "";

    private boolean isStartedAsr = false;
    private boolean isStaredEval = false;

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
                                buttonComposers.setEnabled(true);
                                buttonStickers.setEnabled(true);
                            } else {
                                button.setText(R.string.enable_extension);
                                buttonComposers.setEnabled(false);
                                buttonStickers.setEnabled(false);
                            }
                        }
                    }
                });
    }

    private void initUI() {
        mAsrResultTv = findViewById(R.id.asr_result);
        button = findViewById(R.id.button_enable);
        button.setOnClickListener(
                view -> enableExtension.set(!enableExtension.get()));
        buttonComposers = findViewById(R.id.button_composers);

        buttonComposers.setOnClickListener(view -> choiceComposer());
        buttonStickers = findViewById(R.id.button_stickers);
        buttonStickers
                .setOnClickListener(view -> choiceSticker());
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("Unisound", "ASR_EVAL", key, property);
    }

    private void choiceComposer() {
        if (!isStartedAsr) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appkey", mAppKey);
                jsonObject.put("secret", mAppSecret);
                setExtensionProperty("init_asr", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("user_id", mUid);
                setExtensionProperty("start_asr", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }
        } else {
            setExtensionProperty("stop_asr", "{}");
        }

        isStartedAsr = !isStartedAsr;
        buttonComposers.setText(isStartedAsr ? "Stop ASR" : "Start ASR");
        buttonStickers.setEnabled(!isStartedAsr);
    }

    private void choiceSticker() {
        if (!isStaredEval) {
            setExtensionProperty("init_eval", "{}");

            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("appkey", mEvalAppKey);
                jsonObject.put("userID", String.valueOf(mUid));
                jsonObject.put("mode", "word");
                jsonObject.put("displayText", "Hello world");
                jsonObject.put("audioFormat", "pcm");
                jsonObject.put("eof", "end");
                setExtensionProperty("start_eval", jsonObject.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            setExtensionProperty("stop_eval", "{}");
        }

        isStaredEval = !isStaredEval;
        buttonStickers.setText(isStaredEval ? "Stop EVAL" : "Start EVAL");
        buttonComposers.setEnabled(!isStaredEval);
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

                mUid = uid;
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
        mRtcEngine.enableAudio();
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.joinChannel("", "testapi", null, 0);
    }

    private void enableExtension(boolean enabled) {
        mRtcEngine.enableExtension("Unisound", "ASR_EVAL", enabled);
    }

    @Override
    public void onEvent(String vendor, String extension, String key, String value) {
        Log.i(TAG, "onEvent vendor: " + vendor + "  extension: " + extension + "  key: " + key + "  value: " + value);
        final StringBuilder sb = new StringBuilder(resultStr);

        if ("asr_result".equals(key)) {
            try {
                JSONObject asrJSONObject = new JSONObject(value);
                String type = asrJSONObject.getString("type");
                if ("variable".equals(type)) {
                    sb.append(asrJSONObject.getString("text"));
                } else if ("fixed".equals(type)) {
                    sb.append(asrJSONObject.getString("text"));
                    resultStr += asrJSONObject.getString("text");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            sb.append(value);
        }

        this.runOnUiThread(() -> mAsrResultTv.setText(sb.toString()));
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
