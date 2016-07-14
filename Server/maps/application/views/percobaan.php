<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>

<head>
<link rel="stylesheet" href="<?php echo base_url('/assets/css/bootstrap.min.css')?>">
<link rel="stylesheet" href="<?php echo base_url('/assets/css/bootstrap-theme.min.css')?>">
<script src="<?php echo base_url('/assets/css/jquery.min.js')?>"></script>
<script src="<?php echo base_url('/assets/css/bootstrap.min.js')?>"></script>
<script src="<?php echo base_url('/assets/css/dygraph-combined.js')?>"></script>
<?php echo $map['js']; ?>
<style>
	.dygraph-label {
  font-size: 12px;
}
</style>
</head>
<body>

<nav class="navbar navbar-default" role="navigation">
<div class="navbar-collapse collapse">
    <ul class="nav navbar-nav navbar-left">
        <li><p class="navbar-text">Hasil Uji Coba </p></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-haspopup="true" aria-expanded="false">Lihat Uji Coba <span class="caret"></span></a>
          <ul class="dropdown-menu">
            <li><a href="<?php echo base_url('/maps/percobaan?start=825&end=834')?>">Uji Coba P-01</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=835&end=846')?>">Uji Coba P-02</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=847&end=852')?>">Uji Coba P-03</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=864&end=877')?>">Uji Coba P-04</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=878&end=891')?>">Uji Coba P-05</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1300&end=1312')?>">Uji Coba P-06</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1327&end=1342')?>">Uji Coba P-07</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1343&end=1356')?>">Uji Coba P-08</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=912&end=923&key=1')?>">Uji Coba P-09</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=924&end=937&key=1')?>">Uji Coba P-10</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=924&end=937&key=1')?>">Uji Coba P-11</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1327&end=1342')?>">Uji Coba P-12</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1344&end=1356')?>">Uji Coba P-13</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1344&end=1356&key=1')?>">Uji Coba P-14</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1271&end=1278&key=1')?>">Uji Coba P-15</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1279&end=1287')?>">Uji Coba P-16</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1288&end=1293')?>">Uji Coba P-17</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1279&end=1287&key=1')?>">Uji Coba P-18</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?start=1288&end=1293&key=1')?>">Uji Coba P-19</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan?pcb=1')?>">Uji Coba P-19</a></li>
          </ul> 
        </li>
    </ul>
  </div>
</nav>
<div id="text"> 
</div>

<div style="height:auto;margin-top:-20px;">
	<?php echo $map['html']; ?>
</div>

<!-- Modal -->
<div class="modal fade" id="myModal" tabindex="-1" role="dialog">
  <div class="modal-dialog">
    <div class="modal-content">
      <div class="modal-header" style="text-align:center;">
        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
        <span aria-hidden="true">&times;</span>
        </button>
        <h4 class="modal-title">Bump</h4>
      </div>
      <div class="modal-body" > 
      	<div  id="graph" style="width:600px; height:200px; margin-bottom:20px;"> </div>
      </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->

<div id="coor"></div>

<script type="text/javascript">

	var base_url = window.location.origin;

	g = new Dygraph(
		document.getElementById("graph"),
		"http://128.199.232.180/project/assets/images/file.csv",
		{
			labels: [ "timestamp", "x", "y", "z"],
			legend: "always",
			xlabel: "Time (second)",
			ylabel: "Acceleration (m/s²)",
			rollPeriod: 0,
			axisLabelFontSize: 9
		}
	);

  </script>
</body>
</html>