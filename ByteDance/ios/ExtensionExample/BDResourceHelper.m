//
//  BDResourceHelper.m
//  AgoraWithByteDance
//
//  Created by LLF on 2020/10/10.
//

#import "BDResourceHelper.h"
#import "AppID.h"

static NSString *LICENSE_PATH = @"LicenseBag";
static NSString *COMPOSER_PATH = @"ComposeMakeup";
static NSString *FILTER_PATH = @"FilterResource";
static NSString *STICKER_PATH = @"StickerResource";
static NSString *MODEL_PATH = @"ModelResource";
static NSString *BUNDLE = @"bundle";

@interface BDResourceHelper () {
  NSString *_licensePrefix;
  NSString *_composerPrefix;
  NSString *_filterPrefix;
  NSString *_stickerPrefix;
}

@end

@implementation BDResourceHelper

- (NSString *)licensePath {
  NSString *licenseName;
  if ([self.delegate respondsToSelector:@selector(licenseName)]) {
    licenseName = [self.delegate licenseName];
  } else {
    licenseName = license_name;
  }
  if ([self.delegate respondsToSelector:@selector(licenseDirPath)]) {
    return [[self.delegate licenseDirPath] stringByAppendingString:licenseName];
  }
  if (!_licensePrefix) {
    _licensePrefix = [[NSBundle mainBundle] pathForResource:LICENSE_PATH
                                                     ofType:BUNDLE];
  }
  return [_licensePrefix stringByAppendingFormat:@"/%@", licenseName];
}

- (NSString *)composerNodePath:(NSString *)nodeName {
  if ([self.delegate respondsToSelector:@selector(composerNodeDirPath)]) {
    return [self.delegate composerNodeDirPath];
  }
  if (!_composerPrefix) {
    _composerPrefix = [[[NSBundle mainBundle] pathForResource:COMPOSER_PATH
                                                       ofType:BUNDLE]
        stringByAppendingString:@"/ComposeMakeup/"];
  }
  return [_composerPrefix stringByAppendingString:nodeName];
}

- (NSString *)filterPath:(NSString *)filterName {
  if ([self.delegate respondsToSelector:@selector(filterDirPath)]) {
    return [[self.delegate filterDirPath] stringByAppendingString:filterName];
  }
  if (!_filterPrefix) {
    _filterPrefix = [[[NSBundle mainBundle] pathForResource:FILTER_PATH
                                                     ofType:BUNDLE]
        stringByAppendingFormat:@"/Filter/"];
  }
  return [_filterPrefix stringByAppendingString:filterName];
}

- (NSString *)stickerPath:(NSString *)stickerName {
  if ([self.delegate respondsToSelector:@selector(stickerDirPath)]) {
    return [[self.delegate stickerDirPath] stringByAppendingString:stickerName];
  }
  if (!_stickerPrefix) {
    _stickerPrefix = [[[NSBundle mainBundle] pathForResource:STICKER_PATH
                                                      ofType:BUNDLE]
        stringByAppendingString:@"/stickers/"];
  }
  return [_stickerPrefix stringByAppendingString:stickerName];
}

- (NSString *)modelPath:(NSString *)modelName {
  return [[self modelDirPath] stringByAppendingString:modelName];
}

- (NSString *)composerPath {
  if ([self.delegate respondsToSelector:@selector(composerDirPath)]) {
    return [self.delegate composerDirPath];
  }
  if (!_composerPrefix) {
    _composerPrefix = [[[NSBundle mainBundle] pathForResource:COMPOSER_PATH
                                                       ofType:BUNDLE]
        stringByAppendingString:@"/ComposeMakeup/"];
  }
  return [_composerPrefix stringByAppendingString:@"/composer"];
}

- (NSString *)modelDirPath {
  if ([self.delegate respondsToSelector:@selector(modelDirPath)]) {
    return [self.delegate modelDirPath];
  }
  return [[NSBundle mainBundle] pathForResource:MODEL_PATH ofType:BUNDLE];
}

@end
