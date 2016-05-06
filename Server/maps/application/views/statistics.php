<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>
<head>Graph</head>
<body>
<br>

<?php
	for($i=0;$i<$block;$i++){
		echo "<img src=". base_url('/assets/images/file'.$i.'.png') . "> <br>";
		echo "std = ".$std[$i]."<br>";
		echo "mean = ".$mean[$i]."<br>";
		echo "max = ".$max[$i]."<br>";
		echo "min =".$min[$i]."<br>";
		echo "cplock=".$cpblock[$i]."<br>";
	} 
	
	echo "count=".$count."<br>";
	echo "block=".$block."<br>";
	
?>
</body>
</html>