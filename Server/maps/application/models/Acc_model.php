<?php
if (!defined('BASEPATH'))
    exit('No direct script access allowed');
 
class Acc_Model extends CI_Model {

    public function __construct(){
        // Call the CI_Model constructor
        parent::__construct();
        $this->load->database();
    }
 
    function getLocation() {
    	$query = $this->db->result('SELECT DISTINCT lat,lon,waktu,z,id FROM location
            where id_user=2 
	   		ORDER BY waktu ASC ')->result();
    	return $query;
    }

    function getHasil(){
        $query = $this->db->query('SELECT lat,lon,id,jenis_id 
            FROM location 
            WHERE validasi = 2')->result();
        return $query;
    }

    function getBumpLocation() {
    	$query = $this->db->query('SELECT lat,lon,id,jenis_id,validasi 
            FROM location --l, acc_data a
	   		WHERE 
            (id >= 912 and id <= 937 and jenis_id=3)
            -- id >=1005
            --ORDER BY l.id ASC')->result();
    	return $query;
    }

    function getClusterLocation() {
        $query = $this->db->query('SELECT clat, clon, label
            FROM cluster 
            ')->result();
        return $query;
    }

    function getAccel($id){
    	$query = $this->db->query('SELECT waktu,x,y,z,location_id
    		FROM acc_data 
    		WHERE location_id = '.$id.'
	   		ORDER BY waktu ASC')->result_array();
    	return $query;
    }


    function getTrainingData(){
        $query = $this->db->query('SELECT l.id, l.jenis_id, a.x, a.z, a.y 
            FROM acc_data a, location l 
            WHERE a.location_id = l.id and l.id >= 825 
            and l.id <= 891
            ORDER BY l.id ASC')->result();
        return $query;
    }

    function updateAfterCluster(){
        $query = $this->db->query('UPDATE location
            set validasi = 1
            WHERE validasi = 0'.$id)->result();
        return $query;
    }

    function getPredictData($id){
        $query = $this->db->query('SELECT l.id, l.jenis_id, a.z, a.y
            FROM acc_data a, location l 
            WHERE a.location_id = l.id and l.id ='.$id
            )->result();
        return $query;
    }

    function getPredict(){
        $query = $this->db->query('SELECT l.id, l.jenis_id, a.z , a.y
            FROM acc_data a, location l 
            WHERE a.location_id = l.id and
            l.id >= 912 and l.id <= 937 and l.validasi=0 and l.jenis_id = 3
            ')->result();
        return $query;
    }

    function updateValidasi($data){
        $jenis_id = $data['value'];
        $arr = array(
            'validasi' => '2',
            'jenis_id' => $jenis_id
        );
        $this->db->where('id',$data['id']);
        return $this->db->update('location',$arr);
    }

    function lihatData(){
        $query = $this->db->query('SELECT lat,lon,id,jenis_id
            FROM location 
            WHERE 
            id >=900
             or 
             jenis_id = 6')->result_array();
        return $query;
    }

    function getTanggal($id){
        $query = $this->db->query('SELECT waktu
            FROM acc_data
            WHERE location_id = '.$id. 'LIMIT 1')->result_array();
        return $query;
    }

    function getPercobaan($data){
        $start = $data['start'];
        $end = $data['end'];
        $query = $this->db->query('SELECT lat,lon,id,jenis_id
            FROM location 
            WHERE (id >= ' .$start. ' and  id <= '.$end.' )
             or 
             (jenis_id = 6 and id <1000)')->result();
        return $query;
    }

    function getAll(){
        $query = $this->db->query('SELECT *
            FROM location where id > 900')->result();
        return $query;
    }

}
 