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
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=1')?>">Uji Coba P-1</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=2')?>">Uji Coba P-2</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=3')?>">Uji Coba P-3</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=4')?>">Uji Coba P-4</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=5')?>">Uji Coba P-5</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=6')?>">Uji Coba P-6</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=7')?>">Uji Coba P-7</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=8')?>">Uji Coba P-8</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=9')?>">Uji Coba P-9</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=1')?>">Uji Coba P-10</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=11')?>">Uji Coba P-11</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=4')?>">Uji Coba P-12</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=13')?>">Uji Coba P-13</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=7')?>">Uji Coba P-14</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=15')?>">Uji Coba P-15</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=16')?>">Uji Coba P-16</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=11')?>">Uji Coba P-17</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=18')?>">Uji Coba P-18</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=19')?>">Uji Coba P-19</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=13')?>">Uji Coba P-20</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=21')?>">Uji Coba P-21</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=22')?>">Uji Coba P-22</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=15')?>">Uji Coba P-23</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=24')?>">Uji Coba P-24</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=11')?>">Uji Coba P-25</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=11&key=1')?>">Uji Coba P-26</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=13')?>">Uji Coba P-27</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=13&key=1')?>">Uji Coba P-28</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=15')?>">Uji Coba P-29</a></li>
            <li><a href="<?php echo base_url('/maps/percobaan2?pcb=15&key=1')?>">Uji Coba P-30</a></li>
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