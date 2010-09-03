<?php

class Interfaces extends Controller {
	function Interfaces() {
		parent :: Controller();
		$this -> load -> helper('xml_helper');
		$this -> load -> helper('html_helper');
	}
	
	function index($file = '') {
		$xml_path = $file? '../pub/interfaces/v1/'.$file.'.wadl': '';
		$schema_path = '../pub/schemas/inc/wadl.xsd';
		$data['file'] = $file.'.wadl';
		$data['xml'] = simplexml_load_valid($xml_path, $schema_path, true);
		$this -> load -> view('interface', $data);
	}
}
