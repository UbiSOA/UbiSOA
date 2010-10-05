function initSnippet() {
	$('.snippet code').attr('title', 'Click to expand the code.');
	$('body').click(function(event) {
		if (event.target.tagName == 'CODE')
			$(event.target).addClass('expanded');
		else $('.snippet code').removeClass('expanded');
	});
}

$(function() {
	initSnippet();
});
