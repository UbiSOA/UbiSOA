//
//  GeolocationViewController.h
//  UbiSOA
//
//  Created by Edgardo on 22/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import <UIKit/UIKit.h>
#import "GeolocationMap.h"


@interface GeolocationViewController : UIViewController <UIScrollViewDelegate> {
	NSNetService *service;
	GeolocationMap *map;
	IBOutlet UIScrollView *scrollView;
	UIImageView *imageView;
}

@property (nonatomic, retain) NSNetService *service;
@property (nonatomic, retain) GeolocationMap *map;
@property (nonatomic, retain) UIScrollView *scrollView;

@end
