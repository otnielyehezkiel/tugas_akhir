<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>
<head>
<link rel="stylesheet" href="<?php echo base_url('/assets/css/normalize.css')?>">
<link rel="stylesheet" href="<?php echo base_url('/assets/css/skeleton.css')?>">
<script src="<?php echo base_url('/assets/css/dygraph-combined.js')?>"></script>
</head>
<body>
<br>
<div class="container">
Data Accelerometer <?php echo $jenis ." id=". $id; ?>
<div id="graph" style="width:700px; height:300px;margin-top:30px;">
</div>
<div id="graph2" style="width:700px; height:300px;margin-top:30px;">
</div>
<div id="graph3" style="width:700px; height:300px;margin-top:30px;">
</div>
	<div class="row">
		<h6>
		<div class="six columns">
		std = <?php echo $std ?> <br>
		mean = <?php echo $mean ?> <br>
		max = <?php echo $max ?> <br>
		min = <?php echo $min ?> <br>
		waktu = <?php echo date('d/m/Y H:i:s', ($timestamp/1000)); ?> <br>
		</div>
		<div class="six columns">
		max-min = <?php echo $diffmaxmin ?> <br>
		jumlah data = <?php echo $count ?> <br>
		durasi  = <?php echo $durasi ?> s <br>
		keterangan = <font color="red"><?php echo $validasi ?>  </font>
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
			<a class="button" href="<?php echo base_url('/maps/updateValidasi/?value=true&id='.$id)?>">True</a>
		</div>
		<div class="two columns">
			<a class="button" href="<?php echo base_url('/maps/updateValidasi/?value=false&id='.$id)?>">False</a>
		</div>
	</div>
	<div class="row" >
		<div class="four columns" style="text-align: center;">
			<h5>Predict<h5>
		</div>
	</div>
	<div class="row" >
		<div class="one columns">
			<a class="button" href="<?php echo base_url('/classification/predict_id/?id='.$id)?>">Predict</a>
		</div>
	</div>
</div>

<script type="text/javascript">
  g = new Dygraph(
    document.getElementById("graph"),
    "http://128.199.235.115/project/assets/images/file.csv",
    {
		labels: [ "timestamp", "x", "y", "z"],
		legend: "always",
		xlabel: "Time (second)",
		ylabel: "Acceleration (m/s²)",
		rollPeriod: 0,
		visibility: [false, false, true],
		valueRange: [-10, 30],
		axisLabelFontSize: 14
    }
  );

  g2 = new Dygraph(
    document.getElementById("graph2"),
    "http://128.199.235.115/project/assets/images/file.csv",
    {
		labels: [ "timestamp", "x", "y", "z"],
		legend: "always",
		xlabel: "Time (second)",
		ylabel: "Acceleration (m/s²)",
		rollPeriod: 0,
		visibility: [false, true, false],
		valueRange: [-10, 30],
		axisLabelFontSize: 14
    }
  );

  g3 = new Dygraph(
    document.getElementById("graph3"),
    "http://128.199.235.115/project/assets/images/file.csv",
    {
		labels: [ "timestamp", "x", "y", "z"],
		legend: "always",
		xlabel: "Time (second)",
		ylabel: "Acceleration (m/s²)",
		rollPeriod: 0,
		visibility: [true, false, false],
		valueRange: [-10, 30],
		axisLabelFontSize: 14
    }
  );
</script>
</body>
</html>