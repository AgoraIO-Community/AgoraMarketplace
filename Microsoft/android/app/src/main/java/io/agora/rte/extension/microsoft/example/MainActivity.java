package io.agora.rte.extension.microsoft.example;

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

import org.json.JSONArray;
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

    private static final String TAG = "MainActivity";

    private RtcEngine mRtcEngine;

    private Button button;
    private TextView mAsrResultTv;
    private TextView mRecognizedResultTv;
    private Button buttonComposers;
    private Button buttonTranslate;
    private final ObservableBoolean enableExtension =
            new ObservableBoolean(false);

    private String resultStr = "";

    private boolean isStartedAsr = false;

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
                                buttonTranslate.setEnabled(true);
                            } else {
                                button.setText(R.string.enable_extension);
                                buttonComposers.setEnabled(false);
                                buttonTranslate.setEnabled(false);
                            }
                        }
                    }
                });
    }

    private void initUI() {
        mAsrResultTv = findViewById(R.id.recognizing_result);
        mRecognizedResultTv = findViewById(R.id.recognized_result);
        button = findViewById(R.id.button_enable);
        button.setOnClickListener(
                view -> enableExtension.set(!enableExtension.get()));
        buttonComposers = findViewById(R.id.button_stt);
        buttonComposers.setOnClickListener(view -> choiceComposer());
        buttonTranslate = findViewById(R.id.button_translate);
        buttonTranslate.setOnClickListener(view -> translatorSwitch());
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("Microsoft", "Speech_Recognition", key, property);
    }

    private void choiceComposer() {
        if (!isStartedAsr) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("subscription", Config.mSubscription);
                jsonObject.put("region", Config.mRegion);

                JSONArray jsonArray = new JSONArray();
                // "en-US", "de-DE", "zh-CN"
                jsonArray.put("en-US");
                jsonArray.put("zh-CN");
                jsonObject.put("source_languages", jsonArray);

                jsonObject.put("enable_auto_detect", true);
                setExtensionProperty("init_speech_recognition", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            setExtensionProperty("start_continuous_recognition_async", "{}");
        } else {
            setExtensionProperty("stop_continuous_recognition_async", "{}");
        }

        isStartedAsr = !isStartedAsr;
        buttonComposers.setText(isStartedAsr ? "Stop STT" : "Start STT");
    }

    private void translatorSwitch() {
        if (!isStartedAsr) {
            try {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("subscription", Config.mSubscription);
                jsonObject.put("region", Config.mRegion);

                JSONArray jsonArray = new JSONArray();
                // "en-US", "de-DE", "zh-CN"
                //jsonArray.put("en-US");
                jsonArray.put("zh-CN");
                jsonObject.put("source_languages", jsonArray);

                JSONArray jsonArray_target = new JSONArray();
                // "ja-JP"
                jsonArray_target.put("ja-JP");
                jsonArray_target.put("en-US");
                jsonArray_target.put("zh-Hant");
                jsonObject.put("target_languages", jsonArray_target);

                jsonObject.put("enable_auto_detect", false);
                setExtensionProperty("init_translate_recognition", jsonObject.toString());
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
            }

            setExtensionProperty("start_continuous_translate_async", "{}");
        } else {
            setExtensionProperty("stop_continuous_translate_async", "{}");
        }

        isStartedAsr = !isStartedAsr;
        buttonTranslate.setText(isStartedAsr ? "Stop Translating" : "Start Translating");
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
        config.addExtension("AgoraMicrosoftExtension");
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
        mRtcEngine.enableAudio();
        mRtcEngine.setChannelProfile(Constants.CHANNEL_PROFILE_LIVE_BROADCASTING);
        mRtcEngine.setClientRole(Constants.CLIENT_ROLE_BROADCASTER);
        mRtcEngine.joinChannel("", "hideonbush", null, 0);
    }

    private void enableExtension(boolean enabled) {
        mRtcEngine.enableExtension("Microsoft", "Speech_Recognition", enabled);
    }

    @Override
    public void onEvent(String vendor, String extension, String key, String value) {
        Log.i(TAG, "onEvent vendor: " + vendor + "  extension: " + extension + "  key: " + key + "  value: " + value);
        if ("speech_recognizing".equals(key)) {
            try {
                JSONObject asrJSONObject = new JSONObject(value);
                String text = "Recognizing STT Text：" + asrJSONObject.getString("text")+ "\n";
                this.runOnUiThread(() -> mAsrResultTv.setText(text));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if("speech_recognized".equals(key)) {
            try {
                JSONObject asrJSONObject = new JSONObject(value);
                String text = "STT Text：" + asrJSONObject.getString("text")+ "\n";
                resultStr += text;
                this.runOnUiThread(() -> mRecognizedResultTv.setText(resultStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if("translation_recognizing".equals(key)) {
            try {
                JSONObject asrJSONObject = new JSONObject(value);
                String text = "Recognizing Translation Origin Text：" + asrJSONObject.getString("text") + "\n";
                this.runOnUiThread(() -> mAsrResultTv.setText(text));
                return;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if ("translation_recognized".equals(key)) {
            try {
                JSONObject asrJSONObject = new JSONObject(value);
                String text = "Recognizing Translation Text：" + asrJSONObject.getString("translation") + "\n";
                resultStr += text;
                this.runOnUiThread(() -> mRecognizedResultTv.setText(resultStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else if ("translation_speech_start_detected".equals(key) || "translation_speech_end_detected".equals(key) || "speech_start_detected".equals(key) || "speech_end_detected".equals(key) ) {
            try {
                JSONObject asrJSONObject = new JSONObject(value);
                resultStr += key + "\n";
                this.runOnUiThread(() -> mRecognizedResultTv.setText(resultStr));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            resultStr += key + " : " + value + "\n";
            this.runOnUiThread(() -> mAsrResultTv.setText(key + " : " + value));
            this.runOnUiThread(() -> mRecognizedResultTv.setText(resultStr));
        }
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
