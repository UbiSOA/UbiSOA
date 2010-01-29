//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 22/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationViewController.h"


@implementation GeolocationViewController
@synthesize service, map, scrollView, log;


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
	
	errorRange = 6.0;
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
	
	// Configuring the Log
	logView = [[GeolocationLogViewController alloc] initWithNibName:@"GeolocationLogViewController" bundle:[NSBundle mainBundle]];
	[logView setTitle:@"Log"];
	self.log = @"";
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)dealloc {
	[[GeolocationWiFiSpotter sharedInstance] setDelegate:nil];
	[service release];
	[map release];
	[scrollView release];
	[logView release];
	[log release];
    [super dealloc];
}

- (void)viewDidAppear:(BOOL)animated {
	[super viewDidAppear:animated];
	if (logView.textView.text != nil) self.log = logView.textView.text;
	if (mode == UBTrackingGeolocationMode) {
		action = UBTrackingGeolocationActionType;
		[[GeolocationWiFiSpotter sharedInstance] scan];
		[self setStatusText:@"Capturing signal data…"];
	}
}

#pragma mark -
#pragma mark UIScrollView delegate methods

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)aScrollView {
	if (!dontHideIndicators) indicatorView.alpha = errorRangeView.alpha = 0.0;
	return imageView;
}

- (void)scrollViewDidEndZooming:(UIScrollView *)aScrollView withView:(UIView *)view atScale:(float)scale {
	scrollView.tag = 0;
	[self moveIndicatorViewAnimated:NO];
	[UIView beginAnimations:@"ShowIndicators" context:nil];
	if (!dontHideIndicators) indicatorView.alpha = errorRangeView.alpha = 1.0;
	[UIView commitAnimations];
}

- (void)tapIn:(CGPoint)point {
	if (action > 0) return;
	
	if (mode == UBTrainingGeolocationMode) {
		action = UBTrainingGeolocationActionType;
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"You are about to send training data for the specified location. Do you want to continue?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Yes, Send Data", nil];
		[actionSheet showInView:self.view];
		[actionSheet release];
	}
	
	if (mode == UBTrainingGeolocationMode || mode == UBTestingGeolocationMode) {
		[self animateEstimationButton:YES andDisableIt:YES];
		errorRange = 6;
		[self updateErrorRangeAnimated:YES];
		indicatorCenter = CGPointMake(point.x / imageView.frame.size.width, point.y / imageView.frame.size.height);
		[self moveIndicatorViewAnimated:YES];
	}
	
	if (mode == UBTestingGeolocationMode) {
		action = UBTestingGeolocationActionType;
		[[GeolocationWiFiSpotter sharedInstance] scan];
		[self setStatusText:@"Capturing signal data…"];
	}
}

- (void)moveIndicatorViewAnimated:(BOOL)animate {
	if (animate) [UIView beginAnimations:@"MoveIndicators" context:nil];
	indicatorView.center = CGPointMake(indicatorCenter.x * imageView.frame.size.width, indicatorCenter.y * imageView.frame.size.height);
	errorRangeView.center = indicatorView.center;
	[self updateErrorRangeAnimated:animate];
	if (animate) [UIView commitAnimations];
}

#pragma mark -
#pragma mark UIActionSheet delegate methods

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
	if (action == UBTrainingGeolocationActionType) {
		if (buttonIndex == actionSheet.cancelButtonIndex) {
			[self animateEstimationButton:NO andDisableIt:NO];
			action = 0;
		} else {
			[[GeolocationWiFiSpotter sharedInstance] scan];
			[self setStatusText:@"Capturing signal data…"];
		}
	}
}

#pragma mark -
#pragma mark UISearchBar delegate methods

- (void)searchBar:(UISearchBar *)searchBar selectedScopeButtonIndexDidChange:(NSInteger)selectedScope {
	mode = selectedScope;
	switch (mode) {
		case UBTrackingGeolocationMode:
			[self animateEstimationButton:NO andDisableIt:YES];
			break;
		default:
			[self animateEstimationButton:NO andDisableIt:NO];
			break;
	}
	[self setStatusText:@"Double tap on the map"];
	action = 0;
	
	if (mode == UBTrackingGeolocationMode) {
		if (action > 0) return; action = UBTrackingGeolocationActionType;
		[[GeolocationWiFiSpotter sharedInstance] scan];
		[self setStatusText:@"Capturing signal data…"];
	}
	
	NSString *modeString = @"";
	switch (mode) {
		case UBTrainingGeolocationMode: modeString = @"Training"; break;
		case UBTrackingGeolocationMode: modeString = @"Tracking"; break;
		case UBTestingGeolocationMode: modeString = @"Testing"; break;
	}
	self.log = [self.log stringByAppendingFormat:@"\nMode now is: %@\n\n", modeString];
}

#pragma mark -
#pragma mark GeolocationWiFiSpotter delegate methods

