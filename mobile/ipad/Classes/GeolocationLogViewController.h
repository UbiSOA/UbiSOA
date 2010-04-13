//
//  GeolocationLogViewController.h
//  UbiSOA
//
//  Created by Edgardo on 28/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <MessageUI/MessageUI.h>


@interface GeolocationLogViewController : UIViewController <MFMailComposeViewControllerDelegate> {
	UITextView *textView;
	NSString *mapName;
}

@property (nonatomic, retain) IBOutlet UITextView *textView;
@property (nonatomic, retain) NSString *mapName;

- (IBAction)clear:(id)sender;
- (IBAction)export:(id)sender;

@end
