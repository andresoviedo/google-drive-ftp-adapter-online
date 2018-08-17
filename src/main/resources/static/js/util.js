function parse_query_string(query) {
    var vars = query.split("&");
    var query_string = {};
    for (var i = 0; i < vars.length; i++) {
        var pair = vars[i].split("=");
        var key = decodeURIComponent(pair[0]);
        var value = pair.length >= 2? decodeURIComponent(pair[1]) : "";
        // If first entry with this name
        if (typeof query_string[key] === "undefined") {
            query_string[key] = value;
            // If second entry with this name
        } else if (typeof query_string[key] === "string") {
            var arr = [query_string[key], value];
            query_string[key] = arr;
            // If third or later entry with this name
        } else {
            query_string[key].push(value);
        }
    }
    return query_string;
}

function getQueryParams(){
    var search = window.location.search;
    if (search.startsWith("?")){
        search = search.substring(1);
    }
    // Parse url query parameters
    return parse_query_string(search);
}

function showTab(n) {
    // This function will figure out which tab to display
    var x = document.getElementById(n);
    if (!x) return;

    // Hide the current tab:
    if (currentTab != undefined){
        document.getElementById(currentTab).style.display = "none";
    }
    // This function will display the specified tab of the form...
    x.style.display = "block";
    // update current tab
    currentTab = n;
}

function nextPrev(n) {
    // This function will figure out which tab to display
    var x = document.getElementsByClassName("tab");
    // Exit the function if any field in the current tab is invalid:
    if (n == 1 && !validateForm()) return false;

    // if you have reached the end of the form...
    if (currentTab >= x.length) {
        // ... the form gets submitted:
        document.getElementById("regForm").submit();
        return false;
    }

    // Otherwise, display the correct tab:
    showTab(n);

    //... and fix the Previous/Next buttons:
    if (n == 0) {
        document.getElementById("prevBtn").style.display = "none";
    } else {
        document.getElementById("prevBtn").style.display = "inline";
    }
    if (n == (x.length - 1)) {
        document.getElementById("nextBtn").innerHTML = "Submit";
    } else {
        document.getElementById("nextBtn").innerHTML = "Next";
    }

    //... and run a function that will display the correct step indicator:
    fixStepIndicator(n)
}

function validateForm(formId) {
    // This function deals with validation of the form fields
    var y, i, valid = true;
    y = document.forms[formId].getElementsByTagName("input");
    // A loop that checks every input field in the current tab:
    for (i = 0; i < y.length; i++) {
        // If a field is empty...
        if (y[i].value == "") {
          // add an "invalid" class to the field:
          y[i].className += " invalid";
          // and set the current valid status to false
          valid = false;
        }
        else if (y[i].getAttribute("pattern")){
            var pattern = new RegExp(y[i].getAttribute("pattern"));
            var match = y[i].value.match(pattern);
            if (!match){
                // add an "invalid" class to the field:
                y[i].className += " invalid";
                // and set the current valid status to false
                valid = false;
            }
        }
        if (!valid){
            y[i].focus();
        }
    }

    return valid; // return the valid status
}

function fixStepIndicator(n) {
  // This function removes the "active" class of all steps...
  var i, x = document.getElementsByClassName("step");
  for (i = 0; i < x.length; i++) {
    x[i].className = x[i].className.replace(" active", "");
  }
  //... and adds the "active" class on the current step:
  x[n].className += " active";
}

function processParams(dialogId, messageId, title){
    processAlert(dialogId, messageId, title);
    processPage();
    processAction();
}

function processPage(){
    var params = getQueryParams();
    if (params.page){
        showTab(params.page);
    }
}

function processAction(){
    var params = getQueryParams();
    if (params.action){
    }
}

function processAlert(dialogId, messageId, title){
    var params = getQueryParams();
    var message = undefined;
    if (params.error != undefined){
        title = title || "Error";
        message = params.error;
        if (message == 'auth'){
            title = 'Authentication error';
            message = 'User or password incorrect';
        } else {
            title = 'Unexpected error';
            message = 'Please try again later.';
        }
    } else if (params.code){
        title = title || "Information";
        message = params.code;
        if (message == 'ok'){
            title = 'Information';
            message = 'Action executed successfully';
        } else if (message == 'username'){
            title = 'Registration failed';
            message = 'User already registered';
        } else if (message == 'email'){
            title = 'Registration failed';
            message = 'Email already registered';
        } else if (message == 'invalid-password'){
            title = 'Registration failed';
            message = 'Invalid password';
        } else if (message == 'ftp_user_already_exists'){
            title = 'Request failed';
            message = 'FTP User already registered';
        }
    } else if (params.logout != undefined){
        title = title || 'Logout'
        message = 'Session closed';
    }
    if (message != undefined){
        if (dialogId){
            showDialog(dialogId, title, message);
        } else {
            showMessage(messageId, title, message);
        }
    }
}

function showMessage(msgId, title, message){
    var e = document.getElementById(msgId);
    message = message? ' :'+message : '';
    e.innerHTML = "<b>"+title+"</b>"+message;
    e.style.display='block';
}

function showDialog(dialogId,title,message){
    var dialog = document.getElementById(dialogId);
    dialog.querySelector('.close').addEventListener('click', function() {
        dialog.close();
           });
    var titleEl = dialog.querySelector('.mdl-dialog__title');
    titleEl.innerHTML = title;
    var contentEl = dialog.querySelector('.mdl-dialog__content');
    contentEl.innerHTML = message;
    dialog.showModal();
}

function clearInputs() {
console.log('clearing inputs');
    setTimeout(function(){
        console.log('clearing inputs');
        var i = document.querySelectorAll('input');
        Array.prototype.forEach.call(i, function (elem) {
            if (elem.getAttribute('autocomplete')=='off'){
                elem.value = '';
            }
        });
    }, 1000);
}

function fixMaterialInputs(){
    // fix text inputs when they are dirty (pre-filled values)
    var i = document.querySelectorAll('.mdl-textfield');
    Array.prototype.forEach.call(i, function (elem) {
        elem.classList.add('is-dirty');
    });
}
