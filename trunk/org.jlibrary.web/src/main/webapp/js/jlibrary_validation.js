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
	var username= new LiveValidation('usernameheader',{onlyOnSubmit:true});
	var password= new LiveValidation('passwordheader',{onlyOnSubmit:true});
	username.add(Validate.Presence);
	password.add(Validate.Presence);
}

function documentFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('description',{onlyOnSubmit:true});
	//var content= new LiveValidation('passwordheader',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}

function directoryFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('description',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}

function categoryFormValidation(){
	var name= new LiveValidation('name',{onlyOnSubmit:true});
	var description= new LiveValidation('description',{onlyOnSubmit:true});
	name.add(Validate.Presence);
	description.add(Validate.Presence);
}