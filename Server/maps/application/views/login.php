<?php defined('BASEPATH') OR exit('No direct script access allowed');?>
<html>

<head>
<link rel="stylesheet" href="<?php echo base_url('/assets/css/bootstrap.min.css')?>">
<link rel="stylesheet" href="<?php echo base_url('/assets/css/bootstrap-theme.min.css')?>">
<script src="<?php echo base_url('/assets/css/bootstrap.min.js')?>"></script>
<script src="<?php echo base_url('/assets/css/jquery.min.js')?>"></script>
<style type="text/css">
.col-centered{
    float: none;
    margin: 0 auto;
}
</style>
</head>

<body>

<div class="container">
    <div class="row" style="margin-top:100px;">
        <div class="col-lg-4 col-centered">
            <div class="panel panel-default">
                <div class="panel-heading" style="text-align:center;"><h3 class="panel-title"><strong>Verifikasi</strong></h3>
                </div>
                <div class="panel-body">
                    <?php echo validation_errors(); ?>
                    <?php echo form_open('verifikasi/login'); ?>
                        <div class="form-group">
                        <label for="user">Username</label>
                        <input type="text" class="form-control" id="user" name="user">
                        </div>
                        <div class="form-group">
                        <label for"pass">Password</label>
                        <input type="password" class="form-control" id="pass" name="pass" >
                        </div>
                        <input type="submit" class="btn btn-sm btn-default" value="Login"></button>
                    </form>
                </div>
            </div>    
        </div>
    </div>
    
</div>

</body>
</html>