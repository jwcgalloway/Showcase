<?php
    // Define root directory for use in strings later
	define('ROOT_DIR', dirname(__FILE__));
    include ROOT_DIR . '/php-files/file_management.php';
    include ROOT_DIR . '/php-files/sql_functions.php';
    
    $pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    session_start();
    
    if (!isset($_SESSION['isUser']))
    {
        header("Location: http://{$_SERVER['HTTP_HOST']}/Media-Vault-Project/Media-Vault-Project/logout.php");
        exit();
    }
    
    $accountName = $_SESSION['isUser'];
    //$accountName = 'testuser';
    
    // Check to see if file is set - Attempt to upload file - Add record upon success
	if (isset($_FILES['file'])) {
		if (uploadFile($accountName)) {
			addUploadRecord($accountName);
		}
	}
	 
	try {
		$result = $pdo->query("select (select sum(filesize) from metadata where metadata.owner = users.username) current_storage1, max_storage from users where username = '$accountName'");
	} catch (PDOException $e) {
		echo $e->getMessage();
	}
	
	$pdo = null;
	$rows = $result->fetchAll();
	$row = $rows[0];
	$space = round($row['current_storage1'] / 1024, 2) . 'KB / ' . $row['max_storage'] . "KB";
?>

<!doctype html>
<html>
	<head>
		<title>Team 12 Media Vault</title>
		<link rel="stylesheet" type="text/css" href="style.css">
        <script type="text/javascript" src="javascript/jquery-1.11.3.js"></script>
		
		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

		<!-- Optional theme -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
		
	
	<body>
	
	<!-- NAVIGATION BAR -->
	<nav class="navbar navbar-inverse navbar-fixed-top">
	  <div class="container-fluid">
		<div class="navbar-header">
		  <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
			<span class="sr-only">Toggle navigation</span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
			<span class="icon-bar"></span>
		  </button>
		  <a class="navbar-brand" href="directory.php">Team 12 Media Vault</a>
		</div>
		<div id="navbar" class="navbar-collapse collapse">
		  <ul class="nav navbar-nav navbar-right">
			<li><a class="bottom" href="#" data-toggle="tooltip" data-placement="bottom" title="Current Storage Space: <?php echo $space; ?>"><?php echo $accountName ?></a></li>
			<li><a href="logout.php">Log out</a></li>
		  </ul>
		</div>
	  </div>
	</nav>
	
	<!-- UPLOAD FORM -->
	<!-- Source: http://codepen.io/claviska/pen/vAgmd -->
	<div class="container" style="margin-top: 20px;">
		<div class="row">
			<div class="col-lg-6 col-sm-6 col-12">
				<h4>Upload file</h4>
                <form action="upload.php" method="post" enctype="multipart/form-data">
				    <div class="input-group">
					    <span class="input-group-btn">
						    <span class="btn btn-primary btn-file">
							    Browse&hellip; <input type="file" name="file" id="file" multiple>
						    </span>
					    </span>
					    <input type="text" class="form-control" readonly>
                    </div>
				    <span class="help-block">
					    File must be 2MB or less
					    <input type="submit" class="btn btn-success" value="Upload">
				    </span>
                </form>
                <br><br><br><a href="directory.php">Directory</a>
            </div>
        </div>
	</div>
	
	<script src="javascript/upload.js"></script>
	<!-- Latest compiled and minified JavaScript -->
	<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	</body>

</html>