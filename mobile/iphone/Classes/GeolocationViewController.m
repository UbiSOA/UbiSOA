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
	[imageView release];
	
	indicatorCenter = CGPointMake(0.5, 0.5);
	
	errorRange = 10.0;
	errorRangeView = [[UIImageView alloc] initWithImage:[self createErrorRangeImage:errorRange]];
	errorRangeView.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	[scrollView addSubview:errorRangeView];
	[errorRangeView release];
	
	// Configuring the location indicator
	indicatorView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"IndicatorLocation.png"]];

	indicatorView.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	[scrollView addSubview:indicatorView];
	[indicatorView release];
	
	// Configuring WiFi spotter
	[[GeolocationWiFiSpotter sharedInstance] setDelegate:self];
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc {
	[[GeolocationWiFiSpotter sharedInstance] setDelegate:nil];
	[service release];
	[map release];
	[scrollView release];
    [super dealloc];
}

#pragma mark -
#pragma mark UIScrollView delegate methods

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)aScrollView {
	[indicatorView setAlpha:0.0];
	[errorRangeView setAlpha:0.0];
	return imageView;
}

- (void)scrollViewDidEndZooming:(UIScrollView *)scrollView withView:(UIView *)view atScale:(float)scale {
	indicatorView.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	errorRangeView.center = indicatorView.center;
	[self updateErrorRangeAnimated:NO];
	
	[UIView beginAnimations:@"show" context:nil];
	[indicatorView setAlpha:1.0];
	[errorRangeView setAlpha:1.0];
	[UIView commitAnimations];
}

- (void)tapIn:(CGPoint)point {
	NSLog(@"TAP IN %f,%f", point.x, point.y);
}

#pragma mark -
#pragma mark UISearchBar delegate methods

- (void)searchBar:(UISearchBar *)searchBar selectedScopeButtonIndexDidChange:(NSInteger)selectedScope {
	mode = selectedScope;
	[self setStatusText:@"Double tap on the map"];
}

#pragma mark -
#pragma mark GeolocationWiFiSpotter delegate methods

- (void)spotterDidScan {
	switch (action) {
		case UBLocateGeolocationActionType:
			[self performEstimationOfCurrentLocation];
			break;
	}
}

#pragma mark -
#pragma mark Common methods

- (void)animateEstimationButton:(BOOL)animate andDisableIt:(BOOL)disable {
	UIBarButtonItem *button = [[(UIToolbar *)[self.view viewWithTag:1] items] objectAtIndex:0];
	[button setEnabled:!disable];
	[button setImage:(animate)? nil: [UIImage imageNamed:@"BarIconLocation.png"]];
	if (animate) [button setWidth:37];
	[button setStyle:(animate)? UIBarButtonItemStyleDone: UIBarButtonItemStyleBordered];
	UIActivityIndicatorView *act = (UIActivityIndicatorView *)[self.view viewWithTag:2];
	if (animate) [act startAnimating]; else [act stopAnimating];
}

- (void)setStatusText:(NSString *)newStatus {
	UILabel *label = (UILabel *)[self.view viewWithTag:3];
	[label setText:newStatus];
}

- (UIImage *)createErrorRangeImage:(float)width {
	UIGraphicsBeginImageContext(CGSizeMake(width + 2, width + 2));
	CGContextRef context = UIGraphicsGetCurrentContext();
	
	CGContextSetFillColorWithColor(context, [[kShadowColor colorWithAlphaComponent:0.1] CGColor]);
	CGContextAddEllipseInRect(context, CGRectMake(1, 1, width, width));
	CGContextFillPath(context);
	
	CGContextSetLineWidth(context, 2);
	CGContextSetStrokeColorWithColor(context, [[kShadowColor colorWithAlphaComponent:0.4] CGColor]);
	CGContextAddEllipseInRect(context, CGRectMake(2, 2, width - 2, width - 2));
	CGContextStrokePath(context);
	
	UIImage *theImage = UIGraphicsGetImageFromCurrentImageContext();
	UIGraphicsEndImageContext();
	return theImage;
}

- (void)updateErrorRangeAnimated:(BOOL)animate {
	float orgAlpha = indicatorView.alpha;
	float range = errorRange * scrollView.zoomScale;
	indicatorView.alpha = orgAlpha;
	errorRangeView.alpha = orgAlpha;
	if (animate) [UIView beginAnimations:@"ErrorRangeChange" context:nil];
	[errorRangeView setImage:[self createErrorRangeImage:range]];
	[errorRangeView setFrame:CGRectMake(indicatorCenter.x * imageView.frame.size.width - range / 2.0, indicatorCenter.y * imageView.frame.size.height - range / 2.0, range, range)];
	if (animate) [UIView commitAnimations];
}

#pragma mark -
#pragma mark Estimate current location

- (IBAction)estimateCurrentLocation:(id)sender {
	if (action == UBLocateGeolocationActionType) {
		[self animateEstimationButton:NO andDisableIt:NO];
		action = 0;
		return;
	}
	
	if (action > 0) return; action = UBLocateGeolocationActionType;
	
	[[GeolocationWiFiSpotter sharedInstance] scan];
	[self animateEstimationButton:YES andDisableIt:NO];
}

- (void)performEstimationOfCurrentLocation {
	if (action != UBLocateGeolocationActionType) return;
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
	
 	NSURL *url = [NSURL URLWithString:[[NSString stringWithFormat:@"http://%@:%d/%@/%@", [self.service hostName], [self.service port], [[GeolocationWiFiSpotter sharedInstance] signalData], [[UIDevice currentDevice] model]] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
	ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
	[request start];

	NSLog(@"%@", [request responseHeaders]);
	NSLog(@"Status Code: %d, Status Message: %@", [request responseStatusCode], [request responseStatusMessage]);
	NSLog(@"%@", [request responseString]);
	
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
	
	if ([request responseStatusCode] != 200) {
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Request Error" message:[request responseString] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[alert show];
		[self setStatusText:[request responseString]];
	} else {
		// TO DO: Show current location in GUI
	}
	
	errorRange = 200;
	[self updateErrorRangeAnimated:YES];
	
	[self animateEstimationButton:NO andDisableIt:NO];	
	action = 0;
}

@end
