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
				$stdZ = stats_standard_deviation($axisZ[$c]);
				// $meanZ = $statistics->getMean();
                $skewZ = stats_skew($axisZ[$c]);
                $kurtosisZ = stats_kurtosis($axisZ[$c]);
                $varianceZ = stats_variance($axisZ[$c]);
				$max = $statistics->getMax();
				$min = $statistics->getMin();
                $deviasiZ = $max-$min;
                $statistics = null;
                // sumbu Y
                $statistics = new Statistics();
                $statistics->addSet($axisY[$c]);
                $stdY = stats_standard_deviation($axisY[$c]);
                // $meanY = $statistics->getMean();
                $skewY = stats_skew($axisY[$c]);
                $kurtosisY = stats_kurtosis($axisY[$c]);
                $varianceY = stats_variance($axisY[$c]);
                $max = $statistics->getMax();
                $min = $statistics->getMin();
                $deviasiY = $max-$min;

				$train[$c] = array();
				array_push($train[$c],$jenis,$stdZ,$deviasiZ,$stdY,$deviasiY);
                // array_push($train[$c],$jenis,$stdZ,$deviasiZ,$varianceZ,$stdY,$deviasiY,$varianceY);    
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
		// add to csv
        $fp = fopen(APPPATH .'../assets/images/train.csv','w');
        print_r($fp);
        foreach($train as $rows){
            fputcsv($fp, $rows);
        }
        fclose($fp);

        $this->load->view('acc_data','');
    }

    public function predict(){
        $data = $this->acc_model->getPredict();
        $id = $data[0]->id;
        $c = 0; 
        $flag = 0;
        $axisZ = array();
        $axisZ[$c] = array();
        $axisY[$c] = array();
        $train = array();
        $length = count($data); 
        $id_table = 0;
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
                // $skewZ = stats_skew();
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

        $path =  getcwd();
        $command = escapeshellcmd("python ".$path."/application/controllers/decisiontree.py 2>&1");
        $output = shell_exec($command); 
        echo $output;
    }

    public function predict_id(){
        $id_predict = $this->input->get('id');
        $data = $this->acc_model->getPredictData($id_predict);
        $id = $data[0]->id;
        $c = 0; 
        $flag = 0;
        $axisZ = array();
        $axisZ[$c] = array();
        $axisY[$c] = array();
        $train = array();
        $length = count($data); 
        $id_table = 0;
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
        // print_r($fp);
        foreach($train as $rows){
            fputcsv($fp, $rows);
            // print_r($rows);
        }
        fclose($fp);
        $path =  getcwd();
        $command = escapeshellcmd("python ".$path."/application/controllers/decisiontree.py 2>&1");
        $output = shell_exec($command); 
        echo $output;
    }

    public function test(){
        // get predict data
        // $id = 869;
        $dataPredict = $this->acc_model->getPredictData(812);
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
        $skew = stats_skew($axisZ);
        echo $skew;
    }
    public function cluster(){
        // cluster data bump
        $path =  getcwd() ;
        $command = escapeshellcmd("python ".$path."/application/controllers/birch.py 2>&1");
        $output = shell_exec($command); 
        // echo $output;
        // die();
        // get hasil label
        $label = array_map('str_getcsv', file('./assets/images/foo.csv'));
        // tampilkan pada map
        $map_label = array();
        $this->load->library('googlemaps');
        $config['center'] = '-7.2859516, 112.795845';
        $config['zoom'] = '13';
        $config['map_height'] = '550px';
        $config['maxzoom'] = '20';
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
            // $this->googlemaps->add_marker($marker);
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
                    $marker['icon'] = base_url('/assets/images/bump_marker.png');
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