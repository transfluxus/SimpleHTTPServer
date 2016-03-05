var gui = new dat.GUI({ autoPlace: false });
document.body.appendChild(gui.domElement);
var update = {};
var dirty = false;

function addToUpdate(objName,valName,value) {
	if (!(objName in update)) {
		update[objName] = {};
	}
	var addValue = !valName in update[objName] || update[objName][valName] != value;
	if(addValue) {
		update[objName][valName] = value;	
		dirty = true;	
	}
}

function send(jsonObj) {
		//console.log(jsonObj);
		var xhr = new XMLHttpRequest();
		xhr.open('PUT', 'datgui');
		xhr.setRequestHeader('Content-Type', 'application/json');
		xhr.onload = function() {
		    if (xhr.status === 200) {
		
		    }
		};
		xhr.send(jsonObj);
}

function autoSend() {
	//nsole.log(">>>");
	//console.log(update);
	//console.log(Object.keys(update).length);
	//if(Object.keys(update).length != 0){
	if(dirty) {
		console.log("sending: ");
		var jsonS = JSON.stringify(update, null, 2);
		update = {};
		dirty = false;
		console.log(jsonS);
		send(jsonS);
		//console.log(update);
		//console.log(Object.keys(update).length);
	} 
}


// Generated classes:
var MovingCircle1 = function() {
	this.level = 2;
	this.speed = 0.2;
	this.y = 50.0;
	this.red = false;
	this.reset = function() { send('{"MovingCircle1": {"function": "reset"}}'); };
};

var MovingCircle2 = function() {
	this.level = 2;
	this.speed = 0.2;
	this.y = 120.0;
};

var Tester1 = function() {
	this.red = 0;
	this.green = 0;
	this.blue = 1;
};

var TestClass21 = function() {
	this.value = 'Fill';
};

window.onload = function() {
	var MovingCircle1Folder = gui.addFolder('MovingCircle1');
	var MovingCircle1Obj = new MovingCircle1();
	MovingCircle1Folder.add(MovingCircle1Obj, 'level').min(1.0).max(30.0).step(1).onChange(function(value) { addToUpdate('MovingCircle1','level',MovingCircle1Obj.level); });
	MovingCircle1Folder.add(MovingCircle1Obj, 'speed').min(-20.0).max(20.0).onChange(function(value) { addToUpdate('MovingCircle1','speed',MovingCircle1Obj.speed); });
	MovingCircle1Folder.add(MovingCircle1Obj, 'y').min(0.0).max(200.0).onChange(function(value) { addToUpdate('MovingCircle1','y',MovingCircle1Obj.y); });
	MovingCircle1Folder.add(MovingCircle1Obj, 'red').onChange(function(value) { addToUpdate('MovingCircle1','red',MovingCircle1Obj.red); });
	MovingCircle1Folder.add(MovingCircle1Obj, 'reset')
	MovingCircle1Folder.open();

	var MovingCircle2Folder = gui.addFolder('MovingCircle2');
	var MovingCircle2Obj = new MovingCircle2();
	MovingCircle2Folder.add(MovingCircle2Obj, 'level').min(1.0).max(30.0).step(1).onChange(function(value) { addToUpdate('MovingCircle2','level',MovingCircle2Obj.level); });
	MovingCircle2Folder.add(MovingCircle2Obj, 'speed').min(-20.0).max(20.0).onChange(function(value) { addToUpdate('MovingCircle2','speed',MovingCircle2Obj.speed); });
	MovingCircle2Folder.add(MovingCircle2Obj, 'y').min(0.0).max(200.0).onChange(function(value) { addToUpdate('MovingCircle2','y',MovingCircle2Obj.y); });

	var Tester1Folder = gui.addFolder('Tester1');
	var Tester1Obj = new Tester1();
	Tester1Folder.add(Tester1Obj, 'red').min(0.0).max(255.0).step(1).onChange(function(value) { addToUpdate('Tester1','red',Tester1Obj.red); });
	Tester1Folder.add(Tester1Obj, 'green').min(0.0).max(255.0).step(1).onChange(function(value) { addToUpdate('Tester1','green',Tester1Obj.green); });
	Tester1Folder.add(Tester1Obj, 'blue').min(0.0).max(255.0).step(1).onChange(function(value) { addToUpdate('Tester1','blue',Tester1Obj.blue); });
	Tester1Folder.open();

	var TestClass21Folder = gui.addFolder('TestClass21');
	var TestClass21Obj = new TestClass21();
	TestClass21Folder.add(TestClass21Obj,'value',['Stroke', 'Fill', 'StrokeFill']).onChange(function(value) { addToUpdate('TestClass21','value',TestClass21Obj.value); });
	TestClass21Folder.open();

};
setInterval(autoSend,100);
