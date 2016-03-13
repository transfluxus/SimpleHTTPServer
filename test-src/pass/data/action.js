var synth
var minFreq = 220;
var maxFreq = 440;

var synths = [];

var httpServerPort = 8000;
var baseAddress = "pass"; //"http://127.0.0.1:"+httpServerPort+"/pass/";


function setup() {
  createCanvas(640, 480);
}


function draw() {
	background(200);
}

function mousePressed() {
	console.log(synth === undefined)
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
	synth.freq = getFreq(mouseX);
	synth.amp =  getAmp(mouseY);	
	send("/synth/update",synth);
}

function mouseReleased() {
}


function send(addressPattern,json) {
	httpGet(baseAddress+addressPattern,json,"json",
	function(args) { // callback
		console.log("win: "+args.created);
	},function(err) {	// error
		console.log("loss: "+err);
	});	
}


function getFreq(xPos) {
	return map(xPos,0,width,minFreq,maxFreq);
}

function getAmp(yPos) {
	return  yPos / height; 
}