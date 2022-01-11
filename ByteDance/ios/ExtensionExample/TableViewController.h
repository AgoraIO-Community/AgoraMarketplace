#import <UIKit/UIKit.h>

NS_ASSUME_NONNULL_BEGIN

typedef void (^StickerSelectBlock)(NSString *sticker);
@interface TableViewController : UITableViewController
@property(nonatomic, strong) StickerSelectBlock stickerBlock;
@end

NS_ASSUME_NONNULL_END
