#import "ViewController.h"
#import "Config.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface ViewController ()<AgoraRtcEngineDelegate,
                            AgoraMediaFilterEventDelegate,
                             UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(assign, nonatomic) BOOL enable;
@property(assign, nonatomic) BOOL isStartedASR;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@property(weak, nonatomic) IBOutlet UIButton *asrBtn;
@property(weak, nonatomic) IBOutlet UITextView *resultTv;
@property(strong, nonatomic) NSMutableString *pre_result;
@end

@implementation ViewController

- (void)viewDidLoad {
  [super viewDidLoad];
    self.pre_result = [[NSMutableString alloc] init];
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

    [self.agoraKit enableAudio];
  [self.agoraKit setChannelProfile:AgoraChannelProfileLiveBroadcasting];
  [self.agoraKit setClientRole:AgoraClientRoleBroadcaster];
    [self.agoraKit joinChannelByToken:@"" channelId:@"testapi" info:nil uid:0 joinSuccess:^(NSString * _Nonnull channel, NSUInteger uid, NSInteger elapsed) {
    }];
    
}

- (IBAction)enableExtension:(id)sender {
  self.enable = !self.enable;
  [self.agoraKit enableExtensionWithVendor:@"Microsoft"
                                 extension:@"Speech_Recognition"
                                   enabled:self.enable];
  if (self.enable) {
    [self.enableExtensionBtn setTitle:@"disableExtension"
                             forState:UIControlStateNormal];
  } else {
    [self.enableExtensionBtn setTitle:@"enableExtension"
                             forState:UIControlStateNormal];
  }
}

- (IBAction)setComposer:(id)sender {
    if (!self.isStartedASR) {
        {
            NSError *error;
            NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                                                                @"subscription": subscription,
                                                                @"region": region,
                                                                @"auto_detect_source_languages": @[
                                                                    @"zh-CN", @"en-US"
                                                                ]
                                                            }
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Microsoft"
                                     extension:@"Speech_Recognition"
                                           key:@"init_speech_recognition"
                                         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
        }
        
        {
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Microsoft"
                                     extension:@"Speech_Recognition"
                                           key:@"start_continuous_recognition_async"
                                         value:@"{}"];
        }
    } else {
        {
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Microsoft"
                                     extension:@"Speech_Recognition"
                                           key:@"stop_continuous_recognition_async"
                                         value:@"{}"];
        }
    }
    
    self.isStartedASR = !self.isStartedASR;
    
    [self.asrBtn setTitle:self.isStartedASR ? @"Stop ASR" : @"Start ASR"
                             forState:UIControlStateNormal];
}

- (NSString *)toJson:(NSDictionary *)dic {
  NSError *error;
  NSData *data =
      [NSJSONSerialization dataWithJSONObject:dic
                                      options:NSJSONWritingPrettyPrinted
                                        error:&error];
  return [[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding];
}

- (void)onEvent:(NSString *)provider extension:(NSString *)extension key:(NSString *)key value:(NSString *)value {
    NSString *result = self.pre_result;
    if ([key isEqualToString:@"recognizing_speech"] || [key isEqualToString:@"recognized_speech"]) {
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
        
        NSString *text = json[@"text"];
        result = [result stringByAppendingString:text];
        
        if ([key isEqualToString:@"recognized_speech"]) {
            [self.pre_result setString:result];
        }
        
    } else {
        result = [result stringByAppendingString:value];
        [self.pre_result setString:result];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.resultTv setText:result];
    });
}

@end
