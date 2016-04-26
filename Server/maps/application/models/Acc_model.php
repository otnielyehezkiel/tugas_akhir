<?php
if (!defined('BASEPATH'))
    exit('No direct script access allowed');
 
class Acc_Model extends CI_Model {

    public function __construct(){
        // Call the CI_Model constructor
        $this->load->database();
        parent::__construct();
    }
 
    function getLocation() {
    	$query = $this->db->query('SELECT DISTINCT lat,lon,waktu,z,id FROM location
            where id_user=2
	   		ORDER BY waktu ASC ')->result();
    	return $query;
    }

    function getBumpLocation() {
    	$query = $this->db->query('SELECT l.lat,l.lon,l.id,l.jenis_id 
            FROM location l, acc_data a
	   		WHERE (l.id = a.location_id and a.id_user = 6) 
            ORDER BY id ASC')->result();
    	return $query;
    }

    function getAccel($id){
    	$query = $this->db->query('SELECT *
    		FROM acc_data 
    		WHERE location_id = '.$id.'
	   		ORDER BY waktu ASC')->result();
    	return $query;
    }

    function getTest(){
        $query = $this->db->query('SELECT DISTINCT z,waktu,jenis_id 
            FROM acc_data 
            WHERE (jenis_id=3) and id_user=4
            ORDER BY waktu ASC')->result();
        return $query;
    }

 
}
 