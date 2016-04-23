<?php
include 'db.php';
require 'Slim/Slim.php';
\Slim\Slim::registerAutoloader();

$app = new \Slim\Slim();

$app->post('/accelerometer','insertData');
$app->get('/data','getData');
$app->get('/id_block','getBlockId');
$app->post('/string','insertString');
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
		echo '{"error":{"text":'. $e->getMessage() .'}}'; 
	}
}

function insertLocation() {
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$sql = "INSERT INTO location (lat, lon, jenis_id) VALUES (:lat, :lon, :jenis_id)";
	try {
		$db = getDB();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $data->lat);
		$stmt->bindParam("lon", $data->lon);
		$stmt->bindParam("jenis_id", $data->jenis_id);
		$stmt->execute();
		$db = null;
		$status['STATUS']="SUCCESS";
		echo json_encode($status);
	} catch(PDOException $e) {
		//error_log($e->getMessage(), 3, '/var/tmp/php.log');
		echo '{"error":{"text":'. $e->getMessage() .'}}'; 
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
		echo '{"error":{"text":'. $e->getMessage() .'}}'; 
	}
}


function insertData() {
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$sql = "INSERT INTO acc_data (lat, lon, z, waktu, id_user, jenis_id, block_id) VALUES (:lat, :lon, :z, :waktu, :id_user, :jenis_id, :block_id)";
	try {
		$db = getDB();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $data->lat);
		$stmt->bindParam("lon", $data->lon);
		$stmt->bindParam("z", $data->z);
		$stmt->bindParam("waktu", $data->waktu);
		$stmt->bindParam("id_user", $data->id_user);
		$stmt->bindParam("jenis_id",$data->jenis_id);
		$stmt->bindParam("block_id",$data->block_id);
		$stmt->execute();
		$db = null;
		$status['STATUS']="SUCCESS";
		echo json_encode($status);
	} catch(PDOException $e) {
		//error_log($e->getMessage(), 3, '/var/tmp/php.log');
		echo '{"error":{"text":'. $e->getMessage() .'}}'; 
	}
}

function insertString() {
	$request = \Slim\Slim::getInstance()->request();
	//$data = json_decode($request->getBody());

	$sql = "INSERT INTO acc_data (lat, lon, z, waktu, id_user, jenis_id) VALUES (:lat, :lon, :z, :waktu, :id_user)";
	try {
		$db = getDB();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $request->post('lat'));
		$stmt->bindParam("lon", $request->post('lon'));
		$stmt->bindParam("z", $request->post('z'));
		$stmt->bindParam("waktu", $request->post('waktu'));
		$stmt->bindParam("id_user", $request->post('id_user'));

		$stmt->execute();
		$db = null;
		$status['STATUS']="SUCESS";
		echo json_encode($status['STATUS']);
	} catch(PDOException $e) {
		//error_log($e->getMessage(), 3, '/var/tmp/php.log');
		echo '{"error":{"text":'. $e->getMessage() .'}}'; 
	}
}

function insertArray(){
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$db = getDB();
	$i = 0;
	foreach($data as $obj){
		$sql = "INSERT INTO acc_data (lat, lon, z, waktu, id_user, jenis_id, block_id) VALUES (:lat, :lon, :z, :waktu, :id_user, :jenis_id, :block_id)";
		try {
			
			$stmt = $db->prepare($sql);  
			$stmt->bindParam("lat", $obj->lat);
			$stmt->bindParam("lon", $obj->lon);
			$stmt->bindParam("z", $obj->z);
			$stmt->bindParam("waktu", $obj->waktu);
			$stmt->bindParam("id_user", $obj->id_user);
			$stmt->bindParam("jenis_id",$obj->jenis_id);
			$stmt->bindParam("block_id",$obj->block_id);
			$stmt->execute();
			$status[$i] = "SUCCESS";	
		} catch(PDOException $e) {
			//error_log($e->getMessage(), 3, '/var/tmp/php.log');
			echo '{"error":{"text":'. $e->getMessage() .'}}'; 
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