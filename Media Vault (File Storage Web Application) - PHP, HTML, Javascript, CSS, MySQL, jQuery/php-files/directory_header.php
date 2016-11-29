<?php
    include ROOT_DIR . '/php-files/file_management.php';
    include ROOT_DIR . '/php-files/sql_functions.php';
	include ROOT_DIR . '/php-files/download_functions.php';
    
    $isSelected = false;
    $isFolder = false;
    $selectedFile = null;
    $selectedFolder = null;
    $searchStr = false;

    if (isSetAndNotEmpty($_GET, 'currentDir')) {
        $currentDir = $_GET['currentDir'];
    } else {
        $currentDir = 'uploads/' . $accountName . '/';
    }
    // Process the file's DB record - set isFolder flag if file is folder
    if (isSetAndNotEmpty($_GET, 'selectedFile')) {
        $query = 'SELECT * FROM metadata WHERE filename = "' . $_GET['selectedFile'] . '"';
        $fileRecord = queryDB($query);
        if ($fileRecord == null) {
            return;   
        }
        $selectedFile = $fileRecord[0]['filename'];
        $fileID = $fileRecord[0]['fileid'];
        $fileType = $fileRecord[0]['filetype'];
        
        if ($fileType == 'folder') {
            $isFolder = true;
        }
        $isSelected = true;
    }
    // Process the folder's DB record if folder has been clicked twice - set location to inside folder
    if (isSetAndNotEmpty($_GET, 'selectedFolder')) {
        $query = 'SELECT * FROM metadata WHERE filename = "' . $_GET['selectedFolder'] . '"';
        $folderRecord = queryDB($query);
    }
    
    // Delete
    if (isset($_GET['confirmDelete'])) {
        if (deleteFile($_GET['selectedFile'], $_GET['currentDir'])) {
                deleteFileRecord($selectedFile);
        }
    }
	
    // Colour
	if (isset($_GET['colour'])) {
		changeFileColour($_GET['selectedFile'], $_GET['colour'], $accountName);
	}

    // Edit
    if (isset($_GET['confirmEdit'])) {
        if (isSetAndNotEmpty($_GET, 'newDescription')) {
            $newDes = $_GET['newDescription'];
            $fileName = $_GET['selectedFile'];
            //$editor = $_SESSION['isUser'];
            changeDescription($fileName, $newDes, $accountName);
        }
        if (isSetAndNotEmpty($_GET, 'newName')) {
            $oldName = $_GET['selectedFile'];
            $newName = $_GET['newName'];
            if (renameFile($oldName, $newName, $currentDir, $accountName)) {
                renameFileRecord($oldName, $newName, $accountName);
            }
        }
    }

    // New folder
    if (isset($_GET['confirmNewFolder'])) {
        if (newFolder($_GET['folderName'], $currentDir)) {
            newFolderRecord($_GET['folderName'], $currentDir, $accountName);
        }
    }

    // DOWNLOAD
    // Download file if download button is clicked
    if(isset($_GET['download'])){
        //$fname = $_GET['filename'];
        $fname = $selectedFile;
        //$flocation = NULL;	
        downloadFile($fname, $currentDir);
    }
	
	// SHARE
	// Add file to downloads table and provide link to share
	if(isset($_GET['share'])){
		$link = prepareFileToShare($selectedFile, $currentDir);
		$message = 'Your single use link is: '.$link;
        writeMessage($message);
        //echo 'your single use link is: '.$link;
	}
	
    // Move to...
    if (isset($_GET['moveTo'])) {
        if ($folderPath = moveFile($selectedFile, $_GET['moveTo'], $accountName)) {
            renameFileLocationRecord($selectedFile, $folderPath, $accountName);
        }
    }

	if (isSetAndNotEmpty($_GET, 'searchStr')) {
		$searchStr = $_GET['searchStr'];
	}

?>
