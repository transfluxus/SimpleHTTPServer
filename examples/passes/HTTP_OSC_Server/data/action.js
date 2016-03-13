var synth
var minFreq = 220;
var maxFreq = 440;

var synths = [];

var httpServerPort = 8000;
var baseAddress = "pass/"; //"http://127.0.0.1:"+httpServerPort+"/pass/";


function setup() {
  createCanvas(640, 480);
}


function draw() {
	background(200);
	println("hi");
	if(mouseIsPressed) {
		console.log("hi")
	}
}

function mousePressed() {
	global synth;
	if(synth === undefined) {
		synth = {"freq": getFreq(mouseX),"amp": getAmp(mouseY)};
		send("/synth/new",synth);
	} else {
		synth.freq = getFreq(mouseX);
		synth.amp =  getAmp(mouseY);
		send("/synth/update",synth);
	}
}

function mouseDragged() {
	console.log("d");
	synth.freq = getFreq(mouseX);
	synth.amp =  getAmp(mouseY);	
	console.log(synth);
	send("/synth/new",synth);
}

function mouseReleased() {
	console.log("r");
}


function send(addressPattern,json) {
	httpGet(baseAddress+"addressPattern",json,"json",
	function(args) { // callback
		console.log("win: "+args);
	},function(args) {	// error
		console.log("created: "+args.created);
	});	
}


function getFreq(xPos) {
	return map(xPos,0,width,minFreq,maxFreq);
}

function getAmp(yPos) {
	return  yPos / height; 
}