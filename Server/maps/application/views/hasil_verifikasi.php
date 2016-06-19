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
      <li><a href="<?php echo base_url('/verifikasi/setmap')?>">Verifikasi</a></li>
      <li><a href="<?php echo base_url('/verifikasi/logout')?>">Logout</a></li>
    </ul>
  </div>
</nav>
<div id="text"> 
</div>

<div style="height:auto;margin-top:-20px;">
	<?php echo $map['html']; ?>
</div>

</body>
</html>