var synth
var minFreq = 220;
var maxFreq = 440;

var httpServerPort = 8000;
var baseAddress = "pass"; 

function setup() {
 	createCanvas(640, 480);
}


function draw() {
	background(100);
}

function mousePressed() {
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


function send(addressPattern,json) {
	httpGet(baseAddress+addressPattern,json,"json",
	function(args) { // callback
		console.log("sent: "+args.created);
	},function(err) {	// error
		console.log("error: "+err);
	});	
}


function getFreq(xPos) {
	return map(xPos,0,width,minFreq,maxFreq);
}

function getAmp(yPos) {
	return  yPos / height; 
}