<?php

class Main extends Controller {
	function Welcome() {
		parent::Controller();
	}
	
	function index() {
		header('Location: http://github.com/eaviles/UbiSOA');
	}
}

?>
