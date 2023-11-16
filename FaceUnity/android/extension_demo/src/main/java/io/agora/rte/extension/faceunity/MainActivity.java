package io.agora.rte.extension.faceunity;


import static io.agora.rtc2.Constants.VIDEO_SOURCE_CAMERA_PRIMARY;

import android.Manifest;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.util.Base64;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatSeekBar;
import androidx.databinding.Observable;
import androidx.databinding.ObservableBoolean;

import org.apache.commons.lang3.tuple.ImmutableTriple;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;

import org.apache.commons.lang3.tuple.Triple;

public class MainActivity
        extends AppCompatActivity implements IMediaExtensionObserver {
    static {
        System.loadLibrary("AgoraFaceUnityExtension");
    }
    private static final String TAG = "MainActivity";

    private boolean enableLightMarkup = false;
    private boolean enableSticker = false;

    private RtcEngine mRtcEngine;

    private Button btnLightMarkup;
    private Button button;
    private Button btnComposers;
    private Button btnSticker;
    private TextView tvAITracking;
    private TextView textViewMarkup;
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

    private boolean needReload = true;

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

    static int idx = 1;
    static int curCombineMarkupIdx = 0;
    static String curCombineMarkupPath = "";
    private void initUI() {
        textViewMarkup = findViewById(R.id.markup_textview_id);

        Button btnMarkupCombine = findViewById(R.id.btn_makeup_combine_id);
        btnMarkupCombine.setOnClickListener(view -> {
            File makeupDir = new File(getExternalFilesDir("assets"),
                    "Resource/graphics/face_makeup.bundle");

            String[] combineMarkupNames = {
                    "ailing.bundle",
                    "chaomo.bundle",
                    "chuju.bundle",
                    "chuqiu.bundle",
                    "danyan.bundle",
                    "diadiatu.bundle",
                    "dongling.bundle",
                    "gangfeng.bundle",
                    "guofeng.bundle",
                    "hanguoxuemei.bundle",
                    "hongfeng.bundle",
                    "hunxue.bundle",
                    "jianling.bundle",
                    "jingdian.bundle",
                    "linjia.bundle",
                    "nanshen.bundle",
                    "nuandong.bundle",
                    "nvshen.bundle",
                    "oumei.bundle",
                    "qianzhihe.bundle",
                    "qizhi.bundle",
                    "renyu.bundle",
                    "rose.bundle",
                    "shaonv.bundle",
                    "tianmei.bundle",
                    "wumei.bundle",
                    "xinggan.bundle",
                    "xuejie.bundle",
                    "yanshimao.bundle",
                    "yuansheng.bundle",
                    "zhigan.bundle",
                    "zhiya.bundle",
                    "ziyun.bundle",
            };

            int curIdx = curCombineMarkupIdx++ % combineMarkupNames.length;

            File combinationmakeupItemDir = new File(getExternalFilesDir("assets"),
                    "Resource/makeup/combination_bundle/" + combineMarkupNames[curIdx]);

            {
                try {

                    {
                        if(curCombineMarkupPath.isEmpty()) {
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("data", makeupDir.getAbsolutePath());
                            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());
                        }
                    }

                    {
                        if(!curCombineMarkupPath.isEmpty()) {
                            // remove preload
                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", makeupDir.getAbsolutePath());
                            setExtensionProperty("fuUnbindAllItems", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("item", curCombineMarkupPath);
                            setExtensionProperty("fuDestroyItem", jsonObject.toString());
                        }

                        curCombineMarkupPath = combinationmakeupItemDir.getAbsolutePath();
                    }

                    {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("data", combinationmakeupItemDir.getAbsolutePath());
                        setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());

                        jsonObject = new JSONObject();
                        JSONArray jsonArray = new JSONArray();
                        jsonArray.put(combinationmakeupItemDir.getAbsolutePath());
                        jsonObject.put("obj_handle", makeupDir.getAbsolutePath());
                        jsonObject.put("p_items", jsonArray);
                        setExtensionProperty("fuBindItems", jsonObject.toString());

                        jsonObject = new JSONObject();
                        jsonObject.put("obj_handle", makeupDir.getAbsolutePath());
                        jsonObject.put("name", "makeup_intensity");
                        jsonObject.put("value", 0.8);
                        setExtensionProperty("fuItemSetParam", jsonObject.toString());
                    }

                    textViewMarkup.setVisibility(View.VISIBLE);
                    textViewMarkup.setText(Integer.toString(curIdx) + "-" + combineMarkupNames[curIdx]);

                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }
        });

        //
        button = findViewById(R.id.button_enable);
        tvAITracking = findViewById(R.id.tv_ai_tracking);
        enableAITracking = findViewById(R.id.button_enable_ai);
        button.setOnClickListener(
                view -> enableExtension.set(!enableExtension.get()));

        btnComposers = findViewById(R.id.button_composers);
        btnComposers.setOnClickListener(view -> choiceComposer());

        btnLightMarkup = findViewById(R.id.btn_lightmarkup);
        btnLightMarkup.setOnClickListener(view -> lightMarkup());

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

            //_fuSetLogLevel
            /*
            typedef enum FULOGLEVEL {
                FU_LOG_LEVEL_TRACE = 0,
                FU_LOG_LEVEL_DEBUG = 1,
                FU_LOG_LEVEL_INFO = 2,
                FU_LOG_LEVEL_WARN = 3,
                FU_LOG_LEVEL_ERROR = 4,
                FU_LOG_LEVEL_CRITICAL = 5,
                FU_LOG_LEVEL_OFF = 6
            } FULOGLEVEL;
            */
            jsonObject = new JSONObject();
            jsonObject.put("level", 6);
            setExtensionProperty("fuSetLogLevel", jsonObject.toString());
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
        updateAITrackingResult(0, 0, 0, 0);

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
            jsonObject.put("type", 1 << 8);

            setExtensionProperty("fuLoadAIModelFromPackage", jsonObject.toString());

            jsonObject = new JSONObject();
            jsonObject.put("n", 5);
            setExtensionProperty("fuSetMaxFaces", jsonObject.toString());

            jsonObject = new JSONObject();
            jsonObject.put("enable", true);
            setExtensionProperty("fuIsTracking", jsonObject.toString());

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
                    "Resource/model/ai_human_processor.bundle");

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("data", modelDir.getAbsolutePath());
            jsonObject.put("type", 1 << 9);
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
                jsonObject.put("value", 1 << 8 | 16777216 | 1 << 4 | 1 << 9 | 1 << 30 | 1 << 3);
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
                jsonObject.put("n", 1);
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

    private void updateAITrackingResult(int faces, int hands, int people, int motion) {
        runOnUiThread(() -> {
            String result = "faces: " + faces + "\nhands: " + hands + "\npeople: " + people + "\nmotion: " + motion;
            tvAITracking.setText(result);
        });
    }

    private void setExtensionProperty(String key, String property) {
        mRtcEngine.setExtensionProperty("FaceUnity", "Effect", key, property);
    }

    private JSONArray loadLipstickJSON(File file) {
        try {
            JSONArray ret = new JSONArray();
            JsonReader reader = new JsonReader(new InputStreamReader(new FileInputStream(file)));
            reader.beginObject();

            while (reader.hasNext()) {
                String name = reader.nextName();
                if(name.equals("rgba")) {
                    reader.beginArray();
                    while (reader.hasNext())
                        ret.put(reader.nextDouble());
                    reader.endArray();;
                }
            }
            reader.endObject();

            return ret;
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }
    private Triple<String, Integer, Integer> loadBitmapToBase64(File file) {
        try {
            FileInputStream inputStream = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);

            int width = bitmap.getWidth();
            int height = bitmap.getHeight();

            int size = bitmap.getRowBytes() * bitmap.getHeight();
            ByteBuffer byteBuffer = ByteBuffer.allocate(size);
            bitmap.copyPixelsToBuffer(byteBuffer);
            byte[] bytes = byteBuffer.array();

            return new ImmutableTriple<>(
                    Base64.encodeToString(bytes, Base64.NO_WRAP),
                    width, height);

        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
    private void lightMarkup() {
        File lightMarkupDir = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/light_makeup.bundle");

        File blusherDir = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/blusher/mu_blush_02.png");
        File eyebrowDir = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/eyebrow/mu_eyebrow_02.png");
        File eyeShadow = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/eyeshadow/mu_eyeshadow_02.png");
        File eyeLiner = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/eyeliner/mu_eyeliner_04.png");
        File eyelash = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/eyelash/mu_eyelash_03.png");
        File lipstickDir = new File(getExternalFilesDir("assets"),
                "Resource/light_makeup/lipstick/mu_lip_01.json");

        File[] texLoads = {
                blusherDir,
                eyebrowDir,
                eyeShadow,
                eyeLiner,
                eyelash};

        String[] texNames = {
                "tex_blusher",
                "tex_brow",
                "tex_eye",
                "tex_eyeLiner",
                "tex_eyeLash"};

        enableLightMarkup = !enableLightMarkup;

        try {
            if(enableLightMarkup) {
                //桃花妆 90
                // mu_lip_01 90
                // mu_blush_01 90
                // mu_eyebrow_01 50
                // mu_eyeshadow_01

                ProgressDialog dialog =
                        ProgressDialog.show(this, "", "Loading lightMarkup stuff. Please wait...", true);
                new Thread() {
                    @Override
                    public void run() {
                        runOnUiThread(() -> {
                            btnLightMarkup.setEnabled(false);
                        });

                        try {
                            final double makeup_intensity_lip = 0.9;
                            final double makeup_intensity_blusher = 0.9;
                            final double makeup_intensity_eyeBrow = 1.0;
                            final double makeup_intensity_eye = 1.0;
                            final double makeup_intensity_eyeLiner = 1.0;
                            final double makeup_intensity_eyelash = 1.0;

                            final int is_use_fix = 1;

                            //off
                            final double makeup_intensity_pupil = 0;

                            JSONObject jsonObject = new JSONObject();
                            jsonObject.put("data", lightMarkupDir.getAbsolutePath());
                            setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());

                            //--
                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "is_makeup_on");
                            jsonObject.put("value", 1);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "is_use_fix");
                            jsonObject.put("value", is_use_fix);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity");
                            jsonObject.put("value", 1.0);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_eye");
                            jsonObject.put("value", makeup_intensity_eye);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_eyeBrow");
                            jsonObject.put("value", makeup_intensity_eyeBrow);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_lip");
                            jsonObject.put("value", makeup_intensity_lip);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_pupil");
                            jsonObject.put("value", makeup_intensity_pupil);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_eyeLiner");
                            jsonObject.put("value", makeup_intensity_eyeLiner);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_eyelash");
                            jsonObject.put("value", makeup_intensity_eyelash);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_intensity_blusher");
                            jsonObject.put("value", makeup_intensity_blusher);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_lip_mask");
                            jsonObject.put("value", 1.0);
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            jsonObject = new JSONObject();
                            jsonObject.put("obj_handle", lightMarkupDir);
                            jsonObject.put("name", "makeup_lip_color");
                            jsonObject.put("value", loadLipstickJSON(lipstickDir));
                            setExtensionProperty("fuItemSetParam", jsonObject.toString());

                            //---
                            for (int i = 0; i < texLoads.length; i++) {
                                File currentFile = texLoads[i];
                                String currentName = texNames[i];
                                jsonObject = new JSONObject();
                                jsonObject.put("item", lightMarkupDir.getAbsolutePath());
                                jsonObject.put("name", currentName);

                                Triple<String, Integer, Integer> ret = loadBitmapToBase64(currentFile);
                                String base64_value = ret.getLeft();
                                jsonObject.put("value", base64_value);
                                jsonObject.put("width", ret.getMiddle());
                                jsonObject.put("height", ret.getRight());
                                setExtensionProperty("fuCreateTexForItem", jsonObject.toString());
                            }

                            runOnUiThread(() -> {
                                btnLightMarkup.setEnabled(true);
                                btnLightMarkup.setText(R.string.disable_lightmarkup);
                            });

                            handler.post(dialog::dismiss);
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                    }
                }.start();
            }
            else {
                JSONObject jsonObject = new JSONObject();

                for (int i = 0; i < texLoads.length; i++) {
                    String currentName = texNames[i];

                    jsonObject.put("item", lightMarkupDir.getAbsolutePath());
                    jsonObject.put("name", currentName);

                    setExtensionProperty("fuDeleteTexForItem", jsonObject.toString());
                }

                jsonObject.put("item", lightMarkupDir.getAbsolutePath());
                setExtensionProperty("fuDestroyItem", jsonObject.toString());

                btnLightMarkup.setText(R.string.enable_lightmarkup);
            }

        } catch (JSONException e) {
            Log.e(TAG, e.toString());
        }
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
        int count = 1;
        for(int i = 0; i < count; ++i) {
            File stickerDir =
                    new File(getExternalFilesDir("assets"),
                            "Resource/effect/normal/cat_sparks.bundle");

            enableSticker = !enableSticker;

            if(enableSticker) {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("data", stickerDir.getAbsolutePath());
                    setExtensionProperty("fuCreateItemFromPackage", jsonObject.toString());

                    btnSticker.setText("disableSticker");
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }
            else {
                try {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("item", stickerDir.getAbsolutePath());
                    setExtensionProperty("fuDestroyItem", jsonObject.toString());

                    btnSticker.setText("setSticker");
                } catch (JSONException e) {
                    Log.e(TAG, e.toString());
                }
            }
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
//            @Override
//            public void onWarning(int warn) {
//                Log.w(TAG, String.format("onWarning %d", warn));
//            }

            @Override
            public void onError(int err) {
                Log.e(TAG, String.format("onError %d", err));
            }

            @Override
            public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
                Log.i(TAG, String.format("onJoinChannelSuccess %s %d %d", channel, uid, elapsed));
            }

            @Override
            public void onLocalVideoStateChanged(Constants.VideoSourceType source, int state, int error) {
                super.onLocalVideoStateChanged(source, state, error);
                Log.i(TAG, String.format("onLocalVideoStateChanged %s %d %d", source.toString(), state, error));

                if(source.getValue() == VIDEO_SOURCE_CAMERA_PRIMARY && state == 3 && error == 4) {
                    needReload = true;
                }

                if(source.getValue() == VIDEO_SOURCE_CAMERA_PRIMARY && state == 1 && error == 0) {
                    if(needReload) {
                        runOnUiThread(() -> {
                            loadAIModels();
                            enableExtension(enableExtension.get());
                            needReload = false;
                        });
                    }
                }
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

        initExtension();
        //loadAIModels();
        runOnUiThread(() -> {
            btnSticker.setEnabled(true);
            btnComposers.setEnabled(true);
            enableAITracking.setEnabled(true);
            VideoCanvas canvas = new VideoCanvas(findViewById(R.id.surfaceView));
            mRtcEngine.setupLocalVideo(canvas);
        });
    }

    private void enableExtension(boolean enabled) {
        //ExtensionManager.getInstance(mRtcEngine).initialize();
        int ret = mRtcEngine.enableExtension("FaceUnity", "Effect", enabled);
        Log.i(TAG, "mRtcEngine.enableExtension ret: " + ret + ", enabled: " + enabled);
    }

    @Override
    public void onEvent(String vendor, String extension, String key, String value) {
        Log.d(TAG, "onEvent vendor: " + vendor + "  extension: " + extension + "  key: " + key + "  value: " + value);
        int motion = 0;
        try {
            JSONObject jsonObject = new JSONObject(value);
            if ("fuIsTracking".equals(key)) {
                faces = jsonObject.getInt("faces");

                /**
                 typedef enum FUAIEXPRESSIONTYPE {
                 FUAIEXPRESSION_UNKNOWN = 0,
                 FUAIEXPRESSION_BROW_UP = 1 << 1,
                 FUAIEXPRESSION_BROW_FROWN = 1 << 2,
                 FUAIEXPRESSION_LEFT_EYE_CLOSE = 1 << 3,
                 FUAIEXPRESSION_RIGHT_EYE_CLOSE = 1 << 4,
                 FUAIEXPRESSION_EYE_WIDE = 1 << 5,
                 FUAIEXPRESSION_MOUTH_SMILE_LEFT = 1 << 6,
                 FUAIEXPRESSION_MOUTH_SMILE_RIGHT = 1 << 7,
                 FUAIEXPRESSION_MOUTH_FUNNEL = 1 << 8,
                 FUAIEXPRESSION_MOUTH_OPEN = 1 << 9,
                 FUAIEXPRESSION_MOUTH_PUCKER = 1 << 10,
                 FUAIEXPRESSION_MOUTH_ROLL = 1 << 11,
                 FUAIEXPRESSION_MOUTH_PUFF = 1 << 12,
                 FUAIEXPRESSION_MOUTH_SMILE = 1 << 13,
                 FUAIEXPRESSION_MOUTH_FROWN = 1 << 14,
                 FUAIEXPRESSION_HEAD_LEFT = 1 << 15,
                 FUAIEXPRESSION_HEAD_RIGHT = 1 << 16,
                 FUAIEXPRESSION_HEAD_NOD = 1 << 17,
                 } FUAIEXPRESSIONTYPE;
                 */

                if(jsonObject.has("expression_type")) {
                    motion = (int) (Math.log(jsonObject.getInt("expression_type")) / Math.log(2));

                    if(motion > 17 || motion < 0)
                        motion = 0;
                }


            } else if ("fuHandDetectorGetResultNumHands".equals(key)) {
                hands = jsonObject.getInt("hands");
            } else if ("fuHumanProcessorGetNumResults".equals(key)) {
                people = jsonObject.getInt("people");
            }
            else {
                Log.i(TAG, "test test test");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        updateAITrackingResult(faces, hands, people, motion);
    }

    @Override
    public void onStarted(String s, String s1) {
        Log.i(TAG, String.format("PeterPeterPeter: Extension OnStarted: (%s - %s)", s, s1));
    }

    @Override
    public void onStopped(String s, String s1) {
        Log.e(TAG, "onStopped: " + s + ", s1: " + s1);
    }

    @Override
    public void onError(String s, String s1, int i, String s2) {

    }

    @Override
    public void onBackPressed() {
       //disable
    }
}




