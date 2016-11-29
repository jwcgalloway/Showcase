/**
 * Disable file management buttons if no file has been selected.
 *
 * @param setDisabled - Boolean - true if no file has been selected, therefore disable.
 *
 * @author James Galloway
 */
function setFileManButtons(setDisabled) {
    var inputs = document.getElementById("fileManForm").elements;

    for (var i = 0, element; element = inputs[i++];) {
        if (element == document.getElementById("editBtn") 
            || element == document.getElementById("moveToBtn")
            || element == document.getElementById("downloadBtn")
            || element == document.getElementById("deleteBtn")
            || element == document.getElementById("shareBtn"))
        {

            element.disabled = setDisabled;

            //if (setDisabled) {
            //    element.className += "inactive";
            //} else {
            //    element.className += "active";
            //}
        }
    }
} // end setFileManButtons

/** Directory Sorting **/

var currentSort = null;

/**
 * Order a particular table's rows based on the column header that is selected.
 *
 * @param HTML element - this - the HTML table heading that is clicked.
 * @param boolean - ascending - true if sorting should be in ascending order - false for descending.
 *
 * @author James Galloway
 */
function orderTable(headerIndex) {
    var rows = $('#directoryTable tbody tr').get();

    if (currentSort !== headerIndex) {
        currentSort = headerIndex;
        ascending = true;
    } else if (currentSort == headerIndex && ascending == false) {
        ascending = true;
    } else {
        ascending = false;
    }

    rows.sort(function (a, b) {
        var A = $(a).children('td').eq(headerIndex).attr('sortKey');
        var B = $(b).children('td').eq(headerIndex).attr('sortKey');
        if (A < B) {
            if (ascending) {
                return -1;
            }
            return 1;
        }
        if (A > B) {
            if (ascending) {
                return 1;
            }
            return -1;
        }
        return 0;
    });
    $.each(rows, function (index, row) {
        $('#directoryTable').children('tbody').append(row);

    });

    addSymbol('directoryTable', headerIndex, ascending);
} // end orderTable

/**
 * Adds an up or a down arrow next to the specified column header text.
 *
 * @param str - tableID - the HTML ID of the specified table.
 * @param int - headerIndex - the index value of the specified header relative to its table.
 * @param boolean - ascending - true if sorting in ascending order, false if descending.
 *
 * @author James Galloway
 */
function addSymbol(tableID, headerIndex, ascending) {
    resetHeaders();
    var cell = document.getElementById(tableID).rows[0].cells[headerIndex];
    var cellContent = document.getElementById(tableID).rows[0].cells[headerIndex].innerHTML;

    if (ascending) {
        cell.innerHTML = cellContent.concat(' &#9662');
    } else {
        cell.innerHTML = cellContent.concat(' &#9652');
    }
} // end addSymbol

function resetHeaders() {
    document.getElementById('directoryTable').rows[0].cells[0].innerHTML = 'Name';
    document.getElementById('directoryTable').rows[0].cells[1].innerHTML = 'Type';
    document.getElementById('directoryTable').rows[0].cells[2].innerHTML = 'Last Modified';
    document.getElementById('directoryTable').rows[0].cells[3].innerHTML = 'Size';
    document.getElementById('directoryTable').rows[0].cells[4].innerHTML = 'Colour';
    if (typeof document.getElementById('directoryTable').rows[0].cells[5] !== 'undefined') {
        document.getElementById('directoryTable').rows[0].cells[5].innerHTML = 'Directory';
    }
}

// function swapForm(form) {
// $(function() {

    // $('#login-form-link').click(function(e) {
		// $("#login-form").delay(100).fadeIn(100);
 		// $("#register-form").fadeOut(100);
		// $('#register-form-link').removeClass('active');
		// $(this).addClass('active');
		// e.preventDefault();
	// });
	// $('#register-form-link').click(function(e) {
		// $("#register-form").delay(100).fadeIn(100);
 		// $("#login-form").fadeOut(100);
		// $('#login-form-link').removeClass('active');
		// $(this).addClass('active');
		// e.preventDefault();
	// });

// });
// } // end swapForm