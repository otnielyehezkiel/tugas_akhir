<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Verifikasi extends CI_Controller{

	public function __construct()
    {
        parent::__construct();
        $this->load->helper('url_helper');
        $this->load->helper('url');
        $this->load->library('session');
    }

    public function index(){
    	$this->load->view('login','');
    	
    }

    public function login(){
    	$user = $this->input->post('user');
      	$pass  = $this->input->post('pass');

      	if($pass =="surveyor" && $user=="surveyor"){

      	}
    }

}