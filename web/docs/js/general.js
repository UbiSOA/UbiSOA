function initSnippet() {
	$('.snippet code').hover(function() {
		$(this).css('overflow', 'auto').css('text-overflow', 'inherit');
	}, function() {
		$(this).css('overflow', 'hidden').css('text-overflow', 'ellipsis');
	});
}

$(function() {
	initSnippet();
});