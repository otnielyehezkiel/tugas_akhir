<?php
defined('BASEPATH') OR exit('No direct script access allowed');

class Maps extends CI_Controller{

	public function __construct()
    {
        parent::__construct();
        $this->load->model('acc_model');
        $this->load->helper('url_helper');
        $this->load->helper('url');
    }

	public function index(){
		$this->load->library('googlemaps');

		//$loc = $this->acc_model->getLocation();

	   	$config['center'] = '-7.2859516, 112.795845';
		$config['zoom'] = '13';
		$config['map_height'] = '550px';
		$config['maxzoom'] = '20';
		$config['minify'] ='TRUE';
		// $config['onclick'] = 'document.getElementById(\'text\').innerHTML = event.latLng.lat() + \', \' + event.latLng.lng();';
		$this->googlemaps->initialize($config);
		/*Polyline
		$polyline = array();
		$polyline['points'] = array();
		foreach($loc as $row){
			array_push($polyline['points'],"{$row->lat}, {$row->lon}");
		}
		$this->googlemaps->add_polyline($polyline);*/
		/*Marker*/
		$loc = $this->acc_model->getBumpLocation();
		$marker = array();
	   	foreach ($loc as $row){
	   		//echo $row->jenis_id;
	   		if($row->jenis_id == 3){
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
			$marker['infowindow_content'] = "{$row->id}";
			$marker['draggable'] = FALSE;
			/*$url = base_url('/maps/graph_new/'). "?id={$row->id}";
			$marker['ondblclick'] = "window.open('".$url."','_blank')";*/
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
		    	// event.preventDefault();
			";
			$this->googlemaps->add_marker($marker);
		}		
		$data['map'] = $this->googlemaps->create_map();
		$this->load->view('gmaps',$data);	
	}

	public function addToCsv(){
		$id = $this->input->get('id');

		$fp = fopen('./assets/images/file_id.txt', 'w');
			fwrite($fp, $id);
		fclose($fp);

		$acc = $this->acc_model->getAccel($id);
		if(empty($acc)) {
			die();
		}
		$axisZ = array();
		$waktu = array();
		$timestamp = $acc[0]['waktu'];

		foreach ($acc as &$row) {
			$row['waktu'] -= $timestamp;
			$row['waktu'] /= 1000;
			array_push($axisZ,$row['z']);
		}
		$fp = fopen('./assets/images/file.csv', 'w');
		foreach ($acc as $row) {
			unset($row['location_id']);
			fputcsv($fp, $row);
		}
		fclose($fp);
	}

	public function graph_new(){
		$id = $this->input->get('id');
		$acc = $this->acc_model->getAccel($id);
		if(empty($acc)) {
			echo "Data Kosong";
			die();
		}
		$jenis = $this->acc_model->getJenis($id)[0]->jenis_id;
		$validasi = $this->acc_model->getJenis($id)[0]->validasi;
		$axisZ = array();
		$waktu = array();
		$timestamp = $acc[0]['waktu'];

		foreach ($acc as &$row) {
			$row['waktu'] -= $timestamp;
			$row['waktu'] /= 1000;
			array_push($axisZ,$row['z']);
		}
		$fp = fopen('./assets/images/file.csv', 'w');
		foreach ($acc as $row) {/*
			if($row['x']==null) unset($row['x']);
			if($row['y']==null) unset($row['y']);*/
			unset($row['location_id']);
			fputcsv($fp, $row);
		}
		fclose($fp);

		/*Statistic*/
		$this->load->library('Statistics');
		$statistics = new Statistics();
		$statistics->addSet($axisZ);
		$data['std'] = number_format((float)$statistics->getStdDeviation(), 3, '.', '');
		$data['mean'] = number_format((float)$statistics->getMean(), 3, '.', '');
		$data['max'] = number_format((float)$statistics->getMax(), 3, '.', '');
		$data['min'] = number_format((float)$statistics->getMin(), 3, '.', '');
		$data['diffmaxmin'] = number_format((float)$statistics->getMax() - $statistics->getMin(), 3, '.', '');
		$data['id'] = $id;
		$data['count'] = count($axisZ);
		$data['durasi'] =  $acc[count($axisZ)-1]['waktu'] - $acc[0]['waktu'];
		$data['validasi'] = ($validasi == 0 ? 'Belum Divalidasi' : 'Sudah Divalidasi');
		$data['timestamp'] = $timestamp;
		if($jenis == 3) 
			$data['jenis'] = "Bump";
		elseif($jenis == 2) 
			$data['jenis'] = "Hole";
		elseif($jenis  == 4) 
			$data['jenis'] = "Break";
		elseif($jenis == 5) 
			$data['jenis'] = "True Hole";
		elseif($jenis  == 6) 
			$data['jenis'] = "True Bump";
		elseif($jenis == 1) 
			$data['jenis'] = "Normal";

		$this->load->view('chart',$data);
	}

	public function updateValidasi(){
		$data['id'] = $this->input->get('id');
		$data['value'] = $this->input->get('value');

		if($this->acc_model->updateValidasi($data))
			echo "berhasil";
	}

	public function verifikasi(){
		$data['value'] = $this->input->post('option');
		$data['id'] = $this->input->post('id');
		if($this->acc_model->updateValidasi($data))
			echo "berhasil";
	}

	public function lihatData(){
		$data = $this->acc_model->lihatData();
		$xdata = array();
		$xdata['id'] = array();
		$xdata['lat'] = array();
		$xdata['lon'] = array();
		$xdata['jenis_id'] = array();
		$xdata['tanggal'] = array();
		$c = 0;
		foreach ($data as $row) {
			$tanggal = $this->acc_model->getTanggal($row['id']);
			if($tanggal){
				$xdata['tanggal'][]=$tanggal[0]['waktu'];
			} 
			else{
				$xdata['tanggal'][]=0;
			}
			$xdata['id'][]=$row['id'];
			$xdata['lat'][]=$row['lat'];
			$xdata['lon'][]=$row['lon'];
			$xdata['jenis_id'][]=$row['jenis_id'];
			$c++;
		}
		//var_dump($data);
		$xdata['total'] = $c;
		$this->load->view('lihatdata',$xdata);
	}
}
