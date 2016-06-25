<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Verifikasi extends CI_Controller{

	public function __construct()
    {
        parent::__construct();
        $this->load->helper('url_helper');
        $this->load->helper('url');
        $this->load->library('session');
        $this->load->helper('form');
        $this->load->library('form_validation');
    }

    public function index(){
    	$this->load->view('login','');
    }

    public function login(){

      	$this->form_validation->set_rules('user', 'user', 'trim|required');
		$this->form_validation->set_rules('pass', 'pass', 'trim|required|callback_checkPass');

		if($this->form_validation->run() == FALSE){
		 //Field validation failed.  User redirected to login page
		 redirect('verifikasi', 'refresh');
		}
		else{
			redirect('verifikasi/setmap', 'refresh');
		}
    }

    public function checkPass($pass){
    	$user = $this->input->post('user');

    	if($user == "surveyor" && $pass == "surveyor"){
    		$sess_array = array(
	         'username' => $user
	       );
	       $this->session->set_userdata('logged_in', $sess_array);
	       return true;
    	}
    	else {
    		$this->form_validation->set_message('checkPass', 'Invalid username or password');
     		return false;
    	}
    }

	public function logout(){
		$this->session->unset_userdata('logged_in');
		session_destroy();
		redirect('verifikasi', 'refresh');
	}

	public function setmap(){
		$this->load->model('acc_model');

		if($this->session->userdata('logged_in')){
			$session_data = $this->session->userdata('logged_in');
			$data['username'] = $session_data['username'];
			$this->load->library('googlemaps');

		   	$config['center'] = '-7.2859516, 112.795845';
			$config['zoom'] = '15';
			$config['map_height'] = '550px';
			$config['maxzoom'] = '20';
			$config['minify'] ='TRUE';
			$this->googlemaps->initialize($config);

			$loc = $this->acc_model->getBumpLocation();
			$marker = array();
		   	foreach ($loc as $row){
		   		if($row->validasi == 2){
		   			$marker['icon'] = base_url('/assets/images/yellow_marker.png');
		   		}
		   		elseif($row->jenis_id == 3 && $row->validasi != 2){
		   			$marker['icon'] = base_url('/assets/images/bump_marker.png');
		   		}
		   		elseif($row->jenis_id == 4){
		   			$marker['icon'] = base_url('/assets/images/break_marker.png');	
		   		}
		   		elseif($row->jenis_id == 5){
		   			$marker['icon'] = base_url('/assets/images/true_hole.png');		
		   		}
		   		elseif($row->jenis_id == 6){
					$marker['icon'] = base_url('/assets/images/true_bump.png');	
		   		}
		   		elseif($row->jenis_id == 2){ 
		   			$marker['icon'] = base_url('/assets/images/hole_marker.png');
		   		}
		   		elseif($row->jenis_id == 1){ 
		   			$marker['icon'] = base_url('/assets/images/normal_marker.png');
		   		}
		   		$marker['position'] = "{$row->lat}, {$row->lon}";
				$marker['infowindow_content'] = "Bump <br><i>id={$row->id}</i>";
				$marker['draggable'] = FALSE;
				$marker['ondblclick'] = "
					$.ajax({
				        type:'GET',
				        url:'".base_url('/maps/addToCsv?id='.$row->id)."',
				        success: function(response) {
				            g.updateOptions({ 
							    'file': 'http://128.199.235.115/project/assets/images/file.csv'
							});
						    $('#myModal').modal();
							$(document).ready(function () {
								g.resize(500, 200);
					    	});
				        }
				    });
				";
				$this->googlemaps->add_marker($marker);
			}		
			$data['map'] = $this->googlemaps->create_map();
			$this->load->view('verifikasi_maps', $data);
		}
		else{
		 redirect('verifikasi', 'refresh');
		}
	}

	public function hasil(){
		$this->load->model('acc_model');

		if($this->session->userdata('logged_in')){
			$session_data = $this->session->userdata('logged_in');
			$data['username'] = $session_data['username'];
			$this->load->library('googlemaps');

		   	$config['center'] = '-7.2859516, 112.795845';
			$config['zoom'] = '15';
			$config['map_height'] = '550px';
			$config['maxzoom'] = '20';
			$config['minify'] ='TRUE';
			$this->googlemaps->initialize($config);

			$loc = $this->acc_model->getHasil();
			$marker = array();
			$jenis = '';
		   	foreach ($loc as $row){
		   		//echo $row->jenis_id;
		   		if($row->jenis_id == 3){
		   			$marker['icon'] = base_url('/assets/images/bump_marker.png');
		   			$jenis = 'polisi tidur <br><i> id=';
		   		}
		   		elseif($row->jenis_id == 4){
		   			$marker['icon'] = base_url('/assets/images/break_marker.png');	

		   		}
		   		elseif($row->jenis_id == 5){
		   			$marker['icon'] = base_url('/assets/images/true_hole.png');		
		   		}
		   		elseif($row->jenis_id == 6){
					$marker['icon'] = base_url('/assets/images/true_bump.png');	
		   		}
		   		elseif($row->jenis_id == 2){ 
		   			$marker['icon'] = base_url('/assets/images/hole_marker.png');
		   			$jenis = 'lubang <br><i> id=';
		   		}
		   		elseif($row->jenis_id == 1){ 
		   			$marker['icon'] = base_url('/assets/images/normal_marker.png');
		   			$jenis = 'gundukan <br><i> id=';
		   		}
		   		$marker['position'] = "{$row->lat}, {$row->lon}";
				$marker['infowindow_content'] = $jenis."{$row->id}";
				$marker['draggable'] = FALSE;
				$marker['ondblclick'] = "
					$.ajax({
				        type:'GET',
				        url:'".base_url('/maps/addToCsv?id='.$row->id)."',
				        success: function(response) {
				            g.updateOptions({ 
							    'file': 'http://128.199.235.115/project/assets/images/file.csv'
							});
						    $('#myModal').modal();
							$(document).ready(function () {
								g.resize(500, 200);
					    	});
				        }
				    });
				";
				$this->googlemaps->add_marker($marker);
			}		
			$data['map'] = $this->googlemaps->create_map();
			$this->load->view('hasil_verifikasi', $data);
		}
		else{
		 redirect('verifikasi', 'refresh');
		}
	}
}