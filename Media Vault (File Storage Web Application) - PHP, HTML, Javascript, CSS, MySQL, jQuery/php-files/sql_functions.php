<?php 
	/**
	 * Establishes a connection with a database and returns the results of the specified query.
	 *
	 * @param (String) - $sql - the MySQL query to be executed.
	 * @return (PDO) - $result - the PDO result of the query.
	 * 
	 * @author James Galloway
	 */
function queryDB($sql) {
	$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
	$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	try {
		$result = $pdo->query($sql);
	} catch (PDOException $e) {
		echo $e->getMessage();
	}
	
	$pdo = null;
	return $result->fetchAll();
} // end queryDB
    /**
     * Establishes a connection with a database and executes the specificied prepared statement.
     * 
     * @param (String) - $sql - the MySQL statement to be executed.
     * @param (Array) - $parameters - array of values to be bound.  Must be in the following format: array(':bindString' => $bindValue)
     *
     * @author James Galloway
     */
function alterDB($sql, $parameters) {
	$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
	$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	try {
		$result = $pdo->query('SELECT * FROM metadata');
	} catch (PDOException $e) {
		echo $e->getMessage();
	}
	
	$stmt = $pdo->prepare($sql);
	$stmt->execute($parameters);
	$pdo = null;
} // end alterTable
	/**
	 * Delete a file's associated record in the metadata table.
	 *
	 * @param $file - string - the file name of the record to be deleted.
	 *
	 * @author James Galloway
	 */
function deleteFileRecord($file) {	
	$sql = "DELETE FROM metadata WHERE filename = :filename";
    $parameters = array(
        ':filename' => $file
    );
    alterDB($sql, $parameters);
} // end deleteFileRecord
	/**
	 * Add file record to MySQL DB - 'metadata' table.
	 * * Only includes file name, type and size at the moment *
	 *
	 * @author James Galloway
	 */
function addUploadRecord($owner) {	
	$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
	$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    $sql = "INSERT INTO metadata (filename, filetype, filesize, location, owner)
	        VALUES (:filename, :filetype, :filesize, :location, :owner)";
    $parameters = array(
        ':filename' => $_FILES["file"]["name"],
        ':filetype' => $_FILES["file"]["type"],
        ':filesize' => $_FILES["file"]["size"],
        ':location' => 'uploads/' . $owner . '/',
        ':owner' => $owner,
    );
	try {
		$result = $pdo->query("select (select sum(filesize) from metadata where metadata.owner = users.username) current_storage1, max_storage from users where username = '$owner'");
	} catch (PDOException $e) {
		echo $e->getMessage();
	}
	
	$pdo = null;
	$rows = $result->fetchAll();
	$row = $rows[0];
	if ((($_FILES["file"]["size"] + $row['current_storage1']) / 1024) > $row['max_storage']){
		echo '<script language="javascript">';
		echo 'alert("Not enough storage space left!")';
		echo '</script>';
	}
	else {
		alterDB($sql, $parameters);
	}
} // end addUploadRecord
    /**
     * Rename a file's asscoiated record in the metadata table.
     * 
     * @param $oldName - string - the original name of the file.
     * @param $newName - string - the new name for the file.
     * 
     * @author James Galloway
     */
function renameFileRecord($oldName, $newName, $user) { 
    $sql = "UPDATE metadata SET filename = :newName WHERE filename = :oldName AND owner = :user";
    $fileExtension = pathinfo($oldName, PATHINFO_EXTENSION);
    if ($fileExtension != '') {
        $fileExtension = '.' . $fileExtension;
    }
    $parameters = array(
        ':newName' => $newName . $fileExtension,
        ':oldName' => $oldName,
        ':user' => $user
    );
    alterDB($sql, $parameters);
} // end renameFileRecord
    /**
     * Add record for new folder into metadata table.
     *
     * @param $name - string - the name of the folder to be created.
     * @param $location - string - directory of where the folder is being created.
     *
     * @author James Galloway
     */
function newFolderRecord($name, $location, $owner) {
	$sql = "INSERT INTO metadata (filename, filetype, filesize, location, owner)
			VALUES (:filename, :filetype, :filesize, :location, :owner)";
    $parameters = array(
        ':filename' => $name,
        ':filetype' => 'folder',
        ':filesize' => '0',
        ':location' => $location,
        ':owner' => $owner,
    );
    alterDB($sql, $parameters);
} // end newFolderRecord
    /**
     * Alter table to reflect proper location of a file post-move.
     *
     * @param $file - the filename of the record to be altered.
     * @param $newLocation - string - the new location (file path starting from uploads/) of the file.
     *
     * @author James Galloway
     */
function renameFileLocationRecord($file, $newLocation, $user) {
    $sql = "UPDATE metadata SET location = :newLocation WHERE filename = :file AND owner = :user";
    $parameters = array(
        ':newLocation' => $newLocation,
        ':file' => $file,
        ':user' => $user
    );
    alterDB($sql, $parameters);
} // end renameFileLocationRecord

function changeFileColour($filename, $colour, $user) {
	$sql = "UPDATE metadata SET colour = :newColour WHERE filename = :file AND owner = :user";
    $parameters = array(
        ':newColour' => $colour,
        ':file' => $filename,
        ':user' => $user
    );
    alterDB($sql, $parameters);
}

function changeDescription($filename, $description, $user) {
	$sql = "UPDATE metadata SET description = :newDescription WHERE filename = :file AND owner = :user";
    $parameters = array(
        ':newDescription' => $description,
        ':file' => $filename,
        ':user' => $user
		//':editor' => $editor
    );
    alterDB($sql, $parameters);
}
?>