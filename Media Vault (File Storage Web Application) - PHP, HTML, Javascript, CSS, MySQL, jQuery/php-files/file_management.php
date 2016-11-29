<?php

	/**
	 * Writes the entire contents of a given PDO to rows of a table.
	 *
	 * @param (PDO) - $pdo - the PDO to be written to the table.
	 * @param (array) - $columns - the String values of the PDO's columns to be written.
	 * * NOTE: All cells are hyperlinked individually
     *         Date string is reformatted
     *         Size string is reformatted
	 *
	 * @author James Galloway
	 */
function writeTable($pdo, $columns, $selectedFile, $isFolder, $currentDir, $username, $searchStr) {
    if ($pdo == null) {
        echo "<tr id='listingRow'><td>No files to display</td></tr>";
    } else {
        foreach ($pdo as $row) {
			echo '<tr class="listingRow">';
		    foreach ($columns as $column) {	
                $colourStyle = '';
                if ($column == 'filename') {
                    $sortKey = strtolower(substr($row[$column], 0, 1));
                }
                if ($column == 'filetype') {
                    $sortKey = substr($row[$column], 0, 1);
                    $row[$column] = selectIcon($row[$column]);
                }
                if ($column == 'timestamp') {
                    $sortKey = $row[$column];
                    $row[$column] = date("g:i a - d.m.y", strtotime($row[$column]));
                }
                if ($column == 'filesize') {
                    $sortKey = $row[$column];
                    $row[$column] = round($row[$column] / 1024);
                    $row[$column] = $row[$column] . " KB";
                }
                if ($column == 'colour') {
                    $sortKey = strtolower(substr($row[$column], 0, 1));
                    if ($row[$column] != null && $row[$column] != '' && $row[$column] != 'NULL') {
                        $colourStyle = " style='background-color:$row[$column]' ";
                    } 
                    $row[$column] = '';
                }

                if ($row['filename'] == $selectedFile && $isFolder) {
                    echo '<td sortKey="' . $sortKey . '" ' . $colourStyle . ' class="selectedFile"><a class="dirHref" href="directory.php?currentDir=' . $currentDir . $row['filename'] . '/">' . $row[$column] . '</a></td>';
                } else if ($row['filename'] == $selectedFile && !$isFolder) {
				    echo '<td sortKey="' . $sortKey . '" ' . $colourStyle . ' class="selectedFile"><a class="dirHref" href="directory.php?currentDir=' . $currentDir . '&selectedFile=' . $row['filename'] . '">' . $row[$column] . '</a></td>';    
                } else {
                    echo '<td sortKey="' . $sortKey . '" ' . $colourStyle . '><a class="dirHref" href="directory.php?currentDir=' . $currentDir . '&selectedFile=' . $row['filename'] . '">' . $row[$column] . '</a></td>';
                }
		    }
		    echo "</tr>";
        }
    }
} // end writeTable


/**
 * Similar to writeTable.  The difference is in the href attribute values specified by each listing.  
 */
