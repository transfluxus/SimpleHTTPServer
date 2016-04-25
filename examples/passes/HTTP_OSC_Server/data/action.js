var synth
var minFreq = 220;
var maxFreq = 440;

var synths = [];

function setup() {
  createCanvas(640, 480);
}


function draw() {
	background(0);
}

function mousePressed() {
	synth = {freq: getFreq(mouseX),amp: getAmp(mouseY)};
	createSynth(synth);
}

function mouseDragged() {
	synth.freq = getFreq(mouseX);
	synth.amp: getAmp(mouseY);
	updateSynth(synth);
}

function mouseReleased() {

}

function createSynth() {
	httpGet("/synth/new",synth,"json",
	function(args) { // callback
		console.log(args);
	},function(args) {	// error
		console.log(args);
	});
}	

function updateSynth() {
	httpGet("/synth/update",synth,"json",
	function(args) { // callback
		console.log(args);
	},function(args) {	// error
		console.log(args);
	});
}	
