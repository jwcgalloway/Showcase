<?php
	define('ROOT_DIR', dirname(__FILE__));
	include ROOT_DIR . '/php-files/sql_functions.php';
	include ROOT_DIR . '/php-files/download_functions.php';

	if(isset($_GET['shareId'])){
		$fileId = (int)$_GET['shareId'];
		
		$query = "SELECT filename, location FROM downloads WHERE fileId = {$fileId}";
		
		$results = queryDB($query);
		

		$filename = $results[0]['filename'];
		$location = $results[0]['location'];
		downloadFile($filename, $location);

		$remove = 'DELETE FROM downloads WHERE fileId = :fileId';
		$parameters = array(
			':fileId' => $fileId
		);
		alterDB($remove, $parameters);
		
		
	}


?>
