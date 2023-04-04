#import "ViewController.h"
#import "AppID.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>
#import <AgoraSenseTimeExtension/st_mobile_human_action.h>
#import <AgoraSenseTimeExtension/st_mobile_effect.h>

@interface ViewController ()<AgoraRtcEngineDelegate, AgoraMediaFilterEventDelegate,
UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(assign, nonatomic) BOOL enable;
@property(assign, nonatomic) BOOL enableFaceDetect;
@property(weak, nonatomic) IBOutlet UIView *localVideoView;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@property (weak, nonatomic) IBOutlet UILabel *faceInfoLabel;
@property (weak, nonatomic) IBOutlet UIButton *enableFaceDetectBtn;
@end

@implementation ViewController

- (void)viewDidLoad {
    [super viewDidLoad];
    [self.enableFaceDetectBtn setTitle:self.enableFaceDetect ? @"disableFaceDetect" : @"enableFaceDetect"
                              forState:UIControlStateNormal];
    self.faceInfoLabel.text = @"";
    self.faceInfoLabel.hidden = !self.enableFaceDetect;
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
    
    
    
    AgoraRtcVideoCanvas *canvas = [AgoraRtcVideoCanvas new];
    canvas.view = self.localVideoView;
    [self.agoraKit setupLocalVideo:canvas];
}

- (IBAction)enableExtension:(id)sender {
    self.enable = !self.enable;
    [self.agoraKit enableExtensionWithVendor:@"SenseTime"
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

- (IBAction)initExtension:(id)sender {
    {
        NSString* fileName = [[license_name lastPathComponent] stringByDeletingPathExtension];
        NSString* extension = [license_name pathExtension];
        
        NSError *error;
        NSData *data = [NSJSONSerialization dataWithJSONObject:@{
            @"license_path": [[NSBundle mainBundle]
                              pathForResource:fileName
                              ofType:extension]
        }
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"SenseTime"
         extension:@"Effect"
         key:@"st_mobile_check_activecode"
         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    }
    
    {
        
        NSString *model_path = [[[NSBundle mainBundle]
                                 pathForResource:@"model"
                                 ofType:@"bundle"] stringByAppendingFormat:@"/%@", @"M_SenseME_Face_Video_7.0.0.model"];
        NSError *error;
        NSData *data = [NSJSONSerialization dataWithJSONObject:@{
            @"model_path": model_path,
            @"config": @(ST_MOBILE_HUMAN_ACTION_DEFAULT_CONFIG_IMAGE)
        }
                                                       options:NSJSONWritingPrettyPrinted
                                                         error:&error];
        
        [self.agoraKit
         setExtensionPropertyWithVendor:@"SenseTime"
         extension:@"Effect"
         key:@"st_mobile_human_action_create"
         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    }
    
    {
        [self.agoraKit
         setExtensionPropertyWithVendor:@"SenseTime"
         extension:@"Effect"
         key:@"st_mobile_effect_create_handle"
         value:@"{}"];
    }
    
    
}

- (IBAction)setComposer:(id)sender {
    NSError *error;
    NSData *data = [NSJSONSerialization dataWithJSONObject:@{
        @"param": @(EFFECT_BEAUTY_PLASTIC_SHRINK_GODDESS_FACE),
        @"val": @0.8f
    }
                                                   options:NSJSONWritingPrettyPrinted
                                                     error:&error];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"SenseTime"
     extension:@"Effect"
     key:@"st_mobile_effect_set_beauty_strength"
     value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
}

- (IBAction)setSticker:(id)sender {
    NSString *path = [[[NSBundle mainBundle]
                       pathForResource:@"lips"
                       ofType:@"bundle"] stringByAppendingFormat:@"/%@", @"12自然.zip"];
    NSError *error;
    NSData *data = [NSJSONSerialization dataWithJSONObject:@{
        @"param": @(EFFECT_BEAUTY_MAKEUP_LIP),
        @"path": path
    }
                                                   options:NSJSONWritingPrettyPrinted
                                                     error:&error];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"SenseTime"
     extension:@"Effect"
     key:@"st_mobile_effect_set_beauty"
     value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
}

- (NSString *)toJson:(NSDictionary *)dic {
    NSError *error;
    NSData *data =
    [NSJSONSerialization dataWithJSONObject:dic
                                    options:NSJSONWritingPrettyPrinted
                                      error:&error];
    return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

- (void)onEvent:(NSString *__nullable)provider
      extension:(NSString *__nullable)extension
            key:(NSString *__nullable)key
          value:(NSString *__nullable)value {
    NSLog(@"onEvent: key:%@, value:%@", key, value);
    
    if([key isEqual: @"st_mobile_human_action_detect"]) {
        NSError *jsonError;
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding]
                                                             options:NSJSONReadingMutableContainers
                                                               error:&jsonError];
        
        dispatch_async(dispatch_get_main_queue(), ^{
            self.faceInfoLabel.text = [NSString stringWithFormat:@"FaceCount: %@", json[@"face_count"]];
            
        });
    }
    
}

- (IBAction)enableFaceDetect:(id)sender {
    self.enableFaceDetect = !self.enableFaceDetect;
    
    [self.enableFaceDetectBtn setTitle:self.enableFaceDetect ? @"disableFaceDetect" : @"enableFaceDetect"
                              forState:UIControlStateNormal];
    self.faceInfoLabel.hidden = !self.enableFaceDetect;
    
    NSError *error;
    NSData *data = [NSJSONSerialization dataWithJSONObject:@{
        @"enable": @(self.enableFaceDetect),
    }
                                                   options:NSJSONWritingPrettyPrinted
                                                     error:&error];
    
    [self.agoraKit
     setExtensionPropertyWithVendor:@"SenseTime"
     extension:@"Effect"
     key:@"st_mobile_human_action_detect_enable"
     value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
}

@end
