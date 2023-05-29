#import "ViewController.h"
#import "Config.h"
#import "authpack.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface ViewController ()<AgoraRtcEngineDelegate,
AgoraMediaFilterEventDelegate,
UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(assign, nonatomic) BOOL enable;
@property(assign, nonatomic) BOOL isEnableAITracking;
@property(assign, nonatomic) BOOL enableLightmarkup;
@property(assign, nonatomic) BOOL enableSticker;
@property(assign, nonatomic) BOOL enableSetComposer;
@property(assign, nonatomic) NSInteger faces;
@property(assign, nonatomic) NSInteger hands;
@property(assign, nonatomic) NSInteger people;
@property(weak, nonatomic) IBOutlet UIView *localVideoView;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@property(weak, nonatomic) IBOutlet UIButton *setComposersBtn;
@property(weak, nonatomic) IBOutlet UIButton *setStickerBtn;
@property(weak, nonatomic) IBOutlet UIButton *enableAITrackingBtn;
@property(weak, nonatomic) IBOutlet UISlider *colorLevelSlider;
@property(weak, nonatomic) IBOutlet UISlider *filterLevelSlider;
@property(weak, nonatomic) IBOutlet UILabel *aiTrackingResult;
@property (weak, nonatomic) IBOutlet UIButton *enableLightmarkupBtn;
@end

@implementation ViewController

- (void)changeColorLevelProgress:(UISlider*)slider {
    {
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": [[NSBundle mainBundle]
                            pathForResource:@"face_beautification"
                            ofType:@"bundle"],
            @"name": @"filter_name",
            @"value": @"ziran2",
         }]];
    }
    
    {
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": [[NSBundle mainBundle]
                            pathForResource:@"face_beautification"
                            ofType:@"bundle"],
            @"name": @"color_level",
            @"value": @([slider value] / 1.0),
         }]];
    }
}

- (void)changeFilterLevelProgress:(UISlider*)slider {
    {
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": [[NSBundle mainBundle]
                            pathForResource:@"face_beautification"
                            ofType:@"bundle"],
            @"name": @"filter_name",
            @"value": @"ziran2",
         }]];
    }
    
    {
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": [[NSBundle mainBundle]
                            pathForResource:@"face_beautification"
                            ofType:@"bundle"],
            @"name": @"filter_level",
            @"value": @([slider value] / 1.0),
         }]];
    }
}

- (void)viewDidLoad {
    [super viewDidLoad];
    
    [[self colorLevelSlider] addTarget:self action:@selector(changeColorLevelProgress:) forControlEvents:UIControlEventValueChanged];
    
    [[self filterLevelSlider] addTarget:self action:@selector(changeFilterLevelProgress:) forControlEvents:UIControlEventValueChanged];
    
    [self.enableAITrackingBtn setEnabled:false];
    [self.aiTrackingResult setHidden:true];
    [self.setComposersBtn setEnabled:false];
    [self.setStickerBtn setEnabled:false];
    [self.colorLevelSlider setEnabled:false];
    [self.filterLevelSlider setEnabled:false];
    [self.colorLevelSlider setValue:0.0 animated:false];
    [self.filterLevelSlider setValue:0.0 animated:false];
    
    // Do any additional setup after loading the view.
    [self initRtcEngine];
}



- (void)initRtcEngine {
    AgoraRtcEngineConfig *config = [AgoraRtcEngineConfig new];
    config.appId = appID;
    config.eventDelegate = self;
    self.agoraKit = [AgoraRtcEngineKit sharedEngineWithConfig:config
                                                     delegate:self];
    [self enableExtension:nil];
    [self.agoraKit enableVideo];
    [self.agoraKit setChannelProfile:AgoraChannelProfileLiveBroadcasting];
    [self.agoraKit setClientRole:AgoraClientRoleBroadcaster];
    [self.agoraKit startPreview];
}

- (IBAction)enableExtension:(id)sender {
    self.enable = !self.enable;
    [self.agoraKit enableExtensionWithVendor:@"FaceUnity"
                                   extension:@"Effect"
                                     enabled:self.enable];
    if (self.enable) {
        [self.enableExtensionBtn setTitle:@"disableExtension"
                                 forState:UIControlStateNormal];
    } else {
        [self.enableExtensionBtn setTitle:@"enableExtension"
                                 forState:UIControlStateNormal];
    }
}

