function googleInit() {
    gapi.load('auth2', function() {
        // Ready.
        console.log("ready!");
        auth2 = gapi.auth2.init({
            //client_id: '275751503302-dl2bmgru2varjibm3vf12fk4m71onbib.apps.googleusercontent.com'
            scope: 'profile email'
        });
        var params = getQueryParams();
        if (params.action){
            if (params.action == 'logout'){
                setTimeout(function(){
                    console.log('logging out...');
                    googleSignOut();
                },500)
            }
        }
    });
}

function onSignIn(googleUser) {
    /*// Useful data for your client-side scripts:
    var profile = googleUser.getBasicProfile();
    console.log("ID: " + profile.getId()); // Don't send this directly to your server!
    console.log('Full Name: ' + profile.getName());
    console.log('Given Name: ' + profile.getGivenName());
    console.log('Family Name: ' + profile.getFamilyName());
    console.log("Image URL: " + profile.getImageUrl());
    console.log("Email: " + profile.getEmail());*/

    // The ID token you need to pass to your backend:
    var id_token = googleUser.getAuthResponse().id_token;
    //console.log("ID Token: " + id_token);

    // Validate token
    validateUser(id_token);
}

function validateUser(id_token){
    var xhr = new XMLHttpRequest();
    xhr.open('POST', '/google/login');
    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');
    xhr.onload = function() {
        var tokenStatus = JSON.parse(xhr.responseText);
        console.log(tokenStatus);
        if (tokenStatus.status == "ko"){
            return;
        }

        document.forms['google-loginForm'].elements.namedItem('username').value = tokenStatus.username;
        document.forms['google-loginForm'].elements.namedItem('password').value = id_token;

        // into the <form th:data-login-action="@{/login}" th:data-register-action="@{/register}">
        /*var action;
        if (tokenStatus.status == "registered"){
            action = document.forms['google-loginForm'].getAttribute('data-login-action');
        }*/
        /*else if (tokenStatus.status == "not-registered"){
            document.forms['google-loginForm'].elements.namedItem('email').value = tokenStatus.email;
            action = document.forms['google-loginForm'].getAttribute('data-register-action');
        }*/
        //document.forms['google-loginForm'].action = action;

        document.forms['google-loginForm'].submit();
    }
    xhr.send('token=' + id_token/*+'&'+_csrf.parameterName+'='+_csrf.token*/);
}

function googleSignOut() {
    var auth2 = gapi.auth2.getAuthInstance();
    auth2.signOut().then(function () {
        console.log('User signed out.');
    });
}