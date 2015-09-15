package jmail;

public interface AliasProvider {

	List<Alias> getAllAliases();

	List<Alias> matchAlias();

	void updateAlias(Alias grace);
}
