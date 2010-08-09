$(function() {

	$('#columns').height(window.innerHeight);
	window.onresize = function() {
		$('#columns').height(window.innerHeight);
	};

});