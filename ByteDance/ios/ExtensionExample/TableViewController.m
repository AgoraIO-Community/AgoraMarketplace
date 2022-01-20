#import "TableViewController.h"
#import "TableViewCell.h"

@interface TableViewController ()
@property(nonatomic, strong) NSArray *stickers;
@end

@implementation TableViewController

- (void)viewDidLoad {
  [super viewDidLoad];
  NSString *stickerBundle =
      [[NSBundle mainBundle] pathForResource:@"StickerResource"
                                      ofType:@"bundle"];
  NSString *stickerFiles =
      [stickerBundle stringByAppendingPathComponent:@"stickers"];
  self.stickers =
      [[NSFileManager defaultManager] contentsOfDirectoryAtPath:stickerFiles
                                                          error:nil];
  // Uncomment the following line to preserve selection between presentations.
  // self.clearsSelectionOnViewWillAppear = NO;

  // Uncomment the following line to display an Edit button in the navigation
  // bar for this view controller. self.navigationItem.rightBarButtonItem =
  // self.editButtonItem;
}

#pragma mark - Table view data source

- (NSInteger)numberOfSectionsInTableView:(UITableView *)tableView {
  return 1;
}

- (NSInteger)tableView:(UITableView *)tableView
    numberOfRowsInSection:(NSInteger)section {
  return self.stickers.count;
}

- (UITableViewCell *)tableView:(UITableView *)tableView
         cellForRowAtIndexPath:(NSIndexPath *)indexPath {
  TableViewCell *cell =
      [tableView dequeueReusableCellWithIdentifier:@"cellreusedID"
                                      forIndexPath:indexPath];

  NSString *sticker = [self.stickers objectAtIndex:indexPath.row];
  cell.sticker.text = sticker;
  return cell;
}

- (BOOL)tableView:(UITableView *)tableView
    canEditRowAtIndexPath:(NSIndexPath *)indexPath {
  // Return NO if you do not want the specified item to be editable.

  return YES;
}

- (void)tableView:(UITableView *)tableView
    didSelectRowAtIndexPath:(NSIndexPath *)indexPath {
  self.stickerBlock([self.stickers objectAtIndex:indexPath.row]);
  [self dismissViewControllerAnimated:true completion:nil];
}

/*
// Override to support editing the table view.
- (void)tableView:(UITableView *)tableView
commitEditingStyle:(UITableViewCellEditingStyle)editingStyle
forRowAtIndexPath:(NSIndexPath *)indexPath { if (editingStyle ==
UITableViewCellEditingStyleDelete) {
        // Delete the row from the data source
        [tableView deleteRowsAtIndexPaths:@[indexPath]
withRowAnimation:UITableViewRowAnimationFade]; } else if (editingStyle ==
UITableViewCellEditingStyleInsert) {
        // Create a new instance of the appropriate class, insert it into the
array, and add a new row to the table view
    }
}
*/

/*
// Override to support rearranging the table view.
- (void)tableView:(UITableView *)tableView moveRowAtIndexPath:(NSIndexPath
*)fromIndexPath toIndexPath:(NSIndexPath *)toIndexPath {
}
*/

/*
// Override to support conditional rearranging of the table view.
- (BOOL)tableView:(UITableView *)tableView canMoveRowAtIndexPath:(NSIndexPath
*)indexPath {
    // Return NO if you do not want the item to be re-orderable.
    return YES;
}
*/

/*
#pragma mark - Navigation

// In a storyboard-based application, you will often want to do a little
preparation before navigation
- (void)prepareForSegue:(UIStoryboardSegue *)segue sender:(id)sender {
    // Get the new view controller using [segue destinationViewController].
    // Pass the selected object to the new view controller.
}
*/

@end
