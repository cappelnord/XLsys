// based on Pdict, will not try to look up Rests and just passes them through

PdictRest : Pattern {
	var <>dict, <>which, <>repeats, <>default;
	*new { arg dict, which, repeats=inf, default;
		^super.newCopyArgs(dict, which, repeats, default);
	}
	storeArgs { ^[dict,which,repeats,default ] }
	embedInStream { arg inval;
		var keyStream, key;
		keyStream = which.asStream;
		repeats.value(inval).do({
			var val;
			key = keyStream.next(inval);
			if(key.isNil) { ^inval };

			(key.class == Rest).if ({
				val = key;
			}, {
				val = dict.at(key);
			});

			inval = (val ? default).embedInStream(inval);
		});
		^inval
	}
}