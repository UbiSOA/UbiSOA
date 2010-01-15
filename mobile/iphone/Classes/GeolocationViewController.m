//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 15/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationViewController.h"

@implementation GeolocationViewController
@synthesize scrollView, activityView, imageView;

- (UIView *)viewForZoomingInScrollView:(UIScrollView *)scrollView {
	[imageTrainDot setAlpha:0.0];
	return imageView;
}

- (void)viewDidLoad {
    [super viewDidLoad];
	
	UIImageView *tempImageView = [[UIImageView alloc] initWithImage:[UIImage imageNamed:kMapFile]];
	self.imageView = tempImageView;
	[tempImageView release];
	
	scrollView.contentSize = CGSizeMake(imageView.frame.size.width, imageView.frame.size.height);
	scrollView.maximumZoomScale = 2.0;
	scrollView.minimumZoomScale = (imageView.frame.size.width < imageView.frame.size.height)?
		scrollView.frame.size.width / imageView.frame.size.width:
		scrollView.frame.size.height / imageView.frame.size.height;
	scrollView.clipsToBounds = YES;
	scrollView.delegate = self;
	[scrollView addSubview:imageView];
	[scrollView setZoomScale:scrollView.minimumZoomScale];
	
	imageTrainDot = [[UIImageView alloc] initWithImage:[UIImage imageNamed:@"ico-traindot.png"]];
	[scrollView addSubview:imageTrainDot];
	[imageTrainDot setAlpha:0.0];
	
	activityView = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhiteLarge];
	activityView.hidesWhenStopped = YES;
	[scrollView addSubview:activityView];
	
	spotter = [[GeolocationWiFiSpotter alloc] init];
	[spotter open];
	[[NSNotificationCenter defaultCenter] addObserver:self selector:@selector(scanDone) name:kSpotterNotif object:nil];
	
	mode = UBTrainingGeolocationMode;
}

- (void)didReceiveMemoryWarning {
    [super didReceiveMemoryWarning];
}

- (void)viewDidUnload {
}

- (void)dealloc {
    [super dealloc];
	[spotter close];
	[imageView dealloc];
	[imageTrainDot dealloc];
	[activityView dealloc];
	[scrollView dealloc];
	[spotter release];
}

- (void)tapIn:(CGPoint)point {	
	if (mode == UBTrainingGeolocationMode) {
		lastPoint = point;
		[scrollView setUserInteractionEnabled:NO];
		[activityView setCenter:point];
		[activityView startAnimating];
		[imageTrainDot setCenter:point];
		[imageTrainDot setAlpha:1.0];
		
		UIActionSheet *actionSheet = [[UIActionSheet alloc] initWithTitle:@"You are about to send training data for the specified location. Do you want to continue?" delegate:self cancelButtonTitle:@"Cancel" destructiveButtonTitle:nil otherButtonTitles:@"Yes, Send Data", nil];
		[actionSheet showInView:[self parentViewController].view];
		[actionSheet release];
	}
	
	if (mode == UBTrackingGeolocationMode) {
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
		[spotter scan];
	}
}

- (void)actionSheet:(UIActionSheet *)actionSheet willDismissWithButtonIndex:(NSInteger)buttonIndex {
	if (buttonIndex != [actionSheet cancelButtonIndex]) {
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:YES];
		[spotter scan];
		return;
	}
	
	[scrollView setUserInteractionEnabled:YES];
	[activityView stopAnimating];
	[imageTrainDot setAlpha:0.0];
}

- (void)scanDone {
	NSLog(@"LISTO!!!");
	NSLog(@"X:%f Y:%f", lastPoint.x, lastPoint.y);
	NSString *signalData = @"";
	
	for (int i = 0, n = CFArrayGetCount([spotter networks]); i < n; i++) {
		CFDictionaryRef network = CFArrayGetValueAtIndex([spotter networks], i);
		NSLog(@"%@\t%@", CFDictionaryGetValue(network, @"SSID_STR"), CFDictionaryGetValue(network, @"RSSI"));

		signalData = [NSString stringWithFormat:@"%@%@=%@,", signalData, CFDictionaryGetValue(network, @"BSSID"), CFDictionaryGetValue(network, @"RSSI")];
	}
	
	if (mode == UBTrainingGeolocationMode) {	
		NSURL *url = [NSURL URLWithString:@"http://158.97.88.156:8310/"];
		ASIFormDataRequest *request = [ASIFormDataRequest requestWithURL:url];
		[request setPostValue:[NSString stringWithFormat:@"%f", lastPoint.x / imageView.frame.size.width] forKey:@"latitude"];
		[request setPostValue:[NSString stringWithFormat:@"%f", lastPoint.y / imageView.frame.size.height] forKey:@"longitude"];
		[request setPostValue:[signalData substringToIndex:([signalData length] - 1)] forKey:@"signalData"];
		[request setPostValue:@"iPhone" forKey:@"platform"];
		[request start];
	
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
		NSLog([request responseString]);
	}
	
	if (mode == UBTrackingGeolocationMode) {
		NSURL *url = [NSURL URLWithString:[NSString stringWithFormat:@"%@%@%@", @"http://158.97.88.156:8310/", [signalData substringToIndex:([signalData length] - 1)], @"/iPhone?json"]];
		ASIHTTPRequest *request = [ASIHTTPRequest requestWithURL:url];
		[request start];
		
		CFShow(url);
		CFShow(request);
		
		[[UIApplication sharedApplication] setNetworkActivityIndicatorVisible:NO];
		NSLog([request responseString]);
		
		if ([request responseStatusCode] != 200) {
			UIAlertView *alert = [[UIAlertView alloc] initWithTitle:@"Communication Problem" message:@"The geolocation service cannot be located or the location cannot be estimated." delegate:self cancelButtonTitle:nil otherButtonTitles:@"OK", nil];
			[alert show];
			[alert release];
		} else {
			SBJsonParser *parser = [SBJsonParser new];
			NSDictionary *obj = [parser objectWithString:[request responseString]];
			
			CGPoint point = CGPointMake(0.0, 0.0);
			point.x = [[obj valueForKey:@"latitude"] floatValue] * imageView.frame.size.width;
			point.y = [[obj valueForKey:@"longitude"] floatValue] * imageView.frame.size.height;
			CGPoint offset = CGPointMake(point.x - scrollView.frame.size.width / 2.0, point.y - scrollView.frame.size.height / 2.0);
			
			[UIView beginAnimations:@"Move" context:nil];
			[UIView setAnimationDelay:1.0];
			[UIView setAnimationCurve:UIViewAnimationCurveEaseOut];
			[scrollView setContentOffset:offset];
			[imageTrainDot setCenter:point];
			[imageTrainDot setAlpha:1.0];
			[UIView commitAnimations];
			
			[parser release];
		}
	}
	
	[scrollView setUserInteractionEnabled:YES];
	[activityView stopAnimating];
}

- (void)segmentAction:(id)sender {
	mode = [(UISegmentedControl *)sender selectedSegmentIndex];
}

@end