function writeSearchResults($pdo, $columns, $username) {
    if ($pdo == null) {
        echo "<tr id='listingRow'><td>No results</td></tr>";
    } else {
        foreach ($pdo as $row) {
            $dir = $row['location'];
           	$colour = $row['colour'];
			$colourStyle = '';
			if ($colour != null && $colour != '' && $colour != 'none'){
				$colourStyle = "style='background-color:$colour'";
			}
            echo '<tr class="listingRow">';
		    foreach ($columns as $column) {
                $colourStyle = '';
                if ($column == 'filename') {
                    $sortKey = strtolower(substr($row[$column], 0, 1));
                }
                if ($column == 'filetype') {
                    $sortKey = substr($row[$column], 0, 1);
                    $row[$column] = selectIcon($row[$column]);
                }
                if ($column == 'timestamp') {
                    $sortKey = $row[$column];
                    $row[$column] = date("g:i a - d.m.y", strtotime($row[$column]));
                }
                if ($column == 'filesize') {
                    $sortKey = $row[$column];
                    $row[$column] = round($row[$column] / 1024);
                    $row[$column] = $row[$column] . " KB";
                }
                if ($column == 'colour') {
                    $sortKey = strtolower(substr($row[$column], 0, 1));
                    if ($row[$column] != null && $row[$column] != '' && $row[$column] != 'NULL') {
                        $colourStyle = " style='background-color:$row[$column]' ";
                    } 
                    $row[$column] = '';
                }

                if ($column == 'location') {
                    $sortKey = count(explode("/", $row[$column]));
                    $row[$column] = 'Home/' . substr($row[$column], strlen('uploads/' . $username . '/'), strlen($row[$column]));
                }
                echo '<td sortKey="' . $sortKey . '" ' . $colourStyle . '><a class="dirHref" href="directory.php?currentDir=' . $dir . '&selectedFile=' . $row['filename'] . '">' . $row[$column] . '</a></td>';
            }
		}
	    echo "</tr>";
    }
} // end writeSearchResults

    /**
     * Select the icon that applies to a particular file's type.
     * 
     * @return str - the string location of the appropriate icon.
     */
function selectIcon($type) {
    if ($type == 'folder') {
        $icon = 'folder.png';
    } else if (strpos($type, 'application') !== false) {
        $icon = 'executable.png';
    } else if (strpos($type, 'image') !== false) {
        $icon = 'image.png';
    } else if (strpos($type, 'audio') !== false) {
        $icon = 'audio.png';
    } else if (strpos($type, 'text') !== false) {
        $icon = 'text.png';
    } else if (strpos($type, 'video') !== false) {
        $icon = 'video.png';
    } else if (strpos($type, 'zip') !== false || strpos($type, 'rar') !== false) {
        $icon = 'zip.png';
    } else {
        $icon = 'unknown.png';
    }

     return $image = '<img src="images/' . $icon . '" width="15" height="20" alt="File type icon" />';
} // end selectIcon

///**
// * Determine the correct sorting values for column heading links.
// *
// * @param str $heading - string name of the column to be sorted.
// *
// * @author James Galloway
// */
//function getSortURL($heading) {
//    $url = $_SERVER['REQUEST_URI'];

//    // Remove prior $_GET information if it exists
//    if (strpos($url, '&sort=') !== false) {
//        $url = substr($url, 0, strpos($url, '&sort'));
//    }

//    if (strpos($_SERVER['REQUEST_URI'], '&sort=' . $heading) !== false) {
//        return $url = $url . '&sort=' . $heading . '&sortType=DESC';
//    }
//    return $url = $url . '&sort=' . $heading . '&sortType=ASC';
//} // end getSortURL

/** Upload Related Functions **/

	/**
	 * Upload file to server uploads folder
	 *
	 * @author James Galloway
	 */ 
function uploadFile($currentUser) {
	$dir = ROOT_DIR . '/uploads/' . $currentUser . '/';
	$file = $dir . basename($_FILES["file"]["name"]);
	$fileExtension = pathinfo($file, PATHINFO_EXTENSION);
	
    // Check to see if the user has selected a file.
    if ($_FILES["file"]["error"] == 4) {
        $message = "Please select a file.";
        writeMessage($message);
        return false;
    }

	// Check if user has a file of the same name in the uploads folder
    $sql = 'SELECT * FROM metadata WHERE owner = "' . $currentUser . '" AND location = "uploads/' . $currentUser . '"';
    $userFiles = queryDB($sql);
    foreach ($userFiles as $userFile) {
        if ($userFile['filename'] == $_FILES["file"]["name"]) {
            $message = "A file of the same name already exists in your home directory.";
            writeMessage($message);
            return false;
        }
    }
	// try {
		// $result = $pdo->query("select (select sum(filesize) from metadata where metadata.owner = users.username) current_storage1, max_storage from users where username = '$accountName'");
	// } catch (PDOException $e) {
		// echo $e->getMessage();
	// }
	
	// $pdo = null;
	// $rows = $result->fetchAll();
	// $row = $rows[0];
	// $space = $row['current_storage1'] . 'KB / ' . $row['max_storage'] . "KB";
	
	
	
	// Upload file
	$pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
	$pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
	try {
		$result = $pdo->query("select (select sum(filesize) from metadata where metadata.owner = users.username) current_storage1, max_storage from users where username = '$currentUser'");
	} catch (PDOException $e) {
		echo $e->getMessage();
	}
	$pdo = null;
	$rows = $result->fetchAll();
	$row = $rows[0];
	if ((($_FILES["file"]["size"] + $row['current_storage1']) / 1024) > $row['max_storage']){
		echo "<p>There is not enough storage space left for that file.</p>";
		return true;
	}
	else if (move_uploaded_file($_FILES["file"]["tmp_name"], $file)) {
		return true;
	} 
	else {
		echo "<p>There was an error in uploading the file.</p>";
		print_r(error_get_last());
		return false;
	}
} // end uploadFile

