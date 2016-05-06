<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>
<head>
<link rel="stylesheet" href="<?php echo base_url('/assets/css/normalize.css')?>">
<link rel="stylesheet" href="<?php echo base_url('/assets/css/skeleton.css')?>">
<script src="<?php echo base_url('/assets/css/jquery.min.js')?>"></script>
</head>
<body>
<br>
<div class="container">
	Data Accelerometer <?php echo $jenis ." id=". $id; ?>
	<?php
	echo "<img src=". base_url('/assets/images/chart/chart_data_'.$id.'.png') . "> <br>";
	?>
	<div class="row">
		<h6>
		<div class="six columns">
		std = <?php echo $std ?> <br>
		mean = <?php echo $mean ?> <br>
		max = <?php echo $max ?> <br>
		min = <?php echo $min ?> <br>
		</div>
		<div class="six columns">
		max-min = <?php echo $diffmaxmin ?> <br>
		jumlah data = <?php echo $count ?> <br>
		durasi  = <?php echo $durasi ?> <br>
		keterangan = <font color="red">belum divalidasi </font>
		</div>
		<h6>
	</div>
	<div class="row" >
		<div class="four columns" style="text-align: center;">
			<h5>Validasi<h5>
		</div>
	</div>
	<div class="row" >
		<div class="two columns">
			<a class="button" href="#">True</a>
		</div>
		<div class="two columns">
			<a class="button" href="#">False</a>
		</div>
	</div>
</div>



</body>
</html>