package jmail;

import java.util.List;

public interface AliasProvider {

	List<Alias> getAllAliases();

	List<Alias> matchAlias();

	void updateAlias(Alias grace);
}
