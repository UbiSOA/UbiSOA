//
//  GeolocationViewController.h
//  UbiSOA
//
//  Created by Edgardo on 22/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GeolocationMap.h"
#import "GeolocationWiFiSpotter.h"
#import "ASIFormDataRequest.h"
#import "JSON.h"
#import "GeolocationLogViewController.h"


@interface GeolocationViewController : UIViewController <UIScrollViewDelegate, GeolocationWiFiSpotterDelegate, UIActionSheetDelegate> {
	NSNetService *service;
	GeolocationMap *map;
	IBOutlet UIScrollView *scrollView;
	UIImageView *imageView, *indicatorView, *errorRangeView;
	UBGeolocationActionType action;
	UBGeolocationMode mode;
	CGPoint indicatorCenter;
	float errorRange;
	BOOL dontHideIndicators;
	GeolocationLogViewController *logView;
	NSString *log;
}

@property (nonatomic, retain) NSNetService *service;
@property (nonatomic, retain) GeolocationMap *map;
@property (nonatomic, retain) UIScrollView *scrollView;
@property (nonatomic, retain) NSString *log;

- (void)tapIn:(CGPoint)point;
- (void)moveIndicatorViewAnimated:(BOOL)animate;
- (void)animateEstimationButton:(BOOL)animate andDisableIt:(BOOL)disable;
- (void)setStatusText:(NSString *)newStatus;
- (UIImage *)createErrorRangeImage:(float)width;
- (void)updateErrorRangeAnimated:(BOOL)animate;
- (IBAction)estimateCurrentLocation:(id)sender;
- (void)performEstimationOfCurrentLocation;
- (void)performTraining;
- (IBAction)showLog:(id)sender;

@end
