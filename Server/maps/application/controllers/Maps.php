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

		/*Extract Feature and Predict with Decision Tree*/
		$data = $this->acc_model->getPredict();

		if($data){
			$id = $data[0]->id;
	        $c = 0; 
	        $flag = 0;
	        $axisZ = array();
	        $axisZ[$c] = array();
	        $axisY[$c] = array();
	        $train = array();
	        $length = count($data); 
	        $id_table = 0;
	        $this->load->library('Statistics');
	        foreach($data as $row){
	            $length--;
	            if($row->id == $id && $length!=0){
	                array_push($axisZ[$c],$row->z); 
	                array_push($axisY[$c],$row->y); 
	                if($flag == 0){
	                    $flag = 1;
	                    $jenis = $row->jenis_id;
	                    $id_table = $row->id;
	                }   
	            }
	            elseif(count($axisZ[$c])!=1) {
	                if($length == 0){
	                    array_push($axisZ[$c],$row->z); 
	                    array_push($axisY[$c],$row->y); 
	                }
	                // sumbu Z
	                $statistics = new Statistics();
	                $statistics->addSet($axisZ[$c]);
	                $stdZ = $statistics->getStdDeviation();
	                $max = $statistics->getMax();
	                $min = $statistics->getMin();
	                $deviasiZ = $max-$min;
	                $statistics = null;
	                // sumbu Y
	                $statistics = new Statistics();
	                $statistics->addSet($axisY[$c]);
	                $stdY = $statistics->getStdDeviation();
	                $max = $statistics->getMax();
	                $min = $statistics->getMin();
	                $deviasiY = $max-$min;  
	                $train[$c] = array();
	                // extract feature

	                array_push($train[$c],$stdZ,$deviasiZ,$stdY,$deviasiY,$id_table,$jenis);
	                $flag = 0;
	                $statistics = null;
	                $id = $row->id;
	                $c++;
	                $axisZ[$c] = array();   
	                $axisY[$c] = array();   
	                array_push($axisZ[$c],$row->z); 
	                array_push($axisY[$c],$row->y); 
	            }
	        }
	        // add to csv
	        $fp = fopen(APPPATH .'../assets/images/predict.csv','w');
	        foreach($train as $rows){
	            fputcsv($fp, $rows);
	        }
	        fclose($fp);
	        /*Decision Tree Method*/
	        $path =  getcwd();
	        $command = escapeshellcmd("python ".$path."/application/controllers/decisiontree.py 2>&1");
	        $output = shell_exec($command); 
	        // echo $output;
		}
        /*Tampilkan Pada Map*/
		$path =  getcwd() ;
        $command = escapeshellcmd("python ".$path."/application/controllers/birch.py 2>&1");
        $output = shell_exec($command); 
        $label = array_map('str_getcsv', file('./assets/images/foo.csv'));
        $map_label = array();
        $this->load->library('googlemaps');
        $config['center'] = '-7.2859516, 112.795845';
        $config['zoom'] = '15';
        $config['map_height'] = '550px';
        $config['maxzoom'] = '20';
        $this->googlemaps->initialize($config);

		$loc = $this->acc_model->getClusterLocation();
		$marker = array();
	   	foreach ($loc as $row){
	   		$marker['icon'] = base_url('/assets/images/bump_marker.png');
	   		$marker['position'] = "{$row->clat}, {$row->clon}";
			$marker['infowindow_content'] = "Bump <br><i>id={$row->label}</i>";
			$marker['draggable'] = FALSE;
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
		$file = fopen('./assets/images/file.csv', 'w');
		//print_r($fp);
		foreach ($acc as $row) {
			unset($row['location_id']);
			fputcsv($file, $row);
		}
		fclose($file);
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
		$xdata['total'] = $c;
		$this->load->view('lihatdata',$xdata);
	}

	public function percobaan(){
		if($this->input->get('start') != null && $this->input->get('end') != null){
			$cdata['start'] = $this->input->get('start');
			$cdata['end'] = $this->input->get('end');
		} else {
			$cdata['start'] = '1096';
			$cdata['end'] = '1111';
		}
		$key =0;
		if($this->input->get('key')!=null){
			$key = $this->input->get('key');
		}
		
		$loc = $this->acc_model->getPercobaan($cdata);

		$this->load->library('googlemaps');
        $config['center'] = '-7.2859516, 112.795845';
        $config['zoom'] = '15';
        $config['map_height'] = '550px';
        $config['maxzoom'] = '20';
        $config['onclick'] = 'document.getElementById(\'coor\').innerHTML=
        	\'Posisi: \' + parseFloat(event.latLng.lat()).toFixed(9) + \', \' +
        	parseFloat(event.latLng.lng()).toFixed(9);';
        $this->googlemaps->initialize($config);

		
		$marker = array();
	   	foreach ($loc as $row){
	   		if($row->jenis_id == 3 || $row->jenis_id == 4){
	   			if($row->jenis_id ==3){
	   				$marker['icon'] = base_url('/assets/images/bump_marker.png');
	   			} else {
	   				if($key == 1) continue;
	   				$marker['icon'] = base_url('/assets/images/bump_marker.png');
	   			}
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
	   		}
	   		else {
	   			$marker['icon'] = base_url('/assets/images/true_bump.png');
	   		}
	   		$marker['position'] = "{$row->lat}, {$row->lon}";
			$marker['infowindow_content'] = "Bump <br><i>id={$row->id}</i>";
			$marker['draggable'] = FALSE;

			$this->googlemaps->add_marker($marker);
        }

       
        $data['map'] = $this->googlemaps->create_map();
        $this->load->view('percobaan',$data);	
	}

	public function test(){
		$this->load->library('googlemaps');

	   	$config['center'] = '-7.2859516, 112.795845';
		$config['zoom'] = '15';
		$config['map_height'] = '550px';
		$config['maxzoom'] = '20';
		$config['minify'] ='TRUE';
		$this->googlemaps->initialize($config);

		$loc = $this->acc_model->getAll();
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
			
			$this->googlemaps->add_marker($marker);
		}		
		$data['map'] = $this->googlemaps->create_map();
		$this->load->view('gmaps', $data);
	}
}
