<?php
include 'db.php';
require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

$app->post('/accelerometer','insertData');
$app->get('/data','getData');
$app->get('/id_block','getBlockId');
$app->post('/array','insertArray');
$app->post('/location','insertLocation');

function getBlockId(){
	$sql = "SELECT max(id) FROM location ";
	try {
		$db = getDB();
		$stmt = $db->query($sql);  
		$data = $stmt->fetch(PDO::FETCH_OBJ);
		//print_r($users);
		// echo '{"data": ' . json_encode($data) . '}';
		if($data) {
            //echo json_encode($data);
            print_r(json_encode($data));
            //print_r($data);
        }
        $db = null;
	} catch(PDOException $e) {
	    //error_log($e->getMessage(), 3, '/var/tmp/php.log');
		//echo '{"error":{"text":'. $e->getMessage() .'}}'; 
		echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
	}
}

function insertLocation() {
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$sql = "INSERT INTO location (lat, lon, jenis_id,user_id) VALUES (:lat, :lon, :jenis_id,:user_id)";
	try {
		$db = getDB();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $data->lat);
		$stmt->bindParam("lon", $data->lon);
		$stmt->bindParam("jenis_id", $data->jenis_id);
		$stmt->bindParam("user_id", $data->user_id);
		$stmt->execute();
		$db = null;
		$status['STATUS']="SUCCESS";
		echo json_encode($status);
	} catch(PDOException $e) {
		//error_log($e->getMessage(), 3, '/var/tmp/php.log');
		//echo '{"error":{"text":'. $e->getMessage() .'}}';
		echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
	}
}

function getData(){
	$sql = "SELECT * FROM acc_data ";
	try {
		$db = getDB();
		$stmt = $db->query($sql);  
		$data = $stmt->fetchAll(PDO::FETCH_OBJ);
		//print_r($users);
		// echo '{"data": ' . json_encode($data) . '}';
		if($data) {
            echo json_encode($data);
            $status['STATUS']="SUCCESS";
            echo json_encode($status);
            //print_r($data);
            $db = null;
        }
	} catch(PDOException $e) {
	    //error_log($e->getMessage(), 3, '/var/tmp/php.log');
		//echo '{"error":{"text":'. $e->getMessage() .'}}';
		echo json_encode('{"error":{"text":'. $e->getMessage() .'}}'); 
	}
}


function insertData() {
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$sql ="INSERT INTO acc_data (lat, lon, z, waktu, location_id) VALUES (:lat, :lon, :z, :waktu, :location_id)";
	try {
		$db = getDB();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $data->lat);
		$stmt->bindParam("lon", $data->lon);
		$stmt->bindParam("z", $data->z);
		$stmt->bindParam("waktu", $data->waktu);
		$stmt->bindParam("location_id",$data->location_id);
		$stmt->execute();
		$db = null;
		$status['STATUS']="SUCCESS";
		echo json_encode($status);
	} catch(PDOException $e) {
		//error_log($e->getMessage(), 3, '/var/tmp/php.log');
		//echo '{"error":{"text":'. $e->getMessage() .'}}'; 
		echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
	}
}

function insertArray(){
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$db = getDB();
	$i = 0;
	foreach($data as $obj){
		$sql = "INSERT INTO acc_data (lat, lon, z, waktu,  location_id) VALUES (:lat, :lon, :z, :waktu, :location_id)";
		try {
			
			$stmt = $db->prepare($sql);  
			$stmt->bindParam("lat", $obj->lat);
			$stmt->bindParam("lon", $obj->lon);
			$stmt->bindParam("z", $obj->z);
			$stmt->bindParam("waktu", $obj->waktu);
			$stmt->bindParam("location_id",$obj->location_id);
			$stmt->execute();
			$status[$i] = "SUCCESS";	
		} catch(PDOException $e) {
			//error_log($e->getMessage(), 3, '/var/tmp/php.log');
			//echo '{"error":{"text":'. $e->getMessage() .'}}'; 
			echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
		}
		$i++; 
	}
	$j=0;
	$k=0;
	for($x=0;$x<$i;$x++){
		if($status[$x]=="SUCCESS"){
			$k++;
		}
		else {
			$j++;
		}
	}
	if($j==0) {
		$test['STATUS']="SUCCESS ".$k;
	}
	else {
		$test['STATUS']="GAGAL ".$j;
	}
	$db = null;
	echo json_encode(array_values($test));
}

$app->run();

?>