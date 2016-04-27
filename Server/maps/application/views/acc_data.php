<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>
<head>Data Accelerometer <?php echo $jenis ." id=". $id; ?></head>
<body>
<br>

<?php
	echo "<img src=". base_url('/assets/images/chart_data_'.$id.'.png') . "> <br>";
	echo "std = ".$std."<br>";
	echo "mean = ".$mean."<br>";
	echo "max = ".$max."<br>";
	echo "min =".$min."<br>";
	echo "max-min =".$diffmaxmin."<br>";
	echo "jumlah data =".$count."<br>";
	echo "durasi =".$durasi." s<br>";
?>
</body>
</html>