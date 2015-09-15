package jmail.alias;

import java.util.ArrayList;
import java.util.List;

import jmail.Alias;
import jmail.AliasProvider;

public class MockAliasProvider implements AliasProvider {

	List<Alias> list = new ArrayList<>();
	
	MockAliasProvider() {
		list.add(new Alias("Ian Darwin", "nobody@nowhere.comp"));
	}
	
	@Override
	public List<Alias> getAllAliases() {
		return list; // XXX unmodifiable copy?
	}

	@Override
	public List<Alias> matchAlias(String s) {
		List<Alias> matches = new ArrayList<>();
		for (Alias a : list) {
			if (a.name.contains(s) || a.email.contains(s)) {
				matches.add(a);
			}
		}
		return matches;
	}

	@Override
	public void updateAlias(Alias grace) {
		// ??
	}

}
