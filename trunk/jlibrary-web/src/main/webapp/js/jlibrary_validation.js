window.onload = makeDoubleDelegate(window.onload, validation);
window.onload = makeDoubleDelegate(window.onload, searchValidation);
window.onload = makeDoubleDelegate(window.onload, loginValidation);
function makeDoubleDelegate(function1, function2) {
    return function() {
        if (function1)
            function1();
        if (function2)
            function2();
    }
}

function validation(){
	if(document.getElementById('validation')){
		var idform = document.getElementById('validation').value;
		switch(idform){
			case "directory":
				directoryFormValidation();
				break;
			case "document": 
				documentFormValidation();
				break;
			case "documentUpload":
				documentUploadFormValidation();
				break;
			case "category":
				categoryFormValidation();
				break;
			case "register":
				registerValidation();
				break;
		}
	}
}

function registerValidation(){
	var username = new LiveValidation('username',{onlyOnSubmit:true});
	var password = new LiveValidation('password',{onlyOnSubmit:true});
	var email = new LiveValidation('email',{onlyOnSubmit:true});
	var captcha= new LiveValidation('j_captcha_response',{onlyOnSubmit:true});
	username.add(Validate.Presence);
	password.add(Validate.Presence);
	email.add(Validate.Presence);
	email.add(Validate.Email);
	captcha.add(Validate.Presence);
}

function searchValidation(){
	var search= new LiveValidation('searchtext',{onlyOnSubmit:true});
	search.add(Validate.Presence);
}

function loginValidation(){
	if(document.getElementById('usernameheader')){
		var username= new LiveValidation('usernameheader',{onlyOnSubmit:true});
		var password= new LiveValidation('passwordheader',{onlyOnSubmit:true});
		username.add(Validate.Presence);
		password.add(Validate.Presence);
	}
}

function documentFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('descriptionText',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}

function documentUploadFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('descriptionText',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}

function directoryFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('descriptionText',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}

function categoryFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('descriptionText',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}