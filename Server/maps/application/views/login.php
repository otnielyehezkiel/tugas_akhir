<div class="container" style="margin-top:30px">
    <div class="panel panel-default">
        <div class="panel-heading"><h3 class="panel-title"><strong>Sign In </strong></h3></div>
        <div class="panel-body">
            <!-- <form role="form"> -->
            <?php echo form_open('verifylogin'); ?>
                <div class="form-group">
                <label >Username</label>
                <input type="email" class="form-control" id="user" placeholder="Username">
                </div>
                <div class="form-group">
                <label >Password</label>
                <input type="password" class="form-control" id="pass" placeholder="Password">
                </div>
                <button type="submit" class="btn btn-sm btn-default">Masuk</button>
            </form>
        </div>
    </div>
</div>