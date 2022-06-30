package io.agora.rte.extension.sensetime.example

import android.content.Context
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.sensetime.stmobile.STMobileEffectNative
import com.sensetime.stmobile.STMobileHumanActionNative
import com.sensetime.stmobile.params.STEffectBeautyType
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rte.extension.sensetime.ExtensionManager
import junit.framework.Assert.assertEquals
import org.json.JSONObject
import org.junit.*
import org.junit.runner.RunWith
import java.io.File


@RunWith(AndroidJUnit4::class)
class ExtensionPropertySmokeTest {

    companion object {
        private lateinit var rtcEngine: RtcEngine
        private lateinit var appContext: Context

        @BeforeClass
        @JvmStatic
        fun setUpAll() {
            ActivityScenario.launch(MainActivity::class.java)
            appContext = ApplicationProvider.getApplicationContext()

            // Ensure the makeup_lip/license/models directories been copied
            FileUtils.copyStickerFiles(appContext, "Resource/makeup_lip")
            FileUtils.copyStickerFiles(appContext, "Resource/license")
            FileUtils.copyFileIfNeed(appContext, Constants.mLicenseName, "Resource/license")
            FileUtils.copyModelsFiles(appContext, "Resource/models")

            val config = RtcEngineConfig()
            config.mContext = appContext
            config.mAppId = Constants.mAppId
            config.mEventHandler = object : IRtcEngineEventHandler() {
                override fun onWarning(warn: Int) {
                }

                override fun onError(err: Int) {
                }

                override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {

                }
            }
            rtcEngine = RtcEngine.create(config)

            ExtensionManager.getInstance(rtcEngine).initialize(appContext)
            rtcEngine.enableExtension("SenseTime", "Effect", true)
        }

        @AfterClass
        @JvmStatic
        fun teardownAll() {
            RtcEngine.destroy()
        }
    }

    @Test
    fun st_mobile_check_activecode_success() {
        val licensePath: File = File(
            appContext.getExternalFilesDir(null),
            "Resource/license/" + Constants.mLicenseName
        )
        val jsonObject = JSONObject()
        jsonObject.put("licensePath", licensePath.path)
        val ret = rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_check_activecode", jsonObject.toString())
        assertEquals(ret, 0)
    }

    @Test
    fun st_mobile_human_action_create_success() {
        val licensePath: File = File(
            appContext.getExternalFilesDir(null),
            "Resource/license/" + Constants.mLicenseName
        )
        val jsonObject = JSONObject()
        jsonObject.put("licensePath", licensePath.path)
        rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_check_activecode", jsonObject.toString())

        val modelsPath: File = File(
            appContext.getExternalFilesDir(null),
            "Resource/models/M_SenseME_Face_Extra_Advanced_6.0.13.model"
        )
        val humanActionCreateJson = JSONObject()
        humanActionCreateJson.put("model_path", modelsPath.path)
        humanActionCreateJson.put(
            "config",
            STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE
        )
        val ret = rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_human_action_create", humanActionCreateJson.toString())
        assertEquals(ret, 0)
    }

    @Test
    fun st_mobile_effect_create_handle_success() {
        val licensePath: File = File(
            appContext.getExternalFilesDir(null),
            "Resource/license/" + Constants.mLicenseName
        )
        val jsonObject = JSONObject()
        jsonObject.put("licensePath", licensePath.path)
        rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_check_activecode", jsonObject.toString())

        val modelsPath: File = File(
            appContext.getExternalFilesDir(null),
            "Resource/models/M_SenseME_Face_Extra_Advanced_6.0.13.model"
        )
        val humanActionCreateJson = JSONObject()
        humanActionCreateJson.put("model_path", modelsPath.path)
        humanActionCreateJson.put(
            "config",
            STMobileHumanActionNative.ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE
        )
        rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_human_action_create", humanActionCreateJson.toString())

        val effectCreateHandleJson = JSONObject()
        effectCreateHandleJson.put("config", STMobileEffectNative.EFFECT_CONFIG_IMAGE_MODE)
        val ret = rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_effect_create_handle", humanActionCreateJson.toString())
        assertEquals(ret, 0)
    }

    @Test
    fun st_mobile_effect_set_beauty_strength_success() {
        val jsonObject = JSONObject()
        jsonObject.put("param", STEffectBeautyType.EFFECT_BEAUTY_PLASTIC_SHRINK_GODDESS_FACE)
        jsonObject.put("val", java.lang.Double.valueOf(0.8).toDouble())
        val ret = rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_effect_set_beauty_strength", jsonObject.toString())
        assertEquals(ret, 0)
    }

    @Test
    fun st_mobile_effect_set_beauty_success() {
        val makeupLipPath: File = File(
            appContext.getExternalFilesDir(null),
            "Resource/makeup_lip/12自然.zip"
        )
        val jsonObject = JSONObject()
        jsonObject.put("param", STEffectBeautyType.EFFECT_BEAUTY_MAKEUP_LIP)
        jsonObject.put("path", makeupLipPath.path)
        val ret = rtcEngine.setExtensionProperty("SenseTime", "Effect", "st_mobile_effect_set_beauty", jsonObject.toString())
        assertEquals(ret, 0)
    }
}