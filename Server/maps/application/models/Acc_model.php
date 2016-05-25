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

    function getBumpLocation() {
    	$query = $this->db->query('SELECT lat,lon,id,jenis_id 
            FROM location --l, acc_data a
	   		WHERE 
            (id >= 825 and id <=834 )
             or 
             jenis_id = 6
            --ORDER BY l.id ASC')->result();
    	return $query;
    }

    function getAccel($id){
    	$query = $this->db->query('SELECT waktu,x,y,z,location_id
    		FROM acc_data 
    		WHERE location_id = '.$id.'
	   		ORDER BY waktu ASC')->result_array();
    	return $query;
    }

    function getJenis($id){
        $query = $this->db->query("SELECT jenis_id,validasi
            FROM location 
            WHERE id = ".$id)->result();
        return $query;
    }

    function getTest(){
        $query = $this->db->query('SELECT DISTINCT z,waktu,jenis_id 
            FROM acc_data 
            WHERE (jenis_id=3) and id_user=4
            ORDER BY waktu ASC')->result();
        return $query;
    }

    function getTrainingData(){
        $query = $this->db->query('SELECT l.id, l.jenis_id, a.x, a.z, a.y 
            FROM acc_data a, location l 
            WHERE a.location_id = l.id and l.id >= 864 and l.id <= 891
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
        $query = $this->db->query('SELECT l.id, l.jenis_id, a.z , a.y
            FROM acc_data a, location l 
            WHERE a.location_id = l.id and l.id ='.$id)->result();
        return $query;
    }

    function updateValidasi($data){
        if($data['value']=='true'){
            $arr = array(
                'validasi' => '1'
            );
        }
        else {
            $arr = array(
                'validasi' => '1',
                'jenis_id' => '4'
            );
        }
        $this->db->where('id',$data['id']);
        return $this->db->update('location',$arr);
    }

}
 