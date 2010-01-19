//
//  GeolocationViewController.h
//  UbiSOA
//
//  Created by Edgardo on 15/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "Constants.h"
#import "GeolocationWiFiSpotter.h"
#import "ASIFormDataRequest.h"
#import "JSON.h"

@interface GeolocationViewController : UIViewController<UIScrollViewDelegate, UIActionSheetDelegate> {
	IBOutlet UIScrollView *scrollView;
	IBOutlet UIActivityIndicatorView *activityView;
	IBOutlet UINavigationItem *item;
	UIImageView *imageView, *imageTrainDot;
	GeolocationWiFiSpotter *spotter;
	CGPoint lastPoint;
	UBGeolocationMode mode;
}

@property (nonatomic, retain) UIScrollView *scrollView;
@property (nonatomic, retain) UIActivityIndicatorView *activityView;
@property (nonatomic, retain) UIImageView *imageView;
@property (nonatomic, retain) UINavigationItem *item;

- (void)tapIn:(CGPoint)point;
- (void)scanDone;
- (void)segmentAction:(id)sender;
- (void)locate:(id)sender;

@end
