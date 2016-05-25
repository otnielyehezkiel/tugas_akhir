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
$app->post('/alldata','insertAll');
$app->post('/getuser','userId');
//dipake
function userId(){
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());

	$sql = "INSERT INTO users (nama,device) VALUES (:nama,:device) RETURNING id";
	$query = "SELECT id FROM users where nama = '". $data->nama."'";
	try {
		$db = getDB();
		$stmt = $db->query($query);  
		$id = $stmt->fetch(PDO::FETCH_OBJ);
		if(empty($id)){
			$q = $db->prepare($sql);  
			$q->bindParam("nama", $data->nama);
			$q->bindParam("device", $data->device);
			$q->execute();
			$status['STATUS']="Sudah di-Insert";
			$dat = $q->fetch(PDO::FETCH_OBJ);
			$status['ID']=$dat->id;
		}
		else $status['ID']=$id->id;
		$db = null;
		echo json_encode($status);
	} catch(PDOException $e) {
		echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
	}
}

//dipake
function insertAll(){
	$request = \Slim\Slim::getInstance()->request();
	$data = json_decode($request->getBody());
	$sql = "INSERT INTO location (lat, lon, jenis_id,user_id) VALUES (:lat, :lon, :jenis_id,:user_id) RETURNING id";
	try {
		$db = getDB();
		$db->beginTransaction();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $data{0}->lat);
		$stmt->bindParam("lon", $data{0}->lon);
		$stmt->bindParam("jenis_id", $data{0}->jenis_id);
		$stmt->bindParam("user_id", $data{0}->user_id);
		$stmt->execute();
		$id = $stmt->fetch(PDO::FETCH_OBJ);
		if(empty($id)) {
			$db->rollBack();
		}
	} catch(PDOException $e) {
		echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
		$db->rollBack();
	}

	unset($data{0});

	$i = 0;
	foreach($data as $obj){
		$sql = "INSERT INTO acc_data (lat, lon, x, y, z, waktu,  location_id) VALUES (:lat, :lon, :x, :y, :z, :waktu, :location_id)";
		try {

			$stmt = $db->prepare($sql);  
			$stmt->bindParam("lat", $obj->lat);
			$stmt->bindParam("lon", $obj->lon);
			$stmt->bindParam("x", $obj->x);
			$stmt->bindParam("y", $obj->y);
			$stmt->bindParam("z", $obj->z);
			$stmt->bindParam("waktu", $obj->waktu);
			$stmt->bindParam("location_id",$id->id);
			$stmt->execute();
			$status[$i] = "SUCCESS";	
		} catch(PDOException $e) {
			echo json_encode('{"error":{"text":'. $e->getMessage() .'}}');
			$db->rollBack();
		}
		$i++; 
	}
	// Mengechek apakah berhasil di-insert semua
	$j=0; $k=0;
	for($x=0;$x<$i;$x++){
		if($status[$x]=="SUCCESS")
			$k++;
		else $j++;
	}
	if($j==0){
		$db->commit();
		$test['STATUS']="SUCCESS ".$k;
	}
	else {
		$test['STATUS']="GAGAL ".$j;
		$db->rollBack();
	}
	echo json_encode(array_values($test));	

	$db = null;
}

function getBlockId(){
	$sql = "SELECT max(id) as id FROM location ";
	$query = "SELECT max(location_id) as loc_id FROM acc_data";
	try {
		$db = getDB();
		$stmt = $db->query($sql);  
		$data = $stmt->fetch(PDO::FETCH_OBJ);
		$q = $db->query($query);
		$id = $q->fetch(PDO::FETCH_OBJ);
		if($data->id == $id->loc_id) {
            echo json_encode($data);
        }
        else {
        	$data->id = -1;
        	echo json_encode($data);
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
	$sql ="INSERT INTO acc_data (lat, lon, x, y, z, waktu, location_id) VALUES (:lat, :lon, :x, :y, :z, :waktu, :location_id)";
	try {
		$db = getDB();
		$stmt = $db->prepare($sql);  
		$stmt->bindParam("lat", $data->lat);
		$stmt->bindParam("lon", $data->lon);
		$stmt->bindParam("x", $obj->x);
		$stmt->bindParam("y", $obj->y);
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
		$sql = "INSERT INTO acc_data (lat, lon, x, y, z, waktu,  location_id) VALUES (:lat, :lon, :x, :y, :z, :waktu, :location_id)";
		try {
			
			$stmt = $db->prepare($sql);  
			$stmt->bindParam("lat", $obj->lat);
			$stmt->bindParam("lon", $obj->lon);
			$stmt->bindParam("x", $obj->x);
			$stmt->bindParam("y", $obj->y);
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