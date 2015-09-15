package jmail;

import java.util.List;

public interface AliasProvider {

	List<Alias> getAllAliases();

	List<Alias> matchAlias(String query);

	void updateAlias(Alias grace);
}
