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
			$url = base_url('/maps/getdata/'). "?id={$row->id}";
			$marker['ondblclick'] = "window.open('".$url."','_blank')";
			$this->googlemaps->add_marker($marker);
		}		
		$data['map'] = $this->googlemaps->create_map();
		$this->load->view('gmaps',$data);	
	}
	public function getdata(){
		$id = $this->input->get('id');
		$acc = $this->acc_model->getAccel($id);
		$jenis = $this->acc_model->getJenis($id)[0]->jenis_id;
		$axisZ = array();
		$waktu = array();
		$timestamp = $acc[0]->waktu;

		foreach ($acc as $row) {
			$row->waktu -= $timestamp;
			$row->waktu /= 1000;
			array_push($axisZ,$row->z);
			array_push($waktu,$row->waktu);
		}

		$params = array('width' => 1000, 'height' => 400, 'margin' => 30, 'backgroundColor' => '#eeeeee');
		$this->load->library('chart', $params);
		$this->chart->setFormat(3,',','.');

		$this->chart->addSeries($axisZ,'line','normal ', SOLID,'#FF0000', '#FF0000');

		$this->chart->setXAxis('#000000', SOLID, 1, "Timestamp");
		$this->chart->setYAxis('#000000', SOLID, 2, "Acceleration");
		$this->chart->setLabels($waktu, '#000000', 1, HORIZONTAL);
		$this->chart->setGrid("#bbbbbb", DASHED, "#bbbbbb", DOTTED);
		$this->chart->plot('./assets/images/chart_data_'.$id.'.png');
		/*Statistic*/
		$this->load->library('Statistics');
		$statistics = new Statistics();
		$statistics->addSet($axisZ);
		$data['std'] = $statistics->getStdDeviation();
		$data['mean'] = $statistics->getMean();
		$data['max'] = $statistics->getMax();
		$data['min'] = $statistics->getMin();
		$data['diffmaxmin'] = $statistics->getMax() - $statistics->getMin();
		$data['id'] = $acc[0]->location_id;
		$data['count'] = count($axisZ);
		if($jenis == 3) 
			$data['jenis'] = "Bump";
		elseif($jenis == 2) 
			$data['jenis'] = "Hole";
		elseif($jenis  == 4) 
			$data['jenis'] = "Break";
		elseif($jenis == 5) 
			$data['jenis'] = "True Bump";
		elseif($jenis  == 6) 
			$data['jenis'] = "True Hole";
		elseif($jenis == 1) 
			$data['jenis'] = "Normal";
		$this->load->view('acc_data',$data);
	} 

	public function graph(){
	   	$accData = $this->acc_model->getAccel();
		
		$dataAcc = array();
		$dataAcc2 = array();
		
		$waktu = array();
		foreach($accData as $row){
			if($row->jenis_id ==1){
				array_push($dataAcc,$row->z); 
				array_push($dataAcc2,10);
			}
			else {
				array_push($dataAcc2,$row->z);
				array_push($dataAcc,10);
			}
			array_push($waktu,$row->waktu);
		}	
		/*Chart*/
		$params = array('width' => 1000, 'height' => 350, 'margin' => 30, 'backgroundColor' => '#eeeeee');
		$this->load->library('chart', $params);
		$this->chart->setFormat(5,',','.');

		$this->chart->addSeries($dataAcc,'line','normal ', SOLID,'#00ff00', '#00ff00');
		$this->chart->addSeries($dataAcc2,'line','bump ', SOLID,'#ff0000', '#00ffff');

		$this->chart->setXAxis('#000000', SOLID, 1, "Timestamp");
		$this->chart->setYAxis('#000000', SOLID, 2, "Acceleration");
		$this->chart->setLabels($waktu, '#000000', 1, HORIZONTAL);
		$this->chart->setGrid("#bbbbbb", DASHED, "#bbbbbb", DOTTED);
		$this->chart->plot('./assets/images/file.png');
		/*Statistic*/
		$this->load->library('Statistics');
		$statistics = new Statistics();
		$merge=array_merge($dataAcc2,$dataAcc);
		$statistics->addSet($merge);
		$data['std'] = $statistics->getStdDeviation();
		$data['mean'] = $statistics->getMean();
		$data['max'] = $statistics->getMax();
		$data['min'] = $statistics->getMin();
		$this->load->view('statistics',$data);
	}

	public function test(){
		$accData = $this->acc_model->getTest();
		// die(print_r($accData));

		$dataAcc = array();
		$waktu = array();
		//print_r($accData);
		$timestamp=0;
		$count = 0;
		//print_r($timestamp);
		$c=0;
		$x=array();
		$cpblock=0;
		/*Menampilkan data blok tiap k'second*/
		foreach($accData as $row) { 
			if($timestamp==0){
				$timestamp = $row->waktu;
				$dataAcc[$c]=array(); 
				$waktu[$c]=array();
				$x[$c]=0;
			}
			$time = ($row->waktu - $timestamp)/1000;
			if($time >= 2.5){
				$timestamp = $row->waktu;
				$time = ($row->waktu - $timestamp)/1000;
				$c++;
				$dataAcc[$c]=array(); 
				$waktu[$c]=array();
				$x[$c]=0;
			}
			array_push($dataAcc[$c],$row->z); 
			array_push($waktu[$c],$time);
			$count++;
			$x[$c]++;
		}

	/*	$INDEX =1 ;
		$params = array('width' => 2000, 'height' => 400, 'margin' => 30, 'backgroundColor' => '#eeeeee');
		$this->load->library('chart', $params);
		$this->chart->setFormat(5,',','.');
		$this->chart->addSeries($dataAcc[$INDEX],'line','normal ', SOLID,'#00ff00', '#00ff00');
		$this->chart->setXAxis('#000000', SOLID, 1, "Timestamp");
		$this->chart->setYAxis('#000000', SOLID, 2, "Acceleration");
		$this->chart->setLabels($waktu[$INDEX], '#000000', 1, HORIZONTAL);
		$this->chart->setGrid("#bbbbbb", DASHED, "#bbbbbb", DOTTED);
		$this->chart->plot('./assets/images/file.png');
*/		$this->load->library('Statistics');
		for($i=0;$i<=$c;$i++){
			$params = array('width' => 1500, 'height' => 400, 'margin' => 30, 'backgroundColor' => '#eeeeee');
			$this->load->library('chart',$params);
			$chart = new Chart($params);
			$chart->setFormat(5,',','.');
			$chart->addSeries($dataAcc[$i],'line','normal ', SOLID,'#00ff00', '#00ff00');
			$chart->setXAxis('#000000', SOLID, 1, "Timestamp");
			$chart->setYAxis('#000000', SOLID, 2, "Acceleration");
			$chart->setLabels($waktu[$i], '#000000', 1, HORIZONTAL);
			$chart->setGrid("#bbbbbb", DASHED, "#bbbbbb", DOTTED);
			$chart->plot('./assets/images/file'.$i.'.png');

			$statistics = new Statistics();
			$statistics->addSet($dataAcc[$i]);
			$data['std'][] = $statistics->getStdDeviation();
			$data['mean'][] = $statistics->getMean();
			$data['max'][] = $statistics->getMax();
			$data['min'][] = $statistics->getMin();
			
			
			$data['cpblock'][] = $x[$i];
		}
		$data['block'] =$c+1;
		$data['count'] = $count;

		
		$std=array();
		/*for($i=0;$i<$c;$i++){
			$statistics = new Statistics();
			$statistics->addSet($dataAcc[$i]);
			array_push($std,sprintf("%.6f", $statistics->getStdDeviation()));
		}
		print_r($std);
		$calc = array();
		$statistics->addSet($std);
		$calc['mean'] = $statistics->getMean();
		$calc['median'] = $statistics->getMedian();
		$calc['variance'] = $statistics->getVariance();
		$calc['max'] = $statistics->getMax();
		$calc['min'] = $statistics->getMin();
		print_r($calc);*/

		/*$data['std'] = $statistics->getStdDeviation();
		$data['mean'] = $statistics->getMean();
		$data['max'] = $statistics->getMax();
		$data['min'] = $statistics->getMin();
		$data['count'][] = $count;
		$data['block'] =$c;
		$data['cpblock'] = $x[$INDEX];*/
		$this->load->view('statistics',$data);
	}
}
