
PquantDur : Pattern {
	var <>pattern, <>quant, <>min;
	*new { arg pattern, quant=0, min=1/32;
		^super.newCopyArgs(pattern, quant, min);
	}
	storeArgs { ^[pattern, quant, min ] }

	embedInStream { arg inval;
		var stream = pattern.asStream;
		var val = stream.next;

		((quant > 0) && (min < quant)).if {
			min = quant;
		};

		{val.isNil.not}.while {
			(quant > 0).if {
				val = (val / quant).round * quant;
			};
			(val < min).if {
				val = min;
			};
			val.yield;
			val = stream.next;
		}
		^inval;
	}
}