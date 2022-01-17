package io.agora.rte.extension.bytedance.example;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import io.agora.rtc2.Constants;
import io.agora.rtc2.IMediaExtensionObserver;
import io.agora.rtc2.IRtcEngineEventHandler;
import io.agora.rtc2.RtcEngine;
import io.agora.rtc2.RtcEngineConfig;
import io.agora.rtc2.video.VideoCanvas;
import io.agora.rte.extension.bytedance.ExtensionManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class MainActivity
    extends AppCompatActivity implements IMediaExtensionObserver {
  static {
    System.loadLibrary("effect");
  }

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
        .setOnClickListener(view -> choiceEffect());
  }

  private void initExtension() {
    File LICENSE_PATH = new File(
        getExternalFilesDir("assets"),
        "byte_dance/LicenseBag.bundle/" + io.agora.rte.extension.bytedance.example.Constants.mLicenseName);
    // Check license
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("licensePath", LICENSE_PATH.getPath());
      setExtensionProperty("bef_effect_ai_check_license", jsonObject.toString());
    } catch (JSONException var3) {
      Log.e(TAG, var3.toString());
    }

    File MODEL_DIR = new File(getExternalFilesDir("assets"),
                              "byte_dance/ModelResource.bundle");

    // Init
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("strModelDir", MODEL_DIR.getPath());
      jsonObject.put("deviceName", "");
      setExtensionProperty("bef_effect_ai_init", jsonObject.toString());
    } catch (JSONException var3) {
      Log.e(TAG, var3.toString());
    }

    // Enable composer and sticker
    try {
      JSONObject jsonObject = new JSONObject();
      jsonObject.put("mode", 1);
      jsonObject.put("orderType", 0);
      setExtensionProperty("bef_effect_ai_composer_set_mode", jsonObject.toString());
    } catch (JSONException var3) {
      Log.e(TAG, var3.toString());
    }
  }

  private void setExtensionProperty(String key, String property) {
    mRtcEngine.setExtensionProperty("ByteDance", "Effect", key, property);
  }

  private void choiceComposer() {
    String[] composers =
        ResourceUtils.COMPOSERS.keySet().toArray(new String[] {});
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    String[] items = new String[ResourceUtils.COMPOSERS.size()];
    for (int i = 0; i < ResourceUtils.COMPOSERS.size(); i++) {
      items[i] = composers[i].split(ResourceUtils.COMPOSE_MAKEUP_BUNDLE)[1];
    }
    List<String> nodePaths = new ArrayList<>();
    builder.setTitle(R.string.set_composer_nodes)
        .setMultiChoiceItems(items, null,
                             (dialogInterface, i, b) -> {
                               if (b) {
                                 nodePaths.add(composers[i]);
                               } else {
                                 nodePaths.remove(composers[i]);
                               }
                             })
        .setNegativeButton("Cancel",
                           (dialogInterface, i) -> dialogInterface.dismiss())
        .setPositiveButton("Confirm",
                           (dialogInterface, i) -> {
                             // Composer set nodes
                             JSONArray jsonArray = new JSONArray();

                             for (String nodePath : nodePaths) {
                               jsonArray.put(nodePath);
                             }

                             setExtensionProperty("bef_effect_ai_composer_set_nodes", jsonArray.toString());

                             dialogInterface.dismiss();
                           })
        .create()
        .show();
  }

  private void choiceEffect() {
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    String[] items = new String[ResourceUtils.STICKERS.size()];
    for (int i = 0; i < ResourceUtils.STICKERS.size(); i++) {
      items[i] = ResourceUtils.STICKERS.get(i).split(
          ResourceUtils.STICKER_RESOURCE_BUNDLE)[1];
    }
    builder.setTitle(R.string.set_effect)
        .setSingleChoiceItems(items, -1,
                              (dialogInterface, i) -> {
                                // Set effect
                                try {
                                  JSONObject jsonObject = new JSONObject();
                                  jsonObject.put("strPath", ResourceUtils.STICKERS.get(i));
                                  setExtensionProperty("bef_effect_ai_set_effect", jsonObject.toString());
                                } catch (JSONException var3) {
                                  Log.e(TAG, var3.toString());
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
        super.run();
        String assetsName = "byte_dance";
        File destFile = getExternalFilesDir("assets");
        try {
          ResourceUtils.initResources(getAssets(), assetsName,
                                      destFile.getAbsolutePath());
          bundleLoaded.set(true);
          handler.post(dialog::dismiss);
        } catch (IOException e) { e.printStackTrace(); }
      }
    }.start();
  }

  private void initPermission() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      requestPermissions(new String[] {Manifest.permission.CAMERA,
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
      if (Arrays.equals(grantResults, new int[] {0, 0})) { initRtcEngine(); }
    }
  }

  private void initRtcEngine() {
    RtcEngineConfig config = new RtcEngineConfig();
    config.mContext = getApplicationContext();
    config.mAppId = io.agora.rte.extension.bytedance.example.Constants.mAppId;
    config.mEventHandler = new IRtcEngineEventHandler() {
      @Override
      public void onWarning(int warn) {
        super.onWarning(warn);
      }

      @Override
      public void onError(int err) {
        super.onError(err);
      }

      @Override
      public void onJoinChannelSuccess(String channel, int uid, int elapsed) {
        super.onJoinChannelSuccess(channel, uid, elapsed);
      }
    };
    try {
      mRtcEngine = RtcEngine.create(config);
    } catch (Exception e) {
      Log.e(TAG, e.toString());
    }
    if (mRtcEngine == null) { return; }
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
  public void onEvent(String s, String s1, String s2, String s3) {}
}
