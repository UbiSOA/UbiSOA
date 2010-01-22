//
//  GeolocationViewController.m
//  UbiSOA
//
//  Created by Edgardo on 22/01/10.
//  Copyright 2010 CICESE. All rights reserved.
//

#import "GeolocationViewController.h"


@implementation GeolocationViewController
@synthesize service, map;

/*
 // The designated initializer.  Override if you create the controller programmatically and want to perform customization that is not appropriate for viewDidLoad.
- (id)initWithNibName:(NSString *)nibNameOrNil bundle:(NSBundle *)nibBundleOrNil {
    if (self = [super initWithNibName:nibNameOrNil bundle:nibBundleOrNil]) {
        // Custom initialization
    }
    return self;
}
*/

// Implement viewDidLoad to do additional setup after loading the view, typically from a nib.
- (void)viewDidLoad {
    [super viewDidLoad];
	self.navigationItem.rightBarButtonItem = [[UIBarButtonItem alloc] initWithImage:[UIImage imageNamed:@"tool-location.png"] style:UIBarButtonItemStyleBordered target:nil action:nil];
//	UIActivityIndicatorView *act = [[UIActivityIndicatorView alloc] initWithActivityIndicatorStyle:UIActivityIndicatorViewStyleWhite];
//	[act startAnimating];
//	[act sizeToFit];
//	act.autoresizingMask =
//    (UIViewAutoresizingFlexibleLeftMargin |
//	 UIViewAutoresizingFlexibleRightMargin |
//	 UIViewAutoresizingFlexibleTopMargin |
//	 UIViewAutoresizingFlexibleBottomMargin);
//	UIBarButtonItem *b = [[UIBarButtonItem alloc] initWithCustomView:act];
//	[b setStyle:UIBarButtonItemStyleBordered];
//	self.navigationItem.rightBarButtonItem = b;
//	[b release];
//	[act release];
}

/*
// Override to allow orientations other than the default portrait orientation.
- (BOOL)shouldAutorotateToInterfaceOrientation:(UIInterfaceOrientation)interfaceOrientation {
    // Return YES for supported orientations
    return (interfaceOrientation == UIInterfaceOrientationPortrait);
}
*/

- (void)didReceiveMemoryWarning {
	// Releases the view if it doesn't have a superview.
    [super didReceiveMemoryWarning];
	
	// Release any cached data, images, etc that aren't in use.
}

- (void)viewDidUnload {
	// Release any retained subviews of the main view.
	// e.g. self.myOutlet = nil;
}


- (void)dealloc {
	[service release];
	[map release];
    [super dealloc];
}


@end
