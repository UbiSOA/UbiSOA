//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 22/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationViewController.h"


@implementation GeolocationViewController
@synthesize service, map, scrollView;


- (void)viewDidLoad {
    [super viewDidLoad];
	
	// Load map image to the scroll.
	imageView = [[UIImageView alloc] initWithImage:[UIImage imageWithContentsOfFile:[NSHomeDirectory() stringByAppendingFormat:@"/Documents/%@", map.file]]];
	scrollView.contentSize = CGSizeMake(imageView.frame.size.width, imageView.frame.size.height);
	scrollView.maximumZoomScale = 2.0;
	scrollView.minimumZoomScale = (imageView.frame.size.width < imageView.frame.size.height)?
	scrollView.frame.size.width / imageView.frame.size.width:
	scrollView.frame.size.height / imageView.frame.size.height;
	scrollView.clipsToBounds = YES;
	scrollView.delegate = self;
	[scrollView addSubview:imageView];
	[scrollView setZoomScale:scrollView.minimumZoomScale];
}


- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc {
	[service release];
	[map release];
	[scrollView release];
	[imageView release];
    [super dealloc];
}

#pragma mark -
#pragma mark UIScrollView delegate methods

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
	return imageView;
}


@end
