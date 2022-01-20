#import "ViewController.h"
#import "AppID.h"
#import "authpack.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface ViewController ()<AgoraRtcEngineDelegate,
                             UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(assign, nonatomic) BOOL enable;
@property(weak, nonatomic) IBOutlet UIView *localVideoView;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@end

@implementation ViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  // Do any additional setup after loading the view.
  [self initRtcEngine];
}

- (void)initRtcEngine {
  AgoraRtcEngineConfig *config = [AgoraRtcEngineConfig new];
  config.appId = appID;
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

- (IBAction)initExtension:(id)sender {
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
  [self.agoraKit
      setExtensionPropertyWithVendor:@"FaceUnity"
                           extension:@"Effect"
                                 key:@"fuLoadAIModelFromPackage"
                               value:[self toJson:@{
                                 @"data": [[NSBundle mainBundle]
                                     pathForResource:@"ai_face_processor"
                                              ofType:@"bundle"],
                                 @"type": @(1 << 10)
                               }]];
}

- (IBAction)setComposer:(id)sender {
  [self.agoraKit
      setExtensionPropertyWithVendor:@"FaceUnity"
                           extension:@"Effect"
                                 key:@"fuCreateItemFromPackage"
                               value:[self toJson:@{
                                 @"data": [[NSBundle mainBundle]
                                     pathForResource:@"face_beautification"
                                              ofType:@"bundle"]
                               }]];
}

- (IBAction)setSticker:(id)sender {
  [self.agoraKit setExtensionPropertyWithVendor:@"FaceUnity"
                                      extension:@"Effect"
                                            key:@"fuCreateItemFromPackage"
                                          value:[self toJson:@{
                                            @"data": [[NSBundle mainBundle]
                                                pathForResource:@"CatSparks"
                                                         ofType:@"bundle"]
                                          }]];
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
}

@end
