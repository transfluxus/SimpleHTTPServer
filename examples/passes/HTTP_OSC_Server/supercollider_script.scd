(
SynthDef('funSynth', {
	arg freq = 440, amp = 0.5;
	var sound = SinOsc.ar(freq,mul:amp);
	Out.ar(0,sound);
}).add;
)

s = Synth('funSynth',[freq:0,mul:0]);

(
OSCdef('startlistener',{
	arg msg;
	msg[1].postln;
	s.set('freq',msg[1]);
	s.set('amp',msg[2]);
},'/synth/new');
)
(
OSCdef('startlistener',{
	arg msg;
	msg[1].postln;
	s.set('freq',msg[1]);
	s.set('amp',msg[2]);
},'/synth/update');
)


NetAddr.localAddr