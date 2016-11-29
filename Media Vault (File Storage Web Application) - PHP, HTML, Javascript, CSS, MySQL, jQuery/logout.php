<?php
	$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
	$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	
	session_start();
	
	if (isset($_SESSION['isUser']))
	{
		unset($_SESSION['isUser']);
		header("Location: http://{$_SERVER['HTTP_HOST']}/Media-Vault-Project/Media-Vault-Project/Bootstrap_ver/index.php");
		exit();
	}
?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
	<head>
		<title>Sorry</title>
		<script type="text/javascript" src="javascript/jquery-1.11.3.js"></script>
		<script type="text/javascript" src="javascript/display_functions.js"></script>
		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
		<link rel="stylesheet" type="text/css" href="style2.css">
	</head>
	
	<body>
		<nav class="navbar navbar-inverse navbar-fixed-top">
		  <div class="container-fluid">
			<div class="navbar-header">
			  <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
				<span class="sr-only">Toggle navigation</span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
				<span class="icon-bar"></span>
			  </button>
			  <a class="navbar-brand" href="#">Team 12 Media Vault</a>
			</div>
		  </div>
		</nav><br><br>
	
		<div style="background-color:transparent !important" class="jumbotron_logout">
		  <h1>Oops, looks like you've logged out.</h1><br>      
		  <p>You must be signed in to access this page. To sign in with your account click <a href="index.php">here</a>. If you do not have an account you can sign up for one by clicking <a href="index.php">here</a>.</p>
		</div>
		
	</body>
</html>