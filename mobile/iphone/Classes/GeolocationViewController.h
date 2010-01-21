//
//  GeolocationViewController.h
//  UbiSOA
//
//  Created by Edgardo on 15/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import <QuartzCore/QuartzCore.h>
#import "Constants.h"
#import "GeolocationWiFiSpotter.h"
#import "ASIFormDataRequest.h"
#import "JSON.h"
#import "GeolocationMapsViewController.h"
#import "GeolocationMapViewController.h"
#import "Database.h"

@interface GeolocationViewController : UIViewController<UIScrollViewDelegate, UIActionSheetDelegate> {
	IBOutlet UIScrollView *scrollView;
	IBOutlet UIView *noServicesView;
	UIActivityIndicatorView *activityView;
	UIImageView *imageView, *imageTrainDot;
	GeolocationWiFiSpotter *spotter;
	CGPoint lastPoint;
	UBGeolocationMode mode;
	NSNetServiceBrowser *browser;
	NSMutableArray *services;
	BOOL searchingServices;
	UIAlertView *searchingAlert;
	int activeServiceIndex;
	UIActionSheet *servicesMenu;
	GeolocationMap *selectedMap;
}

@property (nonatomic, retain) UIScrollView *scrollView;
@property (nonatomic, retain) UIView *noServicesView;
@property (nonatomic, retain) GeolocationMap *selectedMap;

- (void)tapIn:(CGPoint)point;
- (void)scanDone;
- (void)segmentAction:(id)sender;
- (void)locate:(id)sender;

// Services related methods
- (void)lookForServices:(id)sender;
- (void)chooseService;
- (void)willUseService:(int)serviceIndex;

// Maps related methods
- (void)showNewMap:(id)sender;
- (void)showMaps:(id)sender;
- (void)showMaps:(id)sender withAnimation:(BOOL)animated;

// NSNetServiceBrowser delegate methods for service browsing
- (void)netServiceBrowserWillSearch:(NSNetServiceBrowser *)browser;
- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didNotSearch:(NSDictionary *)errorDict;
- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didFindService:(NSNetService *)aNetService moreComing:(BOOL)moreComing;
- (void)netServiceBrowser:(NSNetServiceBrowser *)browser didRemoveService:(NSNetService *)aNetService moreComing:(BOOL)moreComing;

// UIActionSheet delegate methods
- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex;

// UIAlertView delegate methods
- (void)alertView:(UIAlertView *)alertView clickedButtonAtIndex:(NSInteger)buttonIndex;
- (void)alertView:(UIAlertView *)alertView didDismissWithButtonIndex:(NSInteger)buttonIndex;

@end
