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
        <li><p class="navbar-text">Road Bump Maps </p></li>
    </ul>
    <ul class="nav navbar-nav navbar-right">
      <li><a href="<?php echo base_url('/verifikasi/hasil')?>">Hasil</a></li>
      <li><a href="<?php echo base_url('/verifikasi/logout')?>">Logout</a></li>
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
        <h4 class="modal-title">Verifikasi Bump</h4>
      </div>
      <div class="modal-body" > 
      	<div  id="graph" style="width:600px; height:200px; margin-bottom:20px;"> </div>
      	Pilih Jenis Bump: 	
      	<form method="POST" id="verifikasi">
        	<div class="radio">
			  <label><input type="radio" id="option" name="option" value="3" checked="">Polisi Tidur</label>
			</div>
			<div class="radio">
			  <label><input type="radio" id="option" name="option" value="2">Lubang</label>
			</div>
			<div class="radio">
			  <label><input type="radio" id="option" name="option" value="1">Gundukan</label>
			</div>
		</form>
      </div>
      <div id="xalert" class="alert alert-success" style="display:none;">
	    <a href="" class="close" onclick="$('.alert').hide();$('#myModal').modal('toggle');location.reload();" aria-label="close">&times;</a>
	    <strong>Berhasil!</strong> Data telah diupdate.
	  </div>
      <div class="modal-footer">
        <button type="button" class="btn btn-default" data-dismiss="modal">Close</button>
        <button id="submit" onclick="update()" type="button" class="btn btn-primary">Save</button>
      </div>
    </div><!-- /.modal-content -->
  </div><!-- /.modal-dialog -->
</div><!-- /.modal -->


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

	function update(){
		var val = $("#verifikasi input[type='radio']:checked").val();
		var id = $.ajax({type: "GET", url: "http://128.199.232.180/project/assets/images/file_id.txt", async: false}).responseText;
        $.ajax({
            url: 'http://128.199.232.180/project/maps/verifikasi',
            type: 'POST',
            data: {
            	option:val,
            	id:id
        	},
            success: function(data){
            	$(".alert").show();
            }
        });
        event.preventDefault();
    }

  </script>
</body>
</html>