- (void)enableAITracking {
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuSetMaxFaces"
     value:[self toJson:@{
        @"n": @5
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuHumanProcessorSetMaxHumans"
     value:[self toJson:@{
        @"max_humans": @5
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuIsTracking"
     value:[self toJson:@{
        @"enable": @(YES)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuHumanProcessorGetNumResults"
     value:[self toJson:@{
        @"enable": @(YES)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuHandDetectorGetResultNumHands"
     value:[self toJson:@{
        @"enable": @(YES)
     }]];
    
    [self.aiTrackingResult setHidden:false];
    [self.enableAITrackingBtn setTitle:@"disableAITracking" forState:UIControlStateNormal];
}

- (void)disableAITracking {
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuIsTracking"
     value:[self toJson:@{
        @"enable": @(NO)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuHumanProcessorGetNumResults"
     value:[self toJson:@{
        @"enable": @(NO)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuHandDetectorGetResultNumHands"
     value:[self toJson:@{
        @"enable": @(NO)
     }]];
    self.faces = 0;
    self.hands = 0;
    self.people = 0;
    [self updateAITrackingResult:0 _:0 _:0];
    [self.aiTrackingResult setHidden:true];
    [self.enableAITrackingBtn setTitle:@"enableAITracking" forState:UIControlStateNormal];
}

- (void)loadAIModels {
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuLoadAIModelFromPackage"
     value:[self toJson:@{
        @"data": [[NSBundle mainBundle]
                  pathForResource:@"ai_face_processor"
                  ofType:@"bundle"],
        @"type": @(1 << 8)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuLoadAIModelFromPackage"
     value:[self toJson:@{
        @"data": [[NSBundle mainBundle]
                  pathForResource:@"ai_hand_processor"
                  ofType:@"bundle"],
        @"type": @(1 << 3)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuLoadAIModelFromPackage"
     value:[self toJson:@{
        @"data": [[NSBundle mainBundle]
                  pathForResource:@"ai_human_processor_gpu"
                  ofType:@"bundle"],
        @"type": @(1 << 9)
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuCreateItemFromPackage"
     value:[self toJson:@{
        @"data": [[NSBundle mainBundle]
                  pathForResource:@"aitype"
                  ofType:@"bundle"]
     }]];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuItemSetParam"
     value:[self toJson:@{
        @"obj_handle": [[NSBundle mainBundle]
                        pathForResource:@"aitype"
                        ofType:@"bundle"],
        @"name": @"aitype",
        @"value": @(1 << 8 | 1 << 30 | 1 << 3),
     }]];
}

- (void)initExtension {
    NSMutableArray *authdata =
    [[NSMutableArray alloc] initWithCapacity:sizeof(g_auth_package)];
    for (int i = 0; i < sizeof(g_auth_package); i++) {
        [authdata addObject:@(g_auth_package[i])];
    }
    [self.agoraKit
     setExtensionPropertyWithVendor:@"FaceUnity"
     extension:@"Effect"
     key:@"fuSetup"
     value:[self toJson:@{@"authdata": authdata}]];
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.enableAITrackingBtn setEnabled:true];
        [self.setComposersBtn setEnabled:true];
        [self.setStickerBtn setEnabled:true];
    });
}

- (IBAction)enableAITrackingAction:(id)sender {
    self.isEnableAITracking = !self.isEnableAITracking;
    
    if (self.isEnableAITracking) {
        [self enableAITracking];
    } else {
        [self disableAITracking];
    }
}

- (NSDictionary *)loadBitmapToBase64:(NSString *)path {
    UIImage *image = [UIImage imageNamed:path];
    
    CFDataRef pixelData = CGDataProviderCopyData(CGImageGetDataProvider(image.CGImage));
    //const uint8_t* data = CFDataGetBytePtr(pixelData);
    int width = (int)image.size.width;
    int height = (int)image.size.height;
    
    NSData *imgData = (__bridge_transfer NSData*)pixelData;
    
    NSString *base64 = [imgData base64EncodedStringWithOptions:NSDataBase64EncodingEndLineWithLineFeed];
    
    NSDictionary *ret = @{
        @"img_data": imgData,
        @"width": [NSNumber numberWithInt:width],
        @"height": [NSNumber numberWithInt:height],
        @"base64": base64,
    };
    
    return ret;
}

- (NSArray *)loadLipstickJSON:(NSString *)path {
    NSData *data = [NSData dataWithContentsOfFile:path];
    id jsonValue = [NSJSONSerialization JSONObjectWithData:data options:kNilOptions error:nil];
    return jsonValue[@"rgba"];
}

- (IBAction)enableLightMarkup:(id)sender {
    NSString *lightMarkupPath = [[NSBundle mainBundle]
                                 pathForResource:@"light_makeup"
                                 ofType:@"bundle"];
    NSString *blusherPath = [[NSBundle mainBundle]
                             pathForResource:@"mu_blush_02"
                             ofType:@"png"];
    NSString *eyebrowPath = [[NSBundle mainBundle]
                             pathForResource:@"mu_eyebrow_02"
                             ofType:@"png"];;
    NSString *eyeShadowPath = [[NSBundle mainBundle]
                               pathForResource:@"mu_eyeshadow_02"
                               ofType:@"png"];;
    NSString *eyeLinerPath = [[NSBundle mainBundle]
                              pathForResource:@"mu_eyeliner_04"
                              ofType:@"png"];;
    NSString *eyelashPath = [[NSBundle mainBundle]
                             pathForResource:@"mu_eyelash_03"
                             ofType:@"png"];;
    NSString *lipstickPath = [[NSBundle mainBundle]
                              pathForResource:@"mu_lip_01"
                              ofType:@"json"];
    
    NSNumber *makeup_intensity_lip = @0.9;
    NSNumber *makeup_intensity_blusher = @0.9;
    NSNumber *makeup_intensity_eyeBrow = @1.0;
    NSNumber *makeup_intensity_eye = @1.0;
    NSNumber *makeup_intensity_eyeLiner = @1.0;
    NSNumber *makeup_intensity_eyelash = @1.0;
    
    NSNumber *is_use_fix = @1;
    
    //off
    NSNumber *makeup_intensity_pupil = @0;
    
    NSArray *texLoads = @[
        blusherPath,
        eyebrowPath,
        eyeShadowPath,
        eyeLinerPath,
        eyelashPath
    ];
    
    NSArray *texNames = @[
        @"tex_blusher",
        @"tex_brow",
        @"tex_eye",
        @"tex_eyeLiner",
        @"tex_eyeLash"
    ];
    
    self.enableLightmarkup = !self.enableLightmarkup;
    
    if(self.enableLightmarkup) {
        [self.enableLightmarkupBtn setTitle:@"disableLightMarkup" forState:UIControlStateNormal];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuCreateItemFromPackage"
         value:[self toJson:@{
            @"data": lightMarkupPath
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"is_makeup_on",
            @"value": @1
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"is_use_fix",
            @"value": is_use_fix
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity",
            @"value": @1.0
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_eye",
            @"value": makeup_intensity_eye
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_eyeBrow",
            @"value": makeup_intensity_eyeBrow
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_lip",
            @"value": makeup_intensity_lip
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_pupil",
            @"value": makeup_intensity_pupil
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_eyeLiner",
            @"value": makeup_intensity_eyeLiner
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_eyelash",
            @"value": makeup_intensity_eyelash
         }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_intensity_blusher",
            @"value": makeup_intensity_blusher
         }]];
        
        //    [self.agoraKit
        //        setExtensionPropertyWithVendor:@"FaceUnity"
        //                             extension:@"Effect"
        //                                   key:@"fuItemSetParam"
        //                                 value:[self toJson:@{
        //                                   @"obj_handle": lightMarkupPath,
        //                                   @"name": @"makeup_lip_mask",
        //                                   @"value": @1.0
        //                                 }]];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuItemSetParam"
         value:[self toJson:@{
            @"obj_handle": lightMarkupPath,
            @"name": @"makeup_lip_color",
            @"value": [self loadLipstickJSON:lipstickPath]
         }]];
        
        NSUInteger idx = 0;
        for (NSString *tex in texLoads) {
            NSString *name = texNames[idx];
            
            NSDictionary* ret = [self loadBitmapToBase64:tex];
            
            [self.agoraKit
             setExtensionPropertyWithVendor:@"FaceUnity"
             extension:@"Effect"
             key:@"fuCreateTexForItem"
             value:[self toJson:@{
                @"item": lightMarkupPath,
                @"name": name,
                @"value": ret[@"base64"],
                @"width": ret[@"width"],
                @"height": ret[@"height"],
             }]];
            
            ++idx;
        }
    }
    else {
        [self.enableLightmarkupBtn setTitle:@"enableLightMarkup" forState:UIControlStateNormal];
        
        NSUInteger idx = 0;
        for (NSString *name in texNames) {
            [self.agoraKit
             setExtensionPropertyWithVendor:@"FaceUnity"
             extension:@"Effect"
             key:@"fuDeleteTexForItem"
             value:[self toJson:@{
                @"item": lightMarkupPath,
                @"name": name,
             }]];
            
            ++idx;
        }
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuDestroyItem"
         value:[self toJson:@{
            @"item": lightMarkupPath,
         }]];
    }
    
    
    
}

- (IBAction)setComposer:(id)sender {
    self.enableSetComposer = !self.enableSetComposer;
    
    if(self.enableSetComposer) {
        [self.setComposersBtn setTitle:@"disableComposer" forState:UIControlStateNormal];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuCreateItemFromPackage"
         value:[self toJson:@{
            @"data": [[NSBundle mainBundle]
                      pathForResource:@"face_beautification"
                      ofType:@"bundle"]
         }]];
        
        [self.colorLevelSlider setEnabled:true];
        [self.filterLevelSlider setEnabled:true];
        
        [self changeColorLevelProgress:self.colorLevelSlider];
        [self changeFilterLevelProgress:self.filterLevelSlider];
    }
    else {
        [self.setComposersBtn setTitle:@"enableComposer" forState:UIControlStateNormal];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"FaceUnity"
         extension:@"Effect"
         key:@"fuDestroyItem"
         value:[self toJson:@{
            @"item": [[NSBundle mainBundle]
                      pathForResource:@"face_beautification"
                      ofType:@"bundle"]
         }]];
        
        [self.colorLevelSlider setEnabled:false];
        [self.filterLevelSlider setEnabled:false];
        
    }
    
    
}

- (IBAction)setSticker:(id)sender {
    self.enableSticker = !self.enableSticker;
    
    if(self.enableSticker) {
        [self.setStickerBtn setTitle:@"disableSticker" forState:UIControlStateNormal];
        
        [self.agoraKit setExtensionPropertyWithVendor:@"FaceUnity"
                                            extension:@"Effect"
                                                  key:@"fuCreateItemFromPackage"
                                                value:[self toJson:@{
                                                    @"data": [[NSBundle mainBundle]
                                                              pathForResource:@"CatSparks"
                                                              ofType:@"bundle"]
                                                }]];
    }
    else {
        [self.setStickerBtn setTitle:@"enableSticker" forState:UIControlStateNormal];
        
        [self.agoraKit setExtensionPropertyWithVendor:@"FaceUnity"
                                            extension:@"Effect"
                                                  key:@"fuDestroyItem"
                                                value:[self toJson:@{
                                                    @"item": [[NSBundle mainBundle]
                                                              pathForResource:@"CatSparks"
                                                              ofType:@"bundle"]
                                                }]];
    }
    
}

- (NSString *)toJson:(NSDictionary *)dic {
    NSError *error;
    NSData *data =
    [NSJSONSerialization dataWithJSONObject:dic
                                    options:NSJSONWritingPrettyPrinted
                                      error:&error];
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

- (void)updateAITrackingResult:(NSInteger)faces _:(NSInteger)hands _:(NSInteger)people {
    dispatch_async(dispatch_get_main_queue(), ^{
        NSString *result = [NSString stringWithFormat:@"faces: %ld\nhands: %ld\npeople: %ld", (long)faces, hands, people];
        [self.aiTrackingResult setText:result];
    });
}

- (void)onEvent:(NSString *)provider extension:(NSString *)extension key:(NSString *)key value:(NSString *)value {
    NSLog(@"onEvent provider: %@ extension: %@ key: %@ value: %@", provider, extension, key, value);
    
    NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
    if ([key isEqualToString:@"fuIsTracking"]) {
        self.faces = [json[@"faces"] integerValue];
    } else if ([key isEqualToString:@"fuHandDetectorGetResultNumHands"]) {
        self.hands = [json[@"hands"] integerValue];
    } else if ([key isEqualToString:@"fuHumanProcessorGetNumResults"]) {
        self.people = [json[@"people"] integerValue];
    }
    
    [self updateAITrackingResult:self.faces _:self.hands _:self.people];
}

- (void)onExtensionStarted:(NSString *)provider extension:(NSString *)extension {
    NSLog(@"onExtensionStarted: %@ extension: %@ ", provider, extension);
    if ([provider isEqualToString:@"FaceUnity"]) {
        [self initExtension];
        [self loadAIModels];
        AgoraRtcVideoCanvas *canvas = [AgoraRtcVideoCanvas new];
        canvas.view = self.localVideoView;
        [self.agoraKit setupLocalVideo:canvas];
    }
}


@end
