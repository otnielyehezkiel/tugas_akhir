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
        $axisY[$c] = array();
    	$train = array();
        $length = count($data); 
    	foreach($data as $row){
            $length--;
    		if($row->id == $id && $length!=0){
    			array_push($axisZ[$c],$row->z); 
                array_push($axisY[$c],$row->y); 
                if($flag == 0){
                    $flag = 1;
                    $jenis = $row->jenis_id;
                    if($jenis == 5 ) $jenis =2;
                    if($jenis == 6 ) $jenis =3;
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
				$meanZ = $statistics->getMean();
				$max = $statistics->getMax();
				$min = $statistics->getMin();
                $deviasiZ = $max-$min;
                $statistics = null;
                // sumbu Y
                $statistics = new Statistics();
                $statistics->addSet($axisY[$c]);
                $stdY = $statistics->getStdDeviation();
                $meanY = $statistics->getMean();
                $max = $statistics->getMax();
                $min = $statistics->getMin();
                $deviasiY = $max-$min;	
				$train[$c] = array();
                // extract feature
				array_push($train[$c],$jenis,$stdZ,$deviasiZ,$meanZ,$stdY,$deviasiY,$meanY);
                $flag = 0;
                $statistics = null;
    			$id	= $row->id;
    			$c++;
    			$axisZ[$c] = array();	
                $axisY[$c] = array();   
    			array_push($axisZ[$c],$row->z); 
                array_push($axisY[$c],$row->y); 
    		}
    	}
		$c++;
        $train[$c] = array();
        $weights = array();
        $weights[4] = 0.5;
        $weight[3]= 0.5;
        array_push($train[$c],0.5);
        $id = 869;
        $dataPredict = $this->acc_model->getPredictData($id);
        $axisZ = array();
        $axisY = array();
        foreach($dataPredict as $row) {
            array_push($axisZ,$row->z);
            array_push($axisY,$row->y);
        }
        // sumbu z
        $statistics = new Statistics();
        $statistics->addSet($axisZ);
        $stdZ = $statistics->getStdDeviation();
        $meanZ = $statistics->getMean();
        $max = $statistics->getMax();
        $min = $statistics->getMin();
        $deviasiZ = $max-$min;
        $statistics = null;
        // sumbu y
        $statistics = new Statistics();
        $statistics->addSet($axisY);
        $stdY = $statistics->getStdDeviation();
        $meanY = $statistics->getMean();
        $max = $statistics->getMax();
        $min = $statistics->getMin();
        $deviasiY = $max-$min;
        $statistics = null; 

        $predict = array();
        $predict[1] = $stdZ;
        $predict[2] = $deviasiZ;
        $predict[3] = $meanZ;
        $predict[4] = $stdY;
        $predict[5] = $deviasiY;
        $predict[6] = $meanY;
        //die(var_dump($predict));
        $svm = new SVM();
        // $start = microtime(true);
        $weights = array();
        $weights[0] = 0.5;
        $weights[1] = 0.5;
        // array_push($train, $weights);
        $model = $svm->crossvalidate($train);
        var_dump($model);
    	//$model = $svm->train($train,$weights);
        // $time_elapsed_secs = microtime(true) - $start;
        // echo $time_elapsed_secs;
    	/*$result = $model->predict($predict);
        if($model->save('./assets/images/model.svm'))
        var_dump($result); */
    }

    public function predict(){
        // get predict data
        // $id = 869;
        $id = $this->input->get('id');
        $dataPredict = $this->acc_model->getPredictData($id);
        $axisZ = array();
        $axisY = array();
        foreach($dataPredict as $row) {
            array_push($axisZ,$row->z);
            array_push($axisY,$row->y);
        }
        // sumbu z
        $statistics = new Statistics();
        $statistics->addSet($axisZ);
        $stdZ = $statistics->getStdDeviation();
        $meanZ = $statistics->getMean();
        $max = $statistics->getMax();
        $min = $statistics->getMin();
        $deviasiZ = $max-$min;
        $statistics = null;
        // sumbu y
        $statistics = new Statistics();
        $statistics->addSet($axisY);
        $stdY = $statistics->getStdDeviation();
        $meanY = $statistics->getMean();
        $max = $statistics->getMax();
        $min = $statistics->getMin();
        $deviasiY = $max-$min;
        $statistics = null; 

        $predict = array();
        $predict[1] = $stdZ;
        $predict[2] = $deviasiZ;
        $predict[3] = $meanZ;
        $predict[4] = $stdY;
        $predict[5] = $deviasiY;
        $predict[6] = $meanY;
        //die(var_dump($predict));
        $svm = new SVMModel();
        $model = $svm->load('./assets/images/model.svm');
        $result = $svm->predict($predict);
        // if($model->save('./assets/images/model.svm'))
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
        $command = escapeshellcmd("python ".$path."/application/controllers/birch.py 2>&1");
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
        foreach($label as $row => $col){
            $marker['position'] = $col[0].", ".$col[1];
            $marker['infowindow_content'] = "<b>id:</b>" .$col[2]. "<br><b>label:</b>".$col[3];
            $marker['draggable'] = FALSE;
            $marker['icon'] = base_url('/assets/images/bump_marker.png');
            $this->googlemaps->add_marker($marker);
            array_push($sumX,$col[0]);
            array_push($sumY,$col[1]);
            array_push($polygon['points'], "{$col[0]}, {$col[1]}");
            if(isset($label[$row+1]) && ($col[3] == $label[$row+1][3])){
                $ct++;
            }
            else{
                if($ct==0){
                    $polygon['points'] = array();
                    $sumX = array();
                    $sumY = array();       
                }
                else {
                    $marker['position'] = (array_sum($sumX)/count($sumX)).", ".(array_sum($sumY)/count($sumY));
                    $marker['infowindow_content'] = "<b>id:</b>" .$col[2]. "<br><b>label:</b>".$col[3];
                    $marker['draggable'] = FALSE;
                    $marker['icon'] = base_url('/assets/images/true_bump.png');
                    $this->googlemaps->add_marker($marker);
                    $polygon['strokeColor'] = '#8E24AA';
                    $polygon['fillColor'] = '#FF3F80';
                    $this->googlemaps->add_polygon($polygon);
                }
                $polygon['points'] = array();
                $sumX = array();
                $sumY = array();       
                $ct=0;
            }
        }
       
        $data['map'] = $this->googlemaps->create_map();
        $this->load->view('gmaps',$data);
    }
}