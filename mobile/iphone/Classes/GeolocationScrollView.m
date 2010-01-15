//
//  GeolocationScrollView.m
//  UbiSOA
//
//  Created by Edgardo on 16/12/09.
//  Copyright 2009 CICESE. All rights reserved.
//

#import "GeolocationScrollView.h"

@implementation GeolocationScrollView

- (void)touchesBegan:(NSSet *)touches withEvent:(UIEvent *)event {
	UITouch *touch = [touches anyObject];
	if ([touch tapCount] != 2) return;
	
/*	CGPoint point = [touch locationInView:self];
	float x = point.x / self.contentSize.width;
	float y = point.y / self.contentSize.height;
	[((GeolocationViewController*)[self delegate]) tapIn:CGPointMake(x, y)];*/
	[((GeolocationViewController*)[self delegate]) tapIn:[touch locationInView:self]];
}

@end
