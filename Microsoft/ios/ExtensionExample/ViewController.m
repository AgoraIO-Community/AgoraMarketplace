#import "ViewController.h"
#import "Config.h"
#import <AgoraRtcKit/AgoraRtcEngineKit.h>

@interface ViewController ()<AgoraRtcEngineDelegate,
                            AgoraMediaFilterEventDelegate,
                             UIPopoverPresentationControllerDelegate>
@property(strong, nonatomic) AgoraRtcEngineKit *agoraKit;
@property(assign, nonatomic) BOOL enable;
@property(assign, nonatomic) BOOL isStartedASR;
@property(assign, nonatomic) BOOL isStartedTSL;
@property(weak, nonatomic) IBOutlet UIButton *enableExtensionBtn;
@property(weak, nonatomic) IBOutlet UIButton *asrBtn;
@property(weak, nonatomic) IBOutlet UIButton *tslBtn;
@property(weak, nonatomic) IBOutlet UITextView *resultTv;
@property(weak, nonatomic) IBOutlet UITextView *resultEndTv;
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

- (IBAction)startTranslate:(id)sender {
    if (!self.isStartedTSL) {
        {
            NSError *error;
            NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                                                                @"subscription": subscription,
                                                                @"region": region,
                                                                @"source_languages": @[
                                                                    @"zh-CN"
                                                                ],
                                                                @"target_languages": @[
                                                                    @"en-US", @"ja-JP", @"zh-Hant"
                                                                ],
                                                                @"enable_auto_detect": @FALSE
                                                            }
                                                           options:NSJSONWritingPrettyPrinted
                                                             error:&error];
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Microsoft"
                                     extension:@"Speech_Recognition"
                                           key:@"init_translate_recognition"
                                         value:[[NSString alloc] initWithData:data encoding:NSUTF8StringEncoding]];
        }
        
        {
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Microsoft"
                                     extension:@"Speech_Recognition"
                                           key:@"start_continuous_translate_async"
                                         value:@"{}"];
        }
    } else {
        {
            
            [self.agoraKit
                setExtensionPropertyWithVendor:@"Microsoft"
                                     extension:@"Speech_Recognition"
                                           key:@"stop_continuous_translate_async"
                                         value:@"{}"];
        }
    }
    
    self.isStartedTSL = !self.isStartedTSL;
    
    [self.tslBtn setTitle:self.isStartedTSL ? @"Stop Translate" : @"Start Translate"
                             forState:UIControlStateNormal];
}

- (IBAction)setComposer:(id)sender {
    if (!self.isStartedASR) {
        {
            NSError *error;
            NSData *data = [NSJSONSerialization dataWithJSONObject:@{
                                                                @"subscription": subscription,
                                                                @"region": region,
                                                                @"source_languages": @[
                                                                    @"zh-CN", @"en-US"
                                                                ],
                                                                @"enable_auto_detect": @TRUE
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

    
    if ([key isEqualToString:@"speech_recognizing"]) {
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
        NSString *text = @"Recognizing STT Text：";
        text = [text stringByAppendingString:json[@"text"]];
        text = [text stringByAppendingString:@"\n"];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.resultEndTv setText:text];
        });
        
    } else if ([key isEqualToString:@"speech_recognized"]) {
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
        NSString *text = @"STT Text：";
        text = [text stringByAppendingString:json[@"text"]];
        text = [text stringByAppendingString:@"\n"];
        result = [result stringByAppendingString:text];
        [self.pre_result setString:result];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.resultTv setText:result];
        });
        
    } else if ([key isEqualToString:@"translation_recognizing"]) {
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:[value dataUsingEncoding:NSUTF8StringEncoding] options:0 error:nil];
        NSString *text = @"Recognizing Translation Origin Text：";
        text = [text stringByAppendingString:json[@"text"]];
        text = [text stringByAppendingString:@"\n"];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.resultEndTv setText:text];
        });
        
    } else if ([key isEqualToString:@"translation_recognized"]) {
        NSData *data = [value dataUsingEncoding:NSUTF8StringEncoding];
        NSDictionary *json = [NSJSONSerialization JSONObjectWithData:data options:NSJSONReadingMutableContainers error:nil];
        result = [result stringByAppendingString:@"translation: \n"];
        result = [result stringByAppendingString:@"en: "];
        result = [result stringByAppendingString:json[@"translation"][@"en"]];
        result = [result stringByAppendingString:@"\n"];
        result = [result stringByAppendingString:@"ja: "];
        result = [result stringByAppendingString:json[@"translation"][@"ja"]];
        result = [result stringByAppendingString:@"\n"];
        result = [result stringByAppendingString:@"zh-Hant: "];
        result = [result stringByAppendingString:json[@"translation"][@"zh-Hant"]];
        result = [result stringByAppendingString:@"\n"];
        [self.pre_result setString:result];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.resultTv setText:result];
        });
        
    } else if ([key isEqualToString:@"translation_speech_start_detected"] || [key isEqualToString:@"speech_start_detected"] || [key isEqualToString:@"translation_speech_end_detected"] || [key isEqualToString:@"speech_end_detected"]) {
        result = [result stringByAppendingString:key];
        result = [result stringByAppendingString:@"\n"];
        [self.pre_result setString:result];
        dispatch_async(dispatch_get_main_queue(), ^{
            [self.resultTv setText:result];
        });
    } else {
        result = [result stringByAppendingString:value];
        [self.pre_result setString:result];
    }
}

@end
 
