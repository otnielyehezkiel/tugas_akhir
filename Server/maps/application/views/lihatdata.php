<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>
<head>
<link rel="stylesheet" href="<?php echo base_url('/assets/css/bootstrap.min.css')?>">
<link rel="stylesheet" href="<?php echo base_url('/assets/css/bootstrap-theme.min.css')?>">
<script src="<?php echo base_url('/assets/css/jquery.min.js')?>"></script>	
<script src="<?php echo base_url('/assets/css/DataTables/media/js/jquery.dataTables.min.js')?>"></script>	
<script src="<?php echo base_url('/assets/css/DataTables/media/js/dataTables.bootstrap.min.js')?>"></script>	
<link rel="stylesheet" href="<?php echo base_url('/assets/css/DataTables/media/css/dataTables.bootstrap.min.css')?>">

<script type="text/javascript">
$(document).ready(function(){
        $('#myTable').DataTable({
        	"searching": false,
        	"pageLength": 10,
        	"info" : false,
        	"bLengthChange": false,
        });
    });
</script>

</head>
<body>

<nav class="navbar navbar-default" role="navigation">
<div class="navbar-collapse collapse">
    <ul class="nav navbar-nav navbar-left">
        <li><p class="navbar-text">Road Bump Maps </p></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="#">LIST BUMP</a></li>
      <li><a href="<?php echo base_url('/maps')?>">MAPS VIEW</a></li>
    </ul>
  </div>
</nav>
<div class="container">
	<div class="table-responsive">
	<table id="myTable" class="table table-bordered">
		<thead> 
		<tr>
			<th> No </th>
			<th> Jenis Bump </th>
			<th> Latitude </th>
			<th> Longitude </th>
			<th> Waktu Terdeteksi </th>
		</tr>
		</thead> 
		<?php 
			for($i=0;$i<$total;$i++){
				$x = $i+1;
				if($jenis_id[$i]==3){
					$jenis_id[$i] = "Polisi Tidur";
				}else if($jenis_id[$i]==6){
					$jenis_id[$i] = "Polisi Tidur";
				}else{
					$jenis_id[$i] = "Gundukan";
				}
				$url = base_url('/maps/graph_new/'). "?id=".$id[$i];
				$tanggal[$i] =  date('d/m/Y H:i:s', ($tanggal[$i]/1000));
				echo "<tr>
						<td>" .$x."</td>
						<td>" .$jenis_id[$i]. "</td>
						<td>" .$lat[$i]. "</td>
						<td>" .$lon[$i]. "</td>
						<td>" .$tanggal[$i]. "</td>
					</tr>";
			}
		?>
	</table>
	</div>
</div>

</body>
</html>