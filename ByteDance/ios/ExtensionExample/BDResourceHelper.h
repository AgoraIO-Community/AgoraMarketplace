//
//  BDResourceHelper.h
//  AgoraWithByteDance
//
//  Created by LLF on 2020/10/10.
//

#import <Foundation/Foundation.h>

static NSString *const FACE_MODEL = @"/ttfacemodel/tt_face_v10.0.model";
static NSString *const FACE_MODEL_EXTRA =
    @"/ttfacemodel/tt_face_extra_v13.0.model";
static NSString *const FACE_ATTRIBUTE_MODEL =
    @"/ttfaceattrmodel/tt_beauty_attr6_v2.0.model";
static NSString *const FACE_VERIFY_MODEL =
    @"/ttfaceverify/tt_faceverify_v7.0.model";
static NSString *const SKELETON_MODEL =
    @"/skeleton_model/tt_skeleton_v7.0.model";
static NSString *const HAND_DET_MODEL = @"/handmodel/tt_hand_det_v11.0.model";
static NSString *const HAND_GESTURE_MODEL =
    @"/handmodel/tt_hand_gesture_v11.0.model";
static NSString *const HAND_SEG_MODEL = @"/handmodel/tt_hand_seg_v2.0.model";
static NSString *const HAND_KP_MODEL = @"/handmodel/tt_hand_kp_v6.0.model";
static NSString *const HAND_BOX_MODEL =
    @"/handmodel/tt_hand_box_reg_v12.0.model";
static NSString *const HAIR_PARSER_MODEL = @"/hairparser/tt_hair_v11.0.model";
static NSString *const PORTRAIT_MODEL = @"/mattingmodel/tt_matting_v14.0.model";
static NSString *const PET_FACE_MODEL = @"/ttpetface/tt_petface_v5.0.model";
static NSString *const HEADSEG_MODEL = @"/headseg/tt_headseg_v5.0.model";
static NSString *const LIGHTCLS_MODEL = @"/lightcls/tt_lightcls_v1.0.model";
static NSString *const HUMAN_DISTANCE_MODEL =
    @"/humandistance/tt_humandist_v1.0.model";
static NSString *const GENERAL_OBJECT_MODEL =
    @"/generalobjectmodel/tt_general_obj_detection_v1.0.model";
static NSString *const GENERAL_OBJECT_CLS_MODEL =
    @"/generalobjectmodel/tt_general_obj_detection_cls_v1.0.model";
static NSString *const GENERAL_OBJECT_TRACK_MODEL =
    @"/generalobjectmodel/tt_sample_v1.0.model";

@protocol BDResourceHelperDelegate<NSObject>

@optional
/// path of license dir
- (NSString *)licenseDirPath;

/// path of composer node
- (NSString *)composerNodeDirPath;

/// path of filter dir
- (NSString *)filterDirPath;

/// path of sticker dir
- (NSString *)stickerDirPath;

/// path of composer
- (NSString *)composerDirPath;

/// path of model dir
- (NSString *)modelDirPath;

/// license name
- (NSString *)licenseName;

@end

NS_ASSUME_NONNULL_BEGIN

@interface BDResourceHelper : NSObject

@property(nonatomic, weak) id<BDResourceHelperDelegate> delegate;

- (NSString *)licensePath;
- (NSString *)composerNodePath:(NSString *)nodeName;
- (NSString *)filterPath:(NSString *)filterName;
- (NSString *)stickerPath:(NSString *)stickerName;
- (NSString *)modelPath:(NSString *)modelName;
- (NSString *)composerPath;
- (NSString *)modelDirPath;

@end

NS_ASSUME_NONNULL_END
