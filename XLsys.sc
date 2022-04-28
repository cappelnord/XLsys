XLsysForest {

	var <trees;

	*new {
		^super.new.init;
	}

	init {
		trees = IdentityDictionary();
	}

	getLsys {|key|
		key = key.asSymbol;

		trees[key].isNil.if {
			trees[key] = XLsys();
		}

		^trees[key];
	}

	// TODO: validate!

	feed {|cmd|
		var items = cmd.split($#);
		items.do {|item|
			var tokens;

			(item.find($@) == nil).if({
				item = "default@" ++ item;
			});

			tokens = item.split($@);
			this.getLsys(tokens[0].asSymbol).feed(tokens[1]);
		};
	}
}

 XLsys {
	classvar <forest;
	classvar stack;

	classvar <>maxItems = 256;
	classvar <>numRecursions = 8;

	var <rules;
	var <axiom;
	var <results;
	var <generation = 0;

	*new {
		^super.new.init;
	}

	init {
		rules = IdentityDictionary();
		axiom = "0";
		this.generate();
	}

	feed {|cmd|
		var items = cmd.split($,);
		items.do {|item|
			var pair = item.split($.);

			(pair[0] == "*").if ({
				axiom = pair[1];

				(pair[1] == "N").if ({
					this.init;
				});
			}, {
				(pair[1] == "N").if ({
					rules.removeAt(pair[0].asSymbol);
				}, {
					rules[pair[0].asSymbol] = pair[1];
				});
			});
		};

		this.generate();
	}

	generate {
		var lastResult = axiom;

		var sortedKeys = rules.keys.asList.collect {|x| x.asString};
		sortedKeys = sortedKeys.sort({|a, b|
			a.size >= b.size;
		});

		results = List();
		results.add(axiom);

		numRecursions.do {
			var result = "";
			{(lastResult.size > 0) && (result.size < maxItems)}.while({
				var foundRule = nil;
				var i = 0;
				{(i < sortedKeys.size) && foundRule.isNil}.while({
					var search = sortedKeys[i];
					(lastResult.find(search) == 0).if {
						foundRule = search;
					};
					i = i + 1;
				});

				foundRule.isNil.if({
					result = result ++ lastResult[0];
					lastResult = lastResult[1..];
				}, {
					result = result ++ rules[foundRule.asSymbol];
					lastResult = lastResult[foundRule.size..];
				});
			});

			result = result[0..maxItems];

			lastResult = result;
			results.add(lastResult);
		};

		generation = generation + 1;
		results.last.postln;
	}

	*getLsys {|key|
		forest = forest ?? {XLsys.push};
		^forest.getLsys(key);
	}

	*feed {|cmd|
		forest = forest ?? {XLsys.push};
		forest.feed(cmd);
	}

	*push {
		var lsys = XLsysForest();
		stack = stack ?? {List()};
		stack.add(lsys);
		forest = lsys;
		^lsys;
	}

	*pop {
		var lsys;
		stack = stack ?? {List()};
		lsys = stack.pop;
		forest = stack.last;
		^lsys;
	}
}