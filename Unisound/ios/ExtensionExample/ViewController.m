#import "ViewController.h"
#import "Config.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface ViewController ()<AgoraRtcEngineDelegate,
                            AgoraMediaFilterEventDelegate,
                             UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(assign, nonatomic) BOOL enable;
@property(assign, nonatomic) BOOL isStartedASR;
@property(assign, nonatomic) BOOL isStartedEval;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@property(weak, nonatomic) IBOutlet UIButton *asrBtn;
@property(weak, nonatomic) IBOutlet UIButton *evalBtn;
@property(weak, nonatomic) IBOutlet UITextView *resultTv;
@property(nonatomic) NSUInteger uid;
@property(strong, nonatomic) NSMutableString *pre_result;
@end

@implementation ViewController

- (void)viewDidLoad {
  [super viewDidLoad];
    self.uid = 0;
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
        self.uid = uid;
    }];
    
}

- (IBAction)enableExtension:(id)sender {
  self.enable = !self.enable;
  [self.agoraKit enableExtensionWithVendor:@"Unisound"
                                 extension:@"ASR_EVAL"
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
                                                                @"appkey": appkey,
                                                                @"secret": secret
                                                            }
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Unisound"
                                     extension:@"ASR_EVAL"
                                           key:@"init_asr"
                                         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
        }
        
        {
            NSError *error;
            NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                                                                @"user_id": [@(self.uid) stringValue],
                                                            }
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Unisound"
                                     extension:@"ASR_EVAL"
                                           key:@"start_asr"
                                         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
        }
    } else {
        {
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Unisound"
                                     extension:@"ASR_EVAL"
                                           key:@"stop_asr"
                                         value:@"{}"];
        }
    }
    
    self.isStartedASR = !self.isStartedASR;
    
    [self.asrBtn setTitle:self.isStartedASR ? @"Stop ASR" : @"Start ASR"
                             forState:UIControlStateNormal];
    [self.evalBtn setEnabled:!self.isStartedASR];
}

- (IBAction)setSticker:(id)sender {
    if (!self.isStartedEval) {
        {
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Unisound"
                                     extension:@"ASR_EVAL"
                                           key:@"init_eval"
                                         value:@"{}"];
        }
        
        {
            NSError *error;
            NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                                                                @"appkey": eval_app_key,
                                                                @"userID": [@(self.uid) stringValue],
                                                                @"mode": @"word",
                                                                @"displayText": @"Hello world",
                                                                @"audioFormat": @"pcm",
                                                                @"eof": @"end"
                                                            }
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Unisound"
                                     extension:@"ASR_EVAL"
                                           key:@"start_eval"
                                         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
        }
    } else {
        {
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Unisound"
                                     extension:@"ASR_EVAL"
                                           key:@"stop_eval"
                                         value:@"{}"];
        }
    }
    
    self.isStartedEval = !self.isStartedEval;
    [self.evalBtn setTitle:self.isStartedEval ? @"Stop Eval" : @"Start Eval"
                             forState:UIControlStateNormal];
    [self.asrBtn setEnabled:!self.isStartedEval];
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
    if ([key isEqualToString:@"asr_result"]) {
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
        if ([json[@"type"] isEqualToString:@"variable"]) {
            result = [result stringByAppendingString:json[@"text"]];
        } else if ([json[@"type"] isEqualToString:@"fixed"]) {
            result = [result stringByAppendingString:json[@"text"]];
            [self.pre_result setString:result];
        }
    } else {
        result = [result stringByAppendingString:value];
    }
    
    dispatch_async(dispatch_get_main_queue(), ^{
        [self.resultTv setText:result];
    });
}

@end
