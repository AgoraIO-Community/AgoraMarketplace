#import "ViewController.h"
#import "AppID.h"
#import "BDResourceHelper.h"
#import "TableViewController.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface ViewController ()<AgoraRtcEngineDelegate,
                             UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(strong, nonatomic) BDResourceHelper *resourceHelper;
@property(assign, nonatomic) BOOL enable;
@property(weak, nonatomic) IBOutlet UIView *localVideoView;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@end

@implementation ViewController

- (BDResourceHelper *)resourceHelper {
  if (_resourceHelper == nil) { _resourceHelper = [BDResourceHelper new]; }
  return _resourceHelper;
}

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
  [self.agoraKit enableExtensionWithVendor:@"ByteDance"
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

- (IBAction)bef_effect_ai_init:(id)sender {
    NSError *error;
    NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                            @"licensePath":
                                [self.resourceHelper licensePath]
                          }
                        options:NSJSONWritingPrettyPrinted
                        error:&error];
    
    [self.agoraKit
        setExtensionPropertyWithVendor:@"ByteDance"
                           extension:@"Effect"
                                 key:@"bef_effect_ai_check_license"
                               value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    
    data = [NSJSONSerialization dataWithJSONObject:@{
                            @"strModelDir":
                                [self.resourceHelper modelDirPath],
                            @"deviceName": @""
                          }
                        options:NSJSONWritingPrettyPrinted
                        error:&error];
    [self.agoraKit
        setExtensionPropertyWithVendor:@"ByteDance"
                       extension:@"Effect"
                             key:@"bef_effect_ai_init"
                           value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    
    
    data = [NSJSONSerialization dataWithJSONObject:@{
                            @"mode": @1,
                            @"orderType": @0
                          }
                        options:NSJSONWritingPrettyPrinted
                        error:&error];
    
    [self.agoraKit
        setExtensionPropertyWithVendor:@"ByteDance"
                             extension:@"Effect"
                                   key:@"bef_effect_ai_composer_set_mode"
                                 value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
}

- (IBAction)bef_effect_ai_set_effect:(id)sender {
  [self performSegueWithIdentifier:@"PopSegue" sender:self];
}

- (void)onEvent:(NSString *__nullable)provider
      extension:(NSString *__nullable)extension
            key:(NSString *__nullable)key
          value:(NSString *__nullable)value {
}

- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
  if ([segue.identifier isEqualToString:@"PopSegue"]) {
    TableViewController *controller = segue.destinationViewController;
    // 设置Controller尺寸
    controller.preferredContentSize = CGSizeMake(200, 400);

    UIPopoverPresentationController *popController =
        controller.popoverPresentationController;
    __weak typeof(self) weakSelf = self;
    controller.stickerBlock = ^(NSString *_Nonnull sticker) {
      if (!weakSelf) { return; }
        NSError *error;
        NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                                @"strPath": [self.resourceHelper
                                    stickerPath:sticker]
                              }
                            options:NSJSONWritingPrettyPrinted
                            error:&error];
        
      [weakSelf.agoraKit
          setExtensionPropertyWithVendor:@"ByteDance"
                               extension:@"Effect"
                                     key:@"bef_effect_ai_set_effect"
                                   value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
    };
    // 设置箭头的“尖儿”所指向的位置
    popController.sourceRect = CGRectMake(0, 50, 100, 0);
    if (popController) { popController.delegate = self; }
  }
}

// UIPopoverPresentationControllerDelegate中的代理方法，当前controller需要遵守此协议
- (UIModalPresentationStyle)adaptivePresentationStyleForPresentationController:
    (UIPresentationController *)controller {
  // 设置弹出的样式
  return UIModalPresentationNone;
}

@end
