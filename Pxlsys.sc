Pxlsys : Pattern {
	var <>tree, <>depth, <>instantUpdate, <>repeats;


	*new {|tree=\default, depth=8, instantUpdate=true, repeats=inf|
		^super.newCopyArgs(tree, depth, instantUpdate, repeats);
	}

	lookup {|collection, repeats=inf, default=nil|
		default = default ? Rest(1);
		^PdictRest(collection, this, repeats, default);
	}

	embedInStream {|inval|

		var treeStream = tree.asStream;
		var depthStream = depth.asStream;

		repeats.do {
			var treeValue = treeStream.next(inval);
			var depthValue = depthStream.next(inval);

			var lsys = XLsys.getLsys(treeValue.asString.asSymbol);

			var generationID;
			var string;
			var i = 0;

			(depthValue < 0).if {
				depthValue = 0;
			};

			(depthValue > XLsys.numRecursions).if {
				depthValue = XLsys.numRecursions - 1;
			};

			generationID = treeValue.asString ++ lsys.generation.asStream;
			string = lsys.results[depthValue];

			{i < string.size}.while({
				var nextGenerationID;
				var nextSymbol = string[i];

				(nextSymbol == $_).if ({
					Rest().yield;
				},{
					nextSymbol.isDecDigit.if({
						nextSymbol.asString.asInteger.yield;
					}, {
						nextSymbol.asSymbol.yield;
					});
				});

				nextGenerationID = treeValue.asString ++ lsys.generation.asStream;

				((nextGenerationID != generationID) && instantUpdate).if {
					string = lsys.results[depthValue];
					generationID = nextGenerationID;
				};

				i = i + 1;
			});
		};
	}
}