/** Delete Related Functions **/
    
    /**
     * Simple function to echo the delete confirmation form.
     *
     * @author James Galloway
     */
function writeDeleteConfirmation($file, $currentDir) {
    echo '<div class="simpleInputDiv">
            <form action="" "method="get" class="simpleInputForm">
                <p>Are you sure you want to delete ' . $file . '?</p>
                <input type="hidden" value="' . $file . '" name="selectedFile">
                <input type="hidden" value="' . $currentDir . '" name="currentDir">
                <input type="submit" name="confirmDelete" value="Yes">
                <input type="submit" name="confirmDelete" value="No">
            </form>
        </div>';
} // end deleteConfirmation

   	/**
	 * Delete selected file.
	 *
	 * @param $file - string - the file to be deleted.
	 *
	 * @author Christian Ruiz
	 */
function deleteFile($file, $currentDir) {
	$file = ROOT_DIR . '/' . $currentDir . $file;
    if (is_dir($file)) {
        if (rmdir($file)) {
            return true;
        }
    } else {
        if (unlink($file)) {
		    return true;
	    } 
    }
	return false;
} // end deleteFile

/** Edit Related Functions **/

    /**
     * Function to create text input form onto page for user to add a description to their selected file.
     *
     * @param $selectedFile - string - file name of selected file - stored in hidden input
     *
     * @author Thomas Shortt
     */
function writeEditForm($selectedFile, $currentDir) {
    echo "<div class='simpleInputDiv'>
        <form action='' 'method='get' class='simpleInputForm'>
            <br>
            <input type='hidden' value='" . $selectedFile . "' name='fileName'>
            <input type='hidden' value='" . $currentDir . "' name='currentDir'>
            <label for='newName'>Name: </label>
            <input type='text' name='newName'>
            <br><br>
            <label for='newName'>Description: </label>
            <input type='text' name='newDescription'>
            <br><br>
            <input type='submit' name='confirmEdit' value='Confirm'>
            <input type='submit' name='cancelEdit' value='Cancel'>
        </form>
    </div>";
} // end writeEditForm

function writeSearchForm() {
    echo '<form name="searchForm" action="search.php" method="POST">
		    <input type="text" name="searchStr" value="">
            <input type="submit" name="searchButton" value="Search">
	      </form>';
} // end writeSearchForm

    /**
     * Rename selected file.
     * 
     * @param $oldName - string - the original name of the file.
     * @param $newName - string - the new name for the file specified by the user.
     * 
     * @author James Galloway
     */
function renameFile($oldName, $newName, $currentDir) {
    $fileExtension = pathinfo($oldName, PATHINFO_EXTENSION);
    if (file_exists(ROOT_DIR . '/uploads/' . $newName . '.' . $fileExtension)) {
        echo "<p>A file of that name already exists.  Please choose a different name.</p>";
        return false;
    }
    if (rename(ROOT_DIR . '/' . $currentDir . $oldName, ROOT_DIR . '/' . $currentDir . $newName . '.' . $fileExtension)) {
        return true;
    }
    echo "<p>There was an error in renaming the file.</p>";
    return false;
} // end renameFile

