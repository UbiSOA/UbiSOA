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


@interface GeolocationViewController : UIViewController <UIScrollViewDelegate, GeolocationWiFiSpotterDelegate> {
	NSNetService *service;
	GeolocationMap *map;
	IBOutlet UIScrollView *scrollView;
	UIImageView *imageView;
	UBGeolocationActionType action;
}

@property (nonatomic, retain) NSNetService *service;
@property (nonatomic, retain) GeolocationMap *map;
@property (nonatomic, retain) UIScrollView *scrollView;

- (void)animateEstimationButton:(BOOL)animate andDisableIt:(BOOL)disable;
- (IBAction)estimateCurrentLocation:(id)sender;
- (void)performEstimationOfCurrentLocation;

@end
