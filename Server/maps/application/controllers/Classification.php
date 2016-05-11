<?php
defined('BASEPATH') OR exit('No direct script access allowed');
class Classification extends CI_Controller{
	public function __construct()
    {
        parent::__construct();
        $this->load->model('acc_model');
        $this->load->helper('url_helper');
        $this->load->helper('url');
        $this->load->library('Statistics');
    }

    public function index(){
    	$data = $this->acc_model->getTrainingData();
    	$id = $data[0]->id;
    	$c = 0; 
        $flag = 0;
    	$axisZ = array();
    	$axisZ[$c] = array();
    	$train = array();
    	foreach($data as $row){
    		if($row->id == $id){
    			array_push($axisZ[$c],$row->z); 
                if($flag == 0){
                    $flag = 1;
                    $jenis = $row->jenis_id;
                    if($jenis == 5 ) $jenis =2;
                    if($jenis == 6 ) $jenis =3;
    	        }	
            }
            elseif(count($axisZ[$c])!=1) {
    			$statistics = new Statistics();
    			$statistics->addSet($axisZ[$c]);
				$std = $statistics->getStdDeviation();
				$mean = $statistics->getMean();
				$max = $statistics->getMax();
				$min = $statistics->getMin();
                $deviasi = $max-$min;	
				$train[$c] = array();
				array_push($train[$c],$jenis);
                $flag = 0;
                array_push($train[$c],$std);  //feature 1
                array_push($train[$c],$deviasi); //feature 2
                array_push($train[$c],$mean); //feature 3
                $statistics = null;
    			$id	= $row->id;
    			$c++;
    			$axisZ[$c] = array();	
    			array_push($axisZ[$c],$row->z); 
    		}

    	}
		
        $id = 312;
        $dataPredict = $this->acc_model->getPredictData($id);
        $axisZ = array();
        foreach($dataPredict as $row) {
            array_push($axisZ,$row->z);
        }
        $statistics = new Statistics();
        $statistics->addSet($axisZ);
        $std = $statistics->getStdDeviation();
        $mean = $statistics->getMean();
        $max = $statistics->getMax();
        $min = $statistics->getMin();
        $deviasi = $max-$min;   
        $predict = array();
        $predict[1] = $std;
        $predict[2] = $deviasi;
        $predict[3] = $mean;
        //die(var_dump($predict));
        $svm = new SVM();
        $start = microtime(true);
    	$model = $svm->train($train);
        $time_elapsed_secs = microtime(true) - $start;
        echo $time_elapsed_secs;
    	$result = $model->predict($predict);
        if($model->save('./assets/images/model.svm'))

    	var_dump($result);
    }

    public function test(){
        $svm = new SVMModel();
        $data = array(
            array(1, 1 => 0.53, 5 => 0.3, 94 => 0.4),
            array(3, 1 => 0.43, 5 => 0.12, 94 => 0.2),
        );
        $model = $svm->load('./assets/images/model.svm');
        print_r($model);
        if($model)echo "berhasil";
        //echo $model->checkProbabilityModel();
        $test =  array(1 => 0.43, 5 => 0.12, 94 => 0.2);
        $result = $svm->predict($test);
        var_dump($result);

        
    }

    public function cluster(){
        // cluster data bump
        $path =  getcwd() ;
        $command = escapeshellcmd("python ".$path."/application/controllers/agglomerative.py 2>&1");
        $output = shell_exec($command); 
        // get hasil label
        $label = array_map('str_getcsv', file('./assets/images/foo.csv'));
        // tampilkan pada map
        $this->load->library('googlemaps');
        $config['center'] = '-7.2859516, 112.795845';
        $config['zoom'] = '13';
        $config['map_height'] = '550px';
        $this->googlemaps->initialize($config);
        unset($label[0]);
        $polygon = array();
        $polygon['points'] = array();
        $i_label = $label[1][3];
        $marker = array();
        $centroid = array();
        $sumX = array(); 
        $sumY = array();
        $ct = 0;
        foreach($label as $row){ 
            if($row[3] == $i_label){
                $marker['position'] = $row[0].", ".$row[1];
                $marker['infowindow_content'] = "<b>id:</b>" .$row[2]. "<br><b>label:</b>".$row[3];
                $marker['draggable'] = FALSE;
                $marker['icon'] = base_url('/assets/images/bump_marker.png');
                $this->googlemaps->add_marker($marker);
                array_push($sumX,$row[0]);
                array_push($sumY,$row[1]);
                array_push($polygon['points'], "{$row[0]}, {$row[1]}");
                $ct++;
            }
            else{
                if($ct == 1)  {
                    $this->googlemaps->add_marker($marker);
                }
                else {
                    $marker['position'] = (array_sum($sumX)/count($sumX)).", ".(array_sum($sumY)/count($sumY));
                    $marker['infowindow_content'] = "<b>id:</b>" .$row[2]. "<br><b>label:</b>".$row[3];
                    $marker['draggable'] = FALSE;
                    $marker['icon'] = base_url('/assets/images/true_bump.png');
                    $this->googlemaps->add_marker($marker);
                }
                $polygon['strokeColor'] = '#8E24AA';
                $polygon['fillColor'] = '#FF3F80';
                $this->googlemaps->add_polygon($polygon);
                $polygon['points'] = array();
                $sumX = array();
                $sumY = array();
                $i_label = $row[3];
                $marker['position'] = $row[0].", ".$row[1];
                $marker['infowindow_content'] = "<b>id:</b>" .$row[2]. "<br><b>label:</b>".$row[3];
                $marker['draggable'] = FALSE;
                $marker['icon'] = base_url('/assets/images/bump_marker.png');
                $this->googlemaps->add_marker($marker);
                array_push($sumX,$row[0]);
                array_push($sumY,$row[1]);
                array_push($polygon['points'], "{$row[0]}, {$row[1]}");
                $ct=1;
            }
        }
        /*$polygon = array();
        $polygon['points'] = array();
        $i_label = $label[1][3];
        $marker = array();
        foreach($label as $row){ 
            // add position to marker           
            $marker['position'] = $row[0].", ".$row[1];
            $marker['infowindow_content'] = "<b>id:</b>" .$row[2]. "<br><b>label:</b>".$row[3];
            $marker['draggable'] = FALSE;
            $this->googlemaps->add_marker($marker);
            // add polygone to every label
            if($row[3] == $i_label){
                array_push($polygon['points'], "{$row[0]}, {$row[1]}");
            }
            else{
                $polygon['strokeColor'] = '#8E24AA';
                $polygon['fillColor'] = '#FF3F80';
                $this->googlemaps->add_polygon($polygon);
                $polygon['points'] = array();
                $i_label = $row[3];
                array_push($polygon['points'], "{$row[0]}, {$row[1]}");
            }
        }*/
        $data['map'] = $this->googlemaps->create_map();
        $this->load->view('gmaps',$data);
    }
}