/** Create Folder Related Functions **/

    /**
     * Write input form for user to name new folder.
     *
     * @author James Galloway
     */
function writeNewFolderForm($currentDir) {
    echo "<div class='simpleInputDiv'>
            <form action='' 'method='get' class='simpleInputForm'>
                <input type='hidden' name='currentDir' value='" . $currentDir . "'>
                <input type='text' name='folderName'>
                <input type='submit' name='newFolderForm' value='Create'>
                <input type='submit' name='newFolderForm' value='Cancel'>
            </form>
        </div>";
} // end writeNewFolderForm

    /**
     * Create new folder in the /uploads/ directory.
     * * NOTE: Folder is currently created with widest possible privilege.
     *
     * @param $name - string - the name of the folder to be created.
     *
     * @author James Galloway
     */
function newFolder($name, $currentDir) {
    if (mkdir(ROOT_DIR . '/' . $currentDir . $name, 0755)) {
        return true;
    }
    return false;
} // end newFolder

/** Move File Related Functions **/

    /**
     * Retrieve a list of valid folders from the database and write them
     * to a dropdown menu.
     *
     * @param $owner - the $_SESSION ID of the current user. Used to compare table values.
     * @param $selectedFile - string name of the selected file.
     *
     * @author Christian Ruiz 
     */
function writeFolders($owner, $selectedFile) {       
    $sql = 'SELECT * FROM metadata WHERE filetype = "folder" AND owner = "' . $owner . '"';
    $folders = queryDB($sql);
    // Write dropdown menu
    foreach ($folders as $singleFolder) {
        echo '<li><input type="submit" name="moveTo" value="' . $singleFolder['filename'] . '"></li>';
    }
    echo '<li><input type="submit"  name="moveTo" value="Home Directory"></li>';

} // end writeFolders

    /**
    * Moves file from current position to destination folder.
    *
    * @param $file - string - file to be moved.
    * @param $location - string - the location of the file to be moved.
    * @param $folder - string - destination folder.
    *
    * @author Christian Ruiz & James Galloway
    */
function moveFile($file, $folder, $currentUser) {
    $sql = "SELECT * FROM metadata WHERE filename = '$file' AND owner = '$currentUser'";
    $tempPath = queryDB($sql);
    $filePath = ROOT_DIR . '/' . $tempPath[0]['location'];
    
    $sql = "SELECT location FROM metadata WHERE filename = '$folder' AND owner = '$currentUser'";
    $tempPath = queryDB($sql);
    if ($tempPath == null) {
        $DBentry = 'uploads/' . $currentUser . '/';
        $folderPath = ROOT_DIR . '/' . $DBentry;
    } else {
        $DBentry = $tempPath[0]['location'] . $folder . '/';
        $folderPath = ROOT_DIR . '/' . $DBentry;
    }
    if (rename($filePath . $file, $folderPath . $file)) {
        return $DBentry;
    }
    return false;
} // end moveFile

    /**
     * Simple helper method to determine whether a particular $_GET element is set and not empty.
     *
     * @param $element - string - the name of the element to be checked.
     *
     * @return - Boolean - true if element is set and not empty - false if not.
     *
     * @author James Galloway
     */
function isSetAndNotEmpty($method, $element) {
    if (isset($method[$element]) && $method[$element] !== '') {
        return true;
    }
    return false;
} // end isSetAndNotEmpty

function writeMessage($message) {
    echo '<div class="modal-header" id="messageFooter">
        <a class="close" data-dismiss="modal">×</a>
        <h4>' . $message . '</h4>
    </div>';
} // end writeMessage

function writeBreadcrumb($user, $dir) {
    echo '<ol class="breadcrumb">';
    $folders = explode("/", $dir);
   
    $url = $folders[0];
    for ($i = 1; $i < count($folders); $i++) {
        $url = $url . '/' . $folders[$i];
        echo '<li><a href="directory.php?currentDir=' . $url . '/">' . $folders[$i] . '</a></li>';
    }

	echo '</ol>';
} // end writeBreadcrumb

?>