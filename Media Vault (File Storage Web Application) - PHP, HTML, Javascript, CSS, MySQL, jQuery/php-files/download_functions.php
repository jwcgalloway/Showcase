<?php

	//include ROOT_DIR . '/php-files/sql_functions.php';
/** Download Related Functions **/

/**
* 
* Checks the metadata table using unique fileid and compares current session user id
* with the file ownership.
*
* @param (int) - $fileid - the fileid of the file which a user wishes to download.
* @param (int) - $id - id of current session user (or NULL).
* @returns (bool) - true or false after comparing whether id matches the id found in metadata table.
* 
* @author Benjamin McCloskey
**/
	 
function ownFile($fileid, $id = NULL){
	if(is_null(id)){
		return true;
	}else{
		$sql = "SELECT id FROM metadata WHERE fileid :fileid";
		
		$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
		$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
		try {
			$result = $pdo->query('SELECT * FROM metadata');
		} catch (PDOException $e) {
			echo $e->getMessage();
		}
		
		$stmt = $pdo->prepare($sql);
		$stmt->bindValue(':fileid', $fileid);
		$idresult = $pdo->query($stmt);
		$foundId = mysql_fetch_assoc($idresult);
		
		$pdo = null;
		
		if($id = $foundId){
			return true;
		}else{
			return false;
		}
		
	}
}

/**
* 
* downloadFile is used to dynamically assess the type of file given it's name and location
* and then make the file available for download.
*
* TODO: catch errors.
*
* @param (string) - $filename - the name of the file to be downloaded.
* @param (string) - $location - the location or path of the file (or NULL).
* @returns (bool) - true or false after comparing whether id matches the id found in metadata table.
* 
* @author Benjamin McCloskey
**/

function downloadFile($filename, $currentDir) {
	//if default location set to uploads folder.
	if(is_null($currentDir)){
		echo "<p>File not found.</p>";
        return false;
	}
	
	//file info to get MIME_TYPE for setting Content-Type header.
	$finfo = finfo_open(FILEINFO_MIME_TYPE);
	$mType = finfo_file($finfo,ROOT_DIR.'/'. $currentDir.$filename);
	
	//download file
	header('Content-Type: '.$mType);
	header('Content-Disposition: attachment; filename="'.$filename.'"');
	readfile(ROOT_DIR.'/'.$currentDir.$filename);
	exit;
	
}

function prepareFileToShare($filename, $currentDir){
	$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
	$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	
	$query = "INSERT INTO downloads (filename, location) VALUES (:filename, :currentDir)";
	$parameters = array(
        ':filename' => $filename,
		':currentDir' => $currentDir
    );
	alterDB($query, $parameters);
	
	$shareQuery = 'SELECT fileId FROM downloads WHERE location = "'.$currentDir.'" AND filename = "'.$filename.'"';
	
	$share = queryDB($shareQuery);
	$shareId = $share[0]['fileId'];
	$pdo = null;
	
	$link ='54.206.80.50/Media-Vault-Project/Media-Vault-Project/Bootstrap_ver/share.php?shareId='.$shareId;
	return $link;
}


?>