- (void)spotterDidScan {
	switch (action) {
		case UBTestingGeolocationActionType:
		case UBTrackingGeolocationActionType:
		case UBLocateGeolocationActionType:
			[self performEstimationOfCurrentLocation]; break;
		case UBTrainingGeolocationActionType: [self performTraining]; break;
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
	dontHideIndicators = YES;
	float range = errorRange * scrollView.zoomScale;
	dontHideIndicators = NO;
	if (animate) [UIView beginAnimations:@"ErrorRangeChange" context:nil];
	[errorRangeView setImage:[self createErrorRangeImage:range]];
	[errorRangeView setFrame:CGRectMake(indicatorCenter.x * imageView.frame.size.width - range / 2.0, indicatorCenter.y * imageView.frame.size.height - range / 2.0, range, range)];
	if (animate) [UIView commitAnimations];
}

- (IBAction)showLog:(id)sender {
	action = 0;
	[self.navigationController pushViewController:logView animated:YES];
	logView.textView.text = self.log;
	logView.mapName = map.name;
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
	[self setStatusText:@"Capturing signal data…"];
	[self animateEstimationButton:YES andDisableIt:NO];
}

- (void)performEstimationOfCurrentLocation {
	if (action == 0) return;
	[self setStatusText:@"Sending request…"];	
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
	
 	NSURL *url = [NSURL URLWithString:[[NSString stringWithFormat:@"http://%@:%d/%@/%@", [self.service hostName], [self.service port], [[GeolocationWiFiSpotter sharedInstance] signalData], kPlatform] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
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
		errorRange = 6;
		action = 0;
	} else {
		SBJsonParser *parser = [SBJsonParser new];
		NSDictionary *obj = [parser objectWithString:[request responseString]];
		
		CGPoint originalCenter = indicatorCenter;		
		indicatorCenter = CGPointMake([[obj valueForKey:@"latitude"] floatValue], [[obj valueForKey:@"longitude"] floatValue]);
		[parser release];
		dontHideIndicators = YES;
		CGPoint offset = CGPointMake(indicatorCenter.x * imageView.frame.size.width - scrollView.frame.size.width / 2.0, indicatorCenter.y * imageView.frame.size.height - scrollView.frame.size.height / 2.0);
		
		switch (action) {
			case UBLocateGeolocationActionType:
				self.log = [self.log stringByAppendingFormat:@"Estimation:\t%.7f\t%.7f\n", indicatorCenter.x, indicatorCenter.y];
				break;
			case UBTrackingGeolocationActionType:
				self.log = [self.log stringByAppendingFormat:@"Tracking:\t%.7f\t%.7f\n", indicatorCenter.x, indicatorCenter.y];
				break;
			case UBTestingGeolocationActionType:
				self.log = [self.log stringByAppendingFormat:@"AP count: %d\nOriginal:\t%.7f\t%.7f\nEstimated:\t%.7f\t%.7f\nError:\t%.3f\n\n", [[GeolocationWiFiSpotter sharedInstance] signalDataAPCount], originalCenter.x, originalCenter.y, indicatorCenter.x, indicatorCenter.y, [GeolocationMap distanceBetweenPoint:originalCenter andThePoint:indicatorCenter]];
				break;
		}
				
		[UIView beginAnimations:@"CenterLocation" context:nil];
		[scrollView setContentOffset:offset];
		[self moveIndicatorViewAnimated:NO];
		[UIView commitAnimations];
		dontHideIndicators = NO;
		[self setStatusText:@"Location estimated"];
		if (action == UBTrackingGeolocationActionType)
			[self setStatusText:@"Tracking your location…"];
		if (action == UBTestingGeolocationActionType)
			[self setStatusText:[NSString stringWithFormat:@"Estimation error: %.2f m", [GeolocationMap distanceBetweenPoint:originalCenter andThePoint:indicatorCenter]]];
		errorRange = random() % 100 + 50;
	}
	
	[self updateErrorRangeAnimated:YES];
	[self animateEstimationButton:NO andDisableIt:action == UBTrackingGeolocationActionType];
	
	if (action == UBTrackingGeolocationActionType)
		[[GeolocationWiFiSpotter sharedInstance] scan];
	else action = 0;
}

#pragma mark -
#pragma mark Training

- (void)performTraining {
	if (action != UBTrainingGeolocationActionType) return;
	[self setStatusText:@"Sending request…"];
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
	
	NSURL *url = [NSURL URLWithString:[[NSString stringWithFormat:@"http://%@:%d", [self.service hostName], [self.service port]] stringByAddingPercentEscapesUsingEncoding:NSUTF8StringEncoding]];
	ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:url];
	[request setPostValue:[NSString stringWithFormat:@"%f", indicatorCenter.x] forKey:@"latitude"];
	[request setPostValue:[NSString stringWithFormat:@"%f", indicatorCenter.y] forKey:@"longitude"];
	[request setPostValue:[[GeolocationWiFiSpotter sharedInstance] signalData] forKey:@"signalData"];
	[request setPostValue:kPlatform forKey:@"platform"];
	[request start];
	
	NSLog(@"%@", [request responseHeaders]);
	NSLog(@"Status Code: %d, Status Message: %@", [request responseStatusCode], [request responseStatusMessage]);
	NSLog(@"%@", [request responseString]);
	
	if ([request responseStatusCode] != 201) {
		UIAlertView *alert = [[[UIAlertView alloc] initWithTitle:@"Request Error" message:[request responseString] delegate:nil cancelButtonTitle:@"OK" otherButtonTitles:nil] autorelease];
		[alert show];
	} else self.log = [self.log stringByAppendingFormat:@"Training:\t%.7f\t%.7f\n", indicatorCenter.x, indicatorCenter.y];

	[self setStatusText:[request responseString]];
	
	[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];	
	[self animateEstimationButton:NO andDisableIt:NO];
	action = 0;
}

@end
