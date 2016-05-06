<?php
function getDB() {
	$dbhost="localhost";
	$dbuser="postgres";
	$dbpass="otniel";
	$dbname="project";
	$dbConnection = new PDO("pgsql:host=$dbhost;port=5432;dbname=$dbname", $dbuser, $dbpass);	
	$dbConnection->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	//print_r($dbConnection);
	return $dbConnection;
}
?>