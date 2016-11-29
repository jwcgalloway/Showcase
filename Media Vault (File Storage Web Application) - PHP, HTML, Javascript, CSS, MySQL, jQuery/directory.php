<?php
    // Define root directory for use in strings later
	define('ROOT_DIR', dirname(__FILE__));
    $pdo = new PDO('mysql:host=localhost;dbname=mediavault', 'root', 'password');
    $pdo->setAttribute(PDO::ATTR_ERRMODE, PDO::ERRMODE_EXCEPTION);
    
    session_start();
    //Determine if user is signed in. If not redirect them to a seperate page.
    if (!isset($_SESSION['isUser']))
	{
		header("Location: http://{$_SERVER['HTTP_HOST']}/Media-Vault-Project/Media-Vault-Project/Bootstrap_ver/logout.php");
		exit();
	}
        
    //Determine the username of the account
    $accountName = $_SESSION['isUser'];
    include ROOT_DIR . '/php-files/directory_header.php';
?>

<!doctype html>
<html>
	<head>
		<title>Team 12 Media Vault</title>
		<link rel="stylesheet" type="text/css" href="style.css">
		<script type="text/javascript" src="javascript/display_functions.js"></script>
        <script type="text/javascript" src="javascript/jquery-1.11.3.js"></script>

		<!-- Latest compiled and minified CSS -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

		<!-- Optional theme -->
		<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
	</head>
	
	<body>
	<!-- NAVIGATION BAR -->
	<?php 
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
			<li><a href="upload.php">Upload</a></li>
			<li><a  class="bottom" href="#" data-toggle="tooltip" data-placement="bottom" title="Current Storage Space: <?php echo $space; ?>"><?php echo $accountName ?></a></li>
			<li><a href="logout.php">Log out</a></li>
		  </ul>
		  <form class="navbar-form navbar-right" name="searchForm" action="" method="GET">
                <input type="text" name="searchStr" class="form-control" placeholder="Search...">
		  </form>
	    </div>
	  </div>
	</nav>
	
    <?php writeBreadcrumb($accountName, $currentDir); ?>

	<!-- SIDEBAR -->
	<form action="directory.php" method="get" id="fileManForm" name="fileManForm">
		<div class="list-group">
			<a href="#" class="list-group-item active">
				<h4 class="list-group-item-heading">        
				<?php
					echo "<b>";
					if (isset($selectedFile)) {
						echo $selectedFile;
					} else {
						echo "No file selected";
					}
					echo "</b>";
				?> 
				</h4>
				<li class="list-group-item">
				<?php
					$description = queryDB('SELECT description FROM metadata WHERE filename = "' . $selectedFile . '" AND owner = "' . $accountName . '"');
				
					if ($description == NULL || $description[0]['description'] == NULL) {
						echo "No description avaliable.";
					} 
					else { 
						foreach ($description as $item)
						{
							echo $item['description'];
						}
					}
				?>
				</li>
					<li class="list-group-item">Colour tag:<br>
						<button type="submit" name="colour" value="red"><img src="images/red.png"></button> 
						<button type="submit" name="colour" value="aqua"><img src="images/aqua.png"></button> 
						<button type="submit" name="colour" value="lime"><img src="images/lime.png"></button> 
						<button type="submit" name="colour" value="yellow"><img src="images/yellow.png"></button> 
						<button type="submit" name="colour" value="pink"><img src="images/pink.png"></button> 
						<button type="submit" name="colour" value=NULL><img src="images/none.png"></button> 
					</li>
			</a>
		</div>
		
		<!-- FILE MANAGEMENT BUTTONS -->
		<div class="panel panel-default">
			<div class="panel-body">
				
					<input type="hidden" value="<?php if ($isSelected) { echo $selectedFile; } ?>" name="selectedFile" id="selectedFileHidden">
					<input type="hidden" value="<?php echo $currentDir; ?>" name="currentDir" id="currentDirHidden">

					<!-- New Folder -->
					<div class="dropdown">
						<button id="newFolderBtn" class="btn btn-default dropdown-toggle" type="button" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							New Folder
						</button>
						<ul class="dropdown-menu" aria-labelledby="newFolderMenu">
							<li><h4>New Folder</h4></li>
							<li><input type='text' name='folderName' placeholder="Name"></li>
							<li>
								<input class="btn btn-default dropdown-toggle" type='submit' name='confirmNewFolder' value='Create'>
							</li>
						</ul>
					</div>
					
					<!-- Edit -->
					<div class="dropdown">
					<button id="editBtn" class="btn btn-default dropdown-toggle" type="button" id="editMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
						Edit
					</button>
						<ul class='dropdown-menu' aria-labelledby='editButton'>
							<li><h4>Edit</h4></li>
							<li><input type='text' name='newName' placeholder="File Name"></li>
							<li><input type='text' name='newDescription' placeholder="Description"></li>
							<li><input class="btn btn-default dropdown-toggle" type='submit' name='confirmEdit' value='Confirm'></li>
						</ul>
					</div>
					
					<!-- Move To... -->
					<div class="dropdown">
						<button id="moveToBtn" class="btn btn-default dropdown-toggle" type="button" id="moveToMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							Move To...
							<span class="caret"></span>
						</button>
						<ul class="dropdown-menu" aria-labelledby="moveToMenu" id="moveToList">
                            <li><h4>Move To...</h4></li>
							<?php writeFolders($accountName, $selectedFile); ?>
						</ul>
					</div>
					
					<input id="downloadBtn" type="submit" class="btn btn-default" value="Download" name="download" id="fileManButton">

					<!-- Delete -->
					<div class="dropdown">
						<button id="deleteBtn" class="btn btn-default dropdown-toggle" type="button" id="moveToMenu" data-toggle="dropdown" aria-haspopup="true" aria-expanded="true">
							Delete
						</button>
					<ul class="dropdown-menu" aria-labelledby="moveToMenu">
						<li>Are you sure you want to delete this file?</li>
						<li><input class="btn btn-default dropdown-toggle" type="submit" name="confirmDelete" value="Yes"></li>
					</div>

					<input  id="shareBtn" type="submit" class="btn btn-default" value="Share" name="share">
			</div>
		</div>
	</form>
    <script> setFileManButtons(<?php echo JSON_encode(!$isSelected); ?>); </script>

	
	<!-- FILES TABLE -->
	<div class="table-responsive">
		<table class="table table-hover" id="directoryTable">
		  <thead>
              <tr>
                <th id="nameHead" onclick="orderTable(0, true)">Name</th>
                <th id="typeHead" onclick="orderTable(1, true)">Type</th>
                <th id="timeHead" onclick="orderTable(2, true)">Last Modified</th>
                <th id="sizeHead" onclick="orderTable(3, true)">Size</th>
                <th id="colourHead" onclick="orderTable(4, true)">Colour</th>
                <?php 
                    if ($searchStr) { 
                        echo '<th id="dirHead"  onclick="orderTable(5, true)">Directory</th>';
                    }
                ?>
              </tr>
		  </thead>
          <tbody>
        <!--File Selection Form -->
        <form action="directory.php" method="post">
	        <?php
		        if (!$searchStr) {
                    // Get metadata table info
		            $metadata = queryDB('SELECT * FROM metadata WHERE location = "' . $currentDir . '" AND owner = "' . $accountName . '"');
		            // Define desired columns
		            $columns = array('filename', 'filetype', 'timestamp', 'filesize', 'colour');
                    // Write to HTML table
		            writeTable($metadata, $columns, $selectedFile, $isFolder, $currentDir, $accountName, $searchStr);
                } else {
		            $metadata = queryDB('SELECT * FROM metadata 
                                WHERE owner = "' . $accountName . '" AND filename LIKE "%' . $searchStr . '%" 
                                OR owner = "' . $accountName . '" AND description LIKE "' . $searchStr . '%" 
                                OR owner = "' . $accountName . '" AND filetype LIKE "' . $searchStr . '"');
                    
		            $columns = array('filename', 'filetype', 'timestamp', 'filesize', 'colour', 'location');
                    writeSearchResults($metadata, $columns, $accountName);
                }
	        ?>
        </form>
		  </tbody>
		</table>
    </div>
		

		<!-- Latest compiled and minified JavaScript -->
		<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	</body>

